package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class OpenReceptActivity extends AppCompatActivity {

    private TextView receptNev,receptLeiras,receptHozzavalok,receptElkeszites;
    private Button back, delete;
    private SQLiteDBHelper DBManager;

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
                if (DBManager.deleteRecipe(r.getId())) {
                    Toast.makeText(OpenReceptActivity.this, "Sikeres törlés",Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    Toast.makeText(OpenReceptActivity.this, "Sikertelen törlés",Toast.LENGTH_SHORT).show();
                }
                if (!r.isOnlineStored()) {
                    if (DBManager.deleteRecipe(r.getId())) {
                        Toast.makeText(OpenReceptActivity.this, "Sikeres törlés", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OpenReceptActivity.this, "Sikertelen törlés", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    FirebaseFirestore fbFirestore = FirebaseFirestore.getInstance();
                    fbFirestore.collection("recipes").document(r.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(OpenReceptActivity.this, "Sikeres törlés", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OpenReceptActivity.this, "Sikertelen törlés", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        displayRecipe();
    }

    private void init(){
        receptNev = findViewById(R.id.receptNevSelected);
        receptLeiras = findViewById(R.id.receptLeirasSelected);
        receptHozzavalok = findViewById(R.id.receptHozzavalokSelected);
        receptElkeszites = findViewById(R.id.receptElkeszitesSelected);

        back = findViewById(R.id.backButtonSelectedRecept);
        delete = findViewById(R.id.deleteButtonSelectedRecept);
        DBManager = new SQLiteDBHelper(this);

        r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");
    }

    public void displayRecipe(){
        //Recipe r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");

        receptNev.setText(r.getName());
        receptLeiras.setText(r.getDescription());
        receptHozzavalok.setText(r.ingredientsToString());
        receptElkeszites.setText(r.getPreparation());
    }
}
