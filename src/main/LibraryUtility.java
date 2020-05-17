package main;

/**
 * Author 2425693
 * 
 * Class to hold eLibrary utility functions such as check number print html header and footer
 */

import java.io.*;
import javax.servlet.http.*;

public class LibraryUtility extends HttpServlet {

	public static void doHeader(PrintWriter out, String title) throws IOException {
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" " + "content=\"text/html; charset=iso-8859-1\">");
		out.println("<title>" + title + "</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>" + title + "</h1>");
	} // doHeader

	public static void doFooter(PrintWriter out) throws IOException {
		out.println("<p>These pages are maintained by 2425693 <hr>");
		out.println("</body>");
		out.println("</html>");
	} // doFooter

	// For checking a string to see if it is a valid number
	public static int checkNumber(String s) {
		// return 0 if zero length
		// 1 if non-digits
		// 2 if OK
		// 3 if longer than 3 digits
		if (s.length() == 0) {
			return 0;
		} else if (s.length() > 3) {
			return 3;
		} else {
			try {
				Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return 1;
			}
		}
		return 2;
	} // checkNumber

}// class
