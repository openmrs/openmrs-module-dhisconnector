<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<openmrs:hasPrivilege privilege="View Connection,Manage Connection">
		<li <c:if test='<%= request.getRequestURI().contains("/configureServer") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/dhisconnector/configureServer/">
				<spring:message code="dhisconnector.configureServer" />
			</a>
		</li>
	</openmrs:hasPrivilege>

	<openmrs:hasPrivilege privilege="View Location Mappings,Manage Location Mappings">
		<li <c:if test='<%= request.getRequestURI().contains("/locationMapping") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/dhisconnector/locationMapping/">
				<spring:message code="dhisconnector.locationMapping" />
			</a>
		</li>
	</openmrs:hasPrivilege>

	<openmrs:hasPrivilege privilege="View Automation,Run Automation,Manage Automation">
		<li <c:if test='<%= request.getRequestURI().contains("/automation") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/dhisconnector/automation/">
				<spring:message code="dhisconnector.automation" />
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/dhis2BackupImport") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/dhis2BackupImport/"><spring:message
				code="dhisconnector.dhis2Backup.import" /></a>
	</li>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/dhis2BackupExport") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/dhis2BackupExport/"><spring:message
				code="dhisconnector.dhis2Backup.export" /></a>
	</li>
	
	<li
			<c:if test='<%= request.getRequestURI().contains("/createMapping") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/createMapping/"><spring:message
				code="dhisconnector.createMapping" /></a>
	</li>

	<li
			<c:if test='<%= request.getRequestURI().contains("/manageMappings") %>'>class="active"</c:if>>
		<a
				href="${pageContext.request.contextPath}/module/dhisconnector/manageMappings/"><spring:message
				code="dhisconnector.manageMappings" /></a>
	</li>

	<openmrs:hasPrivilege privilege="Run Reports">
		<li <c:if test='<%= request.getRequestURI().contains("/runReports") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/dhisconnector/runReports/">
				<spring:message code="dhisconnector.runReports" />
			</a>
		</li>
	</openmrs:hasPrivilege>

	<openmrs:hasPrivilege privilege="Run Failed Data">
		<li <c:if test='<%= request.getRequestURI().contains("/failedData") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/dhisconnector/failedData/">
				<spring:message code="dhisconnector.failedData" />
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<!-- Add further links here -->
</ul>
<h2>
	<spring:message code="dhisconnector.title" />
</h2>
