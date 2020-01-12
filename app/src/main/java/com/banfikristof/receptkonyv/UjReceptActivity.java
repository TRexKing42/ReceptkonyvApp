package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skyhope.materialtagview.TagView;
import com.skyhope.materialtagview.enums.TagSeparator;
import com.skyhope.materialtagview.interfaces.TagItemListener;
import com.skyhope.materialtagview.model.TagModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UjReceptActivity extends AppCompatActivity {

    private TagView tv;
    private Button saveBtn;
    private EditText etNev, etLeiras, etElkeszites;

    private EditText ingNev, ingMennyiseg, ingMertek;
    private Button ingHozzaad;

    private RecyclerView rvIngredients;
    IngredientsAdapter ingredientsAdapter;

    List<Map<String,String>> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uj_recept);

        init();

        tv.initTagListener(new TagItemListener() {
            @Override
            public void onGetAddedItem(TagModel tagModel) {
                //TODO: Hozzáadni fájlhoz
            }

            @Override
            public void onGetRemovedItem(TagModel model) {
                //...
            }
        });

        ingHozzaad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterSize = ingredientsAdapter.getItemCount();

                Map<String, String> hozzavalo = new HashMap<>();
                hozzavalo.put("name",ingNev.getText().toString());
                hozzavalo.put("amount",ingMennyiseg.getText().toString());
                hozzavalo.put("unit",ingMertek.getText().toString());
                ingredients.add(hozzavalo);
                loadIngredients();
                ingredientsAdapter.notifyItemRangeChanged(adapterSize,ingredients.size());


                ingNev.setText("");
                ingMennyiseg.setText("");
                ingMertek.setText("");
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = formToRecipe(true);
                if (recipe == null) return;
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("recipes").child(FirebaseAuth.getInstance().getUid()).push().setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UjReceptActivity.this, "Sikeres mentés!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UjReceptActivity.this, "Sikertelen mentés!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    public interface RvClickListener {
        void onDeleteClicked(int position);
    }

    private Recipe formToRecipe(boolean setUid) {
        if (TextUtils.isEmpty(etNev.getText())){
            Toast.makeText(UjReceptActivity.this, "Étel neve nem lehet üres!",Toast.LENGTH_SHORT).show();
            return null;
        }
        if (TextUtils.isEmpty(etElkeszites.getText())){
            Toast.makeText(UjReceptActivity.this, "Étel elkészítése nem lehet üres!",Toast.LENGTH_SHORT).show();
            return null;
        }
        if (ingredients.isEmpty()) {
            Toast.makeText(UjReceptActivity.this, "Válassz ki legalább egy hozzávalót!",Toast.LENGTH_SHORT).show();
            return null;
        }
        List<TagModel> selectedTags = tv.getSelectedTags();
        Recipe recipe;
        if (TextUtils.isEmpty(etLeiras.getText())){
            recipe = new Recipe(etNev.getText().toString(),etElkeszites.getText().toString(),ingredients);
        } else {
            recipe = new Recipe(etNev.getText().toString(),etLeiras.getText().toString(),etElkeszites.getText().toString(),ingredients);
        }
        recipe.setTags(tagsToString(selectedTags));
        if (setUid) {
            recipe.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            recipe.setOnlineStored(true);
        }
        return recipe;
    }

    private void init() {
        tv = findViewById(R.id.tagView);
        tv.addTagSeparator(TagSeparator.COMMA_SEPARATOR);
        String[] tagList = new String[]{"Gluténmentes", "Laktózmentes", "Vegetáriánus", "Vegán", "Olcsó", "Leves", "Előétel", "Reggeli"};
        tv.setTagList(tagList);

        rvIngredients = findViewById(R.id.ingredientsListRv);
        ingredients = new ArrayList<>();
        loadIngredients();
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));

        saveBtn = findViewById(R.id.ujReceptMentes);

        etNev = findViewById(R.id.ujReceptNev);
        etLeiras = findViewById(R.id.ujReceptLeiras);
        etElkeszites = findViewById(R.id.ujReceptElkeszites);

        ingNev = findViewById(R.id.ujReceptIngNameEt);
        ingMennyiseg = findViewById(R.id.ujReceptIngAmountEt);
        ingMertek = findViewById(R.id.ujReceptIngUnitEt);
        ingHozzaad = findViewById(R.id.ujReceptIngButtonNew);

    }

    private void loadIngredients() {
        ingredientsAdapter = new IngredientsAdapter(ingredients);
        rvIngredients.setAdapter(ingredientsAdapter);
    }

    private List<String> tagsToString(List<TagModel> tags) {

        List<String> result = new ArrayList<>();
        if (tags.isEmpty()){
            result.add("Nincs címke");
        } else {
            for (TagModel item : tags) {
                result.add(item.getTagText());
            }
        }
        return result;
    }
}
