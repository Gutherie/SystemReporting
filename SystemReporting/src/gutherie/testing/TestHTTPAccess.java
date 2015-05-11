package gutherie.testing;

/*
 *   This file is part of SystemReporting.
 *
 *   SystemReporting is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   SystemReporting is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with SystemReporting.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;

public class TestHTTPAccess implements HostTest {
	public TestHTTPAccess(){
		report = new StringBuffer();
		testStatus = false;
		testDuration = 0;
	}

	@Override
	public boolean runTest(InetAddress address) {
		inetAddress = address;
		report.append("Begin HTTP connection test for : " + address.getHostAddress() + System.lineSeparator());
		startTime = System.currentTimeMillis();
		report.append("Start\t\t: " + startTime + System.lineSeparator());
		
		URL url;
		try {
			url = new URL("http://"+ address.getHostAddress());
			URLConnection urlcon = url.openConnection();
			urlcon.connect();
			long endTime = System.currentTimeMillis();
			testDuration = endTime - startTime;
			report.append("End\t\t: " + endTime + System.lineSeparator());
			report.append("Duration\t: " + testDuration + System.lineSeparator());
			report.append("Test completed successfully.");
			testStatus = true;
			return testStatus;		
		} catch (IOException e) {
			report.append("Error, test did not complete successfully: " + e.getMessage());
			return testStatus;
		}

		
		
	}

	@Override
	public String getReport() {
		return report.toString();
	}
	
	@SuppressWarnings("unchecked")
	public String getData(){
		JSONObject data = new JSONObject();
		data.put("host", inetAddress.getHostAddress());
		data.put("status", testStatus);
		data.put("timestamp", startTime);
		data.put("duration", testDuration);
		data.put("id", id);
		data.put("description", description);
		return data.toJSONString();
	}
	

	
	public final static long id=1;
	public final static String description = "HTTP connection test.";
	private boolean testStatus;
	private long startTime;
	private long testDuration;
	private StringBuffer report;
	private InetAddress inetAddress;
}
