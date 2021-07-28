<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require privilege="Manage Mappings" otherwise="/login.htm"
				 redirect="/module/dhisconnector/manageMappings.form"/>

<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.css"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/jquery-2.2.0.min.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/angular.min.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/dhisconnector.js"/>
<openmrs:htmlInclude file="/moduleResources/dhisconnector/manage-mappings.js"/>

<%@ include file="template/localHeader.jsp" %>

<c:if test="${showLogin == 'true'}">
	<c:redirect url="../../login.htm" />
</c:if>
<h3><spring:message code="dhisconnector.manageMappings"/></h3>
<div  ng-app="manageMappingsApp" ng-controller="manageMappingsController">
	<span>
		<a href="createMapping.form"><openmrs:message code="dhisconnector.manageMappings.addNew"/></a>
	</span>
	<span>
		<input type="button" value="<openmrs:message code='dhisconnector.manageMappings.deleteSelected'/>" ng-disabled="disableMultipleDeletion()" ng-click="deleteSelectedMappings()" style="float: right;">
	</span>
	<br /><br />
	<b class="boxHeader"><openmrs:message code="dhisconnector.manageMappings.existingMappings"/></b>
	<div class="box">
		<table>
			<tr>
				<th><label><input type="checkbox" ng-model="selectAllMappings"><openmrs:message code="general.select" /></label></th>
				<th><openmrs:message code="general.name"/></th>
				<th><openmrs:message code="dhisconnector.manageMappings.createdOn" /></th>
				<th style="float: right;"><openmrs:message code="general.action" /></th>
			</tr>
			<tr ng-repeat="mapping in existingMappings track by $index" title="<openmrs:message code='dhisconnector.manageMappings.clickToEdit'/> {{mapping.name}}" ng-click="loadMappingEditor(fetchMappingDisplay(mapping))" class="mapping-tr">
				<td><input type="checkbox" ng-checked="selectAllMappings" class="select-this-mapping" value="{{mapping.name}}[@]{{mapping.dateTime}}"></td>
				<td>{{mapping.name}}</td>
				<td>{{mapping.created}}</td>
				<td style="float: right;">
					<span><input type="image" src="../../images/edit.gif" ng-click="loadMappingEditor(fetchMappingDisplay(mapping))" title="<openmrs:message code='dhisconnector.mapping.editThis'/>" alt="Edit"></span>
					<span><input type="image" src="../../images/copy.gif" ng-click="loadMappingCopier(fetchMappingDisplay(mapping))" title="<openmrs:message code='dhisconnector.mapping.copyThis'/>" alt="Copy"></span>
					<span><input type="image" src="../../images/delete.gif" ng-click="deleteThisSelectedMapping(fetchMappingDisplay(mapping))" title="<openmrs:message code='dhisconnector.mapping.deleteThis'/>" alt="Delete"></span>
				</td>
			</tr>
		</table>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>