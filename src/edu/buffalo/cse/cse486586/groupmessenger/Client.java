package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

import android.os.AsyncTask;
import android.util.Log;

public class Client extends AsyncTask<String, Void, Void> {

	int port;
	Message message;
	
	

	@Override
	protected Void doInBackground(String... msgs) {

		try {
			
			if(GroupMessenger.connectionFlag==0){
				makeConnections();
				GroupMessenger.connectionFlag=1;
			}else{
				System.out.println("Make connection working fine ");
			}

			GroupMessenger.seq_oostream.writeObject(new Message(Message.MSG_SEQ));
			System.out.println("Client step 1 ");
			message = (Message) GroupMessenger.seq_oistream.readObject();
			System.out.println("Client step 2 :KEY:"+message.getKey());
			message.setMessage(msgs[0]);
			System.out.println("Client step 3 :MSG:"+message.getMessage());
			message.setType(Message.MSG_NRM);
			System.out.println("Client step 4 :TYPE:"+message.getType());

			multicast();

		} catch (UnknownHostException e) {
			Log.v("ERROR", "In client creation");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("SERVER IMPL class : error in read object");
		}catch (IOException e) {
			Log.v("ERROR", "In client creation");
		}

		return null;
	}
	
	protected void makeConnections() {		
		try {
			System.out.println("preparing to make connection");
			
			GroupMessenger.seqSocket=new Socket(GroupMessenger.IP, 11108);
			GroupMessenger.seq_oostream = new ObjectOutputStream(GroupMessenger.seqSocket.getOutputStream());
			GroupMessenger.seq_oistream = new ObjectInputStream(GroupMessenger.seqSocket.getInputStream());
			Node n1=new Node(GroupMessenger.seqSocket,GroupMessenger.seq_oostream ,GroupMessenger.seq_oistream );
			System.out.println("connection 1 made");
			Data.out_conn.add(n1);
			
			Socket socket2=new Socket(GroupMessenger.IP, 11112);
			System.out.println("connection 2 made");
			Node n2=new Node(socket2, new ObjectOutputStream(socket2.getOutputStream()), new ObjectInputStream(socket2.getInputStream()));
			Data.out_conn.add(n2);
			
			Socket socket3=new Socket(GroupMessenger.IP, 11116);
			System.out.println("connection 3 made");
			Node n3=new Node(socket3, new ObjectOutputStream(socket3.getOutputStream()), new ObjectInputStream(socket3.getInputStream()));
			Data.out_conn.add(n3);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("1: ERROR in MAKE CONNECTIONS");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("2: ERROR in MAKE CONNECTIONS");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("3: ERROR in MAKE CONNECTIONS");
		}
	}

	private void multicast() {

		System.out.println("in Multi cast ");
		Iterator<Node> it=Data.out_conn.iterator();

		while(it.hasNext()){
			try {
				it.next().getOostream().writeObject(message);
				System.out.println("Multicasting");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(" Error in MULTICAST of message");
			}

		}

	}
}