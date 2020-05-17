package main;
// Form to dispatch a book

// The form allows entry of a single book number, and the request is then
// sent back to THIS servlet.

// If a request is received with no parameter, then just the form is displayed.
// If a request is received with a BookNo= parameter, then the book with the
// given number is dispatched, and the form is displayed for further dispatch requests.

// Thus this servlet is "multi-purpose" - responding appropriately to initial
// and subsequent accesses

// Where the database connection is made below, you should adapt it to log in
// using your MySQL username and password (not cscu9yd/cscu9yd),
// and so that the query accesses your tables

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*; // for JDBC

@WebServlet("/Dispatch")
public class Dispatch extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		LibraryUtility.doHeader(out, "Dispatch a book");

		String bookNo = request.getParameter("BookNo");
		if (bookNo != null) {
			int check = LibraryUtility.checkNumber(bookNo);
			if (check == 1) {
				out.println("<b>Book number \"" + bookNo + "\" malformed</b><p>");
			} else if (check == 2) {
				// we have a book to dispatch

				Connection con = DatabaseAccess.getDatabaseConnection(out);

				try {
					Statement stmt = con.createStatement();
					String query = "SELECT Title " + "FROM Book " + "WHERE Book.BookNo = " + bookNo;
					ResultSet rs = stmt.executeQuery(query); // There will be at most one row in the result set
					boolean noMatch = !rs.next(); // rs.next returns false if there is no row in the result set
					if (noMatch) {
						out.println("<b>No book matches number " + bookNo + "</b><p>");
					} else {
						String update = "UPDATE Loan " + "SET Dispatched = 'Y' " + "WHERE Dispatched = 'N' AND "
								+ "      Date_back IS NULL AND " + "      BookNo = " + bookNo;
						int nrows = stmt.executeUpdate(update);
						if (nrows == 0) {
							out.println("<b>Book number " + bookNo + " not due for dispatch</b><p>");
						} else {
							out.println("<b>Book number " + bookNo + " dispatched</b><p>");
						}
						stmt.close();
					}
				} catch (SQLException e) {
					out.println("<p>SQLException: " + e.getMessage());
				}

				try { // Close database connection
					con.close();
				} catch (SQLException ex) {
					out.println("<p>SQLException: " + ex.getMessage());
				}
			}
		}

		// display form
		out.println(
				"<p><form action=\"" + response.encodeURL("Dispatch") + "\" " + "method=\"get\" name=\"DispatchForm\">"
						+ "Enter number of book " + "<input type=\"text\" name=\"BookNo\" size=10> "
						+ "<input type=\"reset\"> " + "<input type=\"submit\" value=\"Submit number\">" + "</form>");

		out.println("<hr>");
		out.println("<a href=\"LibraryLinks.html\">Return to Library Home</a>");
		LibraryUtility.doFooter(out);
	} // doGet

} // class
