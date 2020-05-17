package main;

import java.awt.print.Book;
/**
 *  Author: 2425693
 *  
 *  class CheckoutCart
 *  
 *  Serlvet that is accessed by clicking on "Checkout" from the ViewCart servlet.
 *  Processes the users cart, checking for the availability of the books it contains.
 *  If books are not availble the user is notified.
 *  For books that are available, a loan record is created in the Loan table in the DB.
 *  The user is notified of which loans were successful.
 *  
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cart.Cart;
import user.LoggedInBorrower;

@WebServlet("/CheckoutCart")
public class CheckoutCart extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		LibraryUtility.doHeader(out, "Checkout");

		HttpSession session = request.getSession();
		ServletContext sc = getServletContext();

		if (session.getAttribute(("LoggedInBorrower")) == null) {
			request.setAttribute("SessionTimeout", "timed out");
			RequestDispatcher rd = sc.getRequestDispatcher("/LoginForm.jsp");
			rd.forward(request, response);
		} else {

			// form to allow borrower to return to their home page
			out.println("<p><form action=\"BorrowerLoggedInPage\"" + "method=\"post\" name=\"BackToHomeForm\">");
			out.println("<br><input type=\"submit\" name=\"backToHome\" value=\"Back to Home\">" + "</form>");

			// get the LoggedInBorrower associated with the current session
			LoggedInBorrower borrower = (LoggedInBorrower) session.getAttribute("LoggedInBorrower");

			// get the cart associated with the current session
			Cart cart = (Cart) session.getAttribute("Cart");

			// get the list of books from the cart
			ArrayList<cart.Book> booksToCheckout = (ArrayList)cart.getBooks();

			// create lists to hold available and unavailable books
			ArrayList<String> unavailableBooks = new ArrayList<String>();
			ArrayList<cart.Book> availableBooks = new ArrayList<cart.Book>();

			// for each book in the cart
			for (cart.Book book : booksToCheckout) {

				// look for an available copy in the database and store its BookNo
				int availableCopyNo = checkAvailability(out, book);

				// if the available BookNo is set to anything other than 0...
				if (availableCopyNo != 0) {
					book.setAvailableCopy(availableCopyNo);
					// ... add the title and the BookNo of the available copy to the avilable books
					// list
					availableBooks.add(book);
				} else {
					// ... otherwise the availbleBookNo was set to 0 and no copies were available
					unavailableBooks.add(book.getTitle() + " by " + book.getAuthor()); // add the book to the list of
																						// unavailable books
				}
			}

			// create a new record in the loans table for the available books
			createLoanRecords(out, availableBooks, borrower);

			if (unavailableBooks.size() > 0) {
				// print out the unavailable books for the borrower to see
				out.println("<p><b>The following books were unavailable: </b></p>");
				for (String phrase : unavailableBooks) {
					out.println("<p>" + phrase + "</p>");
				}
			}

			/**
			 * print out the succesfully loaned books for the borrower to see out.println("
			 * <p>
			 * Succesfully loaned the following books:
			 * </p>
			 * ");
			 * 
			 * if (availableBooks.size() > 0) { for (Book loanedBook : availableBooks) {
			 * out.println("
			 * <p>
			 * " + loanedBook.getAvailableCopy() + " " +loanedBook.getTitle() + " by " +
			 * loanedBook.getAuthor() + "
			 * </p>
			 * "); } }
			 **/

			// print the html footer elements
			LibraryUtility.doFooter(out);

		}
	} // doGet

	/**
	 * Method to check the availability a book in the database Takes the name of a
	 * book to find an available copy of as a parameter and returns the bookNo of an
	 * available copy if there is one of 0 if there is not.
	 * 
	 * @param out  PrintWriter
	 * @param book String book title to be checked for availability in the database
	 */
	public int checkAvailability(PrintWriter out, cart.Book book) {

		int availableCopy = 0;
		Connection con = DatabaseAccess.getDatabaseConnection(out);
		try {

			// find all the copies which correspond to the book title in the database
			String queryBookNos = "SELECT Book.BookNo, Book.Title " + "FROM Book " + "WHERE " + "  Book.Title = '"
					+ book.getTitle() + "' " + "	AND Book.Author = '" + book.getAuthor() + "'";

			Statement stmt = con.createStatement();
			Statement stmtBookNos = con.createStatement();
			Statement stmtExistingLoans = con.createStatement();
			ResultSet rsBookNos = stmtBookNos.executeQuery(queryBookNos);

			ArrayList<Integer> matchingCopies = new ArrayList<Integer>();

			while (rsBookNos.next()) { // while there are more copies in the result set

				// int bookNo = rsBookNos.getInt("BookNo"); //get the book no of the current
				// copy being inspected
				matchingCopies.add(rsBookNos.getInt("BookNo"));
			}

			rsBookNos.close();

			for (int i = 0; i < matchingCopies.size(); i++) {
				// check for loans with null in the Date_back column matching the current book
				// no
				String queryExistngLoans = "SELECT BookNo, Date_back " + "FROM LOAN " + "WHERE BookNo = '"
						+ matchingCopies.get(i) + "'";

				ResultSet rsExistingLoans = stmtExistingLoans.executeQuery(queryExistngLoans);

				if (!rsExistingLoans.next()) { // if there are no loans on record for the current copy...
					availableCopy = matchingCopies.get(i);

				} else { // otherwise check the matching results in the loans table for an
					// for an available copy
					while (rsExistingLoans.next()) {
						if (rsExistingLoans.getDate("Date_back") != null) { // if the loans record matching the book no
							availableCopy = matchingCopies.get(i); // has a date_back i.e. has been returned
							// return bookNo; // return the book no
						}
						rsExistingLoans.close();
					}
				}
			}
			// stmtExisting
			stmt.close(); // close the statement
		} catch (SQLException e) {
			out.println("<p>checkAvailability: SQLException: " + e.getMessage());
		}

		try { // Close the database connection
			con.close();
		} catch (SQLException ex) {
			out.println("<p>checkAvailability: SQLException: " + ex.getMessage());
		}

		// return the the copy number for an available copy, if none availalbe returns 0
		return availableCopy;

	} // checkAvailability

	/**
	 * Creates loan records in the database for books from the borrower's cart which
	 * were available for loan
	 * 
	 * 
	 * @param out            PrintWriter
	 * @param availableBooks List of available copies of books which have previously
	 *                       been found in the database
	 * @param borrower       The borrower who is trying to loan the book
	 */
	private void createLoanRecords(PrintWriter out, ArrayList<cart.Book> availableBooks, LoggedInBorrower borrower) {

		// get a connection to the database
		Connection con = DatabaseAccess.getDatabaseConnection(out);

		try {
			Statement stmt = con.createStatement();

			for (cart.Book book : availableBooks) {
				// create an update statement
				String updateString = "INSERT INTO Loan  " + "VALUES(?,?,?,?,?)";

				try {
					PreparedStatement preparedUpdateStatement = con.prepareStatement(updateString);
					preparedUpdateStatement.setInt(1, Integer.parseInt(borrower.getBorrowerNo()));
					preparedUpdateStatement.setInt(2, book.getAvailableCopy());
					preparedUpdateStatement.setDate(3, null);
					preparedUpdateStatement.setDate(4, null);
					preparedUpdateStatement.setString(5, null);

					// execute the statement, creating a loan record in the table for the current
					// book in the list of available books
					int nrows = preparedUpdateStatement.executeUpdate();
					if (nrows == 0) {
						out.println("<b>Book number " + book.getAvailableCopy() + " not available for loan</b><p>");
					} else {
						out.println("<b>Book number " + book.getAvailableCopy() + " loaned</b><p>");
					}

				} catch (SQLException e) {

					out.println("<p>createLoansRecords: SQLException: " + e.getMessage());
				}
			}

			stmt.close();
		} catch (SQLException e) {
			out.println("<p>createLoanRecords: SQLException: " + e.getMessage());
		}

		try { // Close database connection
			con.close();
		} catch (SQLException ex) {
			out.println("<p>createLoanRecords: SQLException: " + ex.getMessage());
		}
	} // createLoanRecords

} // class
