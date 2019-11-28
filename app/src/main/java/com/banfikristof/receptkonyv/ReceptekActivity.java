package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.LauncherActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReceptekActivity extends AppCompatActivity {


    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nv;
    private ListView lv;

    private SQLiteDBHelper DBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptek);

        init();

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.menu_fooldal:
                        Intent intent = new Intent(ReceptekActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.menu_receptek:
                        Toast.makeText(ReceptekActivity.this, "Ez a jelenlegi hely",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(ReceptekActivity.this, "Még nem",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,getRecipes());
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe s = (Recipe) lv.getItemAtPosition(position);
                Intent intent = new Intent(ReceptekActivity.this,OpenReceptActivity.class);
                intent.putExtra("SelectedRecipe",s);
                startActivity(intent);
                finish();
            }
        });
    }

    private void init() {
        dl = findViewById(R.id.drawerlayout_receptek);
        nv = findViewById(R.id.mainmenu);
        lv = findViewById(R.id.listReceptek);

        DBManager = new SQLiteDBHelper(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,dl,R.string.drawer_open,R.string.drawer_close);
        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public ArrayList<Recipe> getRecipes() {
        Cursor result = DBManager.getRecipes();
        ArrayList<Recipe> returnedList = new ArrayList<>();
        if (result != null && result.getCount() > 0) {
            while (result.moveToNext()){
                String s = result.getString(4);
                String[] s2 = s.split(","); //Egyelőre nem müködik
                returnedList.add(new Recipe(
                        result.getString(0),
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        Arrays.asList(s2)
                ));
            }
        }
        return returnedList;
    }
}
