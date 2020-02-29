package com.banfikristof.receptkonyv.MainFragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.banfikristof.receptkonyv.OpenReceptActivity;
import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.Recipe;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class KezdolapFragment extends Fragment {
    private TextView welcomeText;
    private MaterialCardView recRecipe;
    private List<Recipe> listOfRecipes;
    private ImageView recImg;
    private TextView recTitle, recDesc, recIng;
    private Button favorite, newRecommendation;

    Recipe recipe;

    public KezdolapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_kezdolap, container, false);

        initFragment(v);

        newRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recRecipe.setVisibility(View.INVISIBLE);
                newRecommendation.setVisibility(View.INVISIBLE);
                listOfRecipes = new ArrayList<>();
                fillRecipesList();
            }
        });

        recRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OpenReceptActivity.class);
                intent.putExtra("SelectedRecipe",recipe);
                startActivity(intent);
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recipe.isFavourite()) {
                    FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(recipe.key).child("favourite").setValue(true);
                    favorite.setText(getResources().getText(R.string.unfavourite));
                    recipe.setFavourite(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(recipe.key).child("favourite").setValue(false);
                    favorite.setText(getResources().getText(R.string.add_to_favorites));
                    recipe.setFavourite(false);
                }
            }
        });

        return v;
    }

    private void initFragment(View v) {
        welcomeText = v.findViewById(R.id.homepageWelcomeText);
        recRecipe = v.findViewById(R.id.dailyRecommendation);
        recImg = v.findViewById(R.id.recommendationImg);
        recTitle = v.findViewById(R.id.recommendationTitle);
        recDesc = v.findViewById(R.id.recommendationDesc);
        recIng = v.findViewById(R.id.recommendationIng);
        favorite = v.findViewById(R.id.recommendedToFavBtn);
        newRecommendation = v.findViewById(R.id.newRecommendationBtn);

        listOfRecipes = new ArrayList<>();
        fillRecipesList();
    }

    private void fillRecipesList() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            return;
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    return;
                }
                Recipe r;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    r = item.getValue(Recipe.class);
                    r.key = item.getKey();
                    listOfRecipes.add(r);
                }
                if (listOfRecipes.size() > 0){
                    recipeToRecommended(listOfRecipes.get(new Random().nextInt(listOfRecipes.size())));
                } else {
                    failedToRecommend();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void failedToRecommend(){
        welcomeText.setText(getResources().getString(R.string.welcomeMsgLong));
    }

    private void recipeToRecommended(Recipe r){
        recipe = r;
        recTitle.setText(r.getName());
        recDesc.setText(r.getDescription());
        recIng.setText(r.ingredientsToString());
        if (r.isFavourite()){
            favorite.setText(getResources().getText(R.string.unfavourite));
        }
        if (!r.isHasMainImg()){
            Glide.with(this).load(FirebaseStorage.getInstance().getReference().child("no_picture.png")).centerCrop().into(recImg);
        } else {
            StorageReference img = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(r.key)
                    .child("main_img.jpg");
            Glide.with(this).load(img).thumbnail(0.3f).centerCrop().into(recImg);
        }
        recRecipe.setVisibility(View.VISIBLE);
        newRecommendation.setVisibility(View.VISIBLE);
        welcomeText.setText(getResources().getString(R.string.chefsRecommendation));
    }

    @Override
    public void onResume() {
        super.onResume();
        recRecipe.setVisibility(View.INVISIBLE);
        newRecommendation.setVisibility(View.INVISIBLE);
        listOfRecipes = new ArrayList<>();
        fillRecipesList();
    }
}
