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

let reports;
let mappings;
let reportData;
let dxfJSON;
var servers = [];
let OMRS_WEBSERVICES_BASE_URL = '../..';
let selectedMapping = null;
let selectedPeriod = null;
let selectedStartDate = null;
let selectedEndDate = null;
let availableLocations = null;
let selectedLocations = null;
let selectedReportName = null;
let userAgent = navigator.userAgent;
var serverUuid = "";

	window.addEventListener('beforeunload', function (e) {
        e.preventDefault();
        e.returnValue = '';
   });
   
   $j(document).ready(function () {
	 if(userAgent.match(/firefox|fxios/i)){
    $j('#monthlyPicker').datepicker({
     });
       }
   });
   
function populateReportsDropdown() {
    // fetch reports
    jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/periodindicatorreports?q=hasMapping", function (data) {

        var reportSelect = jQuery('<select id="reportSelect" name="reportSelect"></select>');
        reportSelect.append('<option value="">Select</option>');

        for (var i = 0; i < data.results.length; i++) {
            reportSelect.append('<option value="' + data.results[i].uuid + '">' + data.results[i].name + '</option>');
        }

        jQuery('#reportsSelectContainer').html("");
        jQuery('#reportsSelectContainer').append(reportSelect);

        jQuery("#reportSelect").hide().fadeIn("slow");

        reports = data.results;
    });
}

function hidePeriodPickers(){
    jQuery('#dailyPicker').hide();
    jQuery('#weeklyPicker').hide();
    jQuery('#monthlyPicker').hide();
    jQuery('#sixMonthlyPicker').hide();
    jQuery('#sixMonthTypeSelector').hide();
    jQuery('#yearlyPicker').hide();
    jQuery('#customPeriodPicker').hide();
}

function onMappingSelect() {
    const selectedMappingIndex = parseInt(jQuery('#mappingSelect').val());
    selectedMapping = mappings[selectedMappingIndex];
    document.getElementById('custom-range-option').checked = false;
    selectedStartDate = null;
    selectedEndDate = null;
    populateOrgUnitsOfDataSet();
    toggleCustomRangeCheckbox(false);
    hidePeriodPickers();
    // Checks whether the period type is a financial year or not
    switch (selectedMapping.periodType) {
        case 'Daily':
            initializeDailyPicker();
            break;
        case 'Weekly':
            initializeWeeklyPicker();
            break;
        case 'Monthly':
            initializeMonthlyPicker();
            break
        case 'Yearly':
            initializeYearlyPicker('Jan');
            break;
        case 'SixMonthly':
            initializeSixMonthlyPicker();
            break;
        case 'SixMonthlyApril':
            initializeSixMonthlyAprilPicker();
            break;
        case 'Quarterly':
            initializeQuarterlyPicker();
            break;
        default:
            if (selectedMapping.periodType.split('Financial').length === 2) {
                initializeYearlyPicker(selectedMapping.periodType.split('Financial')[1]);
            } else {
                jQuery('#customPeriodPicker').show();
                toggleCustomRangeCheckbox(true);
            }
    }
}

function initializeDailyPicker() {
    const dailyPicker = jQuery('#dailyPicker');
    dailyPicker.datepicker('destroy');
    dailyPicker.datepicker({
        dateFormat: 'yymmdd',
        onSelect: function (dateText) {
            selectedPeriod = dateText;
            const selectedDate = jQuery(this).datepicker('getDate');
            selectedStartDate = selectedDate;
            selectedEndDate = selectedDate;
        }
    });
    dailyPicker.show();
}

function initializeWeeklyPicker(){
    const weeklyPicker = jQuery('#weeklyPicker');
    weeklyPicker.datepicker('destroy');
    jQuery('#ui-datepicker-div > table > tbody > tr').die('mousemove');
    jQuery('.ui-datepicker-calendar tr').die('mouseleave');
    weeklyPicker.datepicker({
        showOtherMonths: true,
        selectOtherMonths: false,
        showWeek: true,
        firstDay: 1,
        onSelect: function () {
            let date = jQuery(this).datepicker('getDate');
            let week = jQuery.datepicker.iso8601Week(date);
            if (week < 10) {
                selectedPeriod = date.getFullYear() + "W0" + week;
            } else {
                selectedPeriod = date.getFullYear() + "W" + week;
            }
            selectedStartDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay() + 1);
            selectedEndDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay() + 7);
            weeklyPicker.val(selectedPeriod);
        }
    });
    weeklyPicker.show();
    jQuery('#ui-datepicker-div > table > tbody > tr').live('mousemove', function () {
        jQuery(this).find('td a').addClass('ui-state-hover');
    });
    jQuery('.ui-datepicker-calendar tr').live('mouseleave', function () {
        jQuery(this).find('td a').removeClass('ui-state-hover');
    });
}

