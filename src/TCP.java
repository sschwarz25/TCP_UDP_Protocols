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
import java.util.ArrayList;

public class TCP {

    private static byte[] buffer   = new byte[512];
    static String	  filePath = "C:\\Test\\";

    public static void Client ( String IP, int portNumber, File file ) {

	try ( Socket socket = new Socket ( IP, portNumber );
		InputStream inFile = new FileInputStream ( file );
		BufferedReader in = new BufferedReader ( new InputStreamReader ( socket.getInputStream () ) );
		PrintWriter out = new PrintWriter ( socket.getOutputStream (), true ); ) {
	    OutputStream outWrite = socket.getOutputStream ();

	    System.out.println ( "Connected to Server!" );

	    // Get Packet Count Figured out
	    int packetCount = ( int ) Math.ceil ( file.length () / buffer.length );

	    // Convert to String, then bytes, then write to socket
	    out.println ( packetCount );

	    // Console the Packet Size
	    System.out.println ( "Sending " + packetCount + " Packets." );

	    // ------------WAIT ON SERVER

	    // Get Packet Count ACK and Give File Name
	    String packetCountString = in.readLine ();

	    if ( packetCountString.contains ( Integer.toString ( packetCount ) ) ) {
		System.out.println ( "Server ACK Packet Count: " + packetCountString );

		// Tell Server File Name
		out.println ( file.getName () );

		// Send File
		int seqNo = 0;
		while ( ( inFile.read ( buffer ) ) > 0 ) {
		    // out.println ( seqNo );
		    outWrite.write ( buffer );
		    seqNo++;
		}
	    }

	} catch ( UnknownHostException e ) {
	    e.printStackTrace ();

	} catch ( IOException e ) {
	    e.printStackTrace ();
	}
    }

    public static byte[][] Server ( int portNumber ) {

	try ( ServerSocket server = new ServerSocket ( portNumber );
		Socket socket = server.accept ();
		BufferedReader in = new BufferedReader ( new InputStreamReader ( socket.getInputStream () ) );
		InputStream inRead = socket.getInputStream ();
		PrintWriter out = new PrintWriter ( socket.getOutputStream (), true ); ) {

	    String packetCountString = in.readLine ();
	    System.out.println ( "Incoming Packets: " + packetCountString );

	    int packetCount = Integer.parseInt ( packetCountString.trim () );

	    // ACK Packet Count
	    out.println ( packetCount );

	    // Get File Name
	    String fileNameFromClient = in.readLine ();

	    System.out.println ( "File Path: " + filePath + fileNameFromClient );

	    // Send Data
	    int c = 0;
	    ArrayList<String> data = new ArrayList<String> ();
	    try ( OutputStream fileOut = new FileOutputStream ( filePath + fileNameFromClient ); ) {
		while ( true ) {
		    c += inRead.read ( buffer );

		    if ( c < 0 )
			break;

		    if ( buffer != " ".getBytes () ) {
			data.add ( new String ( buffer ) );
			fileOut.write ( buffer );
		    }

		    buffer = " ".getBytes ();
		}
	    }

	} catch ( IOException e ) {
	    e.printStackTrace ();
	}

	return null;
    }
}
