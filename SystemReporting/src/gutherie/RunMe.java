package gutherie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

public class RunMe {

	public static void main(String[] args) {
		System.out.print(INTRO + System.lineSeparator() + HELP);
		
		if (args.length == 0){
			String cmd = "";
			while ((cmd = getLine("Enter Command: ")).compareToIgnoreCase("q") !=0){
				if (cmd.compareToIgnoreCase("setup")==0){
					System.out.println("creating database : " + createDatabase());
				}
				else if(cmd.compareToIgnoreCase("test")==0){
					testSystems();					
				}
				else if(cmd.compareToIgnoreCase("add")==0){
					System.out.println("Adding System : " + addSystem());					
				}
				else if(cmd.compareToIgnoreCase("list")==0){
					listSystems();				
				}
				else if(cmd.compareToIgnoreCase("del")==0){
					System.out.println(delSystem());					
				}
			}
			
		}
		else {
			if (args[1].compareToIgnoreCase("auto")==0){
				System.out.println("STUB: run auto test here");
			}
		}
		
		System.out.print(GOODBYE);
		
		System.exit(0);
	}
	
	private static String getLine(String question){
		String input = "";
		System.out.print(question);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			input= br.readLine();
		} catch (IOException e) {
			System.out.println(e.getMessage() + System.lineSeparator() + "Please try again.");
		}
		return input;
	}
	
	private static String createDatabase(){
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(PROTOCOL + DBNAME + ";create=true");
			conn.setAutoCommit(true);
			Statement stmt = conn.createStatement();
			stmt.execute(CREATETABLES);
				
			return "Database created successfully";
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return "";
	}
	
	/*
	 * add system to the database for testing
	 */
	private static String addSystem(){
		String address = getLine("Enter ip address (xxx.xxx.xxx.xxx) : ");
		String hostname = getLine("Enter hostname (FQDN): ");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(PROTOCOL + DBNAME + ";create=false");
			conn.setAutoCommit(true);
			if (conn != null && conn.isValid(5)){
				InetAddress addr = InetAddress.getByName(address);
				
				PreparedStatement pstmt = conn.prepareStatement("INSERT INTO hosts (ip_a, ip_b, ip_c, ip_d, hostname) VALUES (?, ?, ?, ?, ?)");
				pstmt.setInt(1, 0x0000 ^ addr.getAddress()[0]);
				pstmt.setInt(2, 0x0000 ^ addr.getAddress()[1]);
				pstmt.setInt(3, 0x0000 ^ addr.getAddress()[2]);
				pstmt.setInt(4, 0x0000 ^ addr.getAddress()[3]);
				pstmt.setString(5, hostname);
				pstmt.executeUpdate();
				
				return "Host addess successfully.";
			}
			else {
				return "Failed to add host, no database found";
			}
			
		} catch (UnknownHostException e) {
			return "Failed to add host: " + e.getMessage();
		} catch (SQLException e){
			return "Failed to add host: " + e.getMessage();
		}
	}
	
	/*
	 * delete system from the database
	 */
	private static String delSystem(){
		Connection conn = null;
		try {
			String id = getLine("Enter the id of the host to delete : ");
			Integer.parseInt(id);
			
			conn = DriverManager.getConnection(PROTOCOL + DBNAME + ";create=false");
			conn.setAutoCommit(true);
			Statement stmt = conn.createStatement();
			stmt.execute("DELETE FROM hosts WHERE id=" + id);
			return "System Deleted.";
			
		} catch (SQLException e){
			return "Failed to add host: " + e.getMessage();
		} catch (NumberFormatException e){
			return "invalid ID";
		}
		
	}
	
	/*
	 * List all systems currently in database
	 */
	private static void listSystems(){
		Connection conn = null;
		try {	
			conn = DriverManager.getConnection(PROTOCOL + DBNAME + ";create=false");
			conn.setAutoCommit(true);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM hosts");
			int count = 0;
			while (rs.next()){
				count++;
				
				int a = rs.getInt(2);
				if (a < 0){
					a = 0xFF & a;
				}
				int b = rs.getInt(3);
				if (b < 0){
					b = 0xFF & b;
				}
				int c = rs.getInt(4);
				if (c < 0){
					c = 0xFF & c;
				}
				int d = rs.getInt(5);
				if (d < 0){
					d = 0xFF & d;
				}

				System.out.println(rs.getInt(1) + " " + a + "." + b + "." + c + "." + d + " " + rs.getString(6));
			}
			
			if (count == 0){
				System.out.println("No systems found.");
			}
			
		} catch (SQLException e){
			System.out.println("Failed to get list : " + e.getMessage());
		}
	}
	
	/*
	 * Test all systems in the database
	 */
	private static void testSystems(){
		String choice = getLine("Test one system or all systems (1, all)");
		if (choice.compareToIgnoreCase("1")==0){
			System.out.println("testing one....");
		}
		else {
			System.out.println("testing all....");
		}
		
	}
	
	/*
	 * Test a single system
	 */
	private static void testSystem(int id){
		
	}
	
    private static final String FRAMEWORK = "embedded";
    private static final String PROTOCOL = "jdbc:derby:";
    private static final String DBNAME = "ReportingDB";
	private static final String INTRO = ""
			+ "Welcome to the SystemReporting tool." + System.lineSeparator()
			 + System.lineSeparator() 
			+ "SystemReporting  Copyright (C) 2015  Jason R. Baker" + System.lineSeparator()
			+ "This program comes with ABSOLUTELY NO WARRANTY." + System.lineSeparator()
			+ "This is free software, and you are welcome to redistribute it" + System.lineSeparator()
			+ "under certain conditions.";
	
	private static final String HELP = ""
			+ "" + System.lineSeparator()
			+ "To run checks without interaction, lauch this program with a single parameter 'auto'" + System.lineSeparator()
			+ "\tsetup 	- setup database" + System.lineSeparator()
			+ "\ttest 	- run tests" + System.lineSeparator()
			+ "\tadd  	- add ip address to test" + System.lineSeparator()
			+ "\tlist 	- list ip addresses to test" + System.lineSeparator()
			+ "\tdel 	- delete ip address from test" + System.lineSeparator()
			+ "" + System.lineSeparator()
			+ "\tq 		- quit" + System.lineSeparator()
			+ ""
			+ "";
	
	private static final String GOODBYE = "Good night and keep your stick on the ice.";
	private static final String CREATETABLES = ""
			+ "CREATE TABLE hosts("
			+ "  id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ "  ip_a SMALLINT,"
			+ "  ip_b SMALLINT,"
			+ "  ip_c SMALLINT,"
			+ "  ip_d SMALLINT,"
			+ "  hostname CHAR(100)"
			+ ")";
	
}