function initializeMonthlyPicker() {
    const monthlyPicker = jQuery('#monthlyPicker');
    monthlyPicker.attr("max",
        moment().add(0, 'months').format("YYYY-MM"));
    monthlyPicker.show();
}

function initializeSixMonthlyPicker() {
    const currentYear = moment().year();
    const currentMonth = moment().month();
    const sixMonthly = jQuery('#sixMonthlyPicker');
    const sixMonthTypeSelector = jQuery('#sixMonthTypeSelector');
    // Set back to the maximum possible year if the user entered a wrong value
    if (currentMonth >= 6) {
        sixMonthly.attr("max", currentYear);
        sixMonthTypeSelector.children('option[value="Jul"]').hide();
        sixMonthly.val(currentYear);
    } else {
        sixMonthly.attr("max", currentYear - 1);
        sixMonthTypeSelector.children('option[value="Jul"]').show();
        sixMonthly.val(currentYear - 1);
    }
    sixMonthTypeSelector.show();
    sixMonthly.show();
    handleSixMonthlyPeriodChange();
}

function initializeSixMonthlyAprilPicker() {
    const currentYear = moment().year();
    const currentMonth = moment().month();
    const sixMonthlyApril = jQuery('#sixMonthlyAprilPicker');
    const sixMonthAprilTypeSelector = jQuery('#sixMonthAprilTypeSelector');

    if (currentMonth >= 9 || currentMonth < 4) {
        sixMonthlyApril.attr("max", currentYear);
        sixMonthAprilTypeSelector.children('option[value="Apr"]').hide();
        sixMonthlyApril.val(currentYear);
    } else {
        sixMonthlyApril.attr("max", currentYear - 1);
        sixMonthAprilTypeSelector.children('option[value="Apr"]').show();
        sixMonthlyApril.val(currentYear - 1);
    }
    sixMonthAprilTypeSelector.show();
    sixMonthlyApril.show();
    handleSixMonthlyAprilPeriodChange();
}

// Initialize the quarterly picker to the latest possible quarter
// Make the year picker and quarter selector appear on the display
function initializeQuarterlyPicker () {
    const currentYear = moment().year();
    const currentMonth = moment().month();
    const quarterlyYearPicker = jQuery("#quarterlyPicker");
    const quarterSelector = jQuery("#quarterSelection");

    if (currentMonth < 3) {
        quarterlyYearPicker.attr("max", currentYear -1);
        quarterlyYearPicker.val(currentYear - 1);
    } else {
        quarterlyYearPicker.attr("max", currentYear);
        quarterlyYearPicker.val(currentYear);
    }

    quarterlyYearPicker.show();
    quarterSelector.show();
    handleQuarterlyPeriodChange();
}

function initializeYearlyPicker(month) {
    const currentYear = moment().year();
    const currentMonth = moment().month();
    const yearlyPicker = jQuery('#yearlyPicker');
    // Set back to the maximum possible year if the user entered a wrong value
    if (currentMonth >= moment().month(month).format("M")) {
        yearlyPicker.attr("max", currentYear - 1);
        yearlyPicker.val(currentYear - 1);
    } else {
        yearlyPicker.attr("max", currentYear - 2);
        yearlyPicker.val(currentYear - 2);
    }
    jQuery("#yearlyPicker").change(function () {
        handleYearlyPeriodChange(month, this.value);
    });
    yearlyPicker.show();
    handleYearlyPeriodChange(month, currentYear);
}

function handleMonthlyPeriodChange() {
	let selectedValue = moment(jQuery('#monthlyPicker').val(), "YYYY-MM");
	
	if(userAgent.match(/firefox|fxios/i)){
	selectedValue = moment(jQuery('#monthlyPicker').val()).format('YYYY-MM');
	selectedPeriod = moment(selectedValue).format('YYYYMM');
    }else {
    selectedPeriod = selectedValue.format('YYYYMM');
    selectedStartDate = selectedValue.toDate();
    selectedEndDate = selectedValue.endOf('month').toDate();
    }
}

