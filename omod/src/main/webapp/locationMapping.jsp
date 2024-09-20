<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require anyPrivilege="View Location Mappings,Manage Location Mappings" otherwise="/login.htm"
                 redirect="/module/dhisconnector/locationMapping.form"/>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/flexboxgrid.min.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/jquery-2.2.0.min.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector-locationmapping.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/configuration-server.js"/>

<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
    <c:redirect url="../../login.htm"/>
</c:if>

    <br/>
      <h3><spring:message code="dhisconnector.locationMapping.per.server"/></h3>
  <form >
  <table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse; width: 50%;" id="table">
    <thead>
    <tr><th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white;">Servidor DHIS2</th><th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white; width: 20%;">Unidade Organizacional DHIS2</th>
	<th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white; width: 20%;">Localização do OpenMRS</th>
    <th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white; width: 20%;"></th>
    </tr>
    </thead>
    <tbody id="tableBody">
         <c:forEach items="${dhisLocationToOrgUnitMappings}" var="locationOrgUnit">
    <tr style="background-color: #f2f2f2;" id="orgUnit">
    <td style="border: 1px solid #ddd; padding: 8px;"  id="col0">${locationOrgUnit.serverUrl}</td>
    <td style="border: 1px solid #ddd; padding: 8px;"  id="col1">${locationOrgUnit.orgUnitName}</td>
    <td style="border: 1px solid #ddd; padding: 8px;"  id="col2">${locationOrgUnit.location.name}</td>
    <td style="border: 1px solid #ddd; padding: 8px;"  id="col3">
    <br/>
    <input type="button" value="Remover" onclick="removeMapping('${locationOrgUnit.serverUuid},'+'${locationOrgUnit.orgUnitUid},'+'${locationOrgUnit.location.uuid}')"/>
    </td> 
    </tr>
       </c:forEach>
        </tbody>
  </table>
  </form>
        <br/>
  <form method="post" onsubmit="saveLocationOrgUnitsMappings()">
    <table id="locationMappingTable">
        <thead>
        <tr>
            <th><span  style="font-weight:bold;">Servidor DHIS2</span></th>
            <th><span  style="font-weight:bold;">Unidade Organizacional DHIS2</span></th>
            <th><span  style="font-weight:bold;">Localização do OpenMRS</span></th>
        </tr>
        </thead>
        <tbody>
            <tr>
                <td> 
     <input name="locationOrgUnitsMappings" type="hidden" value="">
     <select name="servers" id="servers" onchange="getOrgUnitsByServer(this)">
          <option value=" "> </option>
          <c:forEach items="${servers}" var="server">
              <option value="${server.uuid}">${server.url}</option>
          </c:forEach>
      </select>   
      </td>
                   <td> 
       <select name="orgUnits" id="orgUnits" style="width:100%;">
            <option value=" "> </option>
            <c:forEach items="${orgUnits}" var="orgUnit">
                <option value="${orgUnit.id}">${orgUnit.name}</option>
            </c:forEach>
        </select>      
                   </td>
                    
                 <td> 
               <span></span>
                   <select name="locations" id="locations">
                        <option value=" "> </option>
                        <c:forEach items="${locations}" var="location">
                            <option value="${location.uuid}">${location.name}</option>
                        </c:forEach>
                    </select>    

                    
                    <input type="submit" value="Adicionar" />
                </td>
            </tr>
        </tbody>
    </table>
</form>
      <br/>

<%-- <h3><spring:message code="dhisconnector.locationMapping"/></h3>

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
</form> --%>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
