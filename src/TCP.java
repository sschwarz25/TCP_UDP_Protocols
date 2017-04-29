import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TCP {

    public static void Client ( String IP, int portNumber, ArrayList<byte[]> data ) {

	try ( Socket connection = new Socket ( IP, portNumber );
		PrintWriter toServer = new PrintWriter ( connection.getOutputStream (), true );
		BufferedReader fromServer = new BufferedReader (
			new InputStreamReader ( connection.getInputStream () ) ); ) {
	    CommunicationProtocol commsProtocol = new CommunicationProtocol ();
	    commsProtocol.setPacketCount ( data.size () );

	    String messageFromServer = "";
	    String messageToServer = "";

	    while ( ( messageFromServer = fromServer.readLine () ) != null ) {
		System.out.println ( "Server: " + messageFromServer );

		messageToServer = commsProtocol.ProcessInput_Client ( messageFromServer );

		if ( messageToServer.contains ( commsProtocol.getSendDataString () ) ) {

		    ArrayList<String> byteStrings = new ArrayList<String> ( data.size () );

		    int sequenceNumber = 0;

		    for ( int i = 0; i < byteStrings.size (); i++ ) {
			byteStrings.set ( i, sequenceNumber + " " + data.get ( i ) );
			toServer.println ( byteStrings.get ( i ) );
			System.out.println ( byteStrings.get ( i ) );
		    }

		    messageToServer = commsProtocol.getComplete ();

		}

		toServer.println ( messageToServer );
	    }

	} catch ( UnknownHostException e ) {
	    System.out.println ( "Cannot find Host Server: " + e );

	} catch ( IOException e ) {
	    System.out.println ( "I/O Connection not established: " + e );
	}
    }

    public static ArrayList<byte[]> Server ( int portNumber ) {

	ArrayList<byte[]> data = null;

	try ( ServerSocket socket = new ServerSocket ( portNumber );

		Socket connection = socket.accept ();
		PrintWriter toClient = new PrintWriter ( connection.getOutputStream (), true );
		BufferedReader fromClient = new BufferedReader (
			new InputStreamReader ( connection.getInputStream () ) ); ) {

	    System.out.println ( "Client connected." );

	    CommunicationProtocol commsProtocol = new CommunicationProtocol ();

	    String messageToClient = commsProtocol.ProcessInput_Server ( "" );
	    String messageFromClient = "";

	    data = new ArrayList<byte[]> ();

	    ArrayList<Boolean> sequenceVerification = null;

	    toClient.println ( messageToClient );

	    while ( ( messageFromClient = fromClient.readLine () ) != null ) {
		System.out.println ( messageFromClient );

		if ( commsProtocol.getServerState () == commsProtocol.getSendDataState () ) {
		    if ( messageFromClient.contains ( commsProtocol.getComplete () ) ) {

			Boolean verified = true;

			for ( int i = 0; i < sequenceVerification.size (); i++ ) {
			    if ( sequenceVerification.get ( i ) == false ) {
				verified = false;
			    } else {
				System.out.println ( "Lost Packet: " + i );
			    }
			}

			if ( verified ) {
			    return data;
			} else {
			    System.out.println ( "Lost Packets" );
			}

		    } else {
			String[] tokens = messageFromClient.split ( "\\s+" );
			byte[] packetBytes = tokens[1].getBytes ();

			try {
			    int sequenceNumber = Integer.parseInt ( tokens[0] );

			    if ( ( sequenceNumber % 10 ) == 0 ) {
				// TODO: Check 10 spots and Ack Back
			    }

			    if ( data.get ( sequenceNumber ) == null ) {
				data.set ( sequenceNumber, packetBytes );
				sequenceVerification.set ( sequenceNumber, true );
			    }

			    continue;
			} catch ( NumberFormatException ne ) {
			    System.out.println ( "Packet not in proper format: " + ne );
			}
		    }

		}

		messageToClient = commsProtocol.ProcessInput_Server ( messageFromClient );

		// PackAck - Setup Verification Array
		if ( messageToClient.contains ( commsProtocol.getPacketCountAck () ) ) {
		    sequenceVerification = new ArrayList<Boolean> ( commsProtocol.getPacketCount () );
		    for ( int i = 0; i < sequenceVerification.size (); i++ ) {
			sequenceVerification.set ( i, false );
		    }

		    toClient.println ( messageToClient );
		}

		// TODO: Tell Client to SEND DATA
		// TODO: Split String into SeqNo and Byte Data
		// TODO: Convert Byte Data String to byte[]
		// TODO: Store Packet and Mark Off SeqNo
		// TODO: ACK every 10 packets
		// TODO: Completed Transfer
		// TODO: Check and ack all packets - FINAL ACK
		// TODO: Receive checksum 3 times from Client with CHECKSUMACK
		// each time.
		// TODO: Triple Verify File
		// TODO: Return and close out.
	    }

	} catch ( IOException e ) {
	    System.out.println ( "Exception thrown when trying to listen on port " + portNumber );
	    System.out.println ( e.getMessage () );
	}
	return data;
    }

}