function handleSixMonthlyPeriodChange() {
    const sixMonthlyPicker = jQuery('#sixMonthlyPicker');
    let selectedYear = sixMonthlyPicker.val();
    const currentYear = moment().year();
    const currentMonth = moment().month();
    const sixMonthTypeSelector = jQuery('#sixMonthTypeSelector');
    const selectedSixMonthPeriod = sixMonthTypeSelector.val();
    // Set back to the maximum possible year if the user entered a wrong value
    if (selectedYear > currentYear) {
        sixMonthlyPicker.val(currentYear - 1);
        selectedYear = currentYear - 1;
    } else if (selectedYear == currentYear) {
        if (currentMonth >= 6) {
            sixMonthTypeSelector.children('option[value="Jul"]').hide();
        } else {
            sixMonthlyPicker.val(currentYear - 1);
            selectedYear = currentYear - 1;
            sixMonthTypeSelector.children('option[value="Jul"]').show();
        }
    } else {
        sixMonthTypeSelector.children('option[value="Jul"]').show();
    }

    if(selectedSixMonthPeriod === 'Jan') {
        selectedPeriod = selectedYear + "S1";
        selectedStartDate = moment(selectedYear, 'YYYY').toDate();
        selectedEndDate = moment(selectedStartDate).add(6, 'months').subtract(1, 'days').toDate();
    } else {
        selectedPeriod = selectedYear + "S2";
        selectedStartDate = moment(selectedYear, 'YYYY').add(6, 'months').toDate();
        selectedEndDate = moment(selectedStartDate).add(6, 'months').subtract(1, 'days').toDate();
    }
}

function handleSixMonthlyAprilPeriodChange() {
    const currentYear = moment().year();
    const currentMonth = moment().month();
    const sixMonthlyAprilPicker = jQuery('#sixMonthlyAprilPicker');
    let selectedYear = sixMonthlyAprilPicker.val();
    const sixMonthAprilTypeSelector = jQuery('#sixMonthAprilTypeSelector');
    const selectedSixMonthAprilPeriod = sixMonthAprilTypeSelector.val();
    // Set back to the maximum possible year if the user entered a wrong value
    if (selectedYear > currentYear) {
        sixMonthlyAprilPicker.val(currentYear - 1);
        selectedYear = currentYear - 1;
    } else if(selectedYear === currentYear) {
        if (currentMonth >= 9 || currentMonth < 4) {
            sixMonthAprilTypeSelector.children('option[value="Apr"]').hide();
        } else {
            sixMonthlyAprilPicker.val(currentYear - 1);
            selectedYear = currentYear - 1;
            sixMonthAprilTypeSelector.children('option[value="Apr"]').show();
        }
    } else {
        sixMonthTypeSelector.children('option[value="Apr]').show();
    }

    if (selectedSixMonthAprilPeriod === 'Apr') {
        selectedPeriod = selectedYear + "S1";
        selectedStartDate = moment(selectedYear, 'YYYY').add(4, 'months').toDate();
        selectedEndDate = moment(selectedStartDate).add(6, 'months').subtract(1, 'days').toDate();
    } else {
        selectedPeriod = selectedYear + "S2";
        selectedStartDate = moment(selectedYear, 'YYYY').add(10, 'months').toDate();
        selectedEndDate = moment(selectedStartDate).add(6, 'months').subtract(1, 'days').toDate();
    }
}

function handleQuarterlyPeriodChange () {
    const currentYear = moment().year();
    const currentMonth = moment().month();
    const quarterlyYearPicker = jQuery("#quarterlyPicker");
    let selectedYear = quarterlyYearPicker.val();
    const quarterSelector = jQuery("#quarterSelection");
    let selectedQuarter = quarterSelector.val();

    if (currentYear === selectedYear) {
        if (currentMonth < 3) {
            quarterlyYearPicker.attr("max", currentYear -1);
            selectedYear = currentYear - 1;
            quarterlyYearPicker.val(selectedYear);
        } else if (currentMonth < 6) {
            quarterSelector.children('option[value="Q2"]').hide();
            quarterSelector.children('option[value="Q3"]').hide();
            quarterSelector.children('option[value="Q4"]').hide();
        } else if (currentMonth < 9) {
            quarterSelector.children('option[value="Q3"]').hide();
            quarterSelector.children('option[value="Q4"]').hide();
        } else {
            quarterSelector.children('option[value="Q4"]').hide()
        }
    } else if (currentYear < selectedYear) {
        selectedYear = currentYear;
        quarterlyYearPicker.val(selectedYear);
    } else {
        quarterSelector.children('option[value="Q1"]').show();
        quarterSelector.children('option[value="Q2"]').show();
        quarterSelector.children('option[value="Q3"]').show();
        quarterSelector.children('option[value="Q4"]').show();
    }

    let quarterNum = selectedQuarter.split("Q");
    selectedPeriod = selectedYear.toString() + selectedQuarter;
    selectedStartDate = moment(selectedYear, 'YYYY').add((parseInt(quarterNum[1]) - 1)*3, 'months').toDate();
    selectedEndDate = moment(selectedStartDate).add(3, 'months').subtract(1, 'days').toDate();
}

