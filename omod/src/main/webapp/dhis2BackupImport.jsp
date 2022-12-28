<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require privilege="Manage Mappings" otherwise="/login.htm"
                 redirect="/module/dhisconnector/dhis2BackupImport.jsp"/>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.css"/>

<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>

<div class="error-encountered">${failureEncountered}</div>
<div class="success-encountered">${successEncountered}</div>
	

<h3><spring:message code="dhisconnector.dhis2backup.importBackup"/></h3>
<form method="POST" id="dhis2api-importForm" enctype="multipart/form-data">
	<spring:message code="dhisconnector.dhis2backup.archiveToUpload"/> <input type="file" name="dhis2APIbBackup"><br /><br />
	<input type="submit" value="<spring:message code='dhisconnector.upload'/>" >
</form>


<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
