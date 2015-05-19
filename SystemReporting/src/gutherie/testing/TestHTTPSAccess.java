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
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLSocketFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONObject;

public class TestHTTPSAccess implements HostTest {
	public TestHTTPSAccess(){
		report = new StringBuffer();
		testStatus = false;
		testDuration = 0;
		errormsg = "";
	}
	
	@Override
	public boolean runTest(String address, String hostName) {
		inetAddress = address;
		inetHost = hostName;
		report.append("Begin HTTPS connection test for : " + address + System.lineSeparator());
		startTime = System.currentTimeMillis();
		report.append("Start\t\t: " + startTime + System.lineSeparator());
		HostnameVerifier originalVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
		
		
		// Not worried about self-signed certificates
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
					public java.security.cert.X509Certificate[] getAcceptedIssuers(){
						return null;
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType){
						
					}
					public void checkServerTrusted(X509Certificate[] certs, String authType){
						
					}
				}
		};
		try{
			SSLContext sslctx = SSLContext.getInstance("SSL");
			sslctx.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sslctx.getSocketFactory());
			HostnameVerifier allHostsValid = new HostnameVerifier(){
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};
			
			
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			
			
			URL url;
			try {
				url = new URL("https://"+ hostName);
				try{
					HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
					con.connect();
					long endTime = System.currentTimeMillis();
					testDuration = endTime - startTime;
					report.append("End\t\t: " + endTime + System.lineSeparator());
					report.append("Duration\t: " + testDuration + System.lineSeparator());
					report.append("Test completed successfully.");
					testStatus = true;
					return testStatus;
				}catch(IOException e1){
					testStatus = false;
					errormsg = e1.getMessage();
					report.append("ERROR: " + e1.getMessage() + System.lineSeparator());
					return testStatus;
				}
			} catch (MalformedURLException e) {
				testStatus = false;
				errormsg = e.getMessage();
				report.append("ERROR: " + e.getMessage() + System.lineSeparator());
				return testStatus;
			}
		}catch(NoSuchAlgorithmException e){
			testStatus = false;
			errormsg = e.getMessage();
			report.append("ERROR: " + e.getMessage() + System.lineSeparator());
			return testStatus;
		}catch(KeyManagementException e){
			testStatus = false;
			errormsg = e.getMessage();
			report.append("ERROR: " + e.getMessage() + System.lineSeparator());
			return testStatus;
		}finally {
			// return the verifier to the original

			try{
				HttpsURLConnection.setDefaultSSLSocketFactory(SSLContext.getDefault().getSocketFactory());
			}catch(NoSuchAlgorithmException e){
				System.out.println("ERROR reseting HTTPS connection verification : " + e.getMessage());
			}
			
		}
	}

	@Override
	public String getReport() {
		// TODO Auto-generated method stub
		return report.toString();
	}

	@Override
	public String getData() {
		JSONObject data = new JSONObject();
		data.put("host", inetAddress);
		data.put("status", testStatus);
		data.put("timestamp", startTime);
		data.put("duration", testDuration);
		data.put("id", id);
		data.put("description", description);
		data.put("errormsg", errormsg);

		return data.toJSONString();
	}
	public final static long id=2;
	public final static String description = "HTTP connection test.";
	private boolean testStatus;
	private long startTime;
	private long testDuration;
	private String errormsg;
	private StringBuffer report;
	private String inetAddress;
	private String inetHost;
}