function handleYearlyPeriodChange(month, selectedYear) {
    const yearlyPicker = jQuery('#yearlyPicker');
    const currentYear = moment().year();
    const currentMonth = moment().month();
    // Set back to the maximum possible year if the user entered a wrong value
    if (selectedYear >= currentYear) {
        if (currentMonth >= moment().month(month).format("M")) {
            yearlyPicker.val(currentYear - 1);
            selectedYear = currentYear - 1;
        } else {
            yearlyPicker.val(currentYear - 2);
            selectedYear = currentYear - 2;
        }
    }
    if (month === 'Jan') {
        selectedPeriod = selectedYear;
        selectedStartDate = moment(selectedPeriod, 'YYYY').toDate();
    } else {
        selectedPeriod = selectedYear + month;
        selectedStartDate = moment(selectedPeriod, 'YYYYMMM').toDate();
    }
    selectedEndDate = moment(selectedStartDate).add(1, 'years').subtract(1, 'days').toDate();
}

function handleCustomPeriodChange() {
    selectedPeriod = jQuery('#customPeriodSelector').val();
}

function toggleCustomRangeCheckbox(checkDisable) {
    var elem = document.getElementById('custom-range-option');
    if(checkDisable) {
        elem.checked = true;
        elem.disabled = true;
        jQuery('#date-range-section').show();
    }
    else {
        elem.disabled = false;
        elem.checked = false;
        jQuery('#date-range-section').hide();
    }
}

function populateMappingsDropdown() {
    // fetch mappings
    jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/mappings", function (data) {
        mappings = data.results;
        let mappingSelect = jQuery('<select id="mappingSelect"></select>');
        mappingSelect.append('<option value="">Select</option>');
        mappingSelect.on('change', onMappingSelect);
        for (let i = 0; i < mappings.length; i++) {
            mappingSelect.append('<option value="' + i + '">' + mappings[i].name + '</option>');
        }
        jQuery('#mappingSelectContainer').html("");
        jQuery('#mappingSelectContainer').append(mappingSelect);
        jQuery("#mappingSelect").hide().fadeIn("slow");
    });
}

function populateOrgUnitsOfDataSet() {
	
	populateServersToSendReport();
	//jQuery('#locationsList').html("");

    // fetch datasets
    let datasetId = selectedMapping.dataSetUID;
    availableLocations = [];
    let orgUnitName = "";
    let orgUnitUid = "";
    let serveruuid = "";
    let serverUrl = "";
    let locationMappings = "";
	
		try {
			const element = document.getElementById(orgUnitSelect);
			$j('#locationsList tbody').remove();
			locationMappings = jQuery('<tbody id="orgUnitSelect"></tbody>');
		} catch (e) {
		    if (e instanceof ReferenceError) {
		    locationMappings = jQuery('<tbody id="orgUnitSelect"></tbody>');
		    }
		}

    jQuery.ajaxSetup({ async: false });
    jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/dhisdatasets/" + datasetId, function (data) {
	
        for (let i = 0; i < data.organisationUnits.length ; i++) {

            orgUnitName = data.organisationUnits[i].name;
            orgUnitUid = data.organisationUnits[i].id;
            
            for (let j = 0; j <  servers.length ; j++) {
	            
	        serveruuid = servers[j].serveruuid;
	        serverUrl = servers[j].url;

            jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/locationmappings/?orgUnitUid=" + orgUnitUid +"&serverUuid=" +serveruuid, function (mappingData) {
			
                if (!(mappingData.locationName === undefined) && mappingData.orgUnitUid === orgUnitUid && mappingData.serverUuid === serveruuid) {
	
						 mappingData.orgUnitName = orgUnitName;
						 mappingData.serverUrl = serverUrl;
						 
						 availableLocations.push(mappingData);
               }
            });

         }
                 
      }
    });
         
       for (let l = 0; l <  availableLocations.length ; l++) {
			
			mappingLocation = availableLocations[l];
     		locationMappings.append('<tr style="background-color: #f2f2f2;"><td style="border: 1px solid #ddd; padding: 8px;"><input type="checkbox" id="' + availableLocations.indexOf(mappingLocation) + '"/></td><td style="border: 1px solid #ddd; padding: 8px;"><span>'+ mappingLocation.serverUrl +'</td><td style="border: 1px solid #ddd; padding: 8px;">'+ mappingLocation.orgUnitName +'</td><td style="border: 1px solid #ddd; padding: 8px;">'+ mappingLocation.locationName +'</span></td></tr>');
		}

    jQuery('#locationsList').append(locationMappings);

    jQuery("#locationMappings").hide().fadeIn("slow");
    
    jQuery.ajaxSetup({ async: true });
        
}

