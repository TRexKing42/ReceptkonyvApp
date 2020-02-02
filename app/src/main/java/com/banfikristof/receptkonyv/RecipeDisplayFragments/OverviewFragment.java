package com.banfikristof.receptkonyv.RecipeDisplayFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.banfikristof.receptkonyv.R;
import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class OverviewFragment extends Fragment {

    private TextView receptLeiras;
    private ImageView recipeImage;

    private OnFragmentInteractionListener mListener;

    public OverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_overview, container, false);

        initFragment(v);
        mListener.onFragmentDisplayed(receptLeiras, recipeImage);

        return v;
    }

    private void initFragment(View v) {
        recipeImage = v.findViewById(R.id.recipeImage);
        receptLeiras = v.findViewById(R.id.receptLeirasSelected);

        Glide.with(this).load("https://baconmockup.com/400/200/").into(recipeImage);
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
        void onFragmentDisplayed(TextView desc, ImageView imageView);
    }
}
