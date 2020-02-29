package com.banfikristof.receptkonyv.RecipeDisplayFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.banfikristof.receptkonyv.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class OverviewFragment extends Fragment {

    private TextView receptLeiras, receptTags;
    private ImageView recipeImage;
    private ScrollView sv;
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
        mListener.onFragmentDisplayed(receptLeiras, recipeImage, receptTags);

        recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener.recipeHasMainImg()) {
                    mListener.showBigImage();
                    visibleImage(false);
                }
            }
        });


        return v;
    }

    private void initFragment(View v) {
        recipeImage = v.findViewById(R.id.recipeImage);
        receptLeiras = v.findViewById(R.id.receptLeirasSelected);

        sv = v.findViewById(R.id.overviewScrollView);
        receptTags = v.findViewById(R.id.receptTagsSelected);
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

    public void visibleImage(boolean visible) {
        if (visible){
            sv.setVisibility(View.VISIBLE);
        } else {
            sv.setVisibility(View.INVISIBLE);
        }
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
        void onFragmentDisplayed(TextView desc, ImageView imageView, TextView receptTags);

        void showBigImage();

        boolean recipeHasMainImg();
    }
}
