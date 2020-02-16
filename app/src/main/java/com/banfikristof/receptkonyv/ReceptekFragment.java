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
import android.widget.EditText;
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
    private Button ujReceptBtn, searchBtn;
    private EditText searchEt;

    public ReceptekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_receptek, container, false);

        initFragment(v);
        recipesToList();

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
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRecipes(searchEt.getText().toString());
            }
        });
        return v;
    }


    private void initFragment(View view){
        lv = view.findViewById(R.id.listReceptek);
        registerForContextMenu(lv);
        ujReceptBtn = view.findViewById(R.id.ujReceptButton);
        searchBtn = view.findViewById(R.id.keresesReceptekButton);
        searchEt = view.findViewById(R.id.keresesReceptekEt);
    }


    private void recipesToList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> listOfRecipes = new ArrayList<>();
                Recipe r;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    r = item.getValue(Recipe.class);
                    r.key = item.getKey();
                    if (!MainActivity.favRecipes) {
                        listOfRecipes.add(r);
                    } else if (MainActivity.favRecipes && r.isFavourite()) {
                        listOfRecipes.add(r);
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,listOfRecipes);
                lv.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    public void searchRecipes(final String searchText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> listOfRecipes = new ArrayList<>();
                Recipe r;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    r = item.getValue(Recipe.class);
                    r.key = item.getKey();
                    if (r.getName().toLowerCase().contains(searchText.toLowerCase()) && (!MainActivity.favRecipes || r.isFavourite())) {
                        listOfRecipes.add(r);
                    }
                    for (String i : r.getTags()) {
                        if (i.toLowerCase().contains(searchText.toLowerCase()) && !listOfRecipes.contains(r)  && (!MainActivity.favRecipes || r.isFavourite())){
                            listOfRecipes.add(r);
                        }
                    }
                    for (Map<String,String> i : r.getIngredients()) {
                        if (i.get("name").toLowerCase().contains(searchText.toLowerCase()) && !listOfRecipes.contains(r) && (!MainActivity.favRecipes || r.isFavourite())){
                            listOfRecipes.add(r);
                        }
                    }
                }
                if (!listOfRecipes.isEmpty()) {
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,listOfRecipes);
                    lv.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface ReceptekFragmentListener {
        public void onRecipeDelete(Recipe recipe);
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

    @Override
    public void onResume() {
        super.onResume();
        recipesToList();
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
                listener.onRecipeDelete(s);
                recipesToList();
                break;
            default:
                return false;
        }
        return true;
    }
}
