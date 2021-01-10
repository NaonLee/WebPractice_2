<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<%
   request.setCharacterEncoding("UTF-8");
%>   

<!DOCTYPE html>
<html>
<head>
	<title>Login page</title>
	<!-- if there is a value in 'result' from login.do, check -->	
	<c:choose>
		<c:when test="${result=='loginFailed' }">
			<script>
				window.onload=function(){
					alert("Please check id and password.");
				}
			</script>
		</c:when>
	</c:choose>
</head>
<body>

	<form name="frmLogin" method="post" action="${contextPath}/member/login.do">
		<table border="1" width="80%" align="center">
			<tr align="center">
				<td>Id</td>
				<td><input type="text" name="id" size="30"></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="password" name="pwd" size="30"></td>
			</tr>
			<tr align="center">
				<td colspan="2">
					<input type="submit" value="Login">
					<input type="reset" value="Reset">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>