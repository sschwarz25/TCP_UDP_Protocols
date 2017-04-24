
public class CommunicationProtocol {
    private static final int WAITING	     = 0;
    private static final int HELLO_DELIVERED = 1;
    private static final int SENDDATA	     = 2;

    private int	serverState = WAITING;
    private int	clientState = WAITING;
    private int	packetCount = 0;

    public String packetCountAck = "PACKETCOUNTACK";
    public String hello		 = "Hello";
    public String packets	 = "Packets";
    public String helloNote	 = "Hello message has been delivered to connected client.";
    public String sendData	 = "Send Data";

    // -------------------------------------------------------------
    // --------------------SERVER INPUT PROCESSOR-------------------
    // -------------------------------------------------------------
    public String ProcessInput_Server ( String input ) {

	String output = null;

	if ( this.getServerState () == this.getWaitingState () ) {
	    output = this.getHelloString ();
	    this.setServerState ( this.getHelloDeliveredState () );
	    System.out.println ( this.getHelloNoteString () );
	}

	if ( this.getServerState () == this.getHelloDeliveredState () ) {
	    if ( input.contains ( this.getPacketsString () ) ) {
		try {
		    String[] tokens = input.split ( "\\s+" );
		    this.setPacketCount ( Integer.parseInt ( tokens[1] ) );

		    output = this.getPacketCountAckString ();

		    this.setServerState ( this.getSendDataState () );

		} catch ( NumberFormatException ne ) {
		    System.err.println ( "Packet Count not a number: " + ne );
		    System.exit ( 5 );
		}
	    }
	}

	if ( this.getServerState () == this.getSendDataState () ) {

	}

	return output;
    }

    // -------------------------------------------------------------
    // --------------------CLIENT INPUT PROCESSOR-------------------
    // -------------------------------------------------------------
    public String ProcessInput_Client ( String input ) {
	String output = null;

	if ( this.getClientState () == this.getWaitingState () ) {
	    if ( input.equalsIgnoreCase ( this.getHelloString () ) ) {
		output = this.getPacketsString ();
		this.setClientState ( this.getSendDataState () );
	    }
	}

	if ( this.getClientState () == this.getSendDataState () ) {
	    if ( input.contains ( this.getPacketCountAck () )) {
		output = this.getSendDataString ();
	    }
	}

	return output;
    }

    // --------------------------------------------------------------
    // --------------------GET | SET---------------------------------
    // --------------------------------------------------------------
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
	return hello;
    }

    public String getPacketsString () {
	return packets + " " + this.getPacketCount ();
    }

    public String getHelloNoteString () {
	return helloNote;
    }
    
    public String getSendDataString () {
	return sendData;
    }

}
