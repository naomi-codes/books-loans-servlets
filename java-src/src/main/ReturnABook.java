package main;

/**
 * Author 2425693
 * 
 * Servlet which currently allows anyone to submit a book to return
 * A book number is entered via a form and if a corresponding open
 * loan is found in the database, the loan is returned and confimation
 * displayed to the user
 * 
 * class ReturnABook
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

import java.util.Calendar;

@WebServlet("/ReturnABook")
public class ReturnABook extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		LibraryUtility.doHeader(out, "Book returns"); // print html header elements

		String bookNo = request.getParameter("BookNo");
		if (bookNo != null) {

			// check that a valid number was entered
			int check = LibraryUtility.checkNumber(bookNo);
			if (check == 1) {
				out.println("<b>Book number \"" + bookNo + "\" malformed</b><p>");
			} else if (check == 2) {
				// potential book to return

				// get the database connection
				Connection con = DatabaseAccess.getDatabaseConnection(out);

				try {

					Statement stmt = con.createStatement();
					String query = "SELECT Title " + "FROM Book " + "WHERE Book.BookNo = " + bookNo;
					ResultSet rs = stmt.executeQuery(query); // There will be at most one row in the result set
					boolean noMatch = !rs.next(); // rs.next returns false if there is no row in the result set
					if (noMatch) {
						out.println("<b>No book matches number " + bookNo + "</b><p>");
					} else {
						// get the current date
						java.util.Date today = Calendar.getInstance().getTime();

						// convert the date to sql format
						java.sql.Date returnDate = new java.sql.Date(today.getTime());
						String update = "UPDATE Loan " + "SET Date_back = '" + returnDate + "' "
								+ "WHERE Dispatched = 'Y' AND " + "      Date_back IS NULL AND " + "      BookNo = "
								+ bookNo;

						// execute an update trying to return the book
						int nrows = stmt.executeUpdate(update);

						if (nrows == 0) { // entered book was not due for return
							out.println("<b>Book number " + bookNo + " not due for return</b><p>");
						} else { // entered book returned
							out.println("<b>Book number " + bookNo + " returned</b><p>");
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

		// Form to return a book
		out.println(
				"<p><form action=\"" + response.encodeURL("ReturnABook") + "\" " + "method=\"get\" name=\"BookForm\">"
						+ "Enter number of book to be returned " + "<input type=\"text\" name=\"BookNo\" size=10> "
						+ "<input type=\"reset\"> " + "<input type=\"submit\" value=\"Submit number\">" + "</form>");

		out.println("<hr>");
		out.println("<a href=\"LibraryLinks.html\">Return to Library Home</a>");
		LibraryUtility.doFooter(out);
	} // doGet

} // class
