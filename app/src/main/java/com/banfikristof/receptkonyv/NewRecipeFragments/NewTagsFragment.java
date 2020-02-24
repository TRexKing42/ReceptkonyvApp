package com.banfikristof.receptkonyv.NewRecipeFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.UjReceptActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewTagsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NewTagsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ChipGroup recipeTags, currentTags;
    private List<String> tagList;
    private List<String> selectedTagList;
    private EditText tagSearchEt;

    private Button next, back;

    public NewTagsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_tags, container, false);

        initFragment(v);

        UjReceptActivity myActivity = (UjReceptActivity)getActivity();
        if (myActivity.editmode){
            loadTagsForEdit(myActivity.recipeToSave.getTags());
            loadTags(tagList);
        }

        tagSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tagSearchEt.getText().toString().isEmpty()){
                    loadTags(tagList);
                } else {
                    loadTags(tagList,tagSearchEt.getText().toString());
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTagList.size() < 1){
                    selectedTagList.add("Nincs címke");
                }
                mListener.tagsDone(selectedTagList);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.backToPrep();
            }
        });

        return v;
    }

    private void initFragment(View v) {
        recipeTags = v.findViewById(R.id.tagsNewRecipe);
        currentTags = v.findViewById(R.id.tagsSelectedNewRecipe);
        //tv.addTagSeparator(TagSeparator.COMMA_SEPARATOR);
        tagSearchEt = v.findViewById(R.id.searchForTags);
        tagList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.tags_array)));
        selectedTagList = new ArrayList<>();
        loadTags(tagList);

        next = v.findViewById(R.id.nextBtn4);
        back = v.findViewById(R.id.backBtn3);
    }

    private void loadTags(List<String> tags, final String search) {
        recipeTags.removeAllViews();
        final Chip customChip = new Chip(getActivity(), null, R.style.Widget_MaterialComponents_Chip_Entry);
        customChip.setText(search);
        customChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customChip.getParent() == recipeTags) {
                    recipeTags.removeView(customChip);
                    currentTags.addView(customChip);
                    selectedTagList.add(search);
                } else if (customChip.getParent() == currentTags){
                    selectedTagList.remove(search);
                    currentTags.removeView(customChip);
                }
            }
        });
        recipeTags.addView(customChip);

        for (int i = 0; i < 7; i++) {
            if (tags.get(i).toLowerCase().contains(search.toLowerCase()) && !selectedTagList.contains(tagList.get(i))) {
                final Chip tChip = new Chip(getActivity(),null, R.style.Widget_MaterialComponents_Chip_Entry);
                final String chipTxt = tags.get(i);
                tChip.setText(tags.get(i));

                tChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tChip.getParent() == recipeTags) {
                            recipeTags.removeView(tChip);
                            currentTags.addView(tChip);
                            selectedTagList.add(chipTxt);
                        } else if (tChip.getParent() == currentTags){
                            selectedTagList.remove(chipTxt);
                            currentTags.removeView(tChip);
                            recipeTags.addView(tChip);
                        }
                    }
                });

                recipeTags.addView(tChip);
            }
        }
    }

    private void loadTags(final List<String> tags) {
        recipeTags.removeAllViews();
        for (int i = 0; i < tags.size(); i++) {
            if (!selectedTagList.contains(tagList.get(i))) {
                final Chip tChip = new Chip(getActivity(),null, R.style.Widget_MaterialComponents_Chip_Entry);
                final String chipTxt = tags.get(i);
                tChip.setText(tags.get(i));

                tChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tChip.getParent() == recipeTags) {
                            recipeTags.removeView(tChip);
                            currentTags.addView(tChip);
                            selectedTagList.add(chipTxt);
                        } else if (tChip.getParent() == currentTags){
                            selectedTagList.remove(chipTxt);
                            currentTags.removeView(tChip);
                            recipeTags.addView(tChip);
                        }
                    }
                });

                recipeTags.addView(tChip);
            }
        }
    }

    private void loadTagsForEdit(final List<String> tags) {
        recipeTags.removeAllViews();
        currentTags.removeAllViews();
        for (int i = 0; i < tags.size(); i++) {
            if (!selectedTagList.contains(tags.get(i))) {
                final Chip tChip = new Chip(getActivity(),null, R.style.Widget_MaterialComponents_Chip_Entry);
                final String chipTxt = tags.get(i);
                tChip.setText(tags.get(i));
                selectedTagList.add(tags.get(i));

                tChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tChip.getParent() == recipeTags) {
                            recipeTags.removeView(tChip);
                            currentTags.addView(tChip);
                            selectedTagList.add(chipTxt);
                        } else if (tChip.getParent() == currentTags){
                            selectedTagList.remove(chipTxt);
                            currentTags.removeView(tChip);
                            recipeTags.addView(tChip);
                        }
                    }
                });

                currentTags.addView(tChip);
            }
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

        void tagsDone(List<String> selectedTagList);

        void backToPrep();
    }
}
