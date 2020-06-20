package main;

/**
 *  Author: 2425693
 *  
 *  class AllLoans
 *  
 *  Servlet responsible for displaying all the loans held in the database whether current or past
 *  Accessible via LibraryLinks.html
 *  
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

@SuppressWarnings("serial")
@WebServlet("/AllLoans")
public class AllLoans extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/html"); // print to html
		PrintWriter out = response.getWriter(); // print to servlet response i.e. webpage being viewed
		LibraryUtility.doHeader(out, "All Loans"); // print html header and opening body tag

		Connection con = DatabaseAccess.getDatabaseConnection(out); // get database connection

		try {

			// query to join books, loans and borrowers table
			String query = "SELECT Borrower.BorrowerNo, " + "       Name, Book.BookNo, Title, "
					+ "       Date_out, Dispatched, Date_back " + "FROM Borrower, Book, Loan " + "WHERE "
					+ "  Borrower.BorrowerNo = Loan.BorrowerNo AND " + "  Book.BookNo = Loan.BookNo ";

			// print out the resulting loans set in a table
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			out.println("<p> " + "<table border>");
			out.println("<tr><td><b>Borrower no</b></td>" + "<td><B>Name</B></td>" + "<td><b>Book no</b></td>"
					+ "<td><B>Title</B></td>" + "<td><B>Date out</B></td>" + "<td><B>Dispatched</B></td>"
					+ "<td><B>Date back</B></td></tr>");
			while (rs.next()) {
				out.println("<tr><td>" + rs.getString("BorrowerNo") + "</td><td>" + rs.getString("Name") + "</td><td>"
						+ rs.getString("BookNo") + "</td><td>" + rs.getString("Title") + "</td><td>"
						+ rs.getDate("Date_out") + "</td><td>" + rs.getString("Dispatched") + "</td><td>"
						+ rs.getDate("Date_back") + "</td></tr>");
			}
			out.println("</table>");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			out.println("<p>SQLException: " + e.getMessage());
		}

		try { // Close the database connection
			con.close();
		} catch (SQLException ex) {
			out.println("<p>SQLException: " + ex.getMessage());
		}

		out.println("<hr>");
		out.println("<a href=\"LibraryLinks.html\">Return to Library Home</a>");
		LibraryUtility.doFooter(out);
	} // doGet

} // class
