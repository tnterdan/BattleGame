package com.battlegame;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.battlegame.HostActivity.IncomingHandler;
 
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
	
	int SERVERPORT = 6666;
	String SERVERIP = "192.168.1.103";

	// Server vars
	private Boolean mIsBound = false;
	Messenger mService = null;
   
	String mClientMsg = "";

	final Messenger mMessenger = new Messenger(new IncomingHandler());
	   
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

		             //startService(new Intent(ConnectActivity.this, SocketService.class));
		 	         
		 	         SERVERIP = ipAddress.getText().toString();
		 	         SERVERPORT = 6666;
		 	         
		 	         if(mIsBound) {
		 	        	 doUnbindService();
		 	        	 doBindService();
		 	         }
		 	         else {
		 	        	 doBindService();
		 	         }
		         
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
	   
	
   private void characterSelect() {
	   Intent intent = new Intent(ConnectActivity.this, CharacterSelectActivity.class);
	   intent.putExtra("type", "client");
       startActivity(intent);
   }
	   
   class IncomingHandler extends Handler {
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what) {
               case SocketService.MSG_CONNECT_SUCCESS:
                   //Toast.makeText(getApplicationContext(), "Connection successful!", Toast.LENGTH_SHORT).show();
                   characterSelect();
                   break;
               case SocketService.MSG_CHAR_SELECT:
                   //Toast.makeText(getApplicationContext(), "Characters selected!", Toast.LENGTH_SHORT).show();
                   break;
               case SocketService.MSG_ATTACK:
                   //Toast.makeText(getApplicationContext(), "Attack message!", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   super.handleMessage(msg);
           }
       }
   }
   
	private ServiceConnection mConnection = new ServiceConnection() {
       public void onServiceConnected(ComponentName className, IBinder service) {
           mService = new Messenger(service);
           tv.setText("Attached.");
           try {
               Message msg = Message.obtain(null, SocketService.MSG_CONNECT_SUCCESS);
               msg.replyTo = mMessenger;
               mService.send(msg);
           } catch (RemoteException e) {
               // In this case the service has crashed before we could even do anything with it
        	   e.printStackTrace();
           }
       }

       public void onServiceDisconnected(ComponentName className) {
           // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
           mService = null;
           tv.setText("Disconnected.");
       }
   };
	   
    void doBindService() {
        bindService(new Intent(this, SocketService.class).putExtra("SERVERPORT", SERVERPORT).putExtra("SERVERIP", SERVERIP), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        tv.setText("Binding.");
    }
    
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, SocketService.MSG_CONNECT_SUCCESS);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            tv.setText("Unbinding.");
        }
    }
}
