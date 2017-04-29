import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCP {

    private static byte[] buffer   = new byte[1024];
    static String	  filePath = "C:\\Test\\";

    public static void Client ( String IP, int portNumber, File file ) {

	try ( Socket socket = new Socket ( IP, portNumber );
		InputStream inFile = new FileInputStream ( file );
		BufferedReader in = new BufferedReader ( new InputStreamReader ( socket.getInputStream () ) );
		PrintWriter out = new PrintWriter ( socket.getOutputStream (), true ); ) {

	    System.out.println ( "Connected to Server!" );

	    // Get Packet Count Figured out
	    int packetCount = ( int ) Math.ceil ( file.length () / buffer.length );

	    // Convert to String, then bytes, then write to socket
	    out.println ( packetCount );

	    // Console the Packet Size
	    System.out.println ( "Sending " + packetCount + " Packets." );

	    // ------------WAIT ON SERVER

	    // Get Packet Count ACK and Give File Name
	    String bufferString = in.readLine ();

	    if ( bufferString.contains ( Integer.toString ( packetCount ) ) ) {
		System.out.println ( "Server ACK Packet Count: " + bufferString );

		// Tell Server File Name
		out.println ( file.getName () );

		// Send File
		int seqNo = 0;
		while ( ( inFile.read( buffer ) ) > 0 ) {
		    out.println (seqNo + " " + new String(buffer) );
		    seqNo++;
		}
	    }

	} catch ( UnknownHostException e ) {
	    e.printStackTrace ();

	} catch ( IOException e ) {
	    e.printStackTrace ();
	}
    }

    public static byte[][] Server ( int portNumber, String fileName ) {

	try ( ServerSocket server = new ServerSocket ( portNumber );
		Socket socket = server.accept ();
		BufferedReader in = new BufferedReader ( new InputStreamReader ( socket.getInputStream () ) );
		PrintWriter out = new PrintWriter ( socket.getOutputStream (), true );
		OutputStream fileOut = new FileOutputStream ( fileName ); ) {

	    String packetCountString = in.readLine ();
	    System.out.println ( "Incoming Packets: " + packetCountString );

	    int packetCount = Integer.parseInt ( packetCountString.trim () );

	    // ACK Packet Count
	    out.println ( packetCount );

	    // Get File Name
	    String fileNameFromClient = in.readLine ();

	    System.out.println ( "File Path: " + filePath + fileNameFromClient );

	    // Send Data
	    String[] data = new String[packetCount];
	    for ( int i = 0; i < packetCount; i++ ) {
		data[i] = in.readLine ();
		System.out.println ( data[i] );
	    }

	    int count = 1;
	    String read = "";

	    while ( ( read = in.readLine () ) != null ) {
		fileOut.write ( read.getBytes (), 0, count );
	    }

	} catch ( IOException e ) {
	    e.printStackTrace ();
	}

	return null;
    }
}
