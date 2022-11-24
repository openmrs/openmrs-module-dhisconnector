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
let OMRS_WEBSERVICES_BASE_URL = '../..';
let selectedMapping = null;
let selectedPeriod = null;
let selectedStartDate = null;
let selectedEndDate = null;
let availableLocations = null;
let selectedLocations = null;

function populateReportsDropdown() {
    // fetch reports
    jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/periodindicatorreports?q=hasMapping", function (data) {

        var reportSelect = jQuery('<select id="reportSelect"></select>');
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
        moment().add(-1, 'months').format("YYYY-MM"));
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
    const selectedValue = moment(jQuery('#monthlyPicker').val(), "YYYY-MM");
    selectedStartDate = selectedValue.toDate();
    selectedEndDate = selectedValue.endOf('month').toDate();
    selectedPeriod = selectedValue.format('YYYYMM');
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
    jQuery('#locationsList').html("");

    // fetch datasets
    let datasetId = selectedMapping.dataSetUID;
    availableLocations = [];
    let locationMappings = jQuery('<tr id="orgUnitSelect"></tr>');
    jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/dhisdatasets/" + datasetId + "?v=full", function (data) {
        for (var i = 0; i < data.organisationUnits.length ; i++) {

            let orgUnitName = data.organisationUnits[i].name;
            jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/locationmappings/?orgUnitUid=" + data.organisationUnits[i].id, function (mappingData) {
                mappingData.orgUnitName = orgUnitName;
                availableLocations.push(mappingData);
                if (!(mappingData.locationName === undefined)) {
                   locationMappings.append('<tr><td><input type="checkbox" id="' + availableLocations.indexOf(mappingData) + '"/><span>'+ mappingData.locationName +'=>'+ mappingData.orgUnitName +'</span></td></tr>');
                }
            });
        }
    });
    jQuery('#locationsList').html("");

    jQuery('#locationsList').append(locationMappings);

    jQuery("#locationMappings").hide().fadeIn("slow");
}

function getReportData(locationUid) {
    reportData = null;
    var reportGUID = selectedMapping.periodIndicatorReportGUID;
    var locationGUID = locationUid;
    let startDate = selectedStartDate;
    let endDate = selectedEndDate;
    if(document.getElementById('custom-range-option').checked){
        startDate = jQuery('#openmrs-start-date').datepicker('getDate');
        endDate = jQuery('#openmrs-end-date').datepicker('getDate');
        if( startDate == '' ||  endDate == '') {
            alert('Please choose start & end date');
            return;
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

function buildDXFJSON(locationUid, orgUnitId) {
    dxfJSON = null;

    return getReportData(locationUid).then(function () {
        dxfJSON = {};
        dxfJSON.dataSet = selectedMapping.dataSetUID
        dxfJSON.period = selectedPeriod.toString();
        dxfJSON.orgUnit = orgUnitId;
        var indicatorValues = reportData.dataSets[0].rows[0];
        
        if(reportData.dataSets.length !== 1){
        indicatorValues = reportData.dataSets[1].rows[0];
        }
        
        var dataValues = [];

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

        dxfJSON.dataValues = dataValues;
    });
}

function slugify(text) {
    return text.toLowerCase().replace(/ /g, '-').replace(/[-]+/g, '-').replace(/[^\w-]+/g, '');
}

function displayPostReponse(json) {
    jQuery('#loadingRow').remove();
    var responseRow = jQuery('<tr id="responseRow"><th class="runHeader">Response</th><td><pre><code className="JSON">' + JSON.stringify(json, null, 2) + '</code></pre></td></tr>');
    jQuery('#tableBody').append(responseRow);
    responseRow.hide().fadeIn("slow");
    jQuery('#send').prop('disabled', false);

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
    selectedLocations = [];
    jQuery("#orgUnitSelect input[type='checkbox']:checked").each(function() {
        selectedLocations.push(availableLocations[this.id])
    })

    jQuery('#send').prop('disabled', true);
	jQuery('#responseRow').remove();
    var loadingRow = jQuery('<tr id="loadingRow"><th class="runHeader"><img class="spinner" src="../../moduleResources/dhisconnector/loading.gif"/>Sending...</th></tr>');
    jQuery('#tableBody').append(loadingRow);
    loadingRow.hide().fadeIn("slow");
    for (let i = 0; i < selectedLocations.length ; i++) {
    
        if (!(selectedLocations[i].locationId === undefined)){
        
            buildDXFJSON(selectedLocations[i].locationUid, selectedLocations[i].orgUnitUid).then(function () {
                // post to dhis
                jQuery.ajax({
                    url: OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/dhisdatavaluesets",
                    type: "POST",
                    data: JSON.stringify(dxfJSON),
                    contentType: "application/json;charset=utf-8",
                    dataType: "json",
                    success: function (data) {
                        displayPostReponse(data);
                    }
                });

            });
        }
    }

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
