import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TCP {

    public static ArrayList<byte[]> Server(int portNumber) {
    	// Concept, explanation, and tutorial @
    	// http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html#later
    	// Not copied, concept and tutorial followed
    		
    	try(	// Using Try/Catch allows for clean, easy socket closure (automated)
    		ServerSocket socket = new ServerSocket( portNumber ); //Secure Socket for server
    		Socket connection = socket.accept(); //Wait for client to connect
    		PrintWriter toClient = new PrintWriter( connection.getOutputStream(), true ); //Get Output stream ready
    		BufferedReader fromClient = new BufferedReader( new InputStreamReader( connection.getInputStream() ));
    	){
    		CommunicationProtocol commsProtocol = new CommunicationProtocol();
    		String messageToClient = commsProtocol.ProcessInput_Server( null );			
    		String messageFromClient = "";
    		ArrayList<byte[]> data = new ArrayList<byte[]>();
    		Boolean	initFlag = true;
    			
    		toClient.println( messageToClient );
    			
    		while(( messageFromClient = fromClient.readLine() ) != null ) {
    		    if ( commsProtocol.getServerState() == commsProtocol.getSendData() ) {
    			if ( initFlag ) {																//Get ArrayList to proper size
    			    initFlag = false;

    			    for( int i = 0; i < commsProtocol.getPacketCount (); i++){
    				data.add(null);
    			    }
    			}

    			String[] tokens = messageFromClient.split( "\\s+" );

			try {
    				int sequenceNumber= Integer.parseInt( tokens[0] );
    				if(( sequenceNumber % 10) == 1) {
    				    //TODO: Check 10 spots and Ack Back
    				}
    						
    				if ( data.get ( sequenceNumber ) == null ){
    				    data.set ( sequenceNumber, tokens[1] );
    				}
    			} catch ( NumberFormatException ne ) {
    				System.out.println("Packet not in proper format: " + ne);
    			}
    		    }
    		}
    				
    		System.out.println( messageFromClient );
    				
    		messageToClient = commsProtocol.ProcessInput_Server( messageFromClient );				
    				
    		//TODO: Completed Transfer
    				
    		toClient.println( messageToClient );				
    				
    		} catch ( IOException e ) {
    			System.out.println( "Exception thrown when trying to listen on port " + portNumber );
    			System.out.println( e.getMessage() );
    		}
	}

    public static void Client ( String IP, int portNumber, ArrayList<byte[]> data ) {

	try (
		Socket connection = new Socket ( IP, portNumber );
		PrintWriter toServer = new PrintWriter ( connection.getOutputStream (), true );
		BufferedReader fromServer = new BufferedReader ( new InputStreamReader ( connection.getInputStream () ) );
	) {
	    CommunicationProtocol commsProtocol = new CommunicationProtocol ();
	    String messageFromServer = "";
	    String messageToServer = "";

	    while ( ( messageFromServer = fromServer.readLine () ) != null ) {
		System.out.println ( "Server: " + messageFromServer );

		messageToServer = commsProtocol.ProcessInput_Client ( messageFromServer );

		if ( messageToServer == "Packets" ) {
		    messageToServer = Integer.toString ( data.size() );
		}

		toServer.println ( messageToServer );
	    }

	} catch ( UnknownHostException e ) {
	    System.out.println ( "Cannot find Host Server: " + e);

	} catch ( IOException e ) {
	    System.out.println ( "I/O Connection not established: " + e);
	}
    }
}
