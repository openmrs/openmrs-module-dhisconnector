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
