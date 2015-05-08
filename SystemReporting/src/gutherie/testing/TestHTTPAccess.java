package gutherie.testing;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TestHTTPAccess implements HostTest {
	public TestHTTPAccess(){
		report = new StringBuffer();
	}

	@Override
	public boolean runTest(InetAddress address) {
		
		URL url;
		try {
			url = new URL("http://"+ address.getHostAddress());
			URLConnection urlcon = url.openConnection();
			urlcon.connect();
			return true;
			
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public String getReport() {
		// TODO Auto-generated method stub
		return null;
	}
	


	public long id=1;
	private StringBuffer report;
}
