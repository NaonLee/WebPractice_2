<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" 
    isELIgnored="false"  %>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />

<%
  request.setCharacterEncoding("UTF-8");
%>    

<html>
<head>
	<meta charset="UTF-8">
	<title>Update member information</title>
<style>
   .text_center{
     text-align:center;
   }
</style>
</head>
<body>
	<form method="post" action="${contextPath }/member/modMember.do">
		<h1 class="text_center">Update member information</h1>
		<table align="center">
			<tr>
				<td width="200"><p align="right">ID</td>
				<td width="400"><input type="text" name="id"></td>
			</tr>
			<tr>
				<td width="200"><p align="right">Password</td>
				<td width="400"><input type="password" name="pwd"></td>
			</tr>
			<tr>
				<td width="200"><p align="right">Name</td>
				<td width="400"><input type="text" name="name"></td>
			</tr>
			<tr>
				<td width="200"><p align="right">Email</td>
				<td width="400"><input type="text" name="email"></td>
			</tr>
			<tr>
				<td width="200"><p>&nbsp;</p></td>
				<td width="400"><input type="submit" value="Update"><input type="reset" value="Reset">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
