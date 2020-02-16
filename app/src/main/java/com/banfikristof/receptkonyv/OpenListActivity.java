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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


public class OpenListActivity extends AppCompatActivity {

    private TextView listName, ingList, delete;
    private Button back;

    private ShoppingList s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_list);

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
                FirebaseDatabase.getInstance().getReference().child("shoppinglists").child(FirebaseAuth.getInstance().getUid()).child(s.key).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(OpenListActivity.this, "Sikeres törlés",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpenListActivity.this, "Sikertelen törlés",Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });
    }

    private void init() {
        listName = findViewById(R.id.titleOfList);
        ingList = findViewById(R.id.listHozzavalokSelected);
        back = findViewById(R.id.listBackBtn);
        delete = findViewById(R.id.listDelBtn);

        s = (ShoppingList) getIntent().getSerializableExtra("SelectedList");

        listName.setText(s.getListName());
        ingList.setText(s.display());
    }
}
