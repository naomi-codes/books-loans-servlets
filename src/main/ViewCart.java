package main;

/**
 * Author 2425693
 * 
 * Servlet which gives a borrower a view of the books in the cart
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cart.Book;
import cart.Cart;
import user.LoggedInBorrower;

@WebServlet("/ViewCart")
public class ViewCart extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		LibraryUtility.doHeader(out, "View Cart Functions"); // print html header elements

		HttpSession session = request.getSession();
		ServletContext sc = getServletContext();

		// if there is no LoggedInBorrower redirect to login
		if (session.getAttribute("LoggedInBorrower") == null) {

			redirectSession(request, response, out, sc);

		} else if (session.getAttribute("Cart") == null) { // if there is no cart redirect to borrower home

			RequestDispatcher rd = sc.getRequestDispatcher("/BorrowerLoggedInPage");
			rd.forward(request, response);

		} else {

			continueSession(request, response, out, session, sc);
		}

	} // doGet

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
	private void continueSession(HttpServletRequest request, HttpServletResponse response, PrintWriter out,
			HttpSession session, ServletContext sc) throws ServletException, IOException {
		// print return to home form
		out.println("<p><form action=\"BorrowerLoggedInPage\"" + "method=\"post\" name=\"BackToHomeForm\">");
		out.println("<br><input type=\"submit\" name=\"backToHome\" value=\"Back to Home\">" + "</form>");

		// if borrower clicks "Remove Selection" remove books for the cart
		if (request.getParameter("removeSelection") != null) {
			Cart previousCart = (Cart) session.getAttribute("Cart");
			String[] booksToRemove = request.getParameterValues("booksToRemove");

			if (booksToRemove != null) {
				out.println("<p><b>No books to remove.</b></p>");
				for (String book : booksToRemove)
					out.println("<p>" + book + "</p>");
				previousCart.removeBooks(booksToRemove);

			}
			// if borrower clicks "Checkout" redirect to checkout servlet
		} else if (request.getParameter("checkoutCart") != null) {
			RequestDispatcher rd = sc.getRequestDispatcher("/CheckoutCart");
			rd.forward(request, response);
		}

		LoggedInBorrower borrower = (LoggedInBorrower) session.getAttribute("LoggedInBorrower");
		String name = borrower.getName();
		String borrowerNo = borrower.getBorrowerNo();

		// display the cart
		Cart cart = (Cart) session.getAttribute("Cart");
		ArrayList<Book> booksInCart = cart.getBooks();

		if (booksInCart.isEmpty()) {
			out.println("<p><b>No books in your cart.</b></p>");
		} else {
			out.println("<h2>Borrower: " + name + " <em>(" + borrowerNo + ")</em></h2>");

			out.println("<p><form action=\"" + response.encodeURL("ViewCart") + "\" "
					+ "method=\"get\" name=\"UpdateCartForm\">");

			out.println("<table border>");

			for (int i = 0; i < booksInCart.size(); i++) {
				out.println("<tr><td>" + booksInCart.get(i).getAuthor() + "</td>" + "<td>"
						+ booksInCart.get(i).getTitle() + "</td>"
						+ "<td><input type=\"checkbox\" name=\"booksToRemove\" value=\"" + booksInCart.get(i).getTitle()
						+ "_" + booksInCart.get(i).getAuthor() + "\"></td></tr>");
			}

			out.println("</table>");
			if (booksInCart.size() > 0) {
				out.println("<br><input type=\"submit\" name=\"removeSelection\" value=\"Remove selection\">"
						+ "<input type=\"submit\" name=\"checkoutCart\" value=\"Checkout cart\">" + "</form>");
			}
		}
		LibraryUtility.doFooter(out);
	} // continueSession

	private void redirectSession(HttpServletRequest request, HttpServletResponse response, PrintWriter out,
			ServletContext sc) throws ServletException, IOException {
		out.println("<p>made it here borrower == null</p>");
		request.setAttribute("SessionTimeout", "timed out");
		RequestDispatcher rd = sc.getRequestDispatcher("/LoginForm.jsp");
		rd.forward(request, response);
	} // redirectSession
}
