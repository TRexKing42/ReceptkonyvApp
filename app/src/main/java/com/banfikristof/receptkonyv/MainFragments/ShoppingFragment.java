package com.banfikristof.receptkonyv.MainFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.banfikristof.receptkonyv.OpenListActivity;
import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.ShoppingList;
import com.banfikristof.receptkonyv.UjListaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShoppingFragment.OnShoppingFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShoppingFragment extends Fragment {

    private ListView lv;
    private Button ujListaButton;

    private OnShoppingFragmentInteractionListener mListener;

    public ShoppingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shopping, container, false);

        initFragment(v);
        listsToList();

        ujListaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UjListaActivity.class);
                startActivity(intent);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShoppingList s = (ShoppingList) lv.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), OpenListActivity.class);
                intent.putExtra("SelectedList",s);
                startActivity(intent);
            }
        });

        return v;
    }

    private void initFragment(View v) {
        lv = v.findViewById(R.id.listShoppingLists);
        registerForContextMenu(lv);
        ujListaButton = v.findViewById(R.id.ujListaButton);
    }

    private void listsToList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("shoppinglists").child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ShoppingList> listOfLists = new ArrayList<>();
                ShoppingList r;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    r = item.getValue(ShoppingList.class);
                    r.key = item.getKey();
                    listOfLists.add(r);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,listOfLists);
                lv.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShoppingFragmentInteractionListener) {
            mListener = (OnShoppingFragmentInteractionListener) context;
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
        listsToList();
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
    public interface OnShoppingFragmentInteractionListener {
        // TODO: Update argument type and name
        // void onFragmentInteraction(Uri uri);
    }
}
