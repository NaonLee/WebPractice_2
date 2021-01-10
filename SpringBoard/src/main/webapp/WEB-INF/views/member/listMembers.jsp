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
	<title>Members list</title>
</head>
<body>
	<table border="1" align="center" width="80%">
		<tr align="center bgcolor="lightgreen">
			<td><b>ID</b></td>
			<td><b>Password</b></td>
			<td><b>Name</b></td>
			<td><b>Email</b></td>
			<td><b>Join Date</b></td>
			<td><b>Delete</b></td>
		</tr>
		<c:forEach var="member" items="${membersList}">
			<tr align="center">
				<td>${member.id}</td>
				<td>${member.pwd}</td>
				<td>${member.name}</td>
				<td>${member.email}</td>
				<td>${member.joinDate}</td>
				<td><a href="${contextPath}/member/removeMember.do?id=${member.id}">Delete</a></td>
			</tr>
		</c:forEach>
	</table>
	
	<a href="${contextPath}/member/memberForm.do"><h1 style="text-align:center">Registration</h1></a>
</body>
</html>
