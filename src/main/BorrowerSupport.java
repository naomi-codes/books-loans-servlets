package main;

/**
 *  Author: 2425693
 *  
 *  class BorrowerSupport
 *  
 *  Serlvet that is accessed on clicking the LibraryLinks.html borrower support button
 *  Responsible for checking whether or not a session exists and redirecting to either
 *  the VerifyBorrower servlet or LoginForm.html depending whether or a session still 
 *  exists for a logged in user.
 *  
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import borrower.LoggedInBorrower;

@WebServlet("/BorrowerSupport")
public class BorrowerSupport extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		LibraryUtility.doHeader(out, "Borrower Support"); // print html header elements down to body tag

		HttpSession session = request.getSession(); // get the session
		ServletContext sc = getServletContext(); // get the servlet context for forwarding requests and responses

		if (session.getAttribute("LoggedInBorrower") == null) { // if there is no LoggedInBorrower associated with the
																// session

			session.invalidate(); // invalidate the session to ensure any previous attributes are discarded
			RequestDispatcher rd = sc.getRequestDispatcher("/LoginForm.jsp");
			rd.forward(request, response); // redirect to the login form

		} else {

			// get the existing borrower from the session
			LoggedInBorrower existingSessionBorrower = (LoggedInBorrower) session.getAttribute("LoggedInBorrower");

			// get the borrowerNo from the logged in borrower
			String borrowerNo = existingSessionBorrower.getBorrowerNo();

			// add the borrowerNo as an attribute to the request
			request.setAttribute("borrowerNo", borrowerNo);

			// forward the request and response to the VerifyBorrower page
			RequestDispatcher rd = sc.getRequestDispatcher("/VerifyBorrower");
			rd.forward(request, response);
		}

		LibraryUtility.doFooter(out);

	} // doGet

} // class
