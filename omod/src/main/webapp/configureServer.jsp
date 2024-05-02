<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require anyPrivilege="View Connection,Manage Connection" otherwise="/login.htm"
                 redirect="/module/dhisconnector/configureServer.form"/>

<%@ include file="template/localHeader.jsp" %>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/configure-server.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/jquery.monthpicker.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/moment.min.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/configure-server.css"/>

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
    <br/>
      <h3><spring:message code="dhisconnector.server.configurations"/></h3>
  <form >
  <table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse; width: 50%;" id="table">
    <thead>
    <tr><th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white; width: 30%;">URL</th>
	<th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white; width: 40%;"></th>
		<th style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white; width: 10%;"></th>
    </tr>
    </thead>
    <tbody id="tableBody">
    <c:forEach items="${servers}" var="server">
    <tr style="background-color: #f2f2f2;" id="server">
    <td style="border: 1px solid #ddd; padding: 8px;"  id="col0"><input name="${server.uuid}" type="checkbox" size="20" value="${server.uuid}" checked="checked" onclick="return false;" hidden="true"/>${server.url}</td>
    <td style="border: 1px solid #ddd; padding: 8px;"  id="col1" >
    <a href ="#" onclick="toggler('${server.uuid}');">Configurar relatórios<br><br></a> 
     <span id="${server.uuid}" class="hidden">
                 <br><input name="saveServerReport" id="saveServerReport" type="button" value="Salvar Configuração" onclick="saveReportsOfTheSelectedServer(this)"/>
     </span></td>
    <td><input type="button" value="Remover" onclick="removeConfiguration(this)"/></td> 
    </tr>
            </c:forEach>
        </tbody>
  </table>
  </form>
      <br/>

  <br/>
      <a href ="#" onclick="toggler('dateConfiguration');">Configurar datas de execução dos relatórios<br></a> 
  <form method="POST">
  <span id="dateConfiguration" class="hidden">
<!--   <h3><spring:message code="dhisconnector.configure.reportExecutionPeriod"/></h3> -->
    <table>
    <tbody>
    <tr>
      <td><spring:message code="dhisconnector.openmrsStartDate"/></td>
      <td><input type="number" name="startDate" min="1" max="31" value="${startDate}"/></td>
    </tr>
    <tr>
      <td><spring:message code="dhisconnector.openmrsEndDate"/></td>
      <td><input type="number" name="endDate" min="1" max="31" value="${endDate}"/></td>
    </tr>
      <tr>
        <td/>
        <td>
            <input name="savePeriod" type="submit" value="<spring:message code="dhisconnector.save" />"/>
        </td>
      </tr>
    </tbody>
  </table>
  </span>
</form>



<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
