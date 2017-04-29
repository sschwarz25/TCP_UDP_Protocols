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
	    
	    int count = 1;
	    
	    while ((count = in.read (bufferSize)) > 0) {
		out.write ( bufferSize, 0, count );
	    }
	    
	} catch ( UnknownHostException e ) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	
	} catch ( IOException e ) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static byte[][] Server ( int portNumber, String fileName ) {

	try ( ServerSocket server = new ServerSocket ( portNumber );
		Socket socket = server.accept ();
		InputStream in = socket.getInputStream ();
		OutputStream out = new FileOutputStream ( fileName ); 
	) {
	    
	    int count = 1;
	    while (( count = in.read (bufferSize)) > 0){
		out.write ( bufferSize, 0, count );
	    }

	} catch ( IOException e ) {
	    // TODO Auto-generated catch block
	    e.printStackTrace ();
	}

	return null;
    }
}
