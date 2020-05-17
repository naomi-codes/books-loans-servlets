package main;

/**
 *  Author: 2425693
 *  
 *  class BorrowerLoggedInPage
 *  
 *  Servlet responsible for displaying all the loans held in the database whether current or past
 *  Accessible via logging in to the site via borrower support link in LibraryLinks.html
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

import cart.Cart;
import user.LoggedInBorrower;

@WebServlet("/BorrowerLoggedInPage")
public class BorrowerLoggedInPage extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// handles if the browser is refreshed
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter(); // set the print writer to print to the viewed web page
		LibraryUtility.doHeader(out, "Library Borrower Support Functions"); // print html header elements up to body tag

		HttpSession session = request.getSession(); // get the current session
		ServletContext sc = getServletContext(); // get the servlet context

		if (session.getAttribute(("LoggedInBorrower")) == null) { // if there is no borrower logged in...
			redirectSession(request, response, sc);
		} else { // ...otherwise

			continueSession(request, response, out, session, sc);
		}

	} // doGet

	private void continueSession(HttpServletRequest request, HttpServletResponse response, PrintWriter out,
			HttpSession session, ServletContext sc) throws ServletException, IOException {

		LoggedInBorrower loggedInBorrower = (LoggedInBorrower) session.getAttribute("LoggedInBorrower"); // get the
																											// logged in
																											// borrower's
																											// borrowerNo
		String name = loggedInBorrower.getName(); // get the borrower's name

		// print welcome page details so the borrower knows they are logged in to their
		// account
		out.println("<h3>Welcome, " + name + " <em>(" + loggedInBorrower.getBorrowerNo() + ")</em> </h2>");

		// print a button allowing the user to view their library record via the
		// ViewLibraryRecord servlet
		out.println("<form method=\"get\" action=\"ViewLibraryRecord\">");
		out.println("<input type=\"submit\" name=\"ViewLibraryRecord\" value=\"View Loans History\">");
		out.println("</form>");

		if (request.getParameter("addToCart") != null) { // if the user has clicked on addToCart...

			addToCart(request, out, session);

		} else if (request.getParameter("checkoutCart") != null) { // if the borrower clicks "Checkout"

			checkoutCart(request, response, out, session, sc);

		} else if (request.getParameter("viewCart") != null) { // otherwise. if the borrower clicks "View Cart"

			RequestDispatcher rd = sc.getRequestDispatcher("/ViewCart");
			rd.forward(request, response); // forward the request to the ViewCart servlet
		}

		// display the books in the database even if they are not available
		displayBooksInDB(out, response);

		if (session.getAttribute("Cart") != null) { // if there currently a cart as a session attribute...
			Cart cart = (Cart) session.getAttribute("Cart");
			if (cart.getBooks().size() > 0)
				displayCartOptions(out, response); // add the "View Cart" and "Checkout" buttons

		}

		out.println("<hr>");

		// print the form displaying the logout button which returns the borrower to the
		// login page
		// via the Logout servlet
		out.println("<p><form action=\"Logout\"" + "method=\"get\" name=\"logOutForm\">");
		out.println("<br><input type=\"submit\" name=\"logOut\" value=\"Logout\">" + "</form>");

		out.println("<a href=\"LibraryLinks.html\">Back to Library Links</a>");
		LibraryUtility.doFooter(out);
	} // continueSession

	/**
	 * 
	 * @param request
	 * @param response
	 * @param sc
	 * @throws ServletException
	 * @throws IOException
	 */
	private void redirectSession(HttpServletRequest request, HttpServletResponse response, ServletContext sc)
			throws ServletException, IOException {
		request.setAttribute("SessionTimeout", "timed out"); // ... add an attirbute to the session to say it has timed
																// out
		RequestDispatcher rd = sc.getRequestDispatcher("/LoginForm.jsp"); // set the disptacher target to the login page
		rd.forward(request, response); // forward the request to the login page
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @param session
	 * @param sc
	 * @throws ServletException
	 * @throws IOException
	 */
	private void checkoutCart(HttpServletRequest request, HttpServletResponse response, PrintWriter out,
			HttpSession session, ServletContext sc) throws ServletException, IOException {
		if (session.getAttribute("Cart") != null) {
			RequestDispatcher rd = sc.getRequestDispatcher("/CheckoutCart");
			rd.forward(request, response); // forward the request to the CheckoutCart servlet
		} else {
			out.println("<p><b>No cart to checkout</b></p>");
		}
	} // checkoutCart

	/**
	 * 
	 * @param request
	 * @param out
	 * @param session
	 */
	private void addToCart(HttpServletRequest request, PrintWriter out, HttpSession session) {
		String[] books = request.getParameterValues("books"); // get the books selected from the displayed list of books

		if (request.getParameterValues("books") != null) { // if checkboxes were ticked for any book...

			out.println("<p><b>Your selection: </b></p>" + "<p><ul>");
			for (int i = 0; i < books.length; i++) { // tell the borrower which books they selected...
				String[] arrOfBookInfo = books[i].split("_", 2);

				String title = arrOfBookInfo[0];
				String author = arrOfBookInfo[1];

				out.println("<li>" + title + " by " + author + "</li>");
			}

			out.println("</ul></p>");

			if (session.getAttribute("Cart") == null) { // if there no cart currently in the session...

				Cart newCart = new Cart(); // ...create a new cart
				newCart.addBooks(books); // add the books to the cart
				out.println("<p>Selection sucessfully added to cart</p>"); // tell the borrower adding the books was
																			// successful
				session.setAttribute("Cart", newCart); // add the cart to the session

			} else if (session.getAttribute("Cart") != null) { // otherwise if a cart exists
				Cart existingCart = (Cart) session.getAttribute("Cart"); // get the existing cart from the session

				existingCart.addBooks(books); // add the book to the existing cart
				out.println("<p>Selection sucessfully added to cart</p>"); // tell the borrower adding the books was
																			// successful
			}
		}
	} // addToCart

	/**
	 * 
	 * @param out      PrintWriter
	 * @param response response to send to the client
	 */
	public void displayBooksInDB(PrintWriter out, HttpServletResponse response) {

		// get a connection to the database
		Connection con = DatabaseAccess.getDatabaseConnection(out);

		// print the header from the form for selecting books
		out.println("<p><B>Select book(s) to loan</B></p>");
		out.println("<form method=\"post\" action=\"" + response.encodeURL("BorrowerLoggedInPage") + "\">");

		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT DISTINCT Book.Title, Book.Author FROM Book"); // fetch books from
																									// the database

			// print books fetched from the database to the page to be displayed
			int count = 0;
			while (rs.next()) {
				count++;
				if (count == 1) {
					out.println("<input type=\"checkbox\" name=\"books\" value=\"" + rs.getString("Title") + "_"
							+ rs.getString("Author") + "\" checked  >" + rs.getString("Title") + " by "
							+ rs.getString("Author") + "<br>");
				} else {
					out.println("<input type=\"checkbox\" name=\"books\" value=\"" + rs.getString("Title") + "_"
							+ rs.getString("Author") + "\" >" + rs.getString("Title") + " by " + rs.getString("Author")
							+ "<br>");

				}

			}

			// print "Add to Cart" button to submit book selection form
			out.println("<input type=\"submit\" name=\"addToCart\" value=\"AddToCart\">");

			out.println("</form>");

			stmt.close();
		} catch (SQLException e) { // catch any error related to the sql query such as malformed syntax

			out.println("SQLException: " + e.getMessage());
		}

		try {
			con.close();
		} catch (SQLException ex) { // catch any error related to closing the database

			out.println("SQLException: " + ex.getMessage());
		}
	} // displayBooksInDB

	/**
	 * Function to display the form holding the cart option buttons "View Cart" and
	 * "Checkout"
	 * 
	 * @param out      PrintWriter
	 * @param response response to send to the client
	 */
	private void displayCartOptions(PrintWriter out, HttpServletResponse response) {
		out.println("<p><form action=\"BorrowerLoggedInPage\"" + "method=\"post\" name=\"ViewCartForm\">"
				+ "<input type=\"submit\" name=\"viewCart\" value=\"View cart\">"
				+ "<input type=\"submit\" name=\"checkoutCart\" value=\"Checkout cart\">" + "</form>");
	} // displayCartOptions
} // class
