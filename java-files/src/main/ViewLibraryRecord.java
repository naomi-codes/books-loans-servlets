package main;

/** Author 2425693
 * 
 * Displays the the borrower's past loans in a table
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import user.LoggedInBorrower;

import java.sql.*; // for JDBC

@WebServlet("/ViewLibraryRecord")
public class ViewLibraryRecord extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		LibraryUtility.doHeader(out, "Your Past Loans");

		HttpSession session = request.getSession();
		ServletContext sc = getServletContext();

		if (session.getAttribute(("LoggedInBorrower")) == null) {
			request.setAttribute("SessionTimeout", "timed out");
			RequestDispatcher rd = sc.getRequestDispatcher("/LoginForm.jsp");
			rd.forward(request, response);
		} else {

			out.println("<p><form action=\"" + response.encodeURL("BorrowerLoggedInPage") + "\" "
					+ "method=\"post\" name=\"BackToHomeForm\">");
			out.println("<br><input type=\"submit\" name=\"backToHome\" value=\"Back to Home\">" + "</form>");

			LoggedInBorrower borrower = (LoggedInBorrower) session.getAttribute("LoggedInBorrower");

			Connection con = DatabaseAccess.getDatabaseConnection(out);
			try {

				// find all the copies which correspond to the book title in the database
				String query = "SELECT Loan.BookNo, Book.Title, Loan.Date_out, Loan.Dispatched, Loan.Date_back "
						+ "FROM Loan, Book " + "WHERE " + "  Loan.BorrowerNo = '" + borrower.getBorrowerNo() + "' "
						+ "AND Book.BookNo = Loan.BookNo";

				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				if (!rs.next()) {
					out.println("<p>No previous loans to display.</p>");
				} else {

					out.println("<p> " + "<table border>");
					out.println("<tr><td><b>Book No</b></td>" + "<td><B>Title</B></td>" + "<td><b>Date out</b></td>"
							+ "<td><B>Dispatched</B></td>" + "<td><B>Date back</B></td></tr>");
					while (rs.next()) { // while there are more copies in the result set
						out.println("<tr><td>" + rs.getString("BookNo") + "</td><td>" + rs.getString("Title")
								+ "</td><td>" + rs.getString("Date_out") + "</td><td>" + rs.getString("Dispatched")
								+ "</td><td>" + rs.getDate("Date_back") + "</td></tr>");
					}
					out.println("</table>");

				}
				stmt.close();
			} catch (SQLException e) {
				out.println("<p>SQLException: " + e.getMessage());
			}

			try { // Close the database connection
				con.close();
			} catch (SQLException ex) {
				out.println("<p>SQLException: " + ex.getMessage());
			}

		}
	} // doGet

} // class
