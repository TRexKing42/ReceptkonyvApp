package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.banfikristof.receptkonyv.MainFragments.KezdolapFragment;
import com.banfikristof.receptkonyv.MainFragments.ReceptekFragment;
import com.banfikristof.receptkonyv.MainFragments.SettingsFragment;
import com.banfikristof.receptkonyv.MainFragments.ShoppingFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ReceptekFragment.ReceptekFragmentListener,
        SettingsFragment.OnFragmentInteractionListener,
        ShoppingFragment.OnShoppingFragmentInteractionListener {

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

        if (!isConnectedToNetwork()) {
            Intent intent = new Intent(MainActivity.this, NoConnectionActivity.class);
            startActivity(intent);
            finish();
        }

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

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, toolbar, R.string.drawer_open, R.string.drawer_close);
        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new KezdolapFragment());
        fragmentTransaction.commit();


        //Login dolgok
        userLoginChanged();
    }

    public boolean isConnectedToNetwork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null;
    }

    private void userLoginChanged() {
        MenuItem loginMenu = nv.getMenu().findItem(R.id.menu_login);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            headerName.setText(getResources().getString(R.string.plsLogin));
            headerEmail.setText("");

            headerPicture.setImageDrawable(null);

            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            headerName.setText(user.getDisplayName());
            headerEmail.setText(user.getEmail());


            if (user.getPhotoUrl() == null) {
                Glide.with(this).load("https://eu.ui-avatars.com/api/?size=60&uppercase=true&name=" + user.getDisplayName().replace(' ', '+')).apply(RequestOptions.circleCropTransform()).into(headerPicture);
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
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
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

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userLoginChanged();


                MenuItem loginMenu = nv.getMenu().findItem(R.id.menu_login);

                // Save user data
                User usr = new User();
                usr.displayName = user.getDisplayName();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                db.child(user.getUid()).setValue(usr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, getResources().getText(R.string.welcome_main) + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Sign in failed.
                Toast.makeText(MainActivity.this, getResources().getText(R.string.just_unsuccesful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;

        switch (menuItem.getItemId()) {
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
                FirebaseAuth.getInstance().signOut();
                firebaseSignInIntent();
                userLoginChanged();
                break;
            case R.id.menu_settings:
                selectedFragment = new SettingsFragment();
                break;
            case R.id.menu_shoppinglists:
                selectedFragment = new ShoppingFragment();
                break;
            default:
                Toast.makeText(MainActivity.this, getResources().getText(R.string.error_notyet), Toast.LENGTH_SHORT).show();
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
    public void onDeleteProfile(final Button deleteProfile) {
        deleteProfile.setEnabled(false);
        Log.w("USER_DELETE","Started...");
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final List<StorageReference> pics = new ArrayList<>();
        Log.w("USER_DELETE","UID="+uid);
        FirebaseStorage.getInstance().getReference().child(uid).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.w("USER_DELETE","Pics of current user:");
                if (!listResult.getPrefixes().isEmpty()) {
                    for (StorageReference i : listResult.getPrefixes()) {
                        i.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                for (StorageReference i : listResult.getItems()) {
                                    pics.add(i);
                                    Log.w("USER_DELETE", i.toString());
                                    i.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.w("USER_DELETE", "Deleted");
                                        }
                                    });
                                }

                                Log.w("USER_DELETE", "Deleting from database...");
                                FirebaseDatabase.getInstance().getReference().child("recipes").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.w("USER_DELETE", "Recipes deleted");
                                        FirebaseDatabase.getInstance().getReference().child("shoppinglists").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.w("USER_DELETE", "Shopping Lists deleted");
                                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.w("USER_DELETE", "User data deleted");
                                                        Log.w("USER_DELETE", "Deleting from auth...");

                                                        AuthUI.getInstance().delete(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.w("USER_DELETE", "Profile delete done!");
                                                                } else {
                                                                    Log.w("USER_DELETE", "Profile delete failed!");
                                                                }
                                                                deleteProfile.setEnabled(true);
                                                                userLoginChanged();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                } else {
                    Log.w("USER_DELETE","User has no pictures.");
                    Log.w("USER_DELETE", "Deleting from database...");
                    FirebaseDatabase.getInstance().getReference().child("recipes").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.w("USER_DELETE", "Recipes deleted");
                            FirebaseDatabase.getInstance().getReference().child("shoppinglists").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w("USER_DELETE", "Shopping Lists deleted");
                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.w("USER_DELETE", "User data deleted");
                                            Log.w("USER_DELETE", "Deleting from auth...");

                                            AuthUI.getInstance().delete(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.w("USER_DELETE", "Profile delete done!");
                                                    } else {
                                                        Log.w("USER_DELETE", "Profile delete failed!");
                                                    }
                                                    userLoginChanged();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnectedToNetwork()){
            Intent intent = new Intent(MainActivity.this,NoConnectionActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
