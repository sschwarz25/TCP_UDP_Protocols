
public class CommunicationProtocol {
	private static final int 	WAITING 			= 0;
	private static final int	HELLO_DELIVERED		= 1;
	private static final int	SENDDATA			= 2;
	
	private int 				serverState			= WAITING;
	private int				clientState			= WAITING;
	private int				current				= 0;
	private int				packetCount			= 0;
	
	public String ProcessInput_Server ( String input ) {
		String output = "";
		
		if ( getServerState() == WAITING ) {
			output = "Hello";
			setServerState ( HELLO_DELIVERED );
			System.out.println( "Hello Out" );
		}
		
		if ( getServerState() == HELLO_DELIVERED ) {
			try {
			    this.setPacketCount ( Integer.parseInt( input ) );
			    System.out.println( "Packet Count: " + getPacketCount() );
			    output = "SendData";
			    setServerState ( getSendData() );
				
			} catch ( NumberFormatException ne ) {
				System.out.println( "Client did not send integer for Packet Count: " + ne );
				output = "Error";
			}
		}
			
		return output;
	}
	
//------------------------------------------------------
//--------------------CLIENT INPUT PROCESSOR------------
//------------------------------------------------------
	public String ProcessInput_Client ( String input ) {
		String output = "";
		
		if ( clientState == WAITING) {
			if ( input.equalsIgnoreCase( "Hello" )) {
				output = "Packets";
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

	public int getSendData () {
	    return SENDDATA;
	}

	public int getPacketCount () {
	    return packetCount;
	}

	public void setPacketCount ( int packetCount ) {
	    this.packetCount = packetCount;
	}
}
