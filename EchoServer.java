import java.net.*;

import javax.xml.crypto.Data;

import java.io.*;

public class EchoServer {

	public static void main(String[] args) throws IOException {

		// Check the arguments
		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>");
			System.err.println("port should be a number between 1025 and 65535\n");
			System.exit(1);
		}

		// Parses the port number
		int portNumber = Integer.parseInt(args[0]);

		// Creates buffer and packets
		byte[] incomingData = new byte[1024]; 
		byte[] buffer = new byte[256];
		DatagramPacket incomingPacket = null; 
		DatagramPacket DpReceive = null;
		DatagramPacket DpSend = null;
		DatagramSocket  serverSocket = null;

		try {
			// Creates a DatagramSocket and binds to the specified port on the local machine
			serverSocket = new DatagramSocket (portNumber);
			
		} catch (SocketException e) {
			System.out.println("Socket could not be opened, or the socket could not bind to the specified port " + portNumber );
			System.out.println(e.getMessage());
		}

		// Receives the Student data from the client
		incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		serverSocket.receive(incomingPacket);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(incomingData);
		ObjectInputStream objectStream = new ObjectInputStream(inputStream);
		try {
			Student student = (Student) objectStream.readObject();
			System.out.println("Student information received: "+student);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Sends a reply to the client about the Student data
		InetAddress IPAddress = incomingPacket.getAddress();
		int port = incomingPacket.getPort();
		String reply = "message is received";
		byte[] replyByte = reply.getBytes();
		DatagramPacket replyPacket = new DatagramPacket(replyByte, replyByte.length, IPAddress, port);
		serverSocket.send(replyPacket);
	

		while(true){

			System.out.println("Waiting ....");  
			// clear the buffer
			buffer = new byte[256];

			// Constructs a DatagramPacket for receiving the data of length buffer.length in the byte array buffer 
			DpReceive = new DatagramPacket(buffer, buffer.length);
			
			// The receive method blocks until a message arrives and it stores the message
			// inside the byte array of the DatagramPacket passed to it.
			serverSocket.receive(DpReceive);
			
		    // convert the byte array into a string message  
			String received = new String(DpReceive.getData()).trim();
			// or
			// String received = new String(buffer, 0, DpReceive.getLength());
			

			// Checks if the message is 'exit' to close the server
			if (received.equals("exit")) {
				System.out.println("server received "+ received);
				System.out.println("exiting...");
				System.out.println("---------------------------------" );
					
				// Closes the server socket
				serverSocket.close();
				break;
			}

			// Prints the received message
			System.out.println("server received " +received.length()+" bytes: "+ received);
									
			// Constructs a DatagramPacket to send OK message to the client
			DpSend = new DatagramPacket("OK".getBytes(), "OK".getBytes().length, DpReceive.getAddress(), DpReceive.getPort());

			// Sends the OK message to the client
			serverSocket.send(DpSend);

			// Prints the sent message
			System.out.println("server sent " + new String(DpSend.getData()).trim());
			System.out.println("---------------------------------");
		}
	}
}