package main;

/**
 * Author: 2425693
 * Servlet which lists the contents of the database
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ListDB")
public class ListDB extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		LibraryUtility.doHeader(out, "Listing the database...");

		// get a connection to the database
		Connection con = DatabaseAccess.getDatabaseConnection(out);

		try {
			// print all the borrowers in the DB
			String queryBorrower = "SELECT * FROM Borrower";

			out.println("<caption>Borrowers</caption>");

			Statement stmt = con.createStatement();
			ResultSet rsBorrower = stmt.executeQuery(queryBorrower);
			out.println("<p> " + "<table border>");
			out.println("<tr><td><b>Borrower no</b></td>" + "<td><B>Name</B></td>" + "<td><b>Department</b></td>");
			while (rsBorrower.next()) {
				out.println("<tr><td>" + rsBorrower.getString("BorrowerNo") + "</td><td>" + rsBorrower.getString("Name")
						+ "</td><td>" + rsBorrower.getString("Department") + "</td>");
			}
			out.println("</table>");
			rsBorrower.close();

			// print all the books in the DB
			String queryBook = "SELECT * FROM Book";

			out.println("<p><caption>Books</caption>");

			ResultSet rsBook = stmt.executeQuery(queryBook);
			out.println("<p> " + "<table border>");
			out.println("<tr><td><b>Book no</b></td>" + "<td><B>Author</B></td>" + "<td><B>Title</B></td>");
			while (rsBook.next()) {
				out.println("<tr><td>" + rsBook.getString("BookNo") + "</td><td>" + rsBook.getString("Author")
						+ "</td><td>" + rsBook.getString("Title") + "</td>");
			}
			out.println("</table>");
			rsBook.close();

			// print all the loans in the db
			String queryLoan = "SELECT * FROM LOAN";

			out.println("<p><caption>Loans</caption>");

			ResultSet rsLoan = stmt.executeQuery(queryLoan);
			out.println("<p> " + "<table border>");
			out.println("<tr><td><b>Book no</b></td>" + "<td><B>Borrower no</B></td>" + "<td><b>Date back</b></td>"
					+ "<td><B>Date out</B></td>" + "<td><B>Dispatched</B></td></tr>");
			while (rsLoan.next()) {
				out.println("<tr><td>" + rsLoan.getString("BorrowerNo") + "</td><td>" + rsLoan.getInt("BookNo")
						+ "</td><td>" + rsLoan.getInt("BorrowerNo") + "</td><td>" + rsLoan.getDate("Date_back")
						+ "</td><td>" + rsLoan.getDate("Date_out") + "</td><td>" + rsLoan.getString("Dispatched")
						+ "</td><td></tr>");
			}
			rsLoan.close();
			out.println("</table>");
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
