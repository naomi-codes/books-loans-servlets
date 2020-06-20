package main;

/**
 * 
 * 
 * Author: 2425693
 * 
 * Class to hold the database connection information and read in db login information from a file
 * 
 * class DatabaseAccess
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.*;

public class DatabaseAccess {

	/**
	 * 
	 * @param out PrintWriter
	 * @return returns a connection to the database
	 */
	public static Connection getDatabaseConnection(PrintWriter out) {
		try { // load the driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			out.println("<p>Failed to load driver " + e.getMessage());
			return null;
		}

		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/bookloans";

		String username = null;
		String password = null;

		try {
			String authFileName = "./auth.txt";

			// set a reader for the file with db user details
			BufferedReader userPassIn = new BufferedReader(new FileReader(authFileName));
			username = userPassIn.readLine(); // read in username
			password = userPassIn.readLine(); // read in password
			userPassIn.close();

		} catch (IOException e) {

			out.println("Failed to read authorization " + e.getMessage());
			return null;

		}
		try {
			// get a database connection using the specified url, username and password
			con = DriverManager.getConnection(url, username, password);

		} catch (SQLException e) {

			out.println("SQLException: " + e.getMessage());
		}
		return con;
	} // getDatabaseConnection

} // class
