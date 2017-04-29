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
    
    private static byte[] bufferSize = new byte[1024];

    public static void Client ( String IP, int portNumber, File file ) {
	
	try ( Socket socket = new Socket(IP, portNumber);
	      InputStream in = new FileInputStream ( file );
	      OutputStream out = socket.getOutputStream ();
	) {
	    
	    System.out.println ("Client Connected!");
	    
	    int count = 1;
	    
	    //Come up with Packet Count
	    //Tell Server Packet Count
	    //Get asked file name
	    //Tell Server File Name
	    //
	    
	    while ((count = in.read (bufferSize)) > 0) {
		out.write ( bufferSize, 0, count );
	    }
	    
	} catch ( UnknownHostException e ) {
	    e.printStackTrace();
	
	} catch ( IOException e ) {
	    e.printStackTrace();
	}
    }

    public static byte[][] Server ( int portNumber, String fileName ) {

	try ( ServerSocket server = new ServerSocket ( portNumber );
		Socket socket = server.accept ();
		InputStream in = socket.getInputStream ();
		OutputStream out = new FileOutputStream ( fileName ); 
	) {
	    
	    //Get Packet Count
	    //Ask for File Name
	    //Get File Name
	    
	    int count = 1;
	    while (( count = in.read (bufferSize)) > 0){
		out.write ( bufferSize, 0, count );
	    }

	} catch ( IOException e ) {
	    e.printStackTrace ();
	}

	return null;
    }
}
