<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require privilege="Run Failed Data" otherwise="/login.htm"
				 redirect="/module/dhisconnector/failedData.form"/>
<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>

<h3><spring:message code="dhisconnector.failedData"/></h3>
<p>${nunmberOfFailedPostAttempts} <spring:message code="dhisconnector.failedData.number"/></p>

<form method="POST">
	<input type="submit" value='<spring:message code="dhisconnector.failedData.pushAgain"/>' <c:if test="${nunmberOfFailedPostAttempts == 0}"><c:out value="disabled='disabled'"/></c:if>>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
