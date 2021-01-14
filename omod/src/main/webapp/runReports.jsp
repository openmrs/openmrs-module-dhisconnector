<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector-runreports.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/jquery.monthpicker.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/moment.min.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/highlight.min.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/default.min.css"/>
<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
  <c:redirect url="../../login.htm" />
</c:if>

<h3><spring:message code="dhisconnector.runReports"/></h3>

<form method="POST">
  <table>
    <tbody id="tableBody">
    <tr>
      <th class="runHeader"><spring:message code="dhisconnector.report"/></th>
      <td>
        <span id="reportsSelectContainer"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/></span>
      </td>
    </tr>
    <tr>
      <th class="runHeader"><spring:message code="dhisconnector.mapping"/></th>
      <td>
        <span id="mappingSelectContainer"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/></span>
      </td>
    </tr>
    <tr>
      <th class="runHeader"><spring:message code="dhisconnector.location"/></th>
      <td>
        <table>
          <thead>
          <tr>
            <th class="runHeader">OpenMRS Location</th>
            <th class="runHeader">DHIS Organisation Unit</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td><span id="locationSelectContainer"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/></span></td>
            <td><span id="orgUnitSelectContainer"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/></span></td>
          </tr>
          </tbody>
        </table>
      </td>
    </tr>
    <tr>
      <th class="runHeader">Period</th>
      <td>
        <input type="text" id="dailyPicker" class="periodSelector" style="display: none"/>
        <input type="text" id="weeklyPicker" class="periodSelector" style="display: none"/>
        <input type="month" id="monthlyPicker" onchange="handleMonthlyPeriodChange()"
               style="display: none"/>
        <input type="number" id="sixMonthlyPicker" min="1970" onchange="handleSixMonthlyPeriodChange()"
               style="display: none"/>
        <select id="sixMonthTypeSelector" onchange="handleSixMonthlyPeriodChange()" style="display: none">
          <option value="Jan">Jan - Jun</option>
          <option value="Jul">Jul - Dec</option>
        </select>
        <input type="number" id="yearlyPicker" min="1970" style="display: none"/>
        <div id="customPeriodPicker" style="display: none">
              <span style="color: red; font-size:small;">
                  The mapped period type is not supported. Please type the appropriate value
              </span>
          <input type="text" id="customPeriodSelector" onchange="handleCustomPeriodChange()"/>
        </div>
      </td>
    </tr>
    <tr>
      <th class="runHeader"><spring:message code="dhisconnector.customRange.openmrs.choose"/></th>
      <td><input type="checkbox" name="customRange" id="custom-range-option"/></td>
    </tr>
    <tr id="date-range-section" style="display:none">
      <th class="runHeader"><spring:message code="dhisconnector.customRange.openmrs.Date"/></th>
      <td>
        <table>
          <thead>
          <tr>
            <th class="runHeader"><spring:message code="dhisconnector.openmrsStartDate"/></th>
            <th class="runHeader"><spring:message code="dhisconnector.openmrsEndDate"/></th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td><input type="text" name="openmrsStartDate" id="openmrs-start-date" class="periodSelector"/></td>
            <td><input type="text" name="openmrsEndDate" id="openmrs-end-date" class="periodSelector"/></td>
          </tr>
          </tbody>
        </table>
      </td>
    </tr>
    <tr>
      <th class="runHeader"><spring:message code="dhisconnector.action"/></th>
      <td><input name="submit" type="button" onclick="sendDataToDHIS()" value="<spring:message code="dhisconnector.post" />"/> <input
              name="submit" type="button" onclick="generateDXFDownload()"
              value="<spring:message code="dhisconnector.dxf.download" />" />
        <input name="submit" type="button" onclick="downloadAdx()" value="<spring:message code="dhisconnector.adx.download" />" /></td>
    </tr>
    </tbody>
  </table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
