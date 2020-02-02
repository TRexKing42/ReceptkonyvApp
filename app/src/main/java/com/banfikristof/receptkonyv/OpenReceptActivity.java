package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class OpenReceptActivity extends AppCompatActivity {

    private TextView receptNev,receptLeiras,receptHozzavalok,receptElkeszites, receptCimkek;
    private ImageButton back, delete;
    private BottomNavigationView bottomNavigationView;

    private Recipe r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_recept);

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(r.key).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OpenReceptActivity.this, "Sikeres törlés",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpenReceptActivity.this, "Sikertelen törlés",Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch(menuItem.getItemId())
                {
                    case R.id.recipeMenu_overview:
                        selectedFragment = new KezdolapFragment();
                        break;
                    case R.id.recipeMenu_ingredients:
                        selectedFragment = new ReceptekFragment();
                        break;
                    case R.id.recipeMenu_preparation:
                        selectedFragment = new SettingsFragment();
                        break;
                    default:
                        Toast.makeText(OpenReceptActivity.this, "Még nem",Toast.LENGTH_SHORT).show();
                        break;
                }

                if (selectedFragment != null) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, selectedFragment);
                    fragmentTransaction.commit();
                }
                return false;
            }
        });
        displayRecipe();
    }

    private void init(){
        receptNev = findViewById(R.id.receptNevSelected);
        receptLeiras = findViewById(R.id.receptLeirasSelected);
        receptHozzavalok = findViewById(R.id.receptHozzavalokSelected);
        receptElkeszites = findViewById(R.id.receptElkeszitesSelected);
        receptCimkek = findViewById(R.id.receptTagsSelected);

        back = findViewById(R.id.backButtonSelectedRecept);
        delete = findViewById(R.id.deleteButtonSelectedRecept);
        bottomNavigationView = findViewById(R.id.bottomNavView);

        r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");
    }

    public void displayRecipe(){
        //Recipe r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");
        receptCimkek.setText(r.tagsToString());
        receptNev.setText(r.getName());
        receptLeiras.setText(r.getDescription());
        receptHozzavalok.setText(r.ingredientsToString());
        receptElkeszites.setText(r.getPreparation());
    }
}
