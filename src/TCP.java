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
    	
	ArrayList<byte[]> data = null;
	
    	try(	
    		ServerSocket 	socket 		= new ServerSocket( portNumber );
    		Socket 		connection 	= socket.accept();
    		PrintWriter 	toClient 	= new PrintWriter( connection.getOutputStream(), true );
    		BufferedReader 	fromClient 	= new BufferedReader( new InputStreamReader( connection.getInputStream() ));
    	){
    		CommunicationProtocol 	commsProtocol 		= new CommunicationProtocol();
    		String 			messageToClient 	= commsProtocol.ProcessInput_Server( null );			
    		String 			messageFromClient 	= "";
    					data 			= new ArrayList<byte[]>(null);
    		ArrayList<Boolean>	sequenceVerification	= null;	
    		toClient.println( messageToClient );
    			
    		while(( messageFromClient = fromClient.readLine() ) != null ) {
    		    System.out.println ( messageFromClient );
    		    
    		    messageToClient = commsProtocol.ProcessInput_Server ( messageFromClient );
    		    
    		    //PackAck - Setup Verification Array
    		    if( messageToClient.contains ( commsProtocol.getPacketCountAck () )){
    			sequenceVerification = new ArrayList<Boolean>(commsProtocol.getPacketCount ());
    			for(int i = 0; i < sequenceVerification.size (); i ++){
    			    sequenceVerification.set ( i, false );
    			}
    		    }
    		    
    		    //TODO: Tell Client to SEND DATA
    		    	//TODO: Split String into SeqNo and Byte Data
    		    	//TODO: Convert Byte Data String to byte[]
    		    	//TODO: Store Packet and Mark Off SeqNo
    		    	//TODO: ACK every 10 packets
    		    //TODO: Completed Transfer
    		    	//TODO: Check and ack all packets - FINAL ACK
    		    	//TODO: Receive checksum 3 times from Client with CHECKSUMACK each time.
    		    	//TODO: Triple Verify File
    		    	//TODO: Return and close out.
    		    
    		    //SEND RECIEVE DATA
    		    if ( commsProtocol.getServerState() == commsProtocol.getSendDataState() ) {
    			
    			String[] tokens = messageFromClient.split( "\\s+" );
    			byte[] packetBytes = tokens[1].getBytes ();
    			
			try {
    				int sequenceNumber= Integer.parseInt( tokens[0] );
    				if(( sequenceNumber % 10) == 1) {
    				    //TODO: Check 10 spots and Ack Back
    				}
    						
    				if ( data.get ( sequenceNumber ) == null ){
    				    data.set ( sequenceNumber, packetBytes );
    				}
    			} catch ( NumberFormatException ne ) {
    				System.out.println("Packet not in proper format: " + ne);
    			}
    		    }
    		} 				
    		    		
    		} catch ( IOException e ) {
    			System.out.println( "Exception thrown when trying to listen on port " + portNumber );
    			System.out.println( e.getMessage() );
    		}
	return data;
	}

    public static void Client ( String IP, int portNumber, ArrayList<byte[]> data ) {

	try (
		Socket connection = new Socket ( IP, portNumber );
		PrintWriter toServer = new PrintWriter ( connection.getOutputStream (), true );
		BufferedReader fromServer = new BufferedReader ( new InputStreamReader ( connection.getInputStream () ) );
	) {
	    CommunicationProtocol commsProtocol = new CommunicationProtocol ();
	    commsProtocol.setPacketCount ( data.size () );
	    
	    String messageFromServer = "";
	    String messageToServer = "";

	    while ( ( messageFromServer = fromServer.readLine () ) != null ) {
		System.out.println ( "Server: " + messageFromServer );

		messageToServer = commsProtocol.ProcessInput_Client ( messageFromServer );
		
		if ( messageToServer.contains ( commsProtocol.getSendDataString () )) {
		    //TODO: Convert byte packet into String
		    //TODO: Concat byte string to SeqNo with space delimiter
		    //TODO: Send Packets in sets of 10
		    //TODO: At 10 packets, wait for ACK
		    //TODO: If not ACK in 1 second, resend 10 packets
		    //TODO: If ACKBAD, resend 10 packets
		    //TODO: If ACK N%10 = 0, send next 10 packets
		    //TODO: Once all packets are sent, send checksum 1
		    //TODO: on ACKCHECKSUM1, send checksum 2
		    //TODO: on ACKCHECKSUM2, send checksum 3
		    //TODO: on ACKCHECKSUM3, send COMPLETE
		    //TODO: on ACKCOMPLETE, close client
		    //TODO: If no ACKCOMPLETE in 5 seconds, Server must have reset for resend
		    	//TODO: Restart Process
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
