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
	<title>Registration form</title>
<style>
	.text_center{
		text-align:center;
	}
</style>
</head>
<body>
	<form method="post" action="${contextPath }/member/addMember.do">
		<h1 class="text_center">Registration page</h1>
		<table align="center">
			<tr>
				<td width="200"><p align="right">ID</td>
				<td width="400"><input type="text" name="id"/></td>
			</tr>
			<tr>
				<td width="200"><p align="right">Password</td>
				<td width="400"><input type="password" name="pwd"/></td>
			</tr>
			<tr>
				<td width="200"><p align="right">Name</td>
				<td width="400"><input type="text" name="name"/></td>
			</tr>
			<tr>
				<td width="200"><p align="right">email</td>
				<td width="400"><input type="text" name="email"/></td>
			</tr>
			<tr>
				<td width="200"><p>&nbsp;</p></td>
				<td width="400"><input type="submit" value="Register"/>
				<input type="reset" value="reset"></td>
			</tr>
		</table>
	</form>

</body>
</html>
