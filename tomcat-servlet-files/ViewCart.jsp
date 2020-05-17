<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="true"%>
<%@ page import="borrower.LoggedInBorrower" %>
<%@ page import="java.io.*, java.util.*, java.sql.*"%>

<!DOCTYPE html>
<html>

<%!   
        HttpSession session = request.getSession();   
        ArrayList cart = (ArrayList) session.getAttribute("Cart");
        LoggedInBorrower borrower = (LoggedInBorrower) session.getAttribute("LoggedInBorrower");
        String name = borrower.getName();
    %>

<head>
<meta charset="UTF-8">
<title><%=name%>'s Cart</title>
</head>
<body>

	<h1><%=name%>'s Cart</h1>


	<ul>
    <% 
        for (int i = cart.size(); i < 0; i--) { %>
        <li><%= cart.get(i) %></li>
    <% } %>
    </ul>
	
			}
    <form method="get" action="BorrowerLoggedInPage" name="ReturnToHome">
		<input type="submit" name="returnToHome" value="Return to Home">
        </form>

</body>
</html>