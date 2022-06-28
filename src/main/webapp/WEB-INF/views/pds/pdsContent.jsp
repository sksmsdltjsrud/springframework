<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<% pageContext.setAttribute("newLine", "\n"); %>
<c:set var="ctp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>pdsContent.jsp</title>
  <jsp:include page="/WEB-INF/views/include/bs4.jsp"/>
</head>
<body>
<p><br/></p>
<div class="container">
  <div class="modal-dialog">
    <div class="modal-content">
    
      <!-- Modal Header -->
      <div class="modal-header">
        <h4 class="modal-title">${vo.title} (분류 : ${vo.part})</h4>
        <button type="button" class="close" onclick="window.close()">&times;</button>
      </div>
      
      <!-- Modal body -->
      <div class="modal-body">
        <hr/>
        - <b>올린이</b> : ${vo.nickName} 
        <hr/>
        - <b>아이디</b> : ${vo.mid}<br/>
        - <b>파일명</b> : ${vo.FName}<br/>
        - <b>파일크기</b> : <fmt:formatNumber value="${vo.FSize / 1024}" pattern="#,##0"/>KByte<br/>
        - <b>올린날짜</b> : ${vo.FDate}<br/>
        - <b>다운로드수</b> : ${vo.downNum}<br/>
        - <b>자료설명</b> : <br/>
        <p>${fn:replace(vo.content, newLine, "<br/>")}</p>
        <hr/>
        <c:set var="fNames" value="${fn:split(vo.FName,'/')}"/> <!-- DB에 저장 -->
        <c:set var="fSNames" value="${fn:split(vo.FSName,'/')}"/> <!-- 시스템에 저장 -->
        <c:forEach var="fSName" items="${fSNames}" varStatus="st">
        	${st.count}. ${fSName}<br/>
			    <c:set var="fSNameLen" value="${fn:length(fSName)}"/>
			  	<c:set var="ext" value="${fn:substring(fSName,fSNameLen-3,fSNameLen)}"/>
			  	<c:set var="extUpper" value="${fn:toUpperCase(ext)}"/>
			  	<c:if test="${extUpper=='JPG' || extUpper=='GIF' || extUpper=='PNG'}">
			  		<img src="${ctp}/pds/${fSName}" width="355px"/>
			  	</c:if>
	  			<hr/>
        </c:forEach>
        
      </div>
      
      <!-- Modal footer -->
      <div class="modal-footer">
        <button type="button" class="btn btn-danger" onclick="window.close()" >Close</button>
      </div>
      
    </div>
  </div>
</div>
<p><br/></p>
</body>
</html>