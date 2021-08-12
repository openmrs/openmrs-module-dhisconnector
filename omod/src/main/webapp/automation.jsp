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
    <input type="checkbox" name="toggleAutomation" <c:if test="${automationEnabled}">checked="checked"</c:if>
           <openmrs:hasPrivilege privilege="Manage Automation" inverse="true">disabled</openmrs:hasPrivilege>>
        <spring:message code="dhisconnector.automation.toggleAutomation"/>
    </input>
    <openmrs:hasPrivilege privilege="Manage Automation">
        <input name="saveAutomationToggle" type="submit" value="<spring:message code="dhisconnector.save" />"/>
    </openmrs:hasPrivilege>
    <br />
    <br />
    <openmrs:hasPrivilege privilege="Manage Automation">
        <span>New mapping</span>
        <span>
            <select name="mapping">
                <option></option>
                <c:forEach items="${mappings}" var="mapping">
                    <option value="${mapping.name}.${mapping.created}">${mapping.name}</option>
                </c:forEach>
            </select>
        </span>
        <input name="addMapping" type="submit" value="<spring:message code="dhisconnector.automation.add" />"/>
    </openmrs:hasPrivilege>
    <br />
    <br />
    <c:if test="${not empty reportToDataSetMappings}">
        <table>
            <thead>
                <tr>
                    <th></th>
                    <th><spring:message code="dhisconnector.automation.mapping"/></th>
                    <th></th>
                    <th><spring:message code="dhisconnector.automation.lastRun"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${reportToDataSetMappings}" var="mpg">
                    <tr class="evenRow">
                        <td>
                            <openmrs:hasPrivilege privilege="Manage Automation,Run Automation">
                                <input type="checkbox" name="mappingIds" value="${mpg.uuid}"/>
                            </openmrs:hasPrivilege>
                        </td>
                        <td>${fn:substringBefore(mpg.mapping, '.')}</td>
                        <td></td>
                        <td>${mpg.lastRun}</td>
                    </tr>
               </c:forEach>
            </tbody>
        </table>
        <br />
        <openmrs:hasPrivilege privilege="Run Automation">
            <input name="run" type="submit" value="<spring:message code='dhisconnector.automation.runSelected'/>">
        </openmrs:hasPrivilege>
        <openmrs:hasPrivilege privilege="Manage Automation">
            <input name="delete" type="submit" value="<spring:message code='dhisconnector.automation.delete'/>">
        </openmrs:hasPrivilege>
    </c:if>
</form>

<c:forEach items="${postResponse}" var="resp">
	<div style="background-color: lightyellow;border: 1px dashed lightgrey;">${resp}</div><br />
</c:forEach>

<%@ include file="/WEB-INF/template/footer.jsp"%>
