package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.banfikristof.receptkonyv.NewRecipeFragments.NewBasicsFragment;
import com.banfikristof.receptkonyv.NewRecipeFragments.NewIngFragment;
import com.banfikristof.receptkonyv.NewRecipeFragments.NewPhotoFragment;
import com.banfikristof.receptkonyv.NewRecipeFragments.NewPrepFragment;
import com.banfikristof.receptkonyv.NewRecipeFragments.NewTagsFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UjReceptActivity extends AppCompatActivity implements
        NewBasicsFragment.OnFragmentInteractionListener,
        NewIngFragment.OnFragmentInteractionListener,
        NewPrepFragment.OnFragmentInteractionListener,
        NewTagsFragment.OnFragmentInteractionListener,
        NewPhotoFragment.OnFragmentInteractionListener {

    private static final int GALLERY_REQUEST = 9999;
    private static final int CAMERA_REQUEST = 9998;

    public int currentFrame = 0;

    public Recipe recipeToSave;

    private String pushId;
    public boolean editmode = false;

    private FrameLayout frameLayout;

    private boolean succesfullPhoto = false;






    private AlertDialog alertDialog;
    private AlertDialog.Builder adBuilder;

    private Recipe QRrecipe;

    public List<Map<String,String>> ingredients;

    Bitmap img;
    StorageReference imgRef;
    private Uri picUri = null;
    private String picPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uj_recept);

        init();

        changeToCorrectFrame();

        if (currentFrame == 0){ //Varázsló indítása
            //currentFrame = 4;
            changeToCorrectFrame();
        }
    }

    private void changeToCorrectFrame() {
        Fragment selectedFragment = null;
        switch (currentFrame){
            case 0:
                selectedFragment = new NewBasicsFragment();
                break;
            case 1:
                selectedFragment = new NewIngFragment();
                break;
            case 2:
                selectedFragment = new NewPrepFragment();
                break;
            case 3:
                selectedFragment = new NewTagsFragment();
                break;
            case 4:
                selectedFragment = new NewPhotoFragment();
                break;
            default:
                break;
        }

        if (selectedFragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.NewRecipeFrame, selectedFragment);
            fragmentTransaction.commit();
        }
    }

    private void uploadImg(final StorageReference reference) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask task = reference.putBytes(data);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UjReceptActivity.this, "Sikertelen képfeltöltés!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void basicsDone(String name, String desc) {
        if (name.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this,"A név és a leírás nem lehet üres!",Toast.LENGTH_SHORT).show();
            return;
        }
        recipeToSave.setName(name);
        recipeToSave.setDescription(desc);
        currentFrame = 1;

        changeToCorrectFrame();
    }

    @Override
    public void startQrImport() {
        IntentIntegrator integrator = new IntentIntegrator(UjReceptActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("QR Code Scannelés");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void ingDone(List<Map<String, String>> ingredients) {
        if (ingredients.size() < 1) {
            Toast.makeText(this,"Szükséges legalább egy hozzávaló!",Toast.LENGTH_SHORT).show();
            return;
        }
        recipeToSave.setIngredients(ingredients);
        currentFrame = 2;

        changeToCorrectFrame();
    }

    @Override
    public void backToBasics() {
        currentFrame = 0;

        changeToCorrectFrame();
    }

    @Override
    public void prepDone(List<String> prep) {
        if (prep.size() < 1) {
            Toast.makeText(this,"Szükséges legalább egy elkészítési lépés!",Toast.LENGTH_SHORT).show();
            return;
        }
        recipeToSave.setPreparation(prep);
        currentFrame = 3;

        changeToCorrectFrame();
    }

    @Override
    public void backToIng() {
        currentFrame = 1;

        changeToCorrectFrame();
    }

    @Override
    public void tagsDone(List<String> selectedTagList) {
        recipeToSave.setTags(selectedTagList);
        currentFrame = 4;

        changeToCorrectFrame();
    }

    @Override
    public void backToPrep() {
        currentFrame = 2;

        changeToCorrectFrame();
    }

    @Override
    public Bitmap takePicture() {
        createPhotoDialog();
        return img;
    }

    @Override
    public void backToPrepTags() {
        currentFrame = 3;

        changeToCorrectFrame();
    }

    @Override
    public void recipeDone() {
        List<String> images = new ArrayList<>();
        recipeToSave.setPictures(images); //Egyéb képeknek
        if (!editmode) {
            pushId = FirebaseDatabase.getInstance().getReference().child("recipes")
                    .child(FirebaseAuth.getInstance().getUid()).push().getKey();
        }
        if (recipeToSave == null) return;
        if (img != null){
            uploadImg(FirebaseStorage.getInstance().getReference()
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(pushId)
                    .child("main_img.jpg"));
            recipeToSave.setHasMainImg(true);

            //Update images list for easy delete
            /*User usr = new User();
            usr.setDisplayName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            usr.addImgList(images,pushId);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
            db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(usr);*/
        }
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("recipes").child(FirebaseAuth.getInstance().getUid()).child(pushId).setValue(recipeToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UjReceptActivity.this, "Sikeres mentés!",Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UjReceptActivity.this, "Sikertelen mentés!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Bitmap getTakenPicture() {
        if (editmode && recipeToSave.isHasMainImg()){
            Glide.with(this).asBitmap().load(imgRef).into(new CustomTarget<Bitmap>(){
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    img = resource;
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        } else if (editmode && !recipeToSave.isHasMainImg()){
            return null;
        }

        return img;
    }

    private void init() {
        frameLayout = findViewById(R.id.NewRecipeFrame);

        recipeToSave = new Recipe();
        recipeToSave.setHasMainImg(false);

        if (getIntent().hasExtra("RecipeEdit")){
            recipeToSave = (Recipe) getIntent().getSerializableExtra("RecipeEdit");
            pushId = recipeToSave.key;
            imgRef = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(recipeToSave.key)
                    .child("main_img.jpg");

            editmode = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case GALLERY_REQUEST:
                    try {
                        Uri imgUri = data.getData();

                        img = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgUri);
                        img = Bitmap.createScaledBitmap(img,500,500,true);

                        NewPhotoFragment fragment = (NewPhotoFragment) getSupportFragmentManager().findFragmentById(R.id.NewRecipeFrame);
                        fragment.loadImage();
                    } catch (IOException e) {
                        Toast.makeText(this,getResources().getString(R.string.imgLoadErrorNew),Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CAMERA_REQUEST:
                    //img = (Bitmap) data.getExtras().get("data");
                    if (picUri != null) {
                        try {
                            img = MediaStore.Images.Media.getBitmap(this.getContentResolver(),picUri);
                        } catch (IOException e) {
                            Toast.makeText(UjReceptActivity.this,"Hiba a kép betöltésénél!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    NewPhotoFragment fragment = (NewPhotoFragment) getSupportFragmentManager().findFragmentById(R.id.NewRecipeFrame);
                    fragment.loadImage();
                    break;
                case IntentIntegrator.REQUEST_CODE:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
                    if (result != null){
                        if (result.getContents() != null) {
                            Gson gson = new Gson();
                            RecipeShare share = gson.fromJson(result.getContents(),RecipeShare.class);
                            saveFromQR(share);
                        }
                    }
                    break;
            }
        }
    }

    private void saveFromQR(final RecipeShare r) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("recipes").child(r.getUid()).child(r.getRid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe;

                //Recept letöltése
                try {
                    recipe = dataSnapshot.getValue(Recipe.class);
                }catch (Exception eee){
                    Toast.makeText(UjReceptActivity.this,"HIBA az importálás közben.",Toast.LENGTH_SHORT).show();
                    return;
                }

                //Kép letöltése ha van
                if (recipe.isHasMainImg()){
                    imgRef = FirebaseStorage.getInstance().getReference()
                            .child(r.getUid())
                            .child(r.getRid())
                            .child("main_img.jpg");
                    Glide.with(UjReceptActivity.this).asBitmap().load(imgRef).into(new CustomTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            img = resource;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                }

                //Egyéb beállítások
                recipe.setUid(FirebaseAuth.getInstance().getUid());
                recipe.setFavourite(false);

                //Recept feltötése a jelenlegi userhez
                recipeToSave = recipe;
                recipeDone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    public void createPhotoDialog(){
        adBuilder = new AlertDialog.Builder(UjReceptActivity.this);
        adBuilder.setMessage(getResources().getString(R.string.photoPromptTxt));
        adBuilder.setPositiveButton(getResources().getString(R.string.photoYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = null;
                try {
                    f = File.createTempFile(new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + recipeToSave.getName(),
                            ".jpg",
                            getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoIntent.resolveActivity(getPackageManager()) != null) {
                    if (f != null) {
                        picPath = f.getAbsolutePath();
                        picUri = FileProvider.getUriForFile(UjReceptActivity.this,"com.banfikristof.receptkonyv.provider",f);
                        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                        photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(photoIntent, CAMERA_REQUEST);
                    } else {

                    }
                }
            }
        });
        adBuilder.setNegativeButton(getResources().getString(R.string.photoNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        adBuilder.setNeutralButton(getResources().getString(R.string.photoCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        adBuilder.setCancelable(false);
        alertDialog = adBuilder.create();
        alertDialog.show();
    }

    public String createLocalFileName(){
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date());
        name += recipeToSave.getName() + "_.jpg";
        return name;
    }
}
