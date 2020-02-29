package com.banfikristof.receptkonyv.MainFragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private TextView recTitle, recDesc;

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

        recRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OpenReceptActivity.class);
                intent.putExtra("SelectedRecipe",recipe);
                startActivity(intent);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recipeToRecommended(Recipe r){
        recipe = r;
        recTitle.setText(r.getName());
        recDesc.setText(r.getDescription());
        if (!r.isHasMainImg()){
            Glide.with(this).load(FirebaseStorage.getInstance().getReference().child("no_picture.png")).into(recImg);
        } else {
            StorageReference img = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(r.key)
                    .child("main_img.jpg");
            Glide.with(this).load(img).centerCrop().into(recImg);
        }
        recRecipe.setVisibility(View.VISIBLE);
        welcomeText.setText(getResources().getString(R.string.chefsRecommendation));
    }

    @Override
    public void onResume() {
        super.onResume();
        recRecipe.setVisibility(View.INVISIBLE);
        listOfRecipes = new ArrayList<>();
        fillRecipesList();
    }
}
