package com.banfikristof.receptkonyv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OpenReceptActivity extends AppCompatActivity {

    private TextView receptNev,receptLeiras,receptHozzavalok,receptElkeszites;
    private Button back;

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

        displayRecipe();
    }

    private void init(){
        receptNev = findViewById(R.id.receptNevSelected);
        receptLeiras = findViewById(R.id.receptLeirasSelected);
        receptHozzavalok = findViewById(R.id.receptHozzavalokSelected);
        receptElkeszites = findViewById(R.id.receptElkeszitesSelected);

        back = findViewById(R.id.backButtonSelectedRecept);
    }

    public void displayRecipe(){
        Recipe r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");

        receptNev.setText(r.getName());
        receptLeiras.setText(r.getDescription());
        receptHozzavalok.setText(r.getIngredientsString());
        receptElkeszites.setText(r.getPreparation());
    }
}
