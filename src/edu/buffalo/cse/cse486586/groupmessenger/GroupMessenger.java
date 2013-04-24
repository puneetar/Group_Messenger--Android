package edu.buffalo.cse.cse486586.groupmessenger;


import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class GroupMessenger extends Activity {

	int port;
	public static String IP ="10.0.2.2",PORTSTR=null,AVDNAME=null;
	public static int SEQ_PORT=11108;
	public static ContentResolver myContentResolver;
	public static Uri myUri;
	public static final String KEY_FIELD = "key";
	public static final String VALUE_FIELD = "value";
	static int connectionFlag=0;
	public static Socket seqSocket;
	public static ObjectOutputStream seq_oostream;
	public static ObjectInputStream seq_oistream;
	public static TextView tv;
	public static int test1counter=0;
	public static int test2counter=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.deleteDatabase(Database.DATABASE_NAME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_messenger);

		//	System.out.println("right place");
		//ContentDataProvider contentdataprovider= new ContentDataProvider();
		//	System.out.println("right place");
		myUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
		myContentResolver = getContentResolver();



		TelephonyManager tel =(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		PORTSTR = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);

		if(PORTSTR.equalsIgnoreCase("5554"))
			AVDNAME="avd0";
		else if(PORTSTR.equalsIgnoreCase("5556"))
			AVDNAME="avd1";
		else if(PORTSTR.equalsIgnoreCase("5558"))
			AVDNAME="avd2";
		else{
			AVDNAME="avd?";
			System.out.println("CANNOT ASSIGN AVD NAME");
		}


		try{
			ServerSocket serverSocket = new ServerSocket (10000);
			new Server().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , serverSocket);
		}catch(Exception e){
			Log.v("ERROR", "In server creation");
		}

		tv = (TextView) findViewById(R.id.textView1);
		tv.setMovementMethod(new ScrollingMovementMethod());

		findViewById(R.id.button1).setOnClickListener(
				new OnPTestClickListener(tv, getContentResolver()));

		final EditText editText = (EditText)findViewById(R.id.editText1);
	
//		editText.setOnKeyListener(new OnKeyListener(){ 
//			public boolean onKey(View v , int keyCode, KeyEvent event){
//				if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER)){
//					new Client().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,""+editText.getText().toString()+"\n");
//					editText.setText("");
//					System.out.println("back to MAIN for next input after ON KEY event ");
//				}
//				return false;
//			}
//		});


		findViewById(R.id.button4).setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View arg0) {


						new Client().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,""+editText.getText().toString()+"\n");
						editText.setText("");
						System.out.println("back to MAIN for next input after OnClick ");
					}

				});


		findViewById(R.id.button2).setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View arg0) {

						//	new Test1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

						for(int i=0;i<5;i++){

							new Client().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,""+AVDNAME+":"+(GroupMessenger.test1counter++)+"\n");
							try {
								Thread.sleep(3000);

							} catch (InterruptedException e) {
								e.printStackTrace();
								System.out.println("Thread cannot sleep");
							}
						}

					}

				});
		//	new MonitorDatabase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		findViewById(R.id.button3).setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View arg0) {

						//			new Test2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

						new Client().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,""+AVDNAME+":"+(GroupMessenger.test2counter++)+"TEST2\n");
					}

				});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
		return true;
	}

	protected void onDestroy (){
		this.deleteDatabase(Database.DATABASE_NAME);
		ContentDataProvider.context.deleteDatabase(Database.DATABASE_NAME);
	}



	private Uri buildUri(String scheme, String authority) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}

	private class Test1 extends AsyncTask<Void,	Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {

			for(int i=0;i<5;i++){

				new Client().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,""+AVDNAME+":"+(GroupMessenger.test1counter++)+"\n");
				try {
					Thread.sleep(3000);

				} catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println("Thread cannot sleep");
				}
			}

			return null;
		}

	}


	private class Test2 extends AsyncTask<Void,	Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {

			new Client().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,""+AVDNAME+":"+(GroupMessenger.test2counter++)+"TEST2\n");

			return null;
		}

	}
}

