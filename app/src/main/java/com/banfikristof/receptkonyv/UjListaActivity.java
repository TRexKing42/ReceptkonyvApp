package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UjListaActivity extends AppCompatActivity {

    private Button save, search;
    private TextView listaPreview;
    private EditText searchName, listName;
    private ListView receptek;

    private List<Map<String, String>> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uj_lista);

        init();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchName.getText().toString().isEmpty()){
                    Toast.makeText(UjListaActivity.this,getResources().getText(R.string.search_name),Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    showRecipes(searchName.getText().toString());
                }
            }
        });

        receptek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe s = (Recipe) receptek.getItemAtPosition(position);
                for (Map<String,String> item : s.getIngredients()) {

                    String name = item.get("name");
                    String unit = item.get("unit");

                    boolean newIng = true;
                    boolean sameUnit = false;

                    for (Map<String,String> item2 : ingredients) {
                        if (item2.get("name").equals(name)) {
                            newIng = false;
                            sameUnit = item2.get("unit").equals(unit);


                            if (sameUnit) {
                                // Nem új és ugyanaz a mértékegység
                                int a = Integer.valueOf(item2.get("amount"));
                                int b = Integer.valueOf(item.get("amount"));
                                item2.put("amount", Integer.toString(a+b));
                            }
                            if (!sameUnit) {
                                // Nem új de nem ugyanaz a mértékegység
                                newIng = true;
                            }
                            break;
                        }
                    }

                    if (newIng){
                        ingredients.add(item);
                    }
                }
                updatePreview();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listName.getText().toString().isEmpty()){
                    Toast.makeText(UjListaActivity.this,getResources().getText(R.string.name_list_pls),Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    ShoppingList s = new ShoppingList();
                    s.setListName(listName.getText().toString());
                    s.setIngredients(ingredients);

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("shoppinglists").child(FirebaseAuth.getInstance().getUid()).push().setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UjListaActivity.this, getResources().getText(R.string.save_good),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UjListaActivity.this, getResources().getText(R.string.save_bad),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void init() {
        ingredients = new ArrayList<>();

        save = findViewById(R.id.saveListBtn);
        search = findViewById(R.id.keresesReceptekButtonList);
        listaPreview = findViewById(R.id.ingredientsInList);
        listaPreview.setText("");
        searchName = findViewById(R.id.keresesReceptekList);
        listName = findViewById(R.id.listName);
        receptek = findViewById(R.id.listReceptekForList);
        registerForContextMenu(receptek);
    }

    private void showRecipes(final String param) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> listOfRecipes = new ArrayList<>();
                Recipe r;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    r = item.getValue(Recipe.class);
                    r.key = item.getKey();
                    if (r.getName().toLowerCase().contains(param.toLowerCase())){
                        listOfRecipes.add(r);
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(UjListaActivity.this, android.R.layout.simple_list_item_1,listOfRecipes);
                receptek.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    private void updatePreview() {
        listaPreview.setText("");
        String newText = "";
        for (Map<String,String> item : ingredients) {
            newText += item.get("amount") + " " + item.get("unit") + " " + item.get("name") + "\n";
        }
        listaPreview.setText(newText);
    }
}
