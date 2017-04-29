import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;

public class UDP {
    private final static String saveFilePath = "C:/data/down";

    public static void Server ( int portNumber ) throws IOException {

	DatagramSocket server = new DatagramSocket ( portNumber );
	byte[] data = new byte[1024];

	System.out.println ( "Server is ready. Waiting for packets..." );
	DatagramPacket receivedPacket = new DatagramPacket ( data, data.length );

	server.receive ( receivedPacket );

	String firstPacketStr = new String ( receivedPacket.getData (), 0, receivedPacket.getLength () );
	StringTokenizer token = new StringTokenizer ( firstPacketStr, " " );
	String filename = token.nextToken ();
	int bytesOfFile = new Integer ( token.nextToken () ).intValue ();
	System.out.println ( "Client will send a file named '" + filename + "' with file bytes(" + bytesOfFile + ")" );

	String replyMsg = "OK";
	DatagramPacket sendPacket = new DatagramPacket ( replyMsg.getBytes (), replyMsg.length (),
		receivedPacket.getAddress (), receivedPacket.getPort () );
	server.send ( sendPacket );

	if ( !new File ( saveFilePath ).exists () )
	    new File ( saveFilePath ).mkdirs ();

	FileOutputStream writer = new FileOutputStream ( saveFilePath + "/" + filename );

	int bytesReceived = 0;
	while ( bytesReceived < bytesOfFile ) {
	    server.receive ( receivedPacket );
	    writer.write ( receivedPacket.getData (), 0, receivedPacket.getLength () );
	    bytesReceived += receivedPacket.getLength ();
	}
	server.close ();
	System.out.println ( "File is completely received" );

    }

    public static void Client ( String IP, int portNumber, File file ) throws IOException {

	DatagramSocket client = new DatagramSocket ();
	byte[] data = new byte[1024];
	DatagramPacket sendPacket;
	client.connect ( InetAddress.getByName ( IP ), portNumber );

	FileInputStream reader = new FileInputStream ( file );
	int fileLength = reader.available ();

	System.out.println ( "Filename: " + file.getName () );
	System.out.println ( "File bytes to send: " + fileLength );
	String fileDis = file.getName () + " " + fileLength;
	sendPacket = new DatagramPacket ( fileDis.getBytes (), fileDis.getBytes ().length );
	client.send ( sendPacket );

	DatagramPacket receivedPacket = new DatagramPacket ( data, data.length );
	client.receive ( receivedPacket );

	if ( new String ( receivedPacket.getData (), 0, receivedPacket.getLength () ).equals ( "OK" ) ) {
	    System.out.println ( "Server has responsed, start sending the file content" );
	    int currentPos = 0;
	    int bytesRead;
	    while ( currentPos < fileLength ) {
		bytesRead = reader.read ( data );
		System.out.println ( bytesRead );

		if ( fileLength - currentPos < 1024 )
		    sendPacket = new DatagramPacket ( data, fileLength - currentPos );
		else
		    sendPacket = new DatagramPacket ( data, data.length );
		client.send ( sendPacket );
		currentPos += bytesRead;
	    }

	    System.out.println ( "File is completely sent." + currentPos );
	}
	// Else print a error message
	else {
	    System.out.println ( "No response from server" );
	}
	client.close ();
    }
}
