package com.banfikristof.receptkonyv.NewRecipeFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.UjReceptActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NewPhotoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ImageButton photoIbtn;
    private ImageView imgPreview;

    Bitmap img;

    private Button next, back;

    public NewPhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_photo, container, false);

        initFragment(v);



        UjReceptActivity myActivity = (UjReceptActivity)getActivity();
        if (myActivity.editmode){
            loadImage();
        }

        photoIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},1);
                    return;
                }
                mListener.takePicture();
                loadImage();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.recipeDone();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.backToPrepTags();
            }
        });

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mListener.takePicture();
                    loadImage();
                } else {
                    Toast.makeText(getActivity(),"You can't use images if you don't give permission.",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void loadImage(){
        if (mListener.getTakenPicture() == null){
            return;
        }
        Glide.with(getActivity()).load(mListener.getTakenPicture()).into(imgPreview);
    }

    private void initFragment(View v) {
        photoIbtn = v.findViewById(R.id.newRecipePhotoIbtn);
        imgPreview = v.findViewById(R.id.newRecipeImgPreview);

        next = v.findViewById(R.id.nextBtn5);
        back = v.findViewById(R.id.backBtn4);
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
        Bitmap takePicture();

        void backToPrepTags();

        void recipeDone();

        Bitmap getTakenPicture();
    }
}
