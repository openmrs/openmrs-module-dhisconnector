<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

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
<a href="createMapping.form">
	<button><openmrs:message code="dhisconnector.manageMappings.addNew"/></button>
</a>
<div>
	<div class="error-encountered">${failureWhileUploading}</div>
	<div class="success-encountered">${successWhileUploading}</div>
</div>
<b class="boxHeader margin-top">Import mappings</b>
<div class="box">
	<form method="POST" enctype="multipart/form-data">
		<input type="file" name="mapping">
		<input type="submit" value='<spring:message code="dhisconnector.upload"/>'>
	</form>
</div>

<div ng-app="manageMappingsApp" ng-controller="manageMappingsController">
	<b class="boxHeader margin-top"><openmrs:message code="dhisconnector.manageMappings.existingMappings"/></b>
	<div class="box">
		<table>
			<tr>
				<th><label><input id="checkAll" type="checkbox" ng-click="toggleAddAllToExportSelected(existingMappings)" ng-model="selectAllMappings"><openmrs:message code="general.select" /></label></th>
				<th><openmrs:message code="general.name"/></th>
				<th><openmrs:message code="dhisconnector.manageMappings.createdOn" /></th>
				<th style="float: right;"><openmrs:message code="general.action" /></th>
			</tr>
			<tr ng-repeat="mapping in existingMappings track by $index" title="<openmrs:message code='dhisconnector.manageMappings.clickToEdit'/> {{mapping.name}}" ng-click="loadMappingEditor(fetchMappingDisplay(mapping))" class="mapping-tr">
				<td>
					<input
							id="{{mapping.name}}"
							type="checkbox"
							class="select-this-mapping"
							value="{{mapping.name}}[@]{{mapping.dateTime}}"
							ng-click="toggleExportSelected(mapping)"
							ng-checked="selectAllMappings">
				</td>
				<td>{{mapping.name}}</td>
				<td>{{mapping.created}}</td>
				<td style="float: right;">
					<span><input type="image" src="../../images/edit.gif" ng-click="loadMappingEditor(fetchMappingDisplay(mapping))" title="<openmrs:message code='dhisconnector.mapping.editThis'/>" alt="Edit"></span>
					<span><input type="image" src="../../images/copy.gif" ng-click="loadMappingCopier(fetchMappingDisplay(mapping))" title="<openmrs:message code='dhisconnector.mapping.copyThis'/>" alt="Copy"></span>
					<span><input type="image" src="../../images/delete.gif" ng-click="deleteThisSelectedMapping(fetchMappingDisplay(mapping))" title="<openmrs:message code='dhisconnector.mapping.deleteThis'/>" alt="Delete"></span>
				</td>
			</tr>
		</table>
		<div class="margin-top">
			<form class="inline-block" method="POST" action="exportMappings.form">
				<input type="hidden" value="" name="selectedMappings" id="selected-mappings-to-export">
				<input ng-disabled="disableMultipleActions()" type="submit" value='Export Selected'>
			</form>
			<input
					type="button"
					class="inline-block"
					value="<openmrs:message code='dhisconnector.manageMappings.deleteSelected'/>"
					ng-disabled="disableMultipleActions()"
					ng-click="deleteSelectedMappings()">
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
<%@ include file="jembiOpenMRSFooter.jsp" %>