function getReportData(locationUid) {
    reportData = null;
    var reportGUID = selectedMapping.periodIndicatorReportGUID;
    var locationGUID = locationUid;
    let startDate = selectedStartDate;
    let endDate = selectedEndDate;
    
    if(selectedMapping.periodType === 'Daily'){
		startDate = selectedEndDate;
		endDate = selectedEndDate;
	}else{
    
    let globalPropertyStartDate = jQuery('#global-property-start-date').val();
    let globalPropertyEndDate = jQuery('#global-property-end-date').val();
    
    if(!(globalPropertyStartDate =='' && globalPropertyEndDate =='')){
    	startDate = new Date(startDate.getFullYear(), startDate.getMonth()-1, globalPropertyStartDate);
    	endDate = new Date(endDate.getFullYear(), endDate.getMonth(), globalPropertyEndDate);
    }

    if(document.getElementById('custom-range-option').checked){
        startDate = jQuery('#openmrs-start-date').datepicker('getDate');
        endDate = jQuery('#openmrs-end-date').datepicker('getDate');
        if( startDate == '' ||  endDate == '') {
            alert('Please choose start & end date');
            return;
        }
    }
   }

    // fetch report data
    return jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/reportingrest/reportdata/" + reportGUID + "?startDate=" + moment(startDate).format('YYYY-MM-DD') + "&endDate=" + moment(endDate).format('YYYY-MM-DD') + "&location=" + locationGUID + "&v=custom:(dataSets)", function (data) {
        reportData = data;
    });
}

function testNotEmpty(selector, message) {
    jQuery(selector).siblings().remove();
    if (jQuery(selector).val() == "" || jQuery(selector).length == 0) {
        jQuery(selector).parent().append('<span class="error" id="nameEmptyError">' + message + '</span>');
        return false;
    }
    return true;
}

function checkMappingAppliesToReport() {
    // mapping.periodIndicatorReportGUID must equal the UUID of the selected report
    jQuery('#mappingSelect').siblings().remove();

    var mappingReport = selectedMapping.periodIndicatorReportGUID;

    if (mappingReport == "" || mappingReport == undefined)
        return false;

    var selectedReport = jQuery('#reportSelect').val();

    if (selectedReport == "" || selectedReport == undefined)
        return false;

    if (mappingReport === selectedReport) {
        return true;
    } else {
        jQuery('#mappingSelect').parent().append('<span class="error" id="nameEmptyError">Selected mapping does not apply to selected report</span>');
        return false;
    }
}

function validateForm() {
    var ret = true;

    // Make sure we have no empty values
    ret &= testNotEmpty('#reportSelect', 'Report cannot be empty');
    ret &= testNotEmpty('#mappingSelect', 'Mapping cannot be empty');

    // Make sure mapping applies to report
    ret &= checkMappingAppliesToReport();

    return ret;
}

function getMappingForIndicator(indicator) {
    var element = selectedMapping.elements.filter(function (v) {
        return v.indicator == indicator;
    })[0];

    if (element == undefined) // There is no mapping for this indicator
        return null;

    return {
        dataElement: element.dataElement,
        comboOption: element.comboOption
    }
}

function buildDXFJSON(locationUid, orgUnitId, serverUuid) {
    dxfJSON = null;

    return getReportData(locationUid).then(function () {
	    selectedReportName = jQuery('#reportSelect option:selected').text();
        dxfJSON = {};
        dxfJSON.dataSet = selectedMapping.dataSetUID
        dxfJSON.period = selectedPeriod.toString();
        dxfJSON.orgUnit = orgUnitId;
        dxfJSON.reportName = selectedReportName;
        
        selectedPeriod = selectedPeriod.toString();
        
        var dataValues = [];
        
        for(let i =0; i < reportData.dataSets.length; i++){
            
            var indicatorValues = reportData.dataSets[i].rows[0];
            
            for (var indicator in indicatorValues) {
            var dataValue = {};
            if (indicatorValues.hasOwnProperty(indicator)) {

                var mapping = getMappingForIndicator(indicator);
                if (mapping !== null) {
                    dataValue.dataElement = mapping.dataElement;
                    dataValue.categoryOptionCombo = mapping.comboOption;
                    dataValue.value = indicatorValues[indicator];
                    dataValue.comment = 'Generated by DHIS Connector OpenMRS Module.'
                    dataValues.push(dataValue);
                }
            }
        }
        
        }
        dataSetServers = [];
        
        for (let i = 0; i < servers.length; i++) {
			if(servers[i].serveruuid === serverUuid){
				dataSetServers.push(servers[i]);
			}
		}
        
        dxfJSON.dataValues = dataValues;
        dxfJSON.dhisServers = dataSetServers;
    });
}

