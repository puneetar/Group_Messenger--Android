package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class Server extends AsyncTask<ServerSocket, String, Void> {

	@Override
	protected Void doInBackground(ServerSocket... sockets) {
		ServerSocket serverSocket = sockets[0];
		Socket socket=null;

		try{
			while(true){
				Log.v("STARTING SERVER", "123");
				socket = serverSocket.accept();
				Log.v("Server accepting a connection", "123");

				Node n=new Node(socket, new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
				Data.in_conn.add(n);

				new ServerImpl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,n);
				//	socket.close();
			}
		}
		catch(IOException e){
			Log.v("ERROR", "In server creation");
			//e.printStackTrace();
		}
		return null;
	}

	protected void onProgressUpdate(String... strings ){
		super.onProgressUpdate(strings[0]);
		//	TextView textView = (TextView) findViewById(R.id.textView1);
		//textView.append(strings[0] + "\n");
		Log.v("Server reading message", strings[0]);
		return;
	}
}


class ServerImpl extends AsyncTask<Node, String, Void> {

	private Socket socket;
	private ObjectOutputStream oostream;
	private ObjectInputStream oistream;
	private Message message;
	private MonitorDatabase monitor;
	
	//static int counter=0;
	@Override
	protected Void doInBackground(Node... node) {
		monitor=new MonitorDatabase();
		//Socket serverSocket = sockets[0];
		socket=node[0].getSocket();
		oostream=node[0].getOostream();
		oistream=node[0].getOistream();

		while (true) {
			try {
				message = (Message) oistream.readObject();
				if (message.getType() == Message.MSG_SEQ) {
					System.out.println("In seq Message");
					seqMessage();
				} else if (message.getType() == Message.MSG_NRM) {
					System.out.println("In normal message");
					nrmMessage();
				} else {
					System.out.println(" message type cannot be found ***");
				}
			
			} catch (OptionalDataException e) {
				e.printStackTrace();
				System.out.println("SERVER IMPL class : error in read object");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("SERVER IMPL class : error in read object");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("SERVER IMPL class : error in read object");
			}
		}
	}

	private void nrmMessage() {

		if(message.getMessage().endsWith("TEST2\n")){

			for(int i=0;i<2;i++){
				new Client().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,""+GroupMessenger.AVDNAME+":"+(GroupMessenger.test2counter++)+"\n");
			}
			//String value1=value.substring(0, value.indexOf("TESTCASE2"));
			message.setMessage((message.getMessage().substring(0, message.getMessage().indexOf("TEST2\n")))+"\n");
			
		}
		
			ContentValues cv = new ContentValues();
			cv.put(GroupMessenger.KEY_FIELD, message.getKey());
			cv.put(GroupMessenger.VALUE_FIELD, message.getMessage());

			GroupMessenger.myContentResolver.insert(GroupMessenger.myUri, cv);
			
			System.out.println("added normal message in database");
			//monitor.displayMessage();
			publishProgress("hi");
			
	}

	private void seqMessage() {

		message.setKey(""+Data.key++);
		try {
			oostream.writeObject(message);
			System.out.println("sent back the message with key");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: in sending the sequence Message back from server");
		}

	}
	
	


	protected void onProgressUpdate(String... strings ){
		super.onProgressUpdate(strings[0]);
    	monitor.displayMessage();
	
	//	Log.v("Server reading message", strings[0]);
	//	return;
	}
}

class MonitorDatabase {

	static int key=0;
	
	protected void displayMessage() {
		TextView textView = GroupMessenger.tv;

		System.out.println("In Monitor Database");

		while(true){
			Cursor resultCursor = GroupMessenger.myContentResolver.query(GroupMessenger.myUri, null, ""+key, null, null);
			if(resultCursor.moveToFirst()){
				System.out.println("I found a result");

				int keyIndex = resultCursor.getColumnIndex(GroupMessenger.KEY_FIELD);
				int valueIndex = resultCursor.getColumnIndex(GroupMessenger.VALUE_FIELD);
				String ar[]=new String[2];
				ar[0] = resultCursor.getString(keyIndex);
				ar[1] = resultCursor.getString(valueIndex);
				
				//textView.append(ar[0]+":"+ar[1]);
				textView.append(ar[1]);
				
				key++;
			}else{
				System.out.println("monitor Database else block");
				break;
			}
		}
	}
}