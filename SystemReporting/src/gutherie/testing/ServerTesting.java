package gutherie.testing;

import java.net.InetAddress;

public class ServerTesting {
	public ServerTesting(InetAddress addr){
		super();
		init();
		host = addr;
	}
	
	public boolean beginTesting(){
		
		return true;
	}
	
	
	private void init(){
		
	}
	
	private InetAddress host;
}
