<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/flexboxgrid.min.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.css"/>

<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
    <c:redirect url="../../login.htm"/>
</c:if>

<h3><spring:message code="dhisconnector.locationMapping"/></h3>

<form method="post">
    <table id="locationMappingTable">
        <thead>
        <tr>
            <th><spring:message code="dhisconnector.locationMapping.location"/></th>
            <th><spring:message code="dhisconnector.locationMapping.orgUnit"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${locations}" var="location">
            <tr class="evenRow">
                <td id="location_${location.uuid}">${location.name}</td>
                <c:set value="" var="savedOrgUnitUuid"/>
                <c:forEach items="${locationToOrgUnitMappings}" var="locationToOrgUnitMapping">
                    <c:if test="${location.id == locationToOrgUnitMapping.location.id}">
                        <c:set value="${locationToOrgUnitMapping.orgUnitUid}" var="savedOrgUnitUuid"/>
                    </c:if>
                </c:forEach>
                <td>
                    <select name="orgUnitOf_${location.uuid}">
                        <option></option>
                        <c:forEach items="${orgUnits}" var="orgUnit">
                            <c:set value="" var="isSelected"/>
                            <c:if test="${orgUnit.id == savedOrgUnitUuid}">
                                <c:set value="selected" var="isSelected"/>
                            </c:if>
                            <option ${isSelected} value="${orgUnit.id}">${orgUnit.name}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <input type="button" value="<spring:message code='dhisconnector.save'/>">
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
