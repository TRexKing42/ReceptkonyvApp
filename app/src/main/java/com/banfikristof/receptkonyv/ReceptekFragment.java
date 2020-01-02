package com.banfikristof.receptkonyv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ReceptekFragment extends Fragment {
    private ReceptekFragmentListener listener;

    private ListView lv;

    public ReceptekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_receptek, container, false);

        initFragment(v);

        recipesIntoList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe s = (Recipe) lv.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(),OpenReceptActivity.class);
                intent.putExtra("SelectedRecipe",s);
                startActivity(intent);
            }
        });
        return v;
    }

    private void initFragment(View view){
        lv = view.findViewById(R.id.listReceptek);
    }

    public interface ReceptekFragmentListener {
        public void onRecipeSelected();
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
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,((MainActivity) getActivity()).getRecipes());
        lv.setAdapter(arrayAdapter);
    }

}
