package com.banfikristof.receptkonyv.RV_Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banfikristof.receptkonyv.R;
import java.util.List;

public class PreparationAdapter extends RecyclerView.Adapter<PreparationAdapter.ViewHolder> {
    public List<String> preparations;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStep;
        public Button btnPDel;

        public ViewHolder(View v) {
            super(v);
            tvStep = v.findViewById(R.id.prepNameTv);
            btnPDel = v.findViewById(R.id.btnDeletePrepRv);
        }
    }

    public PreparationAdapter(List<String> preparations) {
        this.preparations = preparations;
    }


    @NonNull
    @Override
    public PreparationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View ingView = inflater.inflate(R.layout.preparations_rv_item, parent, false);
        return new PreparationAdapter.ViewHolder(ingView);
    }

    @Override
    public void onBindViewHolder(PreparationAdapter.ViewHolder holder, final int position) {
        String step = preparations.get(position);
        TextView tvName = holder.tvStep;
        tvName.setText((position + 1) +". "+ step); //Név
        Button btnDelete = holder.btnPDel;
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = preparations.get(position);
                int ind = preparations.indexOf(item);
                preparations.remove(ind);
                notifyItemRemoved(ind);
            }
        });
    }

    @Override
    public int getItemCount() {
        return preparations.size();
    }


}