function slugify(text) {
    return text.toLowerCase().replace(/ /g, '-').replace(/[-]+/g, '-').replace(/[^\w-]+/g, '');
}

function displayPostReponse(json) {
	if(json !== null && !(json == '')){
    jQuery('#loadingRow').remove();
    var responseRow = jQuery('<tr id="responseRow"><th class="runHeader">Response</th><td><pre><code className="JSON"><table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse; width: 100%;"><tr><th colspan=2 style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white;">Relatório enviado com Sucesso para DHIS</th></tr><tr style="background-color: #f2f2f2;"><td style="border: 1px solid #ddd; padding: 8px;">Total de Registos Novos </td><td style="border: 1px solid #ddd; padding: 8px;">' + JSON.stringify(json.importCount.imported, null, 2) + '</td></tr><tr><td style="border: 1px solid #ddd; padding: 8px;">Total de Registos Actualizados</td><td style="border: 1px solid #ddd; padding: 8px;">' + JSON.stringify(json.importCount.updated, null, 2) + '</td></tr><tr style="background-color: #f2f2f2;"><td style="border: 1px solid #ddd; padding: 8px;">Total de Registos ignorados</td><td style="border: 1px solid #ddd; padding: 8px;">' + JSON.stringify(json.importCount.ignored, null, 2) + '</td></tr><tr><td style="border: 1px solid #ddd; padding: 8px;">Total de Registos Apagados</td><td style="border: 1px solid #ddd; padding: 8px;">' + JSON.stringify(json.importCount.deleted, null, 2) + '</td></tr></table></code></pre></td></tr>');
    jQuery('#tableBody').append(responseRow);
    responseRow.hide().fadeIn("slow");
    jQuery('#send').prop('disabled', false);

    jQuery('pre code').each(function (i, block) {
        hljs.highlightBlock(block);
    });
    }else{
	displayPostReponseError();
}
}

function displayPostReponseError(xhr, status, error) {
	jQuery('#loadingRow').remove();
	var responseRow = jQuery('<tr id="responseRow"><th class="runHeader">Response</th><td><pre><code className="JSON"><table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse; width: 100%;"><tr><th colspan=2 style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #cb2e0c; color: white;">Erro ao enviar relatório, use o botão Reenviar Dados, para voltar a enviar!</th></tr></table></code></pre></td></tr>');
    jQuery('#tableBody').append(responseRow);
    responseRow.hide().fadeIn("slow");	
	jQuery('#send').prop('disabled', false);
    jQuery('#reSend').prop('disabled', false);
    jQuery('pre code').each(function (i, block) {
        hljs.highlightBlock(block);
    });
}

function downloadAdx() {
    selectedLocations = [];
    jQuery("#orgUnitSelect input[type='checkbox']:checked").each(function () {
        selectedLocations.push(availableLocations[this.id])
    })

    if (validateForm()) {
        for (let i = 0; i < selectedLocations.length; i++) {
            buildDXFJSON(selectedLocations[i].locationUid, selectedLocations[i].orgUnitUid).then(function () {
                jQuery.ajax({
                    type: "GET",
                    url: "adxGenerator.form",
                    data: {"dxfDataValueSet": JSON.stringify(dxfJSON)},
                    datatype: "json",
                    success: function (activityMonitorData) {
                        if (activityMonitorData)
                            createDownload(activityMonitorData, 'application/xml', '.adx.xml', selectedLocations[i].orgUnitUid);
                    }
                });
            });
        }
    }
}

