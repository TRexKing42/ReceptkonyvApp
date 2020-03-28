package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class PictureEntry {
    private StorageReference reference;
    private String index;

    public PictureEntry(StorageReference reference, String index) {
        this.reference = reference;
        this.index = index;
    }

    public StorageReference getReference() {
        return reference;
    }

    public void setReference(StorageReference reference) {
        this.reference = reference;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void removeImage(String uid, String rid){
        reference.delete();
        FirebaseDatabase.getInstance().getReference().child("recipes").child(uid).child(rid).child("pictures").child(index).removeValue();
    }
}
