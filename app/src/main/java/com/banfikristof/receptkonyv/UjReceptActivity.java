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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skyhope.materialtagview.TagView;
import com.skyhope.materialtagview.enums.TagSeparator;
import com.skyhope.materialtagview.interfaces.TagItemListener;
import com.skyhope.materialtagview.model.TagModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UjReceptActivity extends AppCompatActivity {

    private TagView tv;
    private Button saveBtn, saveOnlineBtn;
    private EditText etNev, etLeiras, etElkeszites;

    private EditText ingNev, ingMennyiseg, ingMertek;
    private Button ingHozzaad;

    private RecyclerView rvIngredients;
    IngredientsAdapter ingredientsAdapter;

    List<Map<String,String>> ingredients;
    private SQLiteDBHelper DBManager;
    private FirebaseFirestore fbFirestore;

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
                Recipe recipe = formToRecipe(false);
                if (recipe == null) return;
                if (DBManager.newRecipe(recipe) == 1) {
                    Toast.makeText(UjReceptActivity.this, "Sikertelen mentés!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UjReceptActivity.this, "Sikeres mentés!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        saveOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = formToRecipe(true);
                if (recipe == null) return;

                Map<String, Object> recipeData;


                recipeData = recipeToDocument(recipe);
                fbFirestore.collection("recipes").add(recipeData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(UjReceptActivity.this, "Sikeres mentés!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(UjReceptActivity.this, "Sikertelen mentés!",Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("recipes").push().setValue(recipe);
            }
        });
    }

    private Map<String, Object> recipeToDocument(Recipe recipe) {
        Map<String, Object> recipeData;
        Map<String, Object> ingredientsData = new HashMap<>();
        Map<String,Map<String, Object>> ingredients = new HashMap<>();

        //for (int i = 0; i < recipe.getIngredients().size(); i++) {
        //    ingredientsData.clear();
        //    ingredientsData.put("iName", recipe.getIngredients().get(i));
        //    ingredientsData.put("iAmount", /*Currently using placeholder value TODO*/ 1);
        //    ingredientsData.put("iUnit", /*Currently using placeholder value TODO*/ "db");
        //    ingredients.put(String.valueOf(i),ingredientsData);
        //}
        List<String> tagList = new ArrayList<>();
        tagList.add("teszt tag"); //TODO
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userReference = fbFirestore.collection("users").document(uid);
        recipeData = new HashMap<>();
        recipeData.put("name",recipe.getName());
        recipeData.put("description",recipe.getDescription());
        recipeData.put("preparation",recipe.getPreparation());
        recipeData.put("ingredients",recipe.getIngredients());
        recipeData.put("tags",tagList);
        recipeData.put("creationDate",new Timestamp(new Date()));
        recipeData.put("userID",userReference);
        return recipeData;
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
        List<TagModel> selectedTags = tv.getSelectedTags();
        if (selectedTags.isEmpty()) {
            Toast.makeText(UjReceptActivity.this, "Szükség van legalább egy hozzávalóra!",Toast.LENGTH_SHORT).show();
            return null;
        }
        Recipe recipe;
        if (TextUtils.isEmpty(etLeiras.getText())){
            recipe = new Recipe(etNev.getText().toString(),etElkeszites.getText().toString(),ingredients);
        } else {
            recipe = new Recipe(etNev.getText().toString(),etLeiras.getText().toString(),etElkeszites.getText().toString(),ingredients);
        }
        if (setUid) {
            recipe.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            recipe.setOnlineStored(true);
        }
        return recipe;
    }

    private void init() {
        tv = findViewById(R.id.tagView);
        tv.addTagSeparator(TagSeparator.COMMA_SEPARATOR);
        String[] tagList = new String[]{"Tojás", "Tej", "Liszt", "Tészta", "Kenyér", "Víz"};
        tv.setTagList(tagList);

        rvIngredients = findViewById(R.id.ingredientsListRv);
        ingredients = new ArrayList<>();
        loadIngredients();
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));

        saveBtn = findViewById(R.id.ujReceptMentes);
        saveOnlineBtn = findViewById(R.id.ujReceptMentesOnline);

        etNev = findViewById(R.id.ujReceptNev);
        etLeiras = findViewById(R.id.ujReceptLeiras);
        etElkeszites = findViewById(R.id.ujReceptElkeszites);

        ingNev = findViewById(R.id.ujReceptIngNameEt);
        ingMennyiseg = findViewById(R.id.ujReceptIngAmountEt);
        ingMertek = findViewById(R.id.ujReceptIngUnitEt);
        ingHozzaad = findViewById(R.id.ujReceptIngButtonNew);

        DBManager = new SQLiteDBHelper(this);
        fbFirestore = FirebaseFirestore.getInstance();

    }

    private void loadIngredients() {
        ingredientsAdapter = new IngredientsAdapter(ingredients);
        rvIngredients.setAdapter(ingredientsAdapter);
    }

    private List<String> tagsToString(List<TagModel> tags) {
        List<String> result = new ArrayList<>();
        for (TagModel item : tags) {
            result.add(item.getTagText());
        }
        return result;
    }
}
