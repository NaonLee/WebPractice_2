<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<%
  request.setCharacterEncoding("UTF-8");
%>  

<html>
<head>
  <style>
   .cls1 {text-decoration:none;}
   .cls2{text-align:center; font-size:30px;}
  </style>
	<title>Articles list</title>
	<script>
		function fn_articleForm(isLogOn,articleForm,loginForm){
			if(isLogOn != '' && isLogOn != 'false'){			/* If user is logged in, move to article Form(to write) */
				location.href=articleForm;
			} else{												/* if user isn't logged in, give an alert */
				alert("You can write an article after login.");
				/* and move page to the login form with action set /board/articleForm.do to come back after login */
				location.href=loginForm+ '?action=/board/articleForm.do';	
			}
		}
	</script>
</head>
<body>
	<table align="center" border="1" width="80%">
		<tr height="10" align="center" bgcolor="lightgreen">
			<td>Article No</td>
			<td>Writer</td>
			<td>Title</td>
			<td>Written date</td>
		</tr>
		<c:choose>
			<c:when test="${articlesList==null}">	<!-- if the articles list is empty -->
				<tr height="10">
					<td colspan="4"><p align="center">
						<b><span style="font-size:9pt;">There is no article.</span></b>
					</p></td>
				</tr>
			</c:when>
			<c:when test="${articlesList != null}">
				<c:forEach var="article" items="${articlesList}" varStatus="articleNum">
					<tr align="center">
						<td width="5%">${articleNum.count}</td>
						<td width="10%">${article.id}</td>
						<td align="left" width="35%">
							<span style="padding-right:30px"></span>
							<c:choose>
								<c:when test="${article.level} > 1">						<!-- if level isn't 0 (it's answer) -->
									<c:forEach begin="1" end="${article.level}" step="1">
										<span style="padding-left:20px"></span>				<!-- give a padding as many as answer level -->
									</c:forEach>
									<span style="font-size:12px;">[]Answer]</span>
									<a class="cls1" href="${contextPath}/board/viewArticle.do?articleNO=${article.articleNO}">${article.title}</a>
								</c:when>
								<c:otherwise>	<!-- if article is not an answer, just print out -->
									<a class="cls1" href="${contextPath}/board/viewArticle.do?articleNO=${article.articleNO}">${article.title}</a>
								</c:otherwise>
							</c:choose>
						</td>
						<td width="10%">${article.writeDate}</td>
					</tr>
				</c:forEach>
			</c:when>
		</c:choose>
	</table>
	
	<a class="cls1" href="javascript:fn_articleForm('${isLogOn}', '${contextPath}/board/articleForm.do', '${contextPath}/member/loginForm.do')"><p class="cls2">Write article</a>
</body>
</html>
