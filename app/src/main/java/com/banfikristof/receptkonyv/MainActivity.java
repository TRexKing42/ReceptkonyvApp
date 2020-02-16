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
import android.graphics.Point;
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
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ReceptekFragment.ReceptekFragmentListener,
        SettingsFragment.OnFragmentInteractionListener,
        ShoppingFragment.OnShoppingFragmentInteractionListener{

    public static boolean favRecipes = false;
    private static final int RC_SIGN_IN = 123;
    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nv;
    private TextView headerName, headerEmail;
    private ImageView headerPicture;
    private androidx.appcompat.widget.Toolbar toolbar;

    private FirebaseDatabase fbDatabase;

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
        headerPicture = header.findViewById(R.id.profileImgView);

        toolbar = findViewById(R.id.MainToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fbDatabase = FirebaseDatabase.getInstance();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,dl,toolbar,R.string.drawer_open,R.string.drawer_close);
        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new KezdolapFragment());
        fragmentTransaction.commit();


        //Login dolgok
        userLoginChanged();
    }

    private void userLoginChanged() {
        MenuItem loginMenu = nv.getMenu().findItem(R.id.menu_login);
        MenuItem logoutMenu = nv.getMenu().findItem(R.id.menu_logout);
        loginMenu.setEnabled(false);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            loginMenu.setEnabled(true);
            logoutMenu.setEnabled(false);

            headerName.setText(getResources().getString(R.string.plsLogin));
            headerEmail.setText("");

            headerPicture.setImageDrawable(null);
        } else {
            loginMenu.setEnabled(false);
            logoutMenu.setEnabled(true);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            headerName.setText(user.getDisplayName());
            headerEmail.setText(user.getEmail());


            if (user.getPhotoUrl() == null){
                Glide.with(this).load("https://eu.ui-avatars.com/api/?size=60&uppercase=true&name="+user.getDisplayName().replace(' ','+')).apply(RequestOptions.circleCropTransform()).into(headerPicture);
            } else {
                Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(headerPicture);
            }
        }
    }

    private void firebaseSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
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

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userLoginChanged();


                MenuItem loginMenu = nv.getMenu().findItem(R.id.menu_login);
                loginMenu.setEnabled(false);

                // Save user data
                User usr = new User();
                usr.displayName = user.getDisplayName();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                db.child(user.getUid()).setValue(usr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this,"Sikeres regisztráció!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Sikertelen regisztráció!",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(MainActivity.this,"Sikertelen!",Toast.LENGTH_SHORT).show();
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
                MainActivity.favRecipes = false;
                break;
            case R.id.menu_fav_receptek:
                selectedFragment = new ReceptekFragment();
                MainActivity.favRecipes = true;
                break;
            case R.id.menu_login:
                firebaseSignInIntent();
                userLoginChanged();
                break;
            case R.id.menu_settings:
                selectedFragment = new SettingsFragment();
                break;
            case R.id.menu_shoppinglists:
                selectedFragment = new ShoppingFragment();
                break;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                userLoginChanged();
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

    @Override
    public void onRecipeDelete(Recipe recipe) {
        if (recipe.getPictures() != null) {
            for (int i = 0; i < recipe.getPictures().size(); i++) {
                FirebaseStorage.getInstance().getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(recipe.key)
                        .child(recipe.getPictures().get(i))
                        .delete();
            }
        }
        fbDatabase.getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(recipe.key).removeValue();
    }

    @Override
    public void onDeleteProfile() {
        FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                User u;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    u = item.getValue(User.class);
                    users.add(u);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete();
        FirebaseAuth.getInstance().getCurrentUser().delete();
    }
}
