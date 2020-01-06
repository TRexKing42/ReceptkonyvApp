package com.banfikristof.receptkonyv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ReceptekFragment.ReceptekFragmentListener {

    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nv;

    private SQLiteDBHelper DBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        dl = findViewById(R.id.drawerlayout);
        nv = findViewById(R.id.mainmenu);
        nv.setNavigationItemSelectedListener(this);

        DBManager = new SQLiteDBHelper(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,dl,R.string.drawer_open,R.string.drawer_close);
        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new KezdolapFragment());
        fragmentTransaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;

        switch(menuItem.getItemId())
        {
            case R.id.menu_fooldal:
                selectedFragment = new KezdolapFragment();
                break;
            case R.id.menu_receptek:
                selectedFragment = new ReceptekFragment();
                break;
            default:
                Toast.makeText(MainActivity.this, "Még nem",Toast.LENGTH_SHORT).show();
                break;
        }

        if (selectedFragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, selectedFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    ////
    // ReceptekFragment
    ////
    public ArrayList<Recipe> getRecipes() {
        Cursor result = DBManager.getRecipes();
        ArrayList<Recipe> returnedList = new ArrayList<>();
        if (result != null && result.getCount() > 0) {
            while (result.moveToNext()){
                String s = result.getString(result.getColumnIndex(SQLiteDBHelper.COL[3]));
                String[] s2 = s.split(","); //Egyelőre nem müködik
                returnedList.add(new Recipe(
                        result.getString(result.getColumnIndex(SQLiteDBHelper.COL[0])),
                        result.getString(result.getColumnIndex(SQLiteDBHelper.COL[1])),
                        result.getString(result.getColumnIndex(SQLiteDBHelper.COL[2])),
                        result.getString(result.getColumnIndex(SQLiteDBHelper.COL[4])),
                        Arrays.asList(s2)
                ));
            }
        }
        return returnedList;
    }

    @Override
    public void onRecipeSelected() {

    }
}
