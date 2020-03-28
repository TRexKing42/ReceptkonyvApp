package com.banfikristof.receptkonyv.Adapters;

import android.content.Context;
import android.content.Intent;
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

import com.banfikristof.receptkonyv.BigImageActivity;
import com.banfikristof.receptkonyv.OpenReceptActivity;
import com.banfikristof.receptkonyv.PictureEntry;
import com.banfikristof.receptkonyv.R;
import com.banfikristof.receptkonyv.Recipe;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.ViewHolder> {
    public List<PictureEntry> pictures;
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

    public PicturesAdapter(List<PictureEntry> pictures, String recipeKey, Context context) {
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
    public void onBindViewHolder(final PicturesAdapter.ViewHolder holder, final int position) {
        final PictureEntry pic = pictures.get(holder.getAdapterPosition());
        ImageView picView = holder.picIv;
        Glide.with(holder.picIv.getContext()).load(pic.getReference()).thumbnail(0.2f).into(picView);
        picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BigImageActivity.class);
                intent.putExtra("key",recipeKey);
                intent.putExtra("mainImg", false);
                intent.putExtra("picRef",pic.getReference().getName());
                context.startActivity(intent);
            }
        });

        Button btnDelete = holder.btnPicDel;
        btnDelete.setTag(pic.getIndex());
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int ind = holder.getAdapterPosition();//pictures.indexOf(item);
                PictureEntry item = pictures.get(ind);
                if (!item.getIndex().equals(v.getTag().toString())){
                    for (PictureEntry i : pictures) {
                        if (i.getIndex().equals(v.getTag().toString())){
                            item = i;
                            break;
                        }
                    }
                }
                final String firebaseID = pictures.get(ind).getIndex();
                pictures.remove(ind);
                item.getReference().delete();
                final String itemID = item.getIndex();
                FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(recipeKey).child("pictures").child(firebaseID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notifyItemRemoved(ind);
                        ((OpenReceptActivity) context).removeDeletedPic(itemID);
                        ((OpenReceptActivity) context).onLoadAllPictures();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }


}
