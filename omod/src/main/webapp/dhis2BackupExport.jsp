<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:require privilege="Manage Mappings" otherwise="/login.htm"
	redirect="/module/dhisconnector/dhis2BackupExport.jsp" />

<openmrs:htmlInclude
	file="/moduleResources/dhisconnector/dhisconnector.css" />

<%@ include file="template/localHeader.jsp"%>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>

<div class="error-encountered">${failureEncountered}</div>
<div class="success-encountered">${successEncountered}</div>

<h3>
	<spring:message code="dhisconnector.dhis2backup.exportBackup" />
</h3>
<c:choose>
	<c:when test="${dhis2BackupExists == 'true'}">
		<form method="POST" id="dhis2api-exportForm">
			<spring:message code="dhisconnector.dhis2backup.lastSyncedAt" />
			<b>${lastSyncedAt}</b> <br />
			<br /> <input type="submit"
				value="<spring:message code='dhisconnector.export'/>">
		</form>
	</c:when>
	<c:otherwise>
		<spring:message code="dhisconnector.dhis2backup.noneExists" />
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>
<%@ include file="jembiOpenMRSFooter.jsp"%>