function sendDataToDHIS() {
		
	let yesSend = confirm("Do you really want to send the report to the selected period? ("+selectedPeriod.toString()+")");
	if(yesSend){
    selectedLocations = [];
 	let locationsToSend = [];
    
	jQuery("#orgUnitSelect input[type='checkbox']:checked").each(function() {
        selectedLocations.push(availableLocations[this.id])
    })
    
    if(selectedMapping.periodType === 'Daily'){
	
		let locationsToSendNames = '';
		
		for (let i = 0; i < selectedLocations.length ; i++) {
			locationsToSend.push(selectedLocations[i]);
			locationsToSendNames = locationsToSendNames+ ' '+selectedLocations[i].locationName +'\n';
		}
		
	if(locationsToSend.length > 0){
		alert('O relatório será enviado para a(s) localização(ões) : '+locationsToSendNames+' \n .');
	}
	
	}else{

	let locationsToNotSend = '';
	for (let i = 0; i < selectedLocations.length ; i++) {
		jQuery.ajax({
			url: OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/dhismonthcheck?dhisreportdataset="+selectedMapping.dataSetUID+"&periodtype="+selectedMapping.periodType+"&reportperiod="+selectedPeriod.toString()+"&organicunit="+ selectedLocations[i].orgUnitUid,
		  	type: 'GET',
		  	async: false,
		  	success: function(data) {
		  		if(data){
					if(data.monthOpen == false){
						locationsToNotSend = locationsToNotSend+ ' '+selectedLocations[i].locationName +'\n'
					}
					else{
						locationsToSend.push(selectedLocations[i]);
					}
				}
		  },
		  error: function(xhr, status, error) {
			displayPostReponseError(xhr, status, error);
		  }
		});
	}

	if(locationsToNotSend.length > 0){
		alert('O relatório não será enviado para a(s) localização(ões) : '+locationsToNotSend+' \n porque o mês selecionado não está aberto para envio dos dados.');
	}
	}
		
	if(locationsToSend.length > 0){
		
		jQuery('#send').prop('disabled', true);
		jQuery('#responseRow').remove();
	    var loadingRow = jQuery('<tr id="loadingRow"><th class="runHeader"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/>Enviando, aguarde...</th></tr>');
	    jQuery('#tableBody').append(loadingRow);
	    loadingRow.hide().fadeIn("slow");
 	
	    for (let i = 0; i < locationsToSend.length ; i++) {
		
	        if (!(locationsToSend[i].locationId === undefined)){
	        
	            buildDXFJSON(locationsToSend[i].locationUid, locationsToSend[i].orgUnitUid, locationsToSend[i].serverUuid).then(function () {
	                // post to dhis
	                jQuery.ajax({
	                    url: OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/dhisdatavaluesets",
	                    type: "POST",
	                    data: JSON.stringify(dxfJSON),
	                    contentType: "application/json;charset=utf-8",
	                    dataType: "json",
	                    success: function (data) {
							
							for (const [key, value] of Object.entries(data)) {
							  	let response = value;
								let responseDescription = response.description;
								if(response.status == 'OK'){
									displayPostReponseSuccessPerServer(responseDescription, i, locationsToSend.length);
								}else{
									displayPostReponseErrorPerServer(responseDescription, i,locationsToSend.length);
								}
							}
	                        //displayPostReponse(data);
	                    },
	                    error: function (xhr, status, error) {
							displayPostReponseError(xhr, status, error);					
						}
	                });
	
	            });
	            
	        }
	    }
    }

}

}

function displayPostReponseSuccessPerServer(error, mappingIndex, mappingsToBeExecuted) {
	
	mappingsToBeExecuted = mappingsToBeExecuted - 1;
	if(mappingIndex == mappingsToBeExecuted){
		jQuery('#loadingRow').remove();
	}
	
	var responseRow = jQuery('<tr id="responseRow"><th class="runHeader">Response</th><td><pre><code className="JSON"><table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse; width: 100%;"><tr><th colspan=2 style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #04AA6D; color: white;">'+error+'</th></tr></table></code></pre></td></tr>');
    jQuery('#tableBody').append(responseRow);
    responseRow.hide().fadeIn("slow");	
	jQuery('#send').prop('disabled', false);
    jQuery('#reSend').prop('disabled', false);
    jQuery('pre code').each(function (i, block) {
        hljs.highlightBlock(block);
    });
}


function displayPostReponseErrorPerServer(error, mappingIndex, mappingsToBeExecuted) {
	
	mappingsToBeExecuted = mappingsToBeExecuted - 1;
	if(mappingIndex == mappingsToBeExecuted){
		jQuery('#loadingRow').remove();
	}
	
	var responseRow = jQuery('<tr id="responseRow"><th class="runHeader">Response</th><td><pre><code className="JSON"><table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse; width: 100%;"><tr><th colspan=2 style="border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #cb2e0c; color: white;">'+error+'</th></tr></table></code></pre></td></tr>');
    jQuery('#tableBody').append(responseRow);
    responseRow.hide().fadeIn("slow");	
	jQuery('#send').prop('disabled', false);
    jQuery('#reSend').prop('disabled', false);
    jQuery('pre code').each(function (i, block) {
        hljs.highlightBlock(block);
    });
}

function reSendReportDataToDHIS(){
		jQuery('#responseRow').remove();
	    var loadingRow = jQuery('<tr id="loadingRow"><th class="runHeader"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/>Sending...</th></tr>');
 	    jQuery('#tableBody').append(loadingRow);
  	    loadingRow.hide().fadeIn("slow");
	    selectedReportName = jQuery('#reportSelect option:selected').text();
        jQuery.ajax({
            type: "POST",
            url: "resendReportData.form",
            data: JSON.stringify({"selectedReportName": selectedReportName, "selectedPeriod": selectedPeriod}),
            contentType: "application/json;charset=utf-8",
            datatype: "json",
            success: function (data) {
				    displayPostReponse(data);
            }, 
            error: function (xhr, status, error) {
				    displayPostReponseError(xhr, status, error);					
		    }
        });
}

