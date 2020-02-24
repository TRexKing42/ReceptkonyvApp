package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.banfikristof.receptkonyv.NewRecipeFragments.NewPhotoFragment;
import com.banfikristof.receptkonyv.RecipeDisplayFragments.IngredientsFragment;
import com.banfikristof.receptkonyv.RecipeDisplayFragments.OverviewFragment;
import com.banfikristof.receptkonyv.RecipeDisplayFragments.PreparationFragment;
import com.banfikristof.receptkonyv.RecipeDisplayFragments.RecipeOptions;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

public class OpenReceptActivity extends AppCompatActivity implements
        OverviewFragment.OnFragmentInteractionListener,
        IngredientsFragment.OnFragmentInteractionListener,
        PreparationFragment.OnFragmentInteractionListener,
        RecipeOptions.OnFragmentInteractionListener {

    private TextView receptNev;
    private BottomNavigationView bottomNavigationView;
    private ImageView bigImage;
    private StorageReference img;

    private Recipe r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_recept);

        init();

        bigImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigImage.setVisibility(View.INVISIBLE);
                OverviewFragment fragment = (OverviewFragment) getSupportFragmentManager().findFragmentById(R.id.receptFrame);
                fragment.visibleImage(true);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch(menuItem.getItemId())
                {
                    case R.id.recipeMenu_overview:
                        selectedFragment = new OverviewFragment();
                        break;
                    case R.id.recipeMenu_ingredients:
                        selectedFragment = new IngredientsFragment();
                        break;
                    case R.id.recipeMenu_preparation:
                        selectedFragment = new PreparationFragment();
                        break;
                    case R.id.recipeMenu_options:
                        selectedFragment = new RecipeOptions();
                        break;
                    default:
                        Toast.makeText(OpenReceptActivity.this, "Még nem",Toast.LENGTH_SHORT).show();
                        break;
                }

                if (selectedFragment != null) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.receptFrame, selectedFragment);
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });
        displayRecipe();
    }

    private void init(){
        receptNev = findViewById(R.id.receptNevSelected);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        bigImage = findViewById(R.id.overviewBigImageView);
        r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.receptFrame, new OverviewFragment());
        fragmentTransaction.commit();
    }

    public void displayRecipe(){
        //Recipe r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");
        receptNev.setText(r.getName());
        img = FirebaseStorage.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getUid())
                .child(r.key)
                .child("main_img.jpg");

    }

    //Overview Fragment
    @Override
    public void onFragmentDisplayed(TextView desc, ImageView imageView, TextView receptTags) {
        desc.setText(r.getDescription());
        receptTags.setText(r.tagsToString());
        if (!r.isHasMainImg()){
            Glide.with(this).load(FirebaseStorage.getInstance().getReference().child("no_picture.png")).into(imageView);
        } else {
            Glide.with(this).load(img).centerCrop().into(imageView);
        }
    }

    @Override
    public void showBigImage() {
        Glide.with(this).load(img).into(bigImage);
        bigImage.setVisibility(View.VISIBLE);
    }

    //Ingredients Fragment
    @Override
    public void onIngFragmentDisplayed(TextView tv) {
        tv.setText(r.ingredientsToString());
    }

    //Preparations Fragmentt
    @Override
    public void onPrepFragmentDisplayed(TextView tv) {
        tv.setText(r.preparationAsString());
    }


    //Options Fragment
    @Override
    public void onShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,r.getName() + " recept");
        intent.putExtra(Intent.EXTRA_TEXT,
                r.getDescription() +
                        "\n"+ getResources().getString(R.string.ingredientsC) + "\n" +
                        r.ingredientsToString() +
                        "\n" + getResources().getString(R.string.recipePreparationText) + "\n" +
                        r.getPreparation());
        startActivity(Intent.createChooser(intent,"Megosztás"));
    }

    @Override
    public void onDelete() {
        if (r.getPictures() != null) {
            for (int i = 0; i < r.getPictures().size(); i++) {
                FirebaseStorage.getInstance().getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(r.key)
                        .child(r.getPictures().get(i))
                        .delete();
            }
        }
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

    @Override
    public void onEdit() {
        Intent intent = new Intent(OpenReceptActivity.this, UjReceptActivity.class);
        intent.putExtra("RecipeEdit", r);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFavorite() {
        FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(r.key).child("favourite").setValue(true);
    }

    @Override
    public String onGetJSON() {
        Gson gson = new Gson();
        RecipeShare recipeToShare = new RecipeShare();
        recipeToShare.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recipeToShare.setRid(r.key);
        String result = gson.toJson(recipeToShare);
        return result;
    }
}
