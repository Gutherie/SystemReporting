package gutherie.testing;

import java.net.InetAddress;

public interface HostTest {
	public boolean runTest(String address, String hostName);
	public String getReport();
	public String getData();
	
	public long id=0000;
}
