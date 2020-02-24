package com.banfikristof.receptkonyv.NewRecipeFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.UjReceptActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewBasicsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NewBasicsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Button next, qrBtn;
    private EditText etNev, etLeiras;


    public NewBasicsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_basics, container, false);

        initFragment(v);

        UjReceptActivity myActivity = (UjReceptActivity)getActivity();
        if (myActivity.editmode){
            etNev.setText(myActivity.recipeToSave.getName());
            etLeiras.setText(myActivity.recipeToSave.getDescription());
            qrBtn.setEnabled(false);
        }

        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startQrImport();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.basicsDone(etNev.getText().toString(), etLeiras.getText().toString());
            }
        });

        return v;
    }

    private void initFragment(View v) {
        etNev = v.findViewById(R.id.ujReceptNev);
        etLeiras = v.findViewById(R.id.ujReceptLeiras);
        next = v.findViewById(R.id.nextBtn1);
        qrBtn = v.findViewById(R.id.fromQRbtn);
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
        void basicsDone(String name, String desc);

        void startQrImport();
    }
}
