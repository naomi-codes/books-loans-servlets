<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="true"%>
<%@ page import="java.io.*, java.util.*, java.sql.*"%>
<%@ page import="borrower.LoggedInBorrower" %>

<html lang="en" class="gr__tomcat_cs_stir_ac_uk"><head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="http://www.cs.stir.ac.uk/~sbj/styles/basic.css">
  <title>Library functions</title>
</head>
<body data-gr-c-s-loaded="true">
  <h1>Login</h1>

<%! String sessionTimeout;
    String borrowerNo;
    LoggedInBorrower borrower; %>
    
    
    <% borrower = (LoggedInBorrower) session.getAttribute("LoggedInBorrower");
        if (borrower != null) {
    
    borrowerNo = borrower.getBorrowerNo();
    request.setAttribute("borrowerNo", borrowerNo);
    ServletContext context= getServletContext();
RequestDispatcher rd= context.getRequestDispatcher("/VerifyBorrower");
rd.forward(request, response);
} %>

<% if (request.getAttribute("SessionTimeout") != null) {
sessionTimeout = request.getAttribute("SessionTimeout").toString();
}
   if (sessionTimeout != null) { %>
   <p><b>Session timed out - please login again.</b></p>
   <% } %>
  <form action="VerifyBorrower" method="post">
  <div class="container">
    <label for="borrowerNo"><b>Username</b></label>
    <input type="text" placeholder="Enter Borrower No" name="borrowerNo" required>
    <input type="reset" value="Reset">
    <input type="submit" value="Login">
  </div>
  </form>
    <p>
      <a href="LibraryLinks.html">Back to LibraryLinks</a>
        </p>
  <hr>
  <address>

    <a href="mailto:zah00003@students.stir.ac.uk">2425693</a>
  </address>


</body></html>