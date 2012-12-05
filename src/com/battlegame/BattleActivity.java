package com.battlegame;

import com.creature.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class BattleActivity extends Activity {

	// Server vars
	private Boolean mIsBound = false;
	Messenger mService = null;
	
	int seed;
	String type;
	String yourCharacter;
	String enemyCharacter;
	
	Creature yourCreature;
	Creature enemyCreature;
	
	Button attack1;
    Button attack2;
    Button attack3;
	
    TextView yourHealth;
    TextView enemyHealth;
    
    TextView battleMsg;
	
	String mClientMsg = "";
	
	int yourAttackNum = -1;
	int enemyAttackNum = -1;
	
	Attack yourAttack;
	Attack enemyAttack;

	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
         
        ImageView yourPicture = (ImageView) findViewById(R.id.yourPicture);
        ImageView enemyPicture = (ImageView) findViewById(R.id.enemyPicture);
        
        attack1 = (Button) findViewById(R.id.attack1);
        attack2 = (Button) findViewById(R.id.attack2);
        attack3 = (Button) findViewById(R.id.attack3);
        
        yourHealth = (TextView) findViewById(R.id.yourHealth);
        enemyHealth = (TextView) findViewById(R.id.enemyHealth);
        
        battleMsg = (TextView) findViewById(R.id.battleMsg);
        
        Bundle extras = getIntent().getExtras();
        type = extras.getString("type");
        seed = extras.getInt("seed");
        yourCharacter = extras.getString("yourCharacter");
        enemyCharacter = extras.getString("enemyCharacter");
        
        seed = 1234567890;
        
        if(yourCharacter.equals("unicorn")) {
        	yourPicture.setImageResource(R.drawable.lunicorn);
        	yourCreature = new Unicorn(seed);
        }
        else {
        	yourPicture.setImageResource(R.drawable.lwyvern);
        	yourCreature = new Wyvern(seed);
        }
        
    	attack1.setText(yourCreature.getAttack(0).getName());
    	attack2.setText(yourCreature.getAttack(1).getName());
    	attack3.setText(yourCreature.getAttack(2).getName());
    	
    	yourHealth.setText(yourCreature.getHealth() + "/" + yourCreature.getMaxHealth());
        
        if(enemyCharacter.equals("unicorn")) {
        	enemyPicture.setImageResource(R.drawable.runicorn);
        	enemyCreature = new Unicorn(seed);
        }
        else {
        	enemyPicture.setImageResource(R.drawable.rwyvern);
        	enemyCreature = new Wyvern(seed);
        }
        
    	enemyHealth.setText(enemyCreature.getHealth() + "/" + enemyCreature.getMaxHealth());
        
    	battleMsg.setText("Choose an attack!");
         
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
        
         if(mIsBound) {
        	 doUnbindService();
        	 doBindService();
         }
         else {
        	 doBindService();
         }
        
         View.OnClickListener attackListner = new View.OnClickListener() {
         	public void onClick(View argO) {
	            try {
	            	Message msg;
	            	Bundle b = new Bundle();
	            	// Send encrypted data
		    		// First convert string to base 64
		    		// Then use your private key to encrypt
		    		// Then use public key from other guy to encrypt
		    		// Turn that to base 64 again and send it
	            	byte[] sendInfo;
	            	
	            	if(argO.getId() == R.id.attack1) {
	            		sendInfo = new byte[] {2, 0};
	            		yourAttackNum = 0;
	            	}
	            	else if(argO.getId() == R.id.attack2) {
	            		sendInfo = new byte[] {2, 1};
	            		yourAttackNum = 1;
	            	}
	            	else {
	            		sendInfo = new byte[] {2, 2};
	            		yourAttackNum = 2;
	            	}
	            	
	            	b.putString("data", Base64.encodeBytes(RSAEncryption.rsaEncrypt(sendInfo)));
	            	if(type.equals("client")) {
	            		msg = Message.obtain(null, SocketService.MSG_ATTACK);
	            	}
	            	else {
	            		msg = Message.obtain(null, SocketServerService.MSG_ATTACK);
	            	}
	            	msg.setData(b);
	                msg.replyTo = mMessenger;
	                mService.send(msg);
	                
	                if(yourAttackNum > -1 && enemyAttackNum > -1) {
	                	executeTurn();
	                }
	                else {
	                	battleMsg.setText(yourCreature.getAttack(yourAttackNum).getName() + " chosen!");
	                }
	                
	            } catch (RemoteException e) {
	                // In this case the service has crashed before we could even do anything with it
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
        
         attack1.setOnClickListener(attackListner);
         attack2.setOnClickListener(attackListner);
         attack3.setOnClickListener(attackListner);
    }
    
    private void updateHealth() {
    	yourHealth.setText(yourCreature.getHealth() + "/" + yourCreature.getMaxHealth());
    	enemyHealth.setText(enemyCreature.getHealth() + "/" + enemyCreature.getMaxHealth());
    }
    
    private void executeTurn() {
    	yourAttack = yourCreature.getAttack(yourAttackNum);
    	enemyAttack = enemyCreature.getAttack(enemyAttackNum);
    	
    	Random rand = new Random(seed);
    	
		Creature goesFirst = (rand.nextInt(100) < (yourCreature.getSpeed() / ((float)yourCreature.getSpeed() + enemyCreature.getSpeed())) * 100) ? yourCreature : enemyCreature;

		System.out.println(goesFirst.getName() + " goes first");

		if(goesFirst == yourCreature) {
			if(yourAttack.getTarget() == Target.OPPONENT) {
				if(rand.nextInt(100) < yourCreature.getAccuracy()) {
					Toast.makeText(getApplicationContext(), yourAttack.getEffectDesc().replace("ENEMYNAME", enemyCreature.getName()), Toast.LENGTH_SHORT).show();
					enemyCreature.takeDamage(yourCreature.getBaseAttack() + yourAttack.getBaseDamage());
				}
				else {
					Toast.makeText(getApplicationContext(), "Wyvern's attack missed!", Toast.LENGTH_SHORT).show();
				}
			}
			else {
				Toast.makeText(getApplicationContext(), yourAttack.getEffectDesc().replace("ENEMYNAME", enemyCreature.getName()), Toast.LENGTH_SHORT).show();
				yourCreature.takeDamage(yourAttack.getBaseDamage());
			}
		}

		Toast.makeText(getApplicationContext(), enemyAttack.getEffectDesc().replace("ENEMYNAME", yourCreature.getName()), Toast.LENGTH_SHORT).show();

		if(enemyAttack.getTarget() == Target.OPPONENT) {
			yourCreature.takeDamage(enemyCreature.getBaseAttack() + enemyAttack.getBaseDamage());
			if(enemyAttack.getEffect() != null) {
				yourCreature.addEffect(enemyAttack.getEffect());
			}
		}
		else {
			enemyCreature.takeDamage(enemyAttack.getBaseDamage());
		}

		if(goesFirst == enemyCreature) {
			if(yourAttack.getTarget() == Target.OPPONENT) {
				if(rand.nextInt(100) < yourCreature.getAccuracy()) {
					Toast.makeText(getApplicationContext(), yourAttack.getEffectDesc().replace("ENEMYNAME", enemyCreature.getName()), Toast.LENGTH_SHORT).show();
					enemyCreature.takeDamage(yourCreature.getBaseAttack() + yourAttack.getBaseDamage());
				}
				else {
					Toast.makeText(getApplicationContext(), "Wyvern's attack missed!", Toast.LENGTH_SHORT).show();
				}
			}
			else {
				Toast.makeText(getApplicationContext(), yourAttack.getEffectDesc().replace("ENEMYNAME", enemyCreature.getName()), Toast.LENGTH_SHORT).show();
				yourCreature.takeDamage(yourAttack.getBaseDamage());
			}
		}

//		String[] yourEffects = yourCreature.applyEffects();
//		String[] enemyEffects = enemyCreature.applyEffects();
		
//		for(String effectStr : yourEffects) {
//			Toast.makeText(getApplicationContext(), effectStr, Toast.LENGTH_SHORT).show();
//		}
//		
//		for(String effectStr : enemyEffects) {
//			Toast.makeText(getApplicationContext(), effectStr, Toast.LENGTH_SHORT).show();
//		}
		
		updateHealth();
		
		if(!yourCreature.isAlive() && !enemyCreature.isAlive()) {
			battleMsg.setText("Both creatures have been defeated! It's a tie!");
	        attack1.setEnabled(false);
	        attack2.setEnabled(false);
	        attack3.setEnabled(false);
		}
		else if(!yourCreature.isAlive()) {
			battleMsg.setText(yourCreature.getName() + " has been defeated! You lose!");
	        attack1.setEnabled(false);
	        attack2.setEnabled(false);
	        attack3.setEnabled(false);
		}
		else if(!enemyCreature.isAlive()) {
			battleMsg.setText(enemyCreature.getName() + " has been defeated! You win!");
	        attack1.setEnabled(false);
	        attack2.setEnabled(false);
	        attack3.setEnabled(false);
		}
		else {
			battleMsg.setText("Choose an attack!");
		}
		
		yourAttackNum = -1;
		enemyAttackNum = -1;
    }
	    
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	    	Bundle b = msg.getData();
	    	String data = b.getString("data");
	    	try {
				byte[] getInfo = RSAEncryption.rsaDecrypt(Base64.decode(data));

				if(getInfo[0] == 2) {
	            	if(getInfo[1] == 0) {
	            		enemyAttackNum = 0;
	            	}
	            	else if(getInfo[1] == 1) {
	        			enemyAttackNum = 1;
	            	}
	            	else {
	            		enemyAttackNum = 2;
	            	}
					
	            	if(yourAttackNum > -1 && enemyAttackNum > -1) {
	            		executeTurn();
	                }
	                else {
	                	battleMsg.setText("Opponent has chosen attack. Choose an attack!");
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
            		msg = Message.obtain(null, SocketService.MSG_ATTACK);
            	}
            	else {
            		msg = Message.obtain(null, SocketServerService.MSG_ATTACK);
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
                		msg = Message.obtain(null, SocketService.MSG_ATTACK);
                 	}
                 	else {
                		msg = Message.obtain(null, SocketServerService.MSG_ATTACK);
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