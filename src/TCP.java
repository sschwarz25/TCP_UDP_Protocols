import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TCP {

    public static void Client ( String IP, int portNumber, ArrayList<byte[]> data ) {

	try (   Socket            connection	= new Socket ( IP, portNumber );
		PrintWriter       toServer	= new PrintWriter ( connection.getOutputStream (), true );
		BufferedReader    fromServer	= new BufferedReader (
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

		    for ( int i = 0; i < data.size (); i++ ) {
			String byteString = new String ( data.get ( i ) );
			byteStrings.add ( sequenceNumber + " " + byteString );
			sequenceNumber++;
		    }

		    for ( int i = 0; i < data.size (); i++){
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

    public static byte[][] Server ( int portNumber ) {

	byte[][] data = null;

	try ( ServerSocket socket = new ServerSocket ( portNumber );

		Socket connection = socket.accept ();
		PrintWriter toClient = new PrintWriter ( connection.getOutputStream (), true );
		BufferedReader fromClient = new BufferedReader (
			new InputStreamReader ( connection.getInputStream () ) ); ) {

	    System.out.println ( "Client connected." );

	    int sequenceNumber = 0;

	    CommunicationProtocol commsProtocol = new CommunicationProtocol ();

	    String messageToClient = commsProtocol.ProcessInput_Server ( "" );
	    String messageFromClient = "";

	    toClient.println ( messageToClient );

	    while ( ( messageFromClient = fromClient.readLine () ) != null ) {
		System.out.println ( "Client: " + messageFromClient );

		if ( commsProtocol.getServerState () == commsProtocol.getSendDataState () ) {
		    if ( messageFromClient.contains ( commsProtocol.getComplete () ) ) {

			Boolean verified = true;

			for ( int i = 0; i < data.length; i++ ) {
			    if ( data[i] == null ) {
				verified = false;
				System.out.println ( "Lost Packet: " + i );
			    }
			}

			if ( verified ) {
			    return data;

			} else {
			    System.out.println ( "Lost Packets" );
			}

		    } else {
			System.out.println ( messageFromClient );
			String[] tokens = messageFromClient.split ( commsProtocol.getDelimiter (), 2 );
			byte[] packetBytes = tokens[1].getBytes ();

			try {
			    sequenceNumber = Integer.parseInt ( tokens[0] );

			    data[sequenceNumber] = packetBytes;

			    continue;
			} catch ( NumberFormatException ne ) {
			    ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
			    outputStream.write ( data[sequenceNumber] );
			    outputStream.write ( packetBytes );

			    data[sequenceNumber] = outputStream.toByteArray ();
			}
		    }

		}

		messageToClient = commsProtocol.ProcessInput_Server ( messageFromClient );
		if ( messageToClient != null ) {
		    // PackAck - Setup Verification Array
		    if ( messageToClient.contains ( commsProtocol.getPacketCountAck () ) ) {

			data = new byte[commsProtocol.getPacketCount ()][1];

			toClient.println ( messageToClient );
		    }
		}
	    }

	} catch ( IOException e ) {
	    System.out.println ( "Exception thrown when trying to listen on port " + portNumber );
	    System.out.println ( e.getMessage () );
	}
	return null;
    }
}
