<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require anyPrivilege="View Location Mappings,Manage Location Mappings" otherwise="/login.htm"
                 redirect="/module/dhisconnector/locationMapping.form"/>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/flexboxgrid.min.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/jquery-2.2.0.min.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector-locationmapping.js"/>

<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
    <c:redirect url="../../login.htm"/>
</c:if>

<h3><spring:message code="dhisconnector.locationMapping"/></h3>

<form method="post" onsubmit="fetchLocationMappings()">
    <input name="locationMappings" type="hidden" value="">
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
                <td id="location_${location.uuid}" name="${location.uuid}">${location.name}</td>
                <c:set value="" var="savedOrgUnitUuid"/>
                <c:forEach items="${locationToOrgUnitMappings}" var="locationToOrgUnitMapping">
                    <c:if test="${location.id == locationToOrgUnitMapping.location.id}">
                        <c:set value="${locationToOrgUnitMapping.orgUnitUid}" var="savedOrgUnitUuid"/>
                    </c:if>
                </c:forEach>
                <input name="savedOrgUnitUuidOf_${location.uuid}" type="hidden" value="${savedOrgUnitUuid}">
                <td>
                    <select name="orgUnitOf_${location.uuid}"
                            <openmrs:hasPrivilege privilege="Manage Location Mappings" inverse="true">disabled</openmrs:hasPrivilege>>
                        <option value=" "> </option>
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
    <openmrs:hasPrivilege privilege="Manage Location Mappings">
        <input type="submit" value="<spring:message code='dhisconnector.save'/>">
    </openmrs:hasPrivilege>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
