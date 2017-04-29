import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCP {

    private static byte[] buffer = new byte[1024];

    public static void Client ( String IP, int portNumber, File file ) {

	try ( Socket socket = new Socket ( IP, portNumber );
		InputStream inFile = new FileInputStream ( file );
		InputStream in = socket.getInputStream ();
		OutputStream out = socket.getOutputStream ();
		ByteArrayOutputStream baos = new ByteArrayOutputStream (); ) {

	    System.out.println ( "Connected to Server!" );

	    int count = 1;

	    // Get Packet Count Figured out
	    int packetCount = ( int ) Math.ceil ( file.length () / buffer.length );

	    // Convert to String, then bytes, then write to socket
	    baos.write ( Integer.toString ( packetCount ).getBytes () );
	    out.write ( baos.toByteArray () );

	    // Console the Packet Size
	    System.out.println ( "Sending " + packetCount + " Packets." );

	    // ------------WAIT ON SERVER

	    // Get Packet Count ACK and Give File Name
	    in.read ( buffer );
	    String bufferString = new String ( buffer );
	    bufferString.trim ();

	    if ( bufferString.contains ( Integer.toString ( packetCount ) ) ) {
		System.out.println ( "Server ACK Packet Count: " + bufferString );

		// Tell Server File Name
		baos.write ( file.getName ().getBytes () );
		out.write ( baos.toByteArray () );

		// Send File
		// while ( ( count = in.read ( buffer ) ) > 0 ) {
		// out.write ( buffer, 0, count );
		// }
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
		InputStream in = socket.getInputStream ();
		OutputStream out = socket.getOutputStream ();
		OutputStream fileOut = new FileOutputStream ( fileName );
		ByteArrayOutputStream baos = new ByteArrayOutputStream (); ) {

	    in.read ( buffer );
	    String packetCountString = new String ( buffer );
	    System.out.println ( "Incoming Packets: " + packetCountString );

	    int packetCount = Integer.parseInt ( packetCountString.trim () );

	    // ACK Packet Count
	    baos.write ( Integer.toString ( packetCount ).getBytes () );
	    out.write ( baos.toByteArray () );

	    // Get File Name
	    in.read ( buffer );
	    String fileNameFromClient = new String ( buffer );
	    fileName.trim ();
	    
	    System.out.println ( "File Name: " + fileNameFromClient );

	    // Prepare Sequence Validation
	    for ( int i = 0; i < packetCount; i++ ) {

	    }

	    int count = 1;
	    while ( ( count = in.read ( buffer ) ) > 0 ) {
		fileOut.write ( buffer, 0, count );
	    }

	} catch ( IOException e ) {
	    e.printStackTrace ();
	}

	return null;
    }
}
