package com.banfikristof.receptkonyv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skyhope.materialtagview.TagView;
import com.skyhope.materialtagview.enums.TagSeparator;
import com.skyhope.materialtagview.interfaces.TagItemListener;
import com.skyhope.materialtagview.model.TagModel;

import java.util.ArrayList;
import java.util.List;

public class UjReceptActivity extends AppCompatActivity {

    private TagView tv;
    private Button saveBtn;
    private EditText etNev, etLeiras, etElkeszites;

    private SQLiteDBHelper DBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uj_recept);

        init();

        tv.initTagListener(new TagItemListener() {
            @Override
            public void onGetAddedItem(TagModel tagModel) {
                //TODO: Hozzáadni fájlhoz
            }

            @Override
            public void onGetRemovedItem(TagModel model) {
                //...
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etNev.getText())){
                    Toast.makeText(UjReceptActivity.this, "Étel neve nem lehet üres!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etElkeszites.getText())){
                    Toast.makeText(UjReceptActivity.this, "Étel elkészítése nem lehet üres!",Toast.LENGTH_SHORT).show();
                    return;
                }
                List<TagModel> selectedTags = tv.getSelectedTags();
                if (selectedTags.isEmpty()) {
                    Toast.makeText(UjReceptActivity.this, "Szükség van legalább egy hozzávalóra!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Recipe recipe;
                if (TextUtils.isEmpty(etLeiras.getText())){
                    recipe = new Recipe(etNev.getText().toString(),etElkeszites.getText().toString(),tagsToString(selectedTags));
                } else {
                    recipe = new Recipe(etNev.getText().toString(),etLeiras.getText().toString(),etElkeszites.getText().toString(),tagsToString(selectedTags));
                }
                if (DBManager.newRecipe(recipe) == 1) {
                    Toast.makeText(UjReceptActivity.this, "Sikertelen mentés!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UjReceptActivity.this, "Sikeres mentés!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void init() {
        tv = findViewById(R.id.tagView);
        tv.addTagSeparator(TagSeparator.COMMA_SEPARATOR);
        String[] tagList = new String[]{"Tojás", "Tej", "Liszt", "Tészta", "Kenyér", "Víz"};
        tv.setTagList(tagList);

        saveBtn = findViewById(R.id.ujReceptMentes);

        etNev = findViewById(R.id.ujReceptNev);
        etLeiras = findViewById(R.id.ujReceptLeiras);
        etElkeszites = findViewById(R.id.ujReceptElkeszites);

        DBManager = new SQLiteDBHelper(this);
    }

    private List<String> tagsToString(List<TagModel> tags) {
        List<String> result = new ArrayList<>();
        for (TagModel item : tags) {
            result.add(item.getTagText());
        }
        return result;
    }
}
