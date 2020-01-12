package com.banfikristof.receptkonyv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReceptekFragment extends Fragment {
    private ReceptekFragmentListener listener;

    private ListView lv;
    private Button ujReceptBtn, onlineBtn, offlineBtn;
    private boolean onlineList;

    public ReceptekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_receptek, container, false);

        initFragment(v);

        onlineRecipesToList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe s = (Recipe) lv.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(),OpenReceptActivity.class);
                intent.putExtra("SelectedRecipe",s);
                startActivity(intent);
            }
        });

        ujReceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UjReceptActivity.class);
                startActivity(intent);
            }
        });
        onlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlineList = true;
                onlineRecipesToList();
            }
        });
        offlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlineList = false;
                recipesIntoList();
            }
        });
        return v;
    }

    private void onlineRecipesToList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> listOfRecipes = new ArrayList<>();
                Recipe r;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    r = item.getValue(Recipe.class);
                    r.key = item.getKey();
                    listOfRecipes.add(r);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,listOfRecipes);
                lv.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });


        /*
        //   FIRESTORE
        FirebaseFirestore fbFirestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userReference = fbFirestore.collection("users").document(uid);
        fbFirestore.collection("recipes").whereEqualTo("userID", userReference).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Recipe> listOfRecipes = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                Recipe r = new Recipe(documentSnapshot.getId());
                                r.setName(documentSnapshot.get("name").toString());
                                r.setDescription(documentSnapshot.get("description").toString());
                                Map<String, Map<String, Object>> ingredientsData = ((Map<String, Map<String, Object>>)documentSnapshot.get("ingredients"));
                                List<String> ingredients = new ArrayList<>();
                                for (Map.Entry<String, Map<String, Object>> item : ingredientsData.entrySet()){
                                    ingredients.add(item.getValue().get("iAmount").toString() + " " + item.getValue().get("iUnit").toString() + " " + item.getKey());
                                }
                                //r.setIngredients(ingredients);
                                r.setPreparation(documentSnapshot.get("preparation").toString());
                                listOfRecipes.add(r);

                                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,listOfRecipes);
                                lv.setAdapter(arrayAdapter);
                            }
                        }
                    }
                });*/
    }

    private void initFragment(View view){
        lv = view.findViewById(R.id.listReceptek);
        registerForContextMenu(lv);
        ujReceptBtn = view.findViewById(R.id.ujReceptButton);
        onlineBtn = view.findViewById(R.id.onlineReceptekButton);
        offlineBtn = view.findViewById(R.id.offlineReceptekButton);
        onlineList = false;
    }

    public interface ReceptekFragmentListener {
        public void onRecipeDelete(Recipe recipe, String id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReceptekFragmentListener) {
            listener = (ReceptekFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReceptekFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void recipesIntoList(){
        /*ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,((MainActivity) getActivity()).getRecipes());
        lv.setAdapter(arrayAdapter);*/
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onlineList){
            onlineRecipesToList();
        } else {
            recipesIntoList();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Egyéb Opciók");
        menu.add(0,v.getId(),0,"Megnyitás");
        menu.add(0,v.getId(),0,"Törlés");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        Recipe s;
        switch (item.getTitle().toString()){
            case "Megnyitás":
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                s = (Recipe) lv.getItemAtPosition(info.position);
                Intent intent = new Intent(getActivity(),OpenReceptActivity.class);
                intent.putExtra("SelectedRecipe",s);
                startActivity(intent);
                break;
            case "Törlés":
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                s = (Recipe) lv.getItemAtPosition(info.position);
                listener.onRecipeDelete(s, s.key);
                recipesIntoList();
                break;
            default:
                return false;
        }
        return true;
    }
}
