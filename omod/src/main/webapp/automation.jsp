<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>

<h3><spring:message code="dhisconnector.automation.heading"/></h3>

<spring:message code="dhisconnector.automation.description"/>
<form method="post">
	<br />
		<input type="checkbox" name="toogleAutomation" <c:if test="${automationEnabled}">checked="checked"</c:if>><spring:message code="dhisconnector.automation.toggleAutomation"/></input>
	<br />
    <table>
        <thead>
            <tr>
            	<th><spring:message code="dhisconnector.automation.delete"/></th>
            	<th><spring:message code="dhisconnector.automation.run"/></th>
                <th><spring:message code="dhisconnector.automation.mapping"/></th>
                <th><spring:message code="dhisconnector.automation.location"/></th>
                <th><spring:message code="dhisconnector.automation.orgUnit"/></th>
                <th><spring:message code="dhisconnector.automation.reRun"/></th>
            </tr>
        </thead>
        <tbody>
            <tr class="evenRow">
                <td><spring:message code="dhisconnector.automation.add"/></td>
                <td><spring:message code="dhisconnector.automation.new"/></td>
                <td>
                    <select name="mapping">
                        <option></option>
                        <c:forEach items="${mappings}" var="mapping">
                            <option value="${mapping.name}.${mapping.created}">${mapping.name}</option>
                        </c:forEach>
                    </select>
                </td>
                <td>
                    <select name="location">
                        <option></option>
                        <c:forEach items="${locations}" var="location">
                            <option value="${location.uuid}">${location.name}</option>
                        </c:forEach>
                    </select>
                </td>
                <td>
                    <select name="orgUnit">
                        <option></option>
                        <c:forEach items="${orgUnits}" var="orgUnit">
                            <option value="${orgUnit.id}">${orgUnit.name}</option>
                        </c:forEach>
                    </select>
                </td>
                <td></td>
            </tr>
            <c:forEach items="${reportToDataSetMappings}" var="mpg">
                <tr class="evenRow">
                    <td><input type="checkbox" name="mappingIds" value="${mpg.id}"/></td>
                    <td><input type="checkbox" name="runs" value="${mpg.uuid}"/></td>
                    <td>${fn:substringBefore(mpg.mapping, '.')}</td>
                    <td>${mpg.location.name}</td>
                    <td>${orgUnitsByIds[mpg.orgUnitUid]}</td>
                    <td><c:if test="${not empty mpg.lastRun}"><input type="checkbox" name="reRuns" value="${mpg.uuid}"/></c:if> ${mpg.lastRun}</td>
                </tr>
           </c:forEach>
        </tbody>
    </table>
    <input type="submit" value="<spring:message code='dhisconnector.automation.submit'/>">
</form>

<c:forEach items="${postResponse}" var="resp">
	<div style="background-color: lightyellow;border: 1px dashed lightgrey;">${resp}</div><br />
</c:forEach>

<%@ include file="/WEB-INF/template/footer.jsp"%>