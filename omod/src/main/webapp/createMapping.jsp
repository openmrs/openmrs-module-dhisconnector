<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require privilege="Manage Mappings" otherwise="/login.htm"
                 redirect="/module/dhisconnector/createMapping.form"/>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/flexboxgrid.min.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dragula.min.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dragula.min.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.js"/>

<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>

<h3><spring:message code="dhisconnector.createMapping"/></h3>
<h4></h4>
<div id="loading-progress-bar"></div>
<input id="create-mapping-action" type="hidden" value="new">
<div class="error-encountered" id="error-encountered-saving"></div>
<p>
<div class="row">
  <div class="col-xs-5">
    <div class="box" id="reports"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/></div>
  </div>
  <div class="col-xs">
    <div class="box" id="datasets"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/></div>
  </div>
</div>
</p>

<p>
<div class="row real-mapping-section">
  <div class="col-xs-5" id="reportIndicators"></div>
  <div class="col-xs" id="dataElementsMappings"></div>
  <div class="col-xs" id="dataElementsOptions"></div>
  <div class="col-xs" id="categoryComboOptions"></div>
</div>
</p>

<p>
  <button id="saveMappingButton"><spring:message code="dhisconnector.save"/></button>
</p>

<div id="saveMappingPopup">
    <table>
      <tbody>
      <tr>
        <td><b><spring:message code="dhisconnector.mappingName"/></b></td>
        <td><input id="mappingName" type="text" size="30"></td>
      </tr>
      <tr>
        <td><b>Period Type<b></td>
        <td><span id="periodType"></span></td>
      </tr>
      </tbody>
    </table>
    <p>
      <button id="postMappingObject" onClick="saveMapping(event)"><spring:message code="dhisconnector.save"/></button>
    </p>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
