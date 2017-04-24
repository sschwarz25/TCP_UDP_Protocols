import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Program {

    public static void main (String[] args) throws IOException {
	
	//Check Argument Count
	if ( args.length < 4 || args.length > 5 ) {
	    System.err.println ( "Arguments Not Complete" );
	    System.out.println (
		    "The input command is: java tcp_udp_protocols_client_server [TCP | UDP] [Client | Server] DestinatonIP PortNumber ~FilePath (Client Only)" );
	    System.exit ( -1 );
	}

	Boolean tcpUdpFlag = false; 		// True = TCP, False = UDP
	Boolean clientServerFlag = false; 	// True = client, False = Server

	//Decide protocol
	switch ( args[0].toLowerCase () ) {
	    case "tcp" :
		System.out.println ( "TCP Selected." );
		tcpUdpFlag = true;
		break;
	    case "udp" :
		System.out.println ( "UDP Selected." );
		tcpUdpFlag = false;
		break;
	    default :
		System.out.println (
			"The input command is: java tcp_udp_protocols_client_server [TCP | UDP] [Client | Server] DestinatonIP PortNumber ~FilePath (Client Only)" );
		System.exit ( 10 );
	}

	//Decide Client or Server
	switch ( args[1].toLowerCase () ) {
	    case "client" :
		System.out.println ( "Client Selected." );
		clientServerFlag = true;
		break;
	    case "server" :
		System.out.println ( "Server Selected." );
		clientServerFlag = false;
		break;
	    default :
		System.out.println (
			"The input command is: java tcp_udp_protocols_client_server [TCP | UDP] [Client | Server] DestinatonIP PortNumber ~FilePath (Client Only)" );
		System.exit ( 10 );
	}

	//Validate IP Address
	String destinationIp = args[2];
	System.out.println ( "IP: " + destinationIp );
	// TODO: Validate IP Address

	//Validate Port Number
	int portNumber= 7000;
	try {
	    int tPortNumber = Integer.parseInt ( args[3] );
	    portNumber = tPortNumber;
	    System.out.println ( "Port Number: " + portNumber );
	} catch ( NumberFormatException ne ) {
	    System.err.println ( "Argument 4 not an integer for Port Number: " + ne );
	    System.exit ( 2 );
	}
	
	
	byte[] fileBytes = null;
	if(clientServerFlag){
    		try {
    	    		Path path = Paths.get ( args[4] );
    	    		fileBytes = Files.readAllBytes ( path );		
    		} catch ( FileNotFoundException fe){
    	    		System.out.println ( "FNF: " + fe );
    	    		System.exit ( 3 );
    		}
	}
	
	if(tcpUdpFlag){
	    //TCP	    
	    if(clientServerFlag){
		//Client
		TCP.Client ( destinationIp, portNumber, fileSplit(fileBytes) );
	    } else {
		//Server
		fileBytes = reassembleFile( TCP.Server ( portNumber ));
		//TODO: Save File Bytes to File with Naming System
	    }
	} else {
	    //UDP
	}
    }
    
    public static byte[] reassembleFile(ArrayList<byte[]> partitionedBytes) throws IOException {

	ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
	
	for(int i = 0; i < partitionedBytes.size (); i++){
	    outputStream.write ( partitionedBytes.get ( i ) );
	}
	
	System.out.println ( "The file recomposition was a success. Passing byte string back." );
	
	return outputStream.toByteArray ();
    }

    public static ArrayList<byte[]> fileSplit ( byte[] fileBytes ) throws IOException {
	
	int partitionSize = 1024; //  1KB per Packet - ~1400 max to keep under the 1500 limit while concat'ing headers
	int start = 0;
		
	ArrayList<byte[]> partitionArray = new ArrayList<byte[]>();
	
	while ( start < fileBytes.length ) {
	    int end = Math.min ( fileBytes.length,  start + partitionSize );
	    partitionArray.add ( Arrays.copyOfRange ( fileBytes, start, end ) );
	    start += partitionSize;
	}
	
	System.out.println ( "The file partitioning was successful. Paassing byte-string array back." );
	
	return partitionArray;
    }
}
