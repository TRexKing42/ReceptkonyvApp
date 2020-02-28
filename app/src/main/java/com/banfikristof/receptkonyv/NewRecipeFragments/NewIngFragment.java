package com.banfikristof.receptkonyv.NewRecipeFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.banfikristof.receptkonyv.Adapters.IngredientsAdapter;
import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.UjReceptActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewIngFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NewIngFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView rvIngredients;
    private IngredientsAdapter ingredientsAdapter;

    private EditText ingNev, ingMennyiseg, ingMertek, tagSearchEt;
    private Button ingHozzaad;


    private Button next, back;


    List<Map<String,String>> ingredients;

    public NewIngFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_ing, container, false);

        initFragment(v);

        UjReceptActivity myActivity = (UjReceptActivity)getActivity();
        if (myActivity.editmode){
            int adapterSize = ingredientsAdapter.getItemCount();
            int ingNumber = myActivity.recipeToSave.getIngredients().size();
            for (int i = 0; i < ingNumber; i++) {
                ingredients.add(myActivity.recipeToSave.getIngredients().get(i));
            }
            loadIngredients();
            ingredientsAdapter.notifyItemRangeChanged(adapterSize,ingredients.size());
        }

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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.ingDone(ingredients);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.backToBasics();
            }
        });

        return v;
    }

    private void initFragment(View v) {
        ingNev = v.findViewById(R.id.ujReceptIngNameEt);
        ingMennyiseg = v.findViewById(R.id.ujReceptIngAmountEt);
        ingMertek = v.findViewById(R.id.ujReceptIngUnitEt);
        ingHozzaad = v.findViewById(R.id.ujReceptIngButtonNew);

        next = v.findViewById(R.id.nextBtn2);
        back = v.findViewById(R.id.backBtn1);

        rvIngredients = v.findViewById(R.id.ingredientsListRv);
        ingredients = new ArrayList<>();
        loadIngredients();
        rvIngredients.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    private void loadIngredients() {
        ingredientsAdapter = new IngredientsAdapter(ingredients);
        rvIngredients.setAdapter(ingredientsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void ingDone(List<Map<String, String>> ingredients);

        void backToBasics();
        // TODO: Update argument type and name
    }
}
