package com.banfikristof.receptkonyv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NoConnectionActivity extends AppCompatActivity {

    private Button tryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        init();

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnectedToNetwork()){
                    Toast.makeText(NoConnectionActivity.this,getResources().getString(R.string.still_no_network),Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(NoConnectionActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void init() {
        tryAgain = findViewById(R.id.retryNetworkBtn);
    }

    public boolean isConnectedToNetwork(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null;
    }
}
