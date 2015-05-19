package gutherie;

import gutherie.mailer.Mailer;
import gutherie.testing.ServerTesting;
import gutherie.testing.TestHTTPAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

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
		
		if (CONFIG_FILE && args.length > 0){
			System.out.println("Found smtp config: ");
			File configFile = new File(args[0]);
			config = new Properties();
			
			try{
				config.load(new BufferedReader(new FileReader(configFile)));
				System.out.println(config.toString());
				System.setProperty("sun.net.spi.nameservice.nameservers", config.getProperty("dns"));
				//System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
				to = (String)config.get("mail.to");
				from = (String)config.get("mail.from");
				smtp = (String)config.get("mail.smtp.host");
			}catch(IOException e){
				System.out.println("Failed to open config file : " + e.getMessage());
				System.exit(1);;
			}
		}
		
		System.out.print(INTRO + System.lineSeparator() + HELP);
		
		if (args.length == 0 || (args.length == 1 && args[0].compareToIgnoreCase("auto")!= 0)){
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
				else if(cmd.compareToIgnoreCase("results")==0){
					getResults();
				}
			}
			
		}
		else {
			if (args[0].compareToIgnoreCase("auto")==0){
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
			stmt.execute(CREATETABLE01);
			stmt.execute(CREATETABLE02);
			conn.close();
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
				
				PreparedStatement pstmt = conn.prepareStatement("INSERT INTO hosts (ip_a, ip_b, ip_c, ip_d, stringIP, hostname) VALUES (?, ?, ?, ?, ?, ?)");
				pstmt.setInt(1, 0x0000 ^ addr.getAddress()[0]);
				pstmt.setInt(2, 0x0000 ^ addr.getAddress()[1]);
				pstmt.setInt(3, 0x0000 ^ addr.getAddress()[2]);
				pstmt.setInt(4, 0x0000 ^ addr.getAddress()[3]);
				pstmt.setString(5, address.trim());
				pstmt.setString(6, hostname);
				pstmt.executeUpdate();
				
				conn.close();
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
	
	/*erties
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
			conn.close();
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

				System.out.println(rs.getInt(1) + " " + a + "." + b + "." + c + "." + d + " " + rs.getString(7));
			}
			
			if (count == 0){
				System.out.println("No systems found.");
			}
			conn.close();
			
		} catch (SQLException e){
			System.out.println("Failed to get list : " + e.getMessage());
		}
		
	}

	/*
	 * Test all systems in the database
	 */
	private static void testSystems(){
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String choice = getLine("Test one system or all systems (id, all) where id is the integer ID of the host");
		if (choice.compareToIgnoreCase("all")!=0){
			testSystem(Integer.parseInt(choice));
		}
		else {
			Connection conn = null;
			ServerTesting tests = null;
			try{	
				conn = DriverManager.getConnection(RunMe.PROTOCOL + RunMe.DBNAME + ";create=false");
				if (choice.compareToIgnoreCase("1")==0){
					System.out.println("testing one....");
				}
				else {
					System.out.println("testing all....");
					if (conn != null && conn.isValid(5)){
						conn.setAutoCommit(true);
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(SQL_GETALLHOSTS);
						InetAddress byHostNameAddr = null;
						
						while (rs.next()){
							tests = new ServerTesting(rs.getString("stringIP"),rs.getString("hostname"));
							tests.beginTesting();
							PreparedStatement pstmt = conn.prepareStatement(SAVE_TEST);
							pstmt.setInt(1,rs.getInt(1));
							pstmt.setTimestamp(2, timestamp);
							pstmt.setString(3, tests.getResults());
							pstmt.execute();
							
							System.out.println(System.lineSeparator() + "Test data object: " + System.lineSeparator() + tests.getResults());
						}
					}
				}
			
				conn.close();
			}catch (SQLException e){
				System.out.println("Failed to get list, SQL error : " + e.getMessage());
			}
		}
	}
	
	/*
	 * Test a single system
	 */
	private static void testSystem(int id){
		String choice = getLine("Test one system or all systems (1, all)");
		Connection conn = null;
		ServerTesting tests = null;
		try{	
			conn = DriverManager.getConnection(RunMe.PROTOCOL + RunMe.DBNAME + ";create=false");
			if (choice.compareToIgnoreCase("1")==0){
				System.out.println("testing one....");
			}
			else {
				System.out.println("testing all....");
				if (conn != null && conn.isValid(5)){
					conn.setAutoCommit(true);
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(SQL_GETALLHOSTS + " WHERE id=" + id);
					while (rs.next()){
						tests = new ServerTesting(rs.getString("stringIP"), rs.getString("hostname"));
						tests.beginTesting();
						System.out.println(System.lineSeparator() + "Test data object: " + System.lineSeparator() + tests.getResults());
					}
					
					
				}
			}
		
			conn.close();
		}catch (SQLException e){
			System.out.println("Failed to execute query : " + e.getMessage());
		}		
	}
	
	private static void getResults(){
		String choice = getLine("Enter the id (integer) of the host for which you would like to see results :");
		String mailer = getLine("Would you like to mail the results (y/n) :");
		Connection conn = null;

		try{	
			Mailer mail = null;
			if (mailer.compareToIgnoreCase("y")==0){
				mail = new Mailer(config,from,to,"Result of testing on " + (new Date()).toString());
			}
			
			conn = DriverManager.getConnection(RunMe.PROTOCOL + RunMe.DBNAME + ";create=false");
			PreparedStatement pstmt = conn.prepareStatement(GET_TESTS);
			pstmt.setInt(1, Integer.parseInt(choice));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()){
				System.out.println(rs.getString("results"));
				if (mail != null) {
					mail.appendToMessage(rs.getString("results")+ "\n\n");
				}
			}

			conn.close();
			if (mailer.compareToIgnoreCase("y")==0){
				mail.sendMessage();
			}

		}catch (SQLException e){
			System.out.println("Failed to get list : " + e.getMessage());
		}
	}
	
    public static final String FRAMEWORK = "embedded";
    public static final String PROTOCOL = "jdbc:derby:";
    public static final String DBNAME = "/home/jabaker/DEV/derby/ReportingDB";
	private static final String INTRO = ""
			+ "Welcome to the SystemReporting tool." + System.lineSeparator()
			 + System.lineSeparator() 
			+ "SystemReporting  Copyright (C) 2015  Jason R. Baker" + System.lineSeparator()
			+ "This program comes with ABSOLUTELY NO WARRANTY." + System.lineSeparator()
			+ "This is free software, and you are welcome to redistribute it" + System.lineSeparator()
			+ "under certain conditions.";
	
	private static final String HELP = ""
			+ "" + System.lineSeparator()
			+ "To run checks without interaction, launch this program with a single parameter 'auto'" + System.lineSeparator()
			+ "\tsetup 	 - setup database" + System.lineSeparator()
			+ "\ttest 	 - run tests" + System.lineSeparator()
			+ "\tresults - Get test results" +  System.lineSeparator()
			+ "\tadd  	 - add ip address to test" + System.lineSeparator()
			+ "\tlist 	 - list ip addresses to test" + System.lineSeparator()
			+ "\tdel 	 - delete ip address from test" + System.lineSeparator()
			+ "" + System.lineSeparator()
			+ "\tq 		 - quit" + System.lineSeparator()
			+ ""
			+ "";
	
	private static final String GOODBYE = "Good night and keep your stick on the ice.";
	private static final String CREATETABLE01 = ""
			+ "CREATE TABLE hosts("
			+ "  id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ "  ip_a SMALLINT,"
			+ "  ip_b SMALLINT,"
			+ "  ip_c SMALLINT,"
			+ "  ip_d SMALLINT,"
			+ "	 stringIP CHAR(15),"
			+ "  hostname CHAR(100)"
			+ ")";
	private static final String CREATETABLE02 = ""
			+ "CREATE TABLE testlog("
			+ "  id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ "  hostid INT,"
			+ "  timestamp TIMESTAMP,"
			+ "  results VARCHAR(1000)"
			+ ")"
			+ "";
	
	private static final boolean CONFIG_FILE = true;
	private static String smtp;
	private static String to;
	private static String from;
	private static Properties config;
	private static final String SQL_GETALLHOSTS = "SELECT * FROM hosts";
	private static final String SAVE_TEST = "INSERT INTO testlog (hostid, timestamp, results) VALUES (?,?,?)";
	private static final String GET_TESTS = "SELECT * FROM testlog WHERE hostid=? ORDER BY timestamp DESC";
}
