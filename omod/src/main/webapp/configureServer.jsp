<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require anyPrivilege="View Connection,Manage Connection" otherwise="/login.htm"
                 redirect="/module/dhisconnector/configureServer.form"/>

<%@ include file="template/localHeader.jsp" %>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/configure-server.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/jquery.monthpicker.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/moment.min.js"/>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>
<h3><spring:message code="dhisconnector.configureServer"/></h3>

<form method="POST">
  <table>
    <tbody>
    <tr>
      <td><spring:message code="dhisconnector.url"/></td>
      <td><input name="url" type="text" size="30" value="${url}"/></td>
    </tr>
    <tr>
      <td><spring:message code="dhisconnector.user"/></td>
      <td><input name="user" type="text" size="20" value="${user}"/></td>
    </tr>
    <openmrs:hasPrivilege privilege="Manage Connection">
      <tr>
        <td><spring:message code="dhisconnector.pass"/></td>
        <td><input name="pass" placeholder="<hidden>" type="password" size="20"/></td>
      </tr>
    </openmrs:hasPrivilege>
      <tr>
        <td/>
        <td>
          <openmrs:hasPrivilege privilege="Manage Connection">
            <input name="saveConfig" type="submit" value="<spring:message code="dhisconnector.save" />"/>
          </openmrs:hasPrivilege>
          <input name="testConfig" type="submit" value="<spring:message code="dhisconnector.testConnection" />"/>
        </td>
      </tr>
    </tbody>
  </table>
  </form>
  
    <h3><spring:message code="dhisconnector.configure.reportExecutionPeriod"/></h3>
  <form method="POST">
    <table>
    <tbody>
    <tr>
      <td><spring:message code="dhisconnector.openmrsStartDate"/></td>
      <td><input type="text" name="startDate" id="openmrs-start-date" class="periodSelector" value="${startDate}"/></td>
    </tr>
    <tr>
      <td><spring:message code="dhisconnector.openmrsEndDate"/></td>
      <td><input type="text" name="endDate" id="openmrs-end-date" class="periodSelector" value="${endDate}"/></td>
    </tr>
      <tr>
        <td/>
        <td>
            <input name="savePeriod" type="submit" value="<spring:message code="dhisconnector.save" />"/>
        </td>
      </tr>
    </tbody>
  </table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
