package com.battlegame;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;


public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button startButton = (Button) findViewById(R.id.startButton);
        Button friendButton = (Button) findViewById(R.id.friendButton);
        Button highScoreButton = (Button) findViewById(R.id.highScoreButton);
        
        friendButton.setEnabled(false);
        highScoreButton.setEnabled(false);
        
        startButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View argO) {
        		Intent i = new Intent(MainActivity.this, ConnectActivity.class);
        		startActivity(i);
        	}
        });
    }
}