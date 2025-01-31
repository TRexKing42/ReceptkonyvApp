package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.banfikristof.receptkonyv.RecipeDisplayFragments.IngredientsFragment;
import com.banfikristof.receptkonyv.RecipeDisplayFragments.OverviewFragment;
import com.banfikristof.receptkonyv.RecipeDisplayFragments.PicturesFragment;
import com.banfikristof.receptkonyv.RecipeDisplayFragments.PreparationFragment;
import com.banfikristof.receptkonyv.RecipeDisplayFragments.RecipeOptions;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class OpenReceptActivity extends AppCompatActivity implements
        OverviewFragment.OnFragmentInteractionListener,
        IngredientsFragment.OnFragmentInteractionListener,
        PreparationFragment.OnFragmentInteractionListener,
        RecipeOptions.OnFragmentInteractionListener,
        PicturesFragment.OnFragmentInteractionListener{

    private static final int CAMERA_REQUEST = 4321;

    private TextView receptNev;
    private BottomNavigationView bottomNavigationView;
    private StorageReference img;

    public Recipe r;
    private String picPath;
    private Uri picUri;
    private FrameLayout rFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_recept);

        init();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch(menuItem.getItemId())
                {
                    case R.id.recipeMenu_overview:
                        selectedFragment = new OverviewFragment();
                        break;
                    case R.id.recipeMenu_ingredients:
                        selectedFragment = new IngredientsFragment();
                        break;
                    case R.id.recipeMenu_preparation:
                        selectedFragment = new PreparationFragment();
                        break;
                    case R.id.recipeMenu_options:
                        selectedFragment = new RecipeOptions();
                        break;
                    case R.id.recipeMenu_camera:
                        selectedFragment = new PicturesFragment();
                        break;
                    default:
                        Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.error_notyet),Toast.LENGTH_SHORT).show();
                        break;
                }

                if (selectedFragment != null) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.receptFrame, selectedFragment);
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });
        displayRecipe();
    }

    private void init(){
        receptNev = findViewById(R.id.receptNevSelected);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        rFrame = findViewById(R.id.receptFrame);
        r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.receptFrame, new OverviewFragment());
        fragmentTransaction.commit();
    }

    public void displayRecipe(){
        //Recipe r = (Recipe) getIntent().getSerializableExtra("SelectedRecipe");
        receptNev.setText(r.getName());
        img = FirebaseStorage.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getUid())
                .child(r.key)
                .child("main_img.jpg");

    }

    //Overview Fragment
    @Override
    public void onFragmentDisplayed(TextView desc, ImageView imageView, TextView receptTags) {
        desc.setText(r.getDescription());
        receptTags.setText(r.tagsToString());
        if (!r.isHasMainImg()){
            Glide.with(this).load(FirebaseStorage.getInstance().getReference().child("no_picture.png")).centerCrop().into(imageView);
        } else {
            Glide.with(this).load(img).centerCrop().into(imageView);
        }
    }

    @Override
    public void showBigImage() {
        //Glide.with(this).load(img).into(bigImage);
        //bigImage.setVisibility(View.VISIBLE);
        Intent intent = new Intent(OpenReceptActivity.this, BigImageActivity.class);
        intent.putExtra("key",r.key);
        intent.putExtra("mainImg", true);
        startActivity(intent);
    }

    @Override
    public boolean recipeHasMainImg() {
        return r.isHasMainImg();
    }

    //Ingredients Fragment
    @Override
    public void onIngFragmentDisplayed(TextView tv) {
        tv.setText(r.ingredientsToString());
    }

    //Preparations Fragmentt
    @Override
    public void onPrepFragmentDisplayed(TextView tv) {
        tv.setText(r.preparationAsString());
    }


    //Options Fragment
    @Override
    public void onShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,r.getName());
        intent.putExtra(Intent.EXTRA_TEXT,
                r.getDescription() +
                        "\n"+ getResources().getString(R.string.ingredientsC) + "\n" +
                        r.ingredientsToString() +
                        "\n" + getResources().getString(R.string.recipePreparationText) + "\n" +
                        r.preparationAsString());
        startActivity(Intent.createChooser(intent,getResources().getText(R.string.share)));
    }

    @Override
    public void onDelete() {

        if (r.isHasMainImg()){/*
            FirebaseStorage.getInstance().getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(r.key)
                    .child("main_img.jpg")
                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (r.getPictures() != null) {
                        for (Map.Entry item:r.getPictures().entrySet()){
                            FirebaseStorage.getInstance().getReference()
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(r.key)
                                    .child((String) item.getValue())
                                    .delete();
                        }
                    }
                    FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(r.key).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.del_good),Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.del_bad),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });*/
            FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(r.key).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            FirebaseStorage.getInstance().getReference()
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(r.key)
                                    .child("main_img.jpg")
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (r.getPictures() != null) {
                                                for (Map.Entry item:r.getPictures().entrySet()){
                                                    FirebaseStorage.getInstance().getReference()
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(r.key)
                                                            .child((String) item.getValue())
                                                            .delete();
                                                }
                                                Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.del_good),Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.del_good),Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.del_bad),Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(r.key).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (r.getPictures() != null) {
                                for (Map.Entry item:r.getPictures().entrySet()){
                                    FirebaseStorage.getInstance().getReference()
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(r.key)
                                            .child((String) item.getValue())
                                            .delete();
                                }
                                Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.del_good),Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.del_good),Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.del_bad),Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

    }

    @Override
    public void onEdit() {
        Intent intent = new Intent(OpenReceptActivity.this, UjReceptActivity.class);
        intent.putExtra("RecipeEdit", r);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFavorite(Button favourite) {
        FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(r.key).child("favourite").setValue(!r.isFavourite());
        r.setFavourite(!r.isFavourite());
        if (r.isFavourite()) {
            favourite.setText(getResources().getText(R.string.unfavourite));
        } else {
            favourite.setText(getResources().getText(R.string.add_to_favorites));
        }
    }

    @Override
    public String onGetJSON() {
        Gson gson = new Gson();
        RecipeShare recipeToShare = new RecipeShare();
        recipeToShare.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recipeToShare.setRid(r.key);
        String result = gson.toJson(recipeToShare);
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Fragment selectedFragment = null;
        switch(bottomNavigationView.getSelectedItemId())
        {
            case R.id.recipeMenu_overview:
                selectedFragment = new OverviewFragment();
                break;
            case R.id.recipeMenu_ingredients:
                selectedFragment = new IngredientsFragment();
                break;
            case R.id.recipeMenu_preparation:
                selectedFragment = new PreparationFragment();
                break;
            case R.id.recipeMenu_options:
                selectedFragment = new RecipeOptions();
                break;
            case R.id.recipeMenu_camera:
                selectedFragment = new PicturesFragment();
                break;
            default:
                Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.error_notyet),Toast.LENGTH_SHORT).show();
                break;
        }

        if (selectedFragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.receptFrame, selectedFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onLoadAllPictures() {
        try {
            if (r.getPictures() != null) {
                PicturesFragment fragment = (PicturesFragment) getSupportFragmentManager().findFragmentById(R.id.receptFrame);
                fragment.pictures.clear();
                int ind = 0;
                for (Map.Entry item : r.getPictures().entrySet()) {
                    if (item != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReference()
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(r.key)
                                .child((String) item.getValue());

                        PictureEntry pictureEntry = new PictureEntry(reference, (String) item.getKey());
                        fragment.pictures.add(pictureEntry);
                        ind++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTakePicture() {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = File.createTempFile(new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + r.getName(),
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoIntent.resolveActivity(getPackageManager()) != null) {
            if (f != null) {
                picPath = f.getAbsolutePath();
                picUri = FileProvider.getUriForFile(OpenReceptActivity.this, "com.banfikristof.receptkonyv.provider", f);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(photoIntent, CAMERA_REQUEST);
            } else {

            }
        }
    }

    @Override
    public String onGetKey() {
        return r.key;
    }

    private Bitmap fixPicRotation(String p) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(p,options);

        Bitmap bitmap = BitmapFactory.decodeFile(p,options2);
        ExifInterface exifInterface = new ExifInterface(p);
        String s = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);

        int o;
        if (s != null){
            o = Integer.parseInt(s);
        } else {
            o = ExifInterface.ORIENTATION_NORMAL;
        }

        int r;
        switch (o){
            case ExifInterface.ORIENTATION_ROTATE_90:
                r = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                r = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                r = 270;
                break;
            default:
                r = 0;
                break;
        }

        Matrix m = new Matrix();
        m.setRotate(r, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap,0,0,options.outWidth,options.outHeight,m,true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.receptFrame, new PicturesFragment());
        fragmentTransaction.commit();

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA_REQUEST){
                if (picUri != null) {
                    try {

                        Bitmap thisImg = fixPicRotation(picPath);
                        //Bitmap thisImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(),picUri);
                        String picName = createPicName();
                        uploadImg(FirebaseStorage.getInstance().getReference()
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(r.key)
                                .child(picName),
                                thisImg);
                        String pID = FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(r.key).child("pictures").child("pictures").push().getKey();
                        r.getPictures().put(pID,picName);



                        //Online is frissíteni
                        FirebaseDatabase.getInstance().getReference().child("recipes").child(FirebaseAuth.getInstance().getUid()).child(r.key).child("pictures").setValue(r.getPictures())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        onLoadAllPictures();
                                    }
                                });


                    } catch (IOException e) {
                        Toast.makeText(OpenReceptActivity.this,getResources().getText(R.string.img_load_error),Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
    }

    private void uploadImg(final StorageReference reference, Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask task = reference.putBytes(data);
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                onLoadAllPictures();
                Intent intent = new Intent(OpenReceptActivity.this, BigImageActivity.class);
                intent.putExtra("key",r.key);
                intent.putExtra("mainImg", false);
                intent.putExtra("picRef",reference.getName());
                startActivity(intent);

            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OpenReceptActivity.this, getResources().getText(R.string.pic_unsuccesful),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String createPicName(){
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date());
        name += r.getName() + ".jpg";
        return name;
    }

    public void removeDeletedPic(String i){
        r.getPictures().remove(i);
    }
}
