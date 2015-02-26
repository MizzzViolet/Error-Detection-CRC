/**
 * 
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import tcdIO.*;

/**
 *
 * Sending side of a communication channel.
 *
 * The class provides the basic functionality to transmit a datagram to a receiver.
 * The main method acts as test for the class by filling the destination host and port number and the source port number.
 *
 */
/**
 * @author 12307269
 * 
 */
public class Sender {
	static final int DEFAULT_PORT = 50000;
	static final int DEFAULT_DST_PORT = 50001;
	static final String DEFAULT_HOST = "localhost";
	byte divisor[] = { 1, 1, 0, 1 };
	private byte length;
	private byte data[];
	static Terminal terminal;

	DatagramSocket socket;
	InetSocketAddress dstAddress;

	Sender() {
		this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_DST_PORT);
	}

	Sender(String dstHost, int dstPort, int srcPort) {
		try {
			dstAddress = new InetSocketAddress(dstHost, dstPort);
			socket = new DatagramSocket(srcPort);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	byte[] getData() {

		length = terminal.readByte("No. of data items: ");
		data = new byte[length + 1];
		data[0] = length;
		for (int i = 1; i <= length; i++) {
			data[i] = terminal.readByte("Item " + (i) + " : ");
		}
		int totalLength = length + divisor.length ;
		byte rem[], crc[], zeros[];
		rem = new byte[totalLength];
		crc = new byte[totalLength];
		zeros = new byte[totalLength];

		for (int i = 0; i <= data[0]; i++) { /* Dividend after appending 0's */
			rem[i] = data[i];
			crc[i] = data[i];
		}
		for (int i = 0; i < rem.length; i++) { /* divide by zero if needed be */
			zeros[i] = 0;
		}

		terminal.print("Dividend (after appending 0's) are : \n");
		for (int i = 1; i < rem.length; i++) {
			terminal.print(" " + rem[i] + " ");
		}
		terminal.println();
		terminal.print("Remainder:\n");
		rem = divide(zeros,divisor,rem);
		
		for (int i = 1; i < rem.length; i++) {
			terminal.print(" " + rem[i] + " ");
		}
		terminal.println();
		terminal.print("Data sending:\n");
		
		for (int i = (totalLength-(divisor.length - 1)); i < rem.length; i++) {
			crc[i] = rem[i];
		}

		terminal.println();
		return crc;
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
			for (int i = 1; i < rem.length; i++) {
				terminal.print(" " + rem[i] + " ");
			}
			terminal.println();
			for (int i = 0; i < divisor.length; i++) {
				terminal.print(" " + divisor[i] + " ");
			}
			terminal.println();
			cur++;
		}
		return rem;
    }

	/**
	 * Sender Method
	 * 
	 * Transmits single datagram packet to a receiver.
	 */
	void start() {
		byte[] data = null; // data[0..n] are data items
		DatagramPacket packet = null;

		try {
			data = getData(); // fill the data field with something to send
								// method
			terminal.println("Sending packet...");
			packet = new DatagramPacket(data, data.length, dstAddress);
			socket.send(packet);
			terminal.println("Packet sent");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method
	 * 
	 * Sends a packet to a given address
	 * 
	 * @param args
	 *            0, hostname to send to; 1, port number to send to; 2, port
	 *            number to bind to
	 * 
	 */
	public static void main(String[] args) {
		Sender s;
		try {
			String dstHost = args[0];
			int dstPort = Integer.parseInt(args[1]);
			int srcPort = Integer.parseInt(args[2]);

			terminal = new Terminal("Sender");

			s = new Sender(dstHost, dstPort, srcPort);
			s.start();

			terminal.println("Program completed");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}