/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

function fetchLocationMappings() {
	let locationMappings = "";
	jQuery("td[id^='location_']").each(function() {
		let locationUuid = this.attributes.name.value;
		if (jQuery("[name='orgUnitOf_"+locationUuid+"']").val() != jQuery("[name='savedOrgUnitUuidOf_"+locationUuid+"']").val()){
			locationMappings = locationMappings + locationUuid + "=" + jQuery("[name='orgUnitOf_"+locationUuid+"']")[0].value + ",";
		}
	})
	jQuery("input[name='locationMappings']").val(locationMappings);
}

function saveLocationOrgUnitsMappings() {
	
	let locationOrgUnitsMappings = "";
	
	let e = document.getElementById("orgUnits");
    let orgUnitName = e.options[e.selectedIndex].text;
	let orgUnit = jQuery("[name='orgUnits']").val();
	let server = jQuery("[name='servers']").val();
	let location = jQuery("[name='locations']").val();
	
    locationOrgUnitsMappings = locationOrgUnitsMappings + server +","+ orgUnit + "," + location + "," + orgUnitName;
   
	jQuery("input[name='locationOrgUnitsMappings']").val(locationOrgUnitsMappings);

}


function getOrgUnitsByServer(event) {
	
	let selectOrgUnits = $j("#orgUnits").empty();
	let serverUuid = event.value;
	//let text = event.options[event.selectedIndex].text;
	
	jQuery.ajax({
		url: "../../module/dhisconnector/orgUnitsByServer.form?serverUuid="+serverUuid,
		type: "GET",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function(data) {
			console.log(data);
			for (var i=0; i<data.length; i++) {
				console.log(data[i].id);
			  selectOrgUnits.append('<option value="' + data[i].id + '">' + data[i].name + '</option>');
			}
			//window.location = '../../module/dhisconnector/locationMapping.form';
		},
		error: function(data) {
			console.log(data);
		}
	});
}

function removeMapping(mapping){
	        jQuery.ajax({
            type: "POST",
            url: "deleteLocationMapping.form?mapping=" + mapping,
            contentType: "application/json;charset=utf-8",
            datatype: "json",
            success: function (data) {
			window.location = '../../module/dhisconnector/locationMapping.form';
            }, 
            error: function (xhr, status, error) {

		    }
        });
}
