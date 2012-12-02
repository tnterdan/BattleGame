package com.battlegame;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.app.Activity;
//import android.content.Intent;

public class CharacterSelectActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);

        ImageButton unicornButton = (ImageButton) findViewById(R.id.unicornButton);
        ImageButton wyvernButton = (ImageButton) findViewById(R.id.wyvernButton);

        View.OnClickListener selectListner = new View.OnClickListener() {
        	public void onClick(View argO) {
        		
        		// Wait for other player to select character
        		//while(msgNotReceived);
        	
        		//Intent i = new Intent(CharacterSelectActivity.this, BattleActivity.class);
        		//startActivity(i);
        	}
        };
        
        unicornButton.setOnClickListener(selectListner);
        wyvernButton.setOnClickListener(selectListner);
    }
}