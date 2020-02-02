package com.banfikristof.receptkonyv;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class KezdolapFragment extends Fragment {
    private TextView welcomeText;


    public KezdolapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_kezdolap, container, false);

        initFragment(v);

        return v;
    }

    private void initFragment(View v) {
        welcomeText = v.findViewById(R.id.homepageWelcomeText);
    }

}