function reSendFailedDataToDHIS(){
	var rowId = event.target.parentNode.parentNode.id;
    
    var data = document.getElementById(rowId).querySelectorAll(".row-data");
    
    var reportName = data[0].innerHTML;
    
    	jQuery('#responseRow').remove();
 	    var loadingRow = jQuery('<tr id="loadingRow"><th class="runHeader"><img width="50px;" height="50px;" class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/>Sending...</th></tr>');
        jQuery('#tableBody').append(loadingRow);
        loadingRow.hide().fadeIn("slow");
    
        jQuery.ajax({
            type: "POST",
            url: "resendFailedReportData.form",
            data: JSON.stringify({"selectedReportName": reportName }),
            contentType: "application/json;charset=utf-8",
            datatype: "json",
            success: function (data) {
		            getFailedReportDataRender();  
					displayPostReponse(data);  
            }, 
            error: function (xhr, status, error) {
		            getFailedReportDataRender();
				    displayPostReponseError(xhr, status, error);	
		    }
        });
}

function getFailedReportDataRender(){
        jQuery.ajax({
            type: "GET",
            url: "failedReportDataRender.form",
            data: "",
            datatype: "json",
            success: function (data) {
	        for (let i = 0; i < data.length ; i++) {
			displayFailedPostData(data[i],i);
	}
            }
        });
}

function displayFailedPostData(reportName, rowId) {
	jQuery("#"+rowId).remove();
    jQuery('#loadingRow').remove();
    var responseRow = jQuery('<tr style="background-color: #f2f2f2;" id="'+rowId+'"><td style="border: 1px solid #ddd; padding: 8px; width: 70%;" class="row-data">'+reportName+'</td><td style="border: 1px solid #ddd; padding: 8px; width: 20%;"><input id="reSend" name="reSend" type="button" onclick="reSendFailedDataToDHIS();" value="Reenviar Dados"/></td></tr>');
    jQuery('#tableBody').append(responseRow);
    responseRow.hide().fadeIn("slow");
}


function createDownload(content, contentType, extension, orgUnitUid) {
    var dl = document.createElement('a');

    dl.setAttribute('href', 'data:' + contentType + ';charset=utf-8,' + encodeURIComponent(content));
    dl.setAttribute('download', slugify(jQuery('#reportSelect option:selected').text()) + '-' + orgUnitUid.toString() + '-' + slugify(selectedPeriod.toString()) + extension);

    dl.style.display = 'none';
    document.body.appendChild(dl);

    dl.click();

    document.body.removeChild(dl);
}

function generateDXFDownload() {
    selectedLocations = [];
    jQuery("#orgUnitSelect input[type='checkbox']:checked").each(function() {
        selectedLocations.push(availableLocations[this.id])
    })

    if (validateForm()) {
        for (let i = 0; i < selectedLocations.length ; i++) {
            buildDXFJSON(selectedLocations[i].locationUid, selectedLocations[i].orgUnitUid).then(function () {
                createDownload(JSON.stringify(dxfJSON), 'application/json', '.dxf.json', selectedLocations[i].orgUnitUid);
            });
        }
    }
}

function populateServersToSendReport() {
    jQuery('#serversList').html("");

    let serversList = jQuery('<tr id="serverConfigured"></tr>');
    
    let e = document.getElementById("reportSelect");
	let value = e.value;
	let sespReportUuid = value;
	
	let serversToReceiveTheReportSelected = [];
	
        jQuery.ajax({
            type: "GET",
            url: "getServersByReportSelected.form",
            data:  {"sespReportUuid": sespReportUuid},
            datatype: "json",
            success: function (data) {
		    for (let i = 0; i <  data.length ; i++) {
			serversList.append('<tr><td><span>'+ data[i].url +'</span><br></td></tr>');
			serversToReceiveTheReportSelected.push(data[i]);
			}
            }
        });
        servers = serversToReceiveTheReportSelected;
   	
    jQuery('#serversList').html("");

    //jQuery('#serversList').append(serversList);

    jQuery("#serversList").hide().fadeIn("slow");
}

jQuery(function () {
    populateReportsDropdown();
    populateMappingsDropdown();
    hljs.initHighlightingOnLoad();

    // Attach datepickers
    jQuery('#openmrs-start-date').datepicker();
    jQuery('#openmrs-end-date').datepicker();

    jQuery('#custom-range-option').click(function() {
        jQuery('#date-range-section').toggle();
    });
});
