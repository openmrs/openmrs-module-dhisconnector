<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require anyPrivilege="View Automation,Run Automation,Manage Automation" otherwise="/login.htm"
                 redirect="/module/dhisconnector/automation.form"/>

<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>

<h3><spring:message code="dhisconnector.automation.heading"/></h3>

<spring:message code="dhisconnector.automation.description"/>
<form method="post">
	<br />
    <input type="checkbox" name="toogleAutomation" <c:if test="${automationEnabled}">checked="checked"</c:if>
           <openmrs:hasPrivilege privilege="Manage Automation" inverse="true">disabled</openmrs:hasPrivilege>>
        <spring:message code="dhisconnector.automation.toggleAutomation"/>
    </input>
    <br />
    <br />
    <spring:message code="dhisconnector.automation.periodTypeMessage"/>
    <br />
    <table>
        <thead>
            <tr>
                <openmrs:hasPrivilege privilege="Manage Automation">
            	    <th><spring:message code="dhisconnector.automation.delete"/></th>
                </openmrs:hasPrivilege>
                <openmrs:hasPrivilege privilege="Run Automation">
            	    <th><spring:message code="dhisconnector.automation.run"/></th>
                </openmrs:hasPrivilege>
                <th><spring:message code="dhisconnector.automation.mapping"/></th>
                <openmrs:hasPrivilege privilege="Run Automation">
                    <th><spring:message code="dhisconnector.automation.reRun"/></th>
                </openmrs:hasPrivilege>
            </tr>
        </thead>
        <tbody>
            <openmrs:hasPrivilege privilege="Manage Automation">
                <tr class="evenRow">
                    <td><spring:message code="dhisconnector.automation.add"/></td>
                    <openmrs:hasPrivilege privilege="Run Automation">
                        <td><spring:message code="dhisconnector.automation.new"/></td>
                    </openmrs:hasPrivilege>
                    <td>
                        <select name="mapping">
                            <option></option>
                            <c:forEach items="${mappings}" var="mapping">
                                <option value="${mapping.name}.${mapping.created}">${mapping.name}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td></td>
                </tr>
            </openmrs:hasPrivilege>
            <c:forEach items="${reportToDataSetMappings}" var="mpg">
                <tr class="evenRow">
                    <openmrs:hasPrivilege privilege="Manage Automation">
                        <td><input type="checkbox" name="mappingIds" value="${mpg.id}"/></td>
                    </openmrs:hasPrivilege>
                    <openmrs:hasPrivilege privilege="Run Automation">
                        <td><input type="checkbox" name="runs" value="${mpg.uuid}"/></td>
                    </openmrs:hasPrivilege>
                    <td>${fn:substringBefore(mpg.mapping, '.')}</td>
                    <openmrs:hasPrivilege privilege="Run Automation">
                        <td><c:if test="${not empty mpg.lastRun}"><input type="checkbox" name="reRuns" value="${mpg.uuid}"/></c:if> ${mpg.lastRun}</td>
                    </openmrs:hasPrivilege>
                </tr>
           </c:forEach>
        </tbody>
    </table>
    <openmrs:hasPrivilege privilege="Run Automation,Manage Automation">
        <input type="submit" value="<spring:message code='dhisconnector.automation.submit'/>">
    </openmrs:hasPrivilege>
</form>

<c:forEach items="${postResponse}" var="resp">
	<div style="background-color: lightyellow;border: 1px dashed lightgrey;">${resp}</div><br />
</c:forEach>

<%@ include file="/WEB-INF/template/footer.jsp"%>
