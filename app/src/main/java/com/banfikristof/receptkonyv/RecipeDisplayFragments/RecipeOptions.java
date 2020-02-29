package com.banfikristof.receptkonyv.RecipeDisplayFragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.banfikristof.receptkonyv.OpenReceptActivity;
import com.banfikristof.receptkonyv.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeOptions.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RecipeOptions extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Button share, delete, update, favourite, qrBtn;
    private ImageView qrKod;


    public RecipeOptions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_options, container, false);

        initFragment(v);

        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQRCodeFromRecipe(mListener.onGetJSON());
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShare();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDelete();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEdit();
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((OpenReceptActivity)getActivity()).r.isFavourite()) {
                    mListener.onFavorite(favourite);
                } else {
                    mListener.onFavorite(favourite);
                }

            }
        });

        return v;
    }

    private void initFragment(View v) {
        share = v.findViewById(R.id.shareButtonSelectedRecept);
        delete = v.findViewById(R.id.deleteButtonSelectedRecept);
        update = v.findViewById(R.id.editButtonSelectedRecept);
        favourite = v.findViewById(R.id.favButtonSelectedRecept);
        qrBtn = v.findViewById(R.id.qrButtonSelectedRecept);

        if (((OpenReceptActivity)getActivity()).r.isFavourite()) {
            favourite.setText(getResources().getText(R.string.unfavourite));
        } else {
            favourite.setText(getResources().getText(R.string.add_to_favorites));
        }

        qrKod = v.findViewById(R.id.generatedQRcode);
    }

    public void newQRCodeFromRecipe(String recipeInJson){
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(recipeInJson, BarcodeFormat.QR_CODE,1000,1000);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            qrKod.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
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
        // TODO: Update argument type and name
        void onShare();
        void onDelete();
        void onEdit();
        void onFavorite(Button favourite);
        String onGetJSON();
    }
}
