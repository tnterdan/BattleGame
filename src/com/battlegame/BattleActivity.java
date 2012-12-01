package com.battlegame;

import android.widget.Button;
import android.app.Activity;
import android.content.Intent;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
import android.os.Bundle;
//import android.view.Menu;
import android.view.View;
import android.widget.Toast;



public class BattleActivity extends Activity {
	//private static final Object FF0000 = null;
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_battle);
		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(BattleActivity.this, ConnectActivity.class);
				startActivity(i);
			}
			
		});
	}
		public void onMyButtonClick(View view)  
	    {  
	        Toast.makeText(this, "-40", Toast.LENGTH_SHORT).show();
	    
	}
//		public void draw(Graphics g)
//		{
//		    int maxHealth = 100;
//			int health = 100;
//			float healthScale = health / maxHealth;
//		    Object healthBarColor = FF0000;
//			g.setColor(healthBarColor);
//		    Object healthBarHeight = 80;
//			Object healthBarY = null;
//			Object healthBarX = null;
//			float healthBarWidth = 100;
//			g.fillRect(healthBarX, healthBarY, healthBarWidth * healthScale, healthBarHeight);
//		}
}