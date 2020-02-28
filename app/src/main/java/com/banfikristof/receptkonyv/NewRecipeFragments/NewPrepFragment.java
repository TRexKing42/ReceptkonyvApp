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

import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.Adapters.PreparationAdapter;
import com.banfikristof.receptkonyv.UjReceptActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewPrepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NewPrepFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView prepList;

    private List<String> prep;
    private PreparationAdapter adapter;

    private Button next, back;
    private Button newPrepStepBtn;
    private EditText newPrepStepEt;

    public NewPrepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_prep, container, false);

        initFragment(v);

        UjReceptActivity myActivity = (UjReceptActivity)getActivity();
        if (myActivity.editmode){
            int adapterSize = adapter.getItemCount();
            int pNumber = myActivity.recipeToSave.getPreparation().size();
            for (int i = 0; i < pNumber; i++) {
                prep.add(myActivity.recipeToSave.getPreparation().get(i));
            }
            adapter = new PreparationAdapter(prep);
            prepList.setAdapter(adapter);
            adapter.notifyItemRangeChanged(adapterSize,prep.size());
        }

        newPrepStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPrepStepEt.getText().toString().isEmpty()) {
                    return;
                }
                prep.add(newPrepStepEt.getText().toString());
                adapter.notifyItemRangeChanged(adapter.getItemCount(),prep.size());


                newPrepStepEt.setText("");
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.prepDone(prep);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.backToIng();
            }
        });

        return v;
    }

    private void initFragment(View v) {
        newPrepStepEt = v.findViewById(R.id.ujReceptElkeszites);
        newPrepStepBtn = v.findViewById(R.id.newPrepStepBtn);

        next = v.findViewById(R.id.nextBtn3);
        back = v.findViewById(R.id.backBtn2);

        prepList = v.findViewById(R.id.prepListRv);
        prep = new ArrayList<>();
        adapter = new PreparationAdapter(prep);
        prepList.setAdapter(adapter);
        prepList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
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

        void prepDone(List<String> prep);

        void backToIng();
    }
}
