package gutherie.testing;

import java.net.InetAddress;

public interface HostTest {
	public boolean runTest(InetAddress address);
	public String getReport();
	public String getData();
	
	public long id=0000;
}
