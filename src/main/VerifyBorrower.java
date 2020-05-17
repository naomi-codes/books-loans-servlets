package main;

/**
 * Author 2425693
 * 
 * class VerifyBorrower
 * 
 * Servlet to verify that the borrower exists in the database
 * 
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.LoggedInBorrower;


@WebServlet("/VerifyBorrower")
public class VerifyBorrower extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		LibraryUtility.doHeader(out, "Verify Login");

		ServletContext sc = getServletContext();

		HttpSession session = request.getSession();

		if (request.getParameter("confirm") != null) { // if the borrower has clicked "Confirm"
			session = request.getSession();
			session.setMaxInactiveInterval(30); // set the max inactive period for the session

			if (session.getAttribute("LoggedInBorrower") == null) { // if there is no LoggedInBorrower associated with
																	// the session

				// get the confirmed borrower number
				String borrowerNo = request.getParameter("confirmedBorrowerNo").toString();

				// get a LoggedInBorrower object for the session
				LoggedInBorrower borrower = loginBorrower(out, borrowerNo);

				// associate the LoggedInBorrower with the session
				session.setAttribute("LoggedInBorrower", borrower);

				// forward the borrower to the borrower home page
				RequestDispatcher rd = sc.getRequestDispatcher("/BorrowerLoggedInPage");
				rd.forward(request, response);
			} else {
				// if there was already a LoggedInBorrower associated with the session
				// the borrower is forwarded to the home page
				RequestDispatcher rd = sc.getRequestDispatcher("/BorrowerLoggedInPage");
				rd.forward(request, response);
			}

		} else if (request.getParameter("cancel") != null) { // if the borrower clicks "Cancel"
			RequestDispatcher rd = sc.getRequestDispatcher("/LoginForm.jsp");
			rd.forward(request, response);

		} else {

			// otherwise display the page asking borrower to confirm identity

			String enteredBorrowerNo;

			if (request.getParameter("borrowerNo") == null) {
				enteredBorrowerNo = request.getAttribute("borrowerNo").toString();
			} else {
				enteredBorrowerNo = request.getParameter("borrowerNo").toString();
			}

			if (enteredBorrowerNo != null) {

				int check = LibraryUtility.checkNumber(enteredBorrowerNo);
				if (check == 1) {
					out.println("<b>Book number \"" + enteredBorrowerNo + "\" malformed</b><p>");
				} else if (check == 3) { // borrower number too long
					out.println("<b>Book number \"" + enteredBorrowerNo + "\" malformed. Must be three digits</b><p>");
				} else if (check == 2) { // entered a number

					if (verifyBorrowerExists(out, enteredBorrowerNo)) { // check that a borrower with given borrowerNo
																		// exists in the database

						// print confirmation form
						out.println("<p><form method=\"post\" action=\"VerifyBorrower\""
								+ " name=\"VerifyBorrowerNoForm\">" + "<p>Please confirm if <b>" + enteredBorrowerNo
								+ "</b> is your Borrower No.</p>" + "<p>If yes, please press confirm. "
								+ "Otherwise press cancel to return to login page."
								+ "<input type=\"submit\" name=\"confirm\" value=\"Confirm\">"
								+ "<input type='hidden' name='confirmedBorrowerNo' value=\"" + enteredBorrowerNo + "\">"
								+ "<input type=\"submit\" name=\"cancel\" value=\"Cancel\">" + "</form>");
					} else {

						out.println("<p>No such borrower with borrower number: " + enteredBorrowerNo + "</p>");

						// link to return to Library Links
						out.println("<p><a href=\"LibraryLinks.html\">Back to LibraryLinks</a></p>");
					}
				}
			}

		}
		LibraryUtility.doFooter(out);

	} // doGet

	/**
	 * 
	 * Looks for a borrower corresponding to a given borrower number in the database
	 * 
	 * @param out               PrintWriter
	 * @param enteredBorrowerNo borrower number to find in the db
	 * @return
	 */
	private boolean verifyBorrowerExists(PrintWriter out, String enteredBorrowerNo) {
		boolean match = false;
		Connection con = DatabaseAccess.getDatabaseConnection(out);

		try {
			String query = "SELECT Borrower.BorrowerNo FROM Borrower" + " WHERE Borrower.BorrowerNo = '"
					+ Integer.parseInt(enteredBorrowerNo) + "';";

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			match = rs.next(); // rs.next returns false if there is no row in the result set
			stmt.close();

		} catch (SQLException e) {
			out.println("<p>SQLException in method 'verifyBorrowerExists': " + e.getMessage() + "</p>");
		}

		try { // Close database connection
			con.close();
		} catch (SQLException ex) {
			out.println("<p>SQLException in method 'verifyBorrowerExists': " + ex.getMessage() + "</p>");
		}

		return match;
	} // verifyBorrowerExists

	/**
	 * 
	 * Finds a borrower in the database corresponding to a given borrower no and
	 * returns a corresponding LoggedInBorrower object with the resulting row from
	 * the db translated to attributes
	 * 
	 * @param out        PrintWriter
	 * @param borrowerNo borrower to find in db
	 * @return
	 */
	private LoggedInBorrower loginBorrower(PrintWriter out, String borrowerNo) {
		Connection con = DatabaseAccess.getDatabaseConnection(out);

		LoggedInBorrower borrower = null;

		// find the borrower in the db
		try {
			String query = "SELECT Borrower.Name, Borrower.Department FROM Borrower" + " WHERE Borrower.BorrowerNo = '"
					+ Integer.parseInt(borrowerNo) + "';";

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			boolean match;
			match = rs.next(); // rs.next returns false if there is no row in the result set

			if (!match) {
				out.println("<b>No such borrower with Borrower No " + borrowerNo + "</b><p>");
			} else {
				// set the attributes for the LoggedInBorrower
				String name = rs.getString("Name");
				String department = rs.getString("Department");
				borrower = new LoggedInBorrower(borrowerNo, name, department);

			}
			stmt.close();

		} catch (SQLException e) {
			out.println("<p>SQLException in method 'loginBorrower': " + e.getMessage());
		}

		try { // Close database connection
			con.close();
		} catch (SQLException ex) {
			out.println("<p>SQLException in method 'loginBorrower': " + ex.getMessage());
		}

		return borrower;
	} // loginBorrower

}
