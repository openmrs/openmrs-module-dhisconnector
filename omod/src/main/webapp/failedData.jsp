<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
 <openmrs:require privilege="Run Failed Data" otherwise="/login.htm"
				 redirect="/module/dhisconnector/failedData.form"/> 
<%@ include file="template/localHeader.jsp" %>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector-runreports.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/highlight.min.js"/>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>

<script type="text/javascript">
       window.onload = getFailedReportDataRender;
</script>

<h3><spring:message code="dhisconnector.failedData"/></h3>
  <table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse; width: 50%;">
    <tbody id="tableBody">
    <tr><th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white;">Nome do Relatório</th><th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white; width: 20%;">Acção</th></tr>
        </tbody>
  </table>
  <br/>
<p>${nunmberOfFailedPostAttempts} <spring:message code="dhisconnector.failedData.number"/></p>
<form method="POST">
	<openmrs:hasPrivilege privilege="Run Failed Data">
	<input type="submit" name="pushAgain" value='<spring:message code="dhisconnector.failedData.pushAgain"/>' <c:if test="${nunmberOfFailedPostAttempts == 0}"><c:out value="disabled='disabled'"/></c:if>>
	</openmrs:hasPrivilege>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
