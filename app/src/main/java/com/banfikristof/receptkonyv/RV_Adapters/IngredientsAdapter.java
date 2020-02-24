package com.banfikristof.receptkonyv.RV_Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.UjReceptActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    public List<Map<String,String>> ingredientsList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvIngName,tvIngAmount,tvIngUnit;
        public Button btnIngDel;

        public ViewHolder(View v) {
            super(v);
            tvIngName = v.findViewById(R.id.ingredientNameRv);
            tvIngAmount = v.findViewById(R.id.ingredientAmountRv);
            tvIngUnit = v.findViewById(R.id.ingredientUnitRv);
            btnIngDel = v.findViewById(R.id.btnDeleteIngRv);
        }
    }

    public IngredientsAdapter(List<Map<String, String>> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }


    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View ingView = inflater.inflate(R.layout.ingredients_rv_item, parent, false);
        return new ViewHolder(ingView);
    }

    @Override
    public void onBindViewHolder(IngredientsAdapter.ViewHolder holder, final int position) {
        Map<String,String> ing = ingredientsList.get(position);
        TextView ingName = holder.tvIngName;
        ingName.setText(ing.get("name")); //Név
        TextView ingAmount = holder.tvIngAmount;
        ingAmount.setText(ing.get("amount")); //Mennyiség
        TextView ingUnit = holder.tvIngUnit;
        ingUnit.setText(ing.get("unit")); // Mértékegység
        Button btnDelete = holder.btnIngDel;
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> item = ingredientsList.get(position);
                int ind = ingredientsList.indexOf(item);
                ingredientsList.remove(ind);
                notifyItemRemoved(ind);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }


}
