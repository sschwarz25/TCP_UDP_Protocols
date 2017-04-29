
public class CommunicationProtocol {
    private static final int WAITING	     = 0;
    private static final int HELLO_DELIVERED = 1;
    private static final int SENDDATA	     = 2;

    private int	serverState = WAITING;
    private int	clientState = WAITING;
    private int	packetCount = 0;

    private String packetCountAck = "PACKETCOUNTACK";
    private String helloMessage	  = "Hello";
    private String packetsMessage = "Packets";
    private String helloNote	  = "Hello message has been delivered to connected client.";
    private String sendData	  = "Send Data";
    private String delimiter	  = "\\s+";
    private String complete	  = "Complete";

    public String ProcessInput_Server ( String input ) {

	String output = null;

	if ( serverState == WAITING ) {
	    output = helloMessage;
	    serverState = HELLO_DELIVERED;
	    System.out.println ( helloNote );
	    System.out.println ( "STATE CHANGE: WAITING -> HELLO_DELIVERED" );
	}

	if ( serverState == HELLO_DELIVERED ) {
	    if ( input.contains ( packetsMessage ) ) {
		
		try {   
		    String[] tokens = input.split ( delimiter );
		    packetCount = Integer.parseInt ( tokens[1] );
		    output = this.getPacketCountAckString ();
		    serverState = SENDDATA;
		    System.out.println ( output );
		    System.out.println ( "STATE CHANGE: HELLO_DELIVERD -> SENDDATA" );

		} catch ( NumberFormatException ne ) {
		    System.err.println ( "Packet Count not a number: " + ne );
		    System.exit ( 5 );
		}
	    }
	}

	return output;
    }

    public String ProcessInput_Client ( String input ) {
	String output = null;

	if ( clientState == WAITING ) {
	    if ( input.equalsIgnoreCase ( helloMessage ) ) {
		output = getPacketsString ();
		clientState = SENDDATA;
		System.out.println ( output );
		System.out.println ( "STATE CHANGE: WAITING -> SENDDATA" );
	    }
	}

	if ( clientState == SENDDATA ) {
	    if ( input.contains ( packetCountAck ) ) {
		output = getSendDataString ();
	    }
	}

	return output;
    }

    public int getServerState () {
	return serverState;
    }

    public void setServerState ( int serverState ) {
	this.serverState = serverState;
    }

    public int getClientState () {
	return clientState;
    }

    public void setClientState ( int clientState ) {
	this.clientState = clientState;
    }

    public int getSendDataState () {
	return SENDDATA;
    }

    public int getWaitingState () {
	return WAITING;
    }

    public int getHelloDeliveredState () {
	return HELLO_DELIVERED;
    }

    public int getPacketCount () {
	return packetCount;
    }

    public void setPacketCount ( int packetCount ) {
	this.packetCount = packetCount;
    }

    public String getPacketCountAckString () {
	return packetCountAck + " " + this.getPacketCount ();
    }

    public String getPacketCountAck () {
	return packetCountAck;
    }

    public String getHelloString () {
	return helloMessage;
    }

    public String getPacketsString () {
	return packetsMessage + " " + this.packetCount;
    }

    public String getHelloNoteString () {
	return helloNote;
    }

    public String getSendDataString () {
	return sendData;
    }

    
    public String getComplete () {
	return complete;
    }

    
    public void setComplete ( String complete ) {
	this.complete = complete;
    }

}
