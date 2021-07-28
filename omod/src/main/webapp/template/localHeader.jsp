<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<openmrs:hasPrivilege privilege="View Connection,Manage Connection">
		<li <c:if test='<%= request.getRequestURI().contains("/configureServer") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/dhisconnector/configureServer.form">
				<spring:message code="dhisconnector.configureServer" />
			</a>
		</li>
	</openmrs:hasPrivilege>

	<li
        <c:if test='<%= request.getRequestURI().contains("/locationMapping") %>'>class="active"</c:if>>
		<a
        href="${pageContext.request.contextPath}/module/dhisconnector/locationMapping.form"><spring:message
		        code="dhisconnector.locationMapping" /></a>
	</li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/automation") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/dhisconnector/automation.form"><spring:message
				code="dhisconnector.automation" /></a>
	</li>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/dhis2BackupImport") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/dhis2BackupImport.form"><spring:message
				code="dhisconnector.dhis2Backup.import" /></a>
	</li>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/dhis2BackupExport") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/dhis2BackupExport.form"><spring:message
				code="dhisconnector.dhis2Backup.export" /></a>
	</li>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/uploadMapping") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/uploadMapping.form"><spring:message
				code="dhisconnector.uploadMapping" /></a>
	</li>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/exportMappings") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/exportMappings.form"><spring:message
				code="dhisconnector.exportMapping" /></a>
	</li>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/createMapping") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/createMapping.form"><spring:message
				code="dhisconnector.createMapping" /></a>
	</li>

	<li
			<c:if test='<%= request.getRequestURI().contains("/manageMappings") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/manageMappings.form"><spring:message
				code="dhisconnector.manageMappings" /></a>
	</li>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/runReports") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/runReports.form"><spring:message
				code="dhisconnector.runReports" /></a>
	</li>
	<li
			<c:if test='<%= request.getRequestURI().contains("/failedData") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/failedData.form"><spring:message
				code="dhisconnector.failedData" /></a>
	</li>
	
	<!-- Add further links here -->
</ul>
<h2>
	<spring:message code="dhisconnector.title" />
</h2>
