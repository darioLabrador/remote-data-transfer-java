import java.io.*;
import java.net.*;

public class EchoClient {
	public static void main(String[] args) throws IOException {

		// Check the arguments
		if (args.length != 2) {
			System.err.println("Usage: java EchoClient <host name> <port number>");
			System.err.println("host name is server IP address (e.g. 127.0.0.1) ");
			System.err.println("port is a positive number in the range 1025 to 65535\n");
			System.exit(1);
		}

		// Convert the arguments to ensure that they are valid
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		InetAddress ip = InetAddress.getByName(hostName);

		DatagramSocket  clientSocket = null; 
		BufferedReader stdIn = new BufferedReader(	new InputStreamReader(System.in));  

		try {
			//Creates a datagramSocket and binds it to any available port on local machine
			clientSocket = new DatagramSocket(); 
		} catch (SocketException e) {
			System.err.println("the socket could not be opened, or the socket could not bind to the specified port " +
					portNumber);
			System.exit(1);
		} 	  

		// Creates Student object to be sent to Server
		Student student = new Student(1, "John", "Smith");

		// Serializes the Student object
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
        objectStream.writeObject(student);

		// Sends the Student object to the Server
        byte[] data = outputStream.toByteArray();        
        DatagramPacket sentPacket = new DatagramPacket(data, data.length, ip, portNumber);
        clientSocket.send(sentPacket);

		// Confirms Student object was sent
        System.out.println("Student information is sent");


		String userInput = null; 
		byte buffer[] = null;

		// Incoming message form Server
        byte[] incomingData = new byte[1024];
        DatagramPacket receivedPacket = new DatagramPacket(incomingData, incomingData.length);
        clientSocket.receive(receivedPacket);
        String response = new String(receivedPacket.getData()).trim();
        System.out.println("Response from server: " + response); 

		while (true) {
			System.out.print("please enter the message: ");
			userInput = stdIn.readLine();
			
			
		    // convert the string message into a byte array  
			buffer = userInput.getBytes();
			
			//Constructs a DatagramPacket for sending data at specified address and specified port 
			//The data must be in the form of an array of bytes
			DatagramPacket DpSend = new DatagramPacket(buffer, buffer.length, ip, portNumber);
			
			clientSocket.send(DpSend);  

			// Checks if the user inserted 'exit'
			if (userInput.equals("exit") || userInput.equals("exit ")) {

				System.out.println("Exiting the program...");
				
				// Closes the client socket
				clientSocket.close();
				break;	
			}
			
			//Constructs a DatagramPacket for receiving the data of length buffer.length in the byte array buffer 
			DatagramPacket DpReceive = new DatagramPacket(buffer, buffer.length);

			// Receives the OK response from the server
			clientSocket.receive(DpReceive);
				
			// OK response received				
			System.out.println("client received: " + new String(DpReceive.getData()).trim());
				
		} 	 
	}
}