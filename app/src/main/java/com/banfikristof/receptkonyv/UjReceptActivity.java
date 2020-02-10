package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skyhope.materialtagview.TagView;
import com.skyhope.materialtagview.enums.TagSeparator;
import com.skyhope.materialtagview.interfaces.TagItemListener;
import com.skyhope.materialtagview.model.TagModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UjReceptActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 9999;
    private static final int CAMERA_REQUEST = 9998;

    private String pushId;
    private boolean editmode = false;

    private TagView tv;
    private Button saveBtn;
    private EditText etNev, etLeiras, etElkeszites;

    private EditText ingNev, ingMennyiseg, ingMertek;
    private Button ingHozzaad;
    private ImageButton photoIbtn;
    private ImageView imgPreview;

    private AlertDialog alertDialog;
    private AlertDialog.Builder adBuilder;

    private RecyclerView rvIngredients;
    IngredientsAdapter ingredientsAdapter;

    Bitmap img;
    StorageReference imgRef;
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
                if (!editmode) {
                    pushId = FirebaseDatabase.getInstance().getReference().child("recipes")
                            .child(FirebaseAuth.getInstance().getUid()).push().getKey();
                }
                if (recipe == null) return;
                if (img != null){
                    uploadImg(FirebaseStorage.getInstance().getReference()
                            .child(FirebaseAuth.getInstance().getUid())
                            .child(pushId)
                            .child("main_img.jpg"));
                    recipe.setHasMainImg(true);
                } else {
                    recipe.setHasMainImg(false);
                }
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("recipes").child(FirebaseAuth.getInstance().getUid()).child(pushId).setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        photoIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPhotoDialog();
            }
        });

    }

    private void uploadImg(final StorageReference reference) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask task = reference.putBytes(data);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UjReceptActivity.this, "Sikertelen képfeltöltés!",Toast.LENGTH_SHORT).show();
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
            recipe.setHasMainImg(true);
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
        photoIbtn = findViewById(R.id.newRecipePhotoIbtn);
        imgPreview = findViewById(R.id.newRecipeImgPreview);

        etNev = findViewById(R.id.ujReceptNev);
        etLeiras = findViewById(R.id.ujReceptLeiras);
        etElkeszites = findViewById(R.id.ujReceptElkeszites);

        ingNev = findViewById(R.id.ujReceptIngNameEt);
        ingMennyiseg = findViewById(R.id.ujReceptIngAmountEt);
        ingMertek = findViewById(R.id.ujReceptIngUnitEt);
        ingHozzaad = findViewById(R.id.ujReceptIngButtonNew);

        if (getIntent().hasExtra("RecipeEdit")){
            Recipe r = (Recipe) getIntent().getSerializableExtra("RecipeEdit");
            etNev.setText(r.getName());
            etLeiras.setText(r.getDescription());
            etElkeszites.setText(r.getPreparation());
            pushId = r.key;

            int adapterSize = ingredientsAdapter.getItemCount();
            int ingNumber = r.getIngredients().size();
            Map<String, String> hozzavalo = new HashMap<>();
            for (int i = 0; i < ingNumber; i++) {
                ingredients.add(r.getIngredients().get(i));
            }
            loadIngredients();
            ingredientsAdapter.notifyItemRangeChanged(adapterSize,ingredients.size());

            imgRef = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(r.key)
                    .child("main_img.jpg");
            Glide.with(this).load(imgRef).into(imgPreview);
            Glide.with(this).asBitmap().load(imgRef).into(new CustomTarget<Bitmap>(){
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    img = resource;
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });

            editmode = true;
        }

    }

    public void loadRecipe(){

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case GALLERY_REQUEST:
                    try {
                        Uri imgUri = data.getData();

                        img = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgUri);
                        img = Bitmap.createScaledBitmap(img,500,500,true);

                        Glide.with(this).load(img).into(imgPreview);
                    } catch (IOException e) {
                        Toast.makeText(this,getResources().getString(R.string.imgLoadErrorNew),Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CAMERA_REQUEST:
                    img = (Bitmap) data.getExtras().get("data");
                    img = Bitmap.createScaledBitmap(img,500,500,true);
                    Glide.with(this).load(img).into(imgPreview);
                    break;
            }
        }
    }

    public void createPhotoDialog(){
        adBuilder = new AlertDialog.Builder(UjReceptActivity.this);
        adBuilder.setMessage(getResources().getString(R.string.photoPromptTxt));
        adBuilder.setPositiveButton(getResources().getString(R.string.photoYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (photoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(photoIntent, CAMERA_REQUEST);
                }
            }
        });
        adBuilder.setNegativeButton(getResources().getString(R.string.photoNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, GALLERY_REQUEST);
            }
        });
        adBuilder.setNeutralButton(getResources().getString(R.string.photoCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        adBuilder.setCancelable(false);
        alertDialog = adBuilder.create();
        alertDialog.show();
    }
}
