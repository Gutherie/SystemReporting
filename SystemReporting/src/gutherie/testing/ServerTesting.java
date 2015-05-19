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


import java.net.InetAddress;
import java.util.Vector;

import org.json.simple.JSONArray;


public class ServerTesting {
	public ServerTesting(String addr, String hostname){
		super();
		init();
		hostByAddress = addr;
		hostByName = hostname;
	}
	
	public boolean beginTesting(){
		boolean status = true;
		for (int i = 0; i < tests.size(); i++){
			status = status & tests.get(i).runTest(hostByAddress,hostByName);
			System.out.println(tests.get(i).getReport());
			results.add(tests.get(i).getData());
			
			//System.out.println("Test Data: " + i + "\n" + tests.get(i));
		}
		
		return false;
	}
	
	public String getResults(){
		return results.toJSONString();
	}
	
	
	private void init(){
		results = new JSONArray();
		tests = new Vector<HostTest>();
		
		tests.add(new TestHTTPAccess());
		tests.add(new TestHTTPSAccess());
		tests.add(new TestHTTPSValid());
		
	}
	
	private String hostByAddress;
	private String hostByName;
	private Vector<HostTest> tests;
	private JSONArray results;
}
