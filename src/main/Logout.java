package main;

/**
 * Author 2425693
 * 
 * Servlet which allows the user to logout. Invalidates 
 * the session and redirects user to the login page
 */
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Logout")
public class Logout extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		LibraryUtility.doHeader(out, "Borrower Support");

		HttpSession session = request.getSession(false);

		session.invalidate();

		out.print("You are successfully logged out!");

		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher("/LoginForm.jsp");
		rd.forward(request, response);
		out.close();

		LibraryUtility.doFooter(out);
	} // doGet

} // class
