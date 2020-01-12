package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ReceptekFragment.ReceptekFragmentListener {

    private static final int RC_SIGN_IN = 123;
    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nv;
    public FirebaseUser user;
    private TextView headerName, headerEmail;
    private ImageView headerPicture;

    private FirebaseFirestore fbFirestore;
    private FirebaseDatabase fbDatabase;
    private SQLiteDBHelper DBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        dl = findViewById(R.id.drawerlayout);
        nv = findViewById(R.id.mainmenu);
        headerName = findViewById(R.id.menu_header_nev);
        nv.setNavigationItemSelectedListener(this);
        View header = nv.getHeaderView(0);
        headerName = header.findViewById(R.id.menu_header_nev);
        headerEmail = header.findViewById(R.id.menu_header_email);
        headerPicture = header.findViewById(R.id.menu_header_pic);

        fbFirestore = FirebaseFirestore.getInstance();
        fbDatabase = FirebaseDatabase.getInstance();
        DBManager = new SQLiteDBHelper(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,dl,R.string.drawer_open,R.string.drawer_close);
        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new KezdolapFragment());
        fragmentTransaction.commit();
    }

    private void firebaseSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG,true)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in

                user = FirebaseAuth.getInstance().getCurrentUser();
                headerName.setText(user.getDisplayName());
                headerPicture.setImageURI(user.getPhotoUrl());
                headerEmail.setText(user.getEmail());

                // Save user into Firestore/RTDB
                //Realtime DB
                DatabaseReference db = fbDatabase.getReference("/users");
                db.child(user.getUid()).child("displayName").setValue(user.getDisplayName());

                //Firestore
                fbFirestore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()) {
                                Toast.makeText(MainActivity.this, "Üdv újra, " + user.getDisplayName() + "!",Toast.LENGTH_SHORT).show();
                            } else {
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("displayName",user.getDisplayName());
                                fbFirestore.collection("users").document(user.getUid()).set(userData);
                                Toast.makeText(MainActivity.this, "Üdv, " + user.getDisplayName() + "!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;

        switch(menuItem.getItemId())
        {
            case R.id.menu_fooldal:
                selectedFragment = new KezdolapFragment();
                break;
            case R.id.menu_receptek:
                selectedFragment = new ReceptekFragment();
                break;
            case R.id.menu_login:
                firebaseSignInIntent();
                break;
            default:
                Toast.makeText(MainActivity.this, "Még nem",Toast.LENGTH_SHORT).show();
                break;
        }

        if (selectedFragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, selectedFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    ////
    // ReceptekFragment
    ////
    /*public ArrayList<Recipe> getRecipes() {
        Cursor result = DBManager.getRecipes();
        ArrayList<Recipe> returnedList = new ArrayList<>();
        if (result != null && result.getCount() > 0) {
            while (result.moveToNext()){
                String s = result.getString(result.getColumnIndex(SQLiteDBHelper.COL[3]));
                String[] s2 = s.split(","); //Egyelőre nem müködik
                returnedList.add(new Recipe(
                        result.getString(result.getColumnIndex(SQLiteDBHelper.COL[0])),
                        result.getString(result.getColumnIndex(SQLiteDBHelper.COL[1])),
                        result.getString(result.getColumnIndex(SQLiteDBHelper.COL[2])),
                        result.getString(result.getColumnIndex(SQLiteDBHelper.COL[4])),
                        Arrays.asList(s2)
                ));
            }
        }
        return returnedList;
    }*/

    @Override
    public void onRecipeDelete(Recipe recipe) {
        if (!recipe.isOnlineStored()) {
            if (DBManager.deleteRecipe(recipe.getId())) {
                Toast.makeText(MainActivity.this, "Sikeres törlés", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Sikertelen törlés", Toast.LENGTH_SHORT).show();
            }
        } else {
            fbFirestore.collection("recipes").document(recipe.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this, "Sikeres törlés", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Sikertelen törlés", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
