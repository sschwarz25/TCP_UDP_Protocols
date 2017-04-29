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
		InputStream in = new FileInputStream ( file );
		OutputStream out = socket.getOutputStream ();
		ByteArrayOutputStream baos = new ByteArrayOutputStream (); ) {

	    System.out.println ( "Connected to Server!" );

	    int count = 1;
	    int packetCount = ( int ) Math.ceil ( file.length () / buffer.length );

	    baos.write ( Integer.toString ( packetCount ).getBytes () );
	    byte[] packetCountByteString = baos.toByteArray ();
	    out.write ( packetCountByteString );

	    System.out.println ("Sending " + packetCount + " Packets.");
	    
	    // Get asked file name
	    // Tell Server File Name
	    //

	    while ( ( count = in.read ( buffer ) ) > 0 ) {
		out.write ( buffer, 0, count );
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
		OutputStream out = new FileOutputStream ( fileName ); 
		ByteArrayOutputStream baos = new ByteArrayOutputStream (); ) {

	    in.read ( buffer );

	    String bufferString = new String ( buffer );
	    System.out.println ( "Incoming Packets: " + bufferString );
	    
	    int packetCount = Integer.parseInt ( bufferString );
	    
	    System.out.println ( packetCount );

	    // Ask for File Name
	    // Get File Name

	    int count = 1;
	    while ( ( count = in.read ( buffer ) ) > 0 ) {
		out.write ( buffer, 0, count );
	    }

	} catch ( IOException e ) {
	    e.printStackTrace ();
	}

	return null;
    }
}
