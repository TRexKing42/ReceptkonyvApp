package com.banfikristof.receptkonyv.Adapters;

import android.content.Context;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banfikristof.receptkonyv.OpenReceptActivity;
import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.Recipe;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.ViewHolder> {
    public List<StorageReference> pictures;
    public String recipeKey;

    public Context context; //Mindig OpenReceptActivity

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView picIv;
        public Button btnPicDel;

        public ViewHolder(View v) {
            super(v);
            picIv = v.findViewById(R.id.picRvImageIv);
            btnPicDel = v.findViewById(R.id.picRvDeleteBtn);
        }
    }

    public PicturesAdapter(List<StorageReference> pictures, String recipeKey, Context context) {
        this.pictures = pictures;
        this.recipeKey = recipeKey;
        this.context = context;
    }


    @NonNull
    @Override
    public PicturesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View picView = inflater.inflate(R.layout.pictures_rv_item, parent, false);
        return new PicturesAdapter.ViewHolder(picView);
    }

    @Override
    public void onBindViewHolder(PicturesAdapter.ViewHolder holder, final int position) {
        StorageReference pic = pictures.get(position);
        ImageView picView = holder.picIv;
        Glide.with(holder.picIv.getContext()).load(pic).thumbnail(0.3f).into(picView);

        Button btnDelete = holder.btnPicDel;
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference item = pictures.get(position);
                int ind = pictures.indexOf(item);
                pictures.remove(ind);
                notifyItemRemoved(ind);

                item.delete();
                FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(recipeKey).child("pictures").child(String.valueOf(position)).removeValue();
                ((OpenReceptActivity) context).removeDeletedPic(position);
                ((OpenReceptActivity) context).onLoadAllPictures();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }


}
