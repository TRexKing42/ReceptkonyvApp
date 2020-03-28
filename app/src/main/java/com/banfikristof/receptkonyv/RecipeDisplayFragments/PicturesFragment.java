package com.banfikristof.receptkonyv.RecipeDisplayFragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.banfikristof.receptkonyv.Adapters.PicturesAdapter;
import com.banfikristof.receptkonyv.PictureEntry;
import com.banfikristof.receptkonyv.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PicturesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PicturesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView rvPics;
    private PicturesAdapter picturesAdapter;
    private Button newBtn;

    public List<PictureEntry> pictures;

    public PicturesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pictures, container, false);

        initFragment(v);

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},1);
                    return;
                }
                mListener.onTakePicture();
            }
        });



        return v;
    }

    private void initFragment(View v) {
        newBtn = v.findViewById(R.id.newPicBtnReceptek);
        rvPics = v.findViewById(R.id.recipePicturesRv);
        pictures =  new ArrayList<>();
        picturesAdapter = new PicturesAdapter(pictures, mListener.onGetKey(), getActivity());
        rvPics.setAdapter(picturesAdapter);
        rvPics.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        mListener.onLoadAllPictures();
        picturesAdapter.notifyDataSetChanged();
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

    @Override
    public void onResume() {
        super.onResume();
        mListener.onLoadAllPictures();
        picturesAdapter.notifyDataSetChanged();
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
        void onLoadAllPictures();

        void onTakePicture();

        String onGetKey();
    }
}
