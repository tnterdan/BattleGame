package com.battlegame;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
 
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class ConnectActivity extends Activity {
	// Client vars
	protected Button connectBtn;
	protected Button hostBtn;
	protected TextView tv;
	protected TextView ipAddress;
	protected TextView port;
	protected Socket socket;

	// Server vars
   private SocketService mBoundService;
   private Boolean mIsBound = false;

	String mClientMsg = "";
	   
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		connectBtn = (Button) findViewById(R.id.sendButton);
		hostBtn = (Button) findViewById(R.id.hostButton);
		tv = (TextView) findViewById(R.id.myTextView);
		ipAddress = (TextView) findViewById(R.id.ipAddress);
		port = (TextView) findViewById(R.id.port);
		connectBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CharSequence connectionString = "Waiting to connect to " + ipAddress.getText();
				Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();

		  	    try {
		 	         InetAddress serverAddr = InetAddress.getByName(ipAddress.getText().toString());		 	       
		 	         //socket = new Socket(serverAddr, Integer.parseInt(port.getText().toString()));

		             startService(new Intent(ConnectActivity.this, SocketService.class));
		         
		 	         //connectionString = "Connection successful";
			  	     //Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();
			         try {
			        	 //PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
			        	 //out.println("ConnectionSuccess/FriendCode");
			        	 Log.d("Client", "Client sent message");
//			         } catch (UnknownHostException e) {
//			        	 tv.setText("Unknown Host Exception");
//			        	 e.printStackTrace();
//			         } catch (IOException e) {
//			        	 tv.setText("IO Exception");
//			        	 e.printStackTrace();
			         }
			         catch (Exception e) {
			        	 tv.setText("Unknown Exception");
			        	 e.printStackTrace();
			         }
		  	    } catch (UnknownHostException e1) {
		  	    	connectionString = "Unknown host";
			        Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();
			        e1.printStackTrace();
		  	    } catch (IOException e1) {
		  	    	connectionString = "Could not connect";
		  	    	Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();
		 	        e1.printStackTrace();
		 	    }
		  	    catch (NumberFormatException e1) {
		  	    	connectionString = "Invalid IP Address/Port";
		  	    	Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();
		 	        e1.printStackTrace();
		  	    }
		  	    catch (Exception e1) {
		  	    	connectionString = "Unknown Error";
		  	    	Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();
		 	        e1.printStackTrace();
		  	    }
	         }
		});

        hostBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), HostActivity.class);
                startActivity(nextScreen);
            }
        });
        
	}
}
