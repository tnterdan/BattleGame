package com.battlegame;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class CharacterSelectActivity extends Activity {

	// Server vars
	private Boolean mIsBound = false;
	Messenger mService = null;
	String type;
	int seed;
	
	String mClientMsg = "";
	
    String yourCharacter; 
    String enemyCharacter;

	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	/**
	 * This method starts the battle activity
	 */
	private void startGame() {
		Intent i = new Intent(CharacterSelectActivity.this, BattleActivity.class);
		i.putExtra("type", type);
		i.putExtra("int", seed);
		i.putExtra("yourCharacter", yourCharacter);
		i.putExtra("enemyCharacter", enemyCharacter);
		startActivity(i);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);

        ImageButton unicornButton = (ImageButton) findViewById(R.id.unicornButton);
        ImageButton wyvernButton = (ImageButton) findViewById(R.id.wyvernButton);

        Bundle extras = getIntent().getExtras();
        seed = extras.getInt("seed");
        type = extras.getString("type");
        
        // Set to use client/server key for encryption/decryption
        // This assumes only two users total
        // Also must create asset manager to open the files with
        
        RSAEncryption.assetMgr = this.getAssets();

        if(type.equals("client")) {
        	RSAEncryption.publicKeyType = "server";
        	RSAEncryption.privateKeyType = "client";
        }
        else {
        	RSAEncryption.publicKeyType = "client";
        	RSAEncryption.privateKeyType = "server";
        }
        
        // Bind the socket service
        
         if(mIsBound) {
        	 doUnbindService();
        	 doBindService();
         }
         else {
        	 doBindService();
         }
         
         /**
          * This event listener is for selecting a character.
          * When the user presses a creature's ImageButton, a Message is created.
          * This message is sent to the opponent with a byte representing the character chosen.
          */
         View.OnClickListener selectListener = new View.OnClickListener() {
        	 public void onClick(View argO) {
	            try {
	            	Message msg;
	            	Bundle b = new Bundle();
	            	// Send encrypted data
		    		// First convert string to base 64
		    		// Then use your private key to encrypt
		    		// Then use public key from other guy to encrypt
		    		// Turn that to base 64 again and send it
	            	
	            	// Unicorn = 0
	            	// Wyvern = 1
	            	byte[] sendInfo;
	            	
	            	if(argO.getId() == R.id.unicornButton) {
	            		sendInfo = new byte[] {1, 0};
	            		yourCharacter = "unicorn";
	            	}
	            	else {
	            		sendInfo = new byte[] {1, 1};
	            		yourCharacter = "wyvern";
	            	}
	            	
	            	//b.putString("data", Base64.encodeBytes(RSAEncryption.rsaEncrypt(RSAEncryption.rsaPrivateEncrypt(Base64.encodeBytes(textToSend.getBytes("US-ASCII")).getBytes("US-ASCII")))));
	            	
	            	b.putString("data", Base64.encodeBytes(RSAEncryption.rsaEncrypt(sendInfo)));
	            	
	            	if(type.equals("client")) {
	            		msg = Message.obtain(null, SocketService.MSG_CHAR_SELECT);
	            	}
	            	else {
	            		msg = Message.obtain(null, SocketServerService.MSG_CHAR_SELECT);
	            	}
	            	msg.setData(b);
	                msg.replyTo = mMessenger;
	                mService.send(msg);
	                
	                if(yourCharacter != null && enemyCharacter != null) {
	                	startGame();
	                }
	                else {
	                	Toast.makeText(getApplicationContext(), "Waiting for opponent...", Toast.LENGTH_LONG).show();
	                }
	            } catch (RemoteException e) {
	         	   e.printStackTrace();
	            } catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        };
         
        unicornButton.setOnClickListener(selectListener);
        wyvernButton.setOnClickListener(selectListener);
    }
	    
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	    	Bundle b = msg.getData();
	    	String data = b.getString("data");
	    	try {
				//String getText = new String(Base64.decode(RSAEncryption.rsaPublicDecrypt(RSAEncryption.rsaDecrypt(Base64.decode(data)))));
				byte[] getInfo = RSAEncryption.rsaDecrypt(Base64.decode(data));
				if(getInfo[0] == 1) {
					if(getInfo[1] == 0) {
						enemyCharacter = "unicorn";
					}
					else {
						enemyCharacter = "wyvern";
					}
					
	            	if(yourCharacter != null && enemyCharacter != null) {
	            		startGame();
	                }
	                else {
	                	Toast.makeText(getApplicationContext(), "Opponent has chosen. Waiting for you...", Toast.LENGTH_LONG).show();
	                }
				}
	    	} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }	
	}
	private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            try {
            	Message msg;
            	if(type.equals("client")) {
            		msg = Message.obtain(null, SocketService.MSG_CHAR_SELECT);
            	}
            	else {
            		msg = Message.obtain(null, SocketServerService.MSG_CHAR_SELECT);
            	}
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
        }
    };
 	   
     void doBindService() {
    	 if(type.equals("client")) {
    		 bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
    	 }
    	 else {
    		 bindService(new Intent(this, SocketServerService.class), mConnection, Context.BIND_AUTO_CREATE);
    	 }
         mIsBound = true;
     }
     
     void doUnbindService() {
         if (mIsBound) {
             // If we have received the service, and hence registered with it, then now is the time to unregister.
             if (mService != null) {
                 try {
                 	Message msg;
                 	if(type.equals("client")) {
                		msg = Message.obtain(null, SocketService.MSG_CHAR_SELECT);
                 	}
                 	else {
                		msg = Message.obtain(null, SocketServerService.MSG_CHAR_SELECT);
                 	}
                 	msg.replyTo = mMessenger;
                 	mService.send(msg);
                 } catch (RemoteException e) {
                     // There is nothing special we need to do if the service has crashed.
                 }
             }
             // Detach our existing connection.
             unbindService(mConnection);
             mIsBound = false;
         }
     }
}