/**
 * 
 */
// package cs.tcd.ie;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import tcdIO.*;

/**
 * Receiving side of a communication channel.
 *
 * The class provides the basic functionality to receive a datagram from a sender.
 * The main method acts as test for the class by filling the port number at which to receive the datagram.
 */
public class Receiver {

	static final int DEFAULT_PORT = 50001;
	static Terminal terminal;
	byte divisor[] = { 1, 1, 0, 1 };
	DatagramSocket socket;
	InetSocketAddress dstAddress;
	
	/**
	 * Constructor
	 * 
	 */
	Receiver() {
		this(DEFAULT_PORT);
	}
	

	/**
	 * Constructor
	 * 	 
	 * Attempts to create socket at given port
	 */
	Receiver(int port) {
		try {
			socket= new DatagramSocket(port);
		}
		catch(java.lang.Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Attempts to print the given array of bytes, expecting the number of 
	 * data items in the first byte.
	 * 
	 * @param data byte array to print
	 */
	void printContent(byte[] data) {
		terminal.println("Packet length:" + data.length);
		terminal.println("No. of data items received: "+ data.length);
		terminal.println("Data items: ");
		for(int i= 1; i<(data[0]+divisor.length); i++) {
			terminal.print(""+data[i]+" ");
		}
		terminal.println();
		int totalLength = data[0]+divisor.length-1;
		byte zeros[] = new byte[totalLength];
		for (int i = 0; i < totalLength; i++) { /* divide by zero if needed be */
				zeros[i] = 0;
				terminal.print(""+zeros[i]+"");
		}
		terminal.println();
		terminal.println("Result:");
		data = divide(zeros,divisor,data);
		
		for(int i= 1; i<=totalLength; i++) {
			terminal.print(""+data[i]+" ");
		}
		terminal.println();
		
	}
	
	/** crc calculation **/
    static byte[] divide(byte zeros[],byte divisor[], byte rem[])
    {
    	int cur = 1;
		while ((rem.length - cur) >= divisor.length) {

			for (int i = 0; i < divisor.length; i++) {

				if (i == 0 && (rem[cur + i] == 0)) {
					rem[cur + i] = (byte) (rem[cur + i] ^ zeros[i]);
				} else {
					rem[cur + i] = (byte) (rem[cur + i] ^ divisor[i]);
				}
			}

			cur++;
		}
		return rem;
    }

	
	/**
	 * Receiver Method
	 * 
	 * Attempts to receive a single datagram.
	 */
	void start() {
		byte[] data;
		DatagramPacket packet;
		
		try {
			data= new byte[1024];
			packet= new DatagramPacket(data, data.length);
			terminal.println("Waiting for packet...");
			socket.receive(packet);			
			terminal.println("Received packet");
			printContent(data);
		}
		catch(java.lang.Exception e) {
			e.printStackTrace();
		}		
	}


	/**
	 * Test method
	 * 
	 * Creates a socket and attempts to receive a packet at a given port number
	 * 
	 * @param args arg[0] Port number to receive information on
	 */
	public static void main(String[] args) {
		Receiver r;
		
		try {
			terminal= new Terminal("Receiver");
			// int port= Integer.parseInt(args[0]);
			int port = 50001;
			r= new Receiver(port);	
			r.start();

			terminal.println("Program completed");
		} catch(java.lang.Exception e) {
			e.printStackTrace();
		}
	}

}