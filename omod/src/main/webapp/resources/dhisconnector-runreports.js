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
let locations;
let weekStartDate;
let weekEndDate;
let reportData;
let dxfJSON;
let OMRS_WEBSERVICES_BASE_URL = '../..';
let selectedMapping;

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

function onMappingSelect() {
    selectedMapping = mappings[jQuery('#mappingSelect').val()];
    // clear the date
    document.getElementById('custom-range-option').checked = false;
    jQuery('#un-recognized-period').html('');
    toggleCustomRangeCheckbox(false);
    jQuery('#periodSelector').val("");
    jQuery('#periodSelector').monthpicker('destroy');
    jQuery('#periodSelector').datepicker('destroy');
    jQuery('#ui-datepicker-div > table > tbody > tr').die('mousemove');
    jQuery('.ui-datepicker-calendar tr').die('mouseleave');

    weekStartDate = null;
    weekEndDate = null;

    if (selectedMapping.periodType === undefined)
        return;
    if (selectedMapping.periodType === 'Daily') {
        // set up daily
        jQuery('#periodSelector').datepicker({
            dateFormat: 'yymmdd'
        });
    } else if (selectedMapping.periodType === 'Weekly') {
        // set up weekly
        jQuery('#periodSelector').datepicker({
            showOtherMonths: true,
            selectOtherMonths: false,
            showWeek: true,
            firstDay: 1,
            onSelect: function (dateText, inst) {
                var date = jQuery(this).datepicker('getDate');
                var week = jQuery.datepicker.iso8601Week(date);
                if (week < 10) {
                    var displayWeek = date.getFullYear() + "W0" + week;
                } else {
                    var displayWeek = date.getFullYear() + "W" + week;
                }

                weekStartDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay() + 1);
                weekEndDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay() + 7)

                jQuery('#periodSelector').val(displayWeek);
            }
        });

        jQuery('#ui-datepicker-div > table > tbody > tr').live('mousemove', function () {
            jQuery(this).find('td a').addClass('ui-state-hover');
        });
        jQuery('.ui-datepicker-calendar tr').live('mouseleave', function () {
            jQuery(this).find('td a').removeClass('ui-state-hover');
        });
    } else if (selectedMapping.periodType === 'Monthly') {
        // set up monthly
        jQuery('#periodSelector').monthpicker({
            pattern: 'yyyymm'
        });
    } else {
        // This is unknown
        var text = 'DHIS period: ' + selectedMapping.periodType + ', Please type the appropriate value';
        jQuery('#un-recognized-period').html(text);
        toggleCustomRangeCheckbox(true);
    }
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

function getPeriodDates() {
    var startDate, endDate;
    if(document.getElementById('custom-range-option').checked) {
        // Do the custom thing.
        startDate = jQuery('#openmrs-start-date').datepicker('getDate');
        endDate = jQuery('#openmrs-end-date').datepicker('getDate');
        if( startDate == '' ||  endDate == '') {
            alert('Please choose start & end date');
        }
        else {
            return {
                startDate: startDate,
                endDate: endDate
            };
        }
    }
    else {
        if (selectedMapping.periodType === undefined)
            return;
        if (selectedMapping.periodType === 'Daily') {
            var date = jQuery('#periodSelector').datepicker('getDate');
            startDate = date;
            endDate = date;
        } else if (selectedMapping.periodType === 'Weekly') {
            startDate = weekStartDate;
            endDate = weekEndDate;
        } else if (selectedMapping.periodType === 'Monthly') {
            var date = jQuery('#periodSelector').monthpicker('getDate');
            startDate = new Date(date.getFullYear(), date.getMonth(), 1);
            endDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
        }
        return {
            startDate: startDate,
            endDate: endDate
        }
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

function populateOpenMRSLocationsDropdown() {
    // fetch locations
    jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/location", function (data) {

        var locationSelect = jQuery('<select id="locationSelect"></select>');
        locationSelect.append('<option value="">Select</option>');

        //reportSelect.on('change', onReportSelect);

        for (var i = 0; i < data.results.length; i++) {
            locationSelect.append('<option value="' + data.results[i].uuid + '">' + data.results[i].display + '</option>');
        }

        jQuery('#locationSelectContainer').html("");

        jQuery('#locationSelectContainer').append(locationSelect);

        jQuery("#locationSelect").hide().fadeIn("slow");

        locations = data.results;
    });
}

function populateDHISOrgUnitsDropdown() {
    // fetch orgunits
    jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/dhisconnector/orgunits", function (data) {

        var orgUnitSelect = jQuery('<select id="orgUnitSelect"></select>');
        orgUnitSelect.append('<option value="">Select</option>');

        //reportSelect.on('change', onReportSelect);

        for (var i = 0; i < data.results.length; i++) {
            orgUnitSelect.append('<option value="' + data.results[i].id + '">' + data.results[i].name + '</option>');
        }

        jQuery('#orgUnitSelectContainer').html("");

        jQuery('#orgUnitSelectContainer').append(orgUnitSelect);

        jQuery("#orgUnitSelect").hide().fadeIn("slow");

        orgUnitSelect = data.results;
    });
}

function getReportData() {
    // http://localhost:8084/openmrs/ws/rest/v1/reportingrest/reportdata/e858e06a-06a0-4f26-9b97-a225089cd526?startDate=2012-10-10&endDate=2012-11-11&location=5c303ee9-ca10-43fc-829f-c9e45bb7748e&v=custom:(dataSets)
    reportData = null;

    var reportGUID = mappings.filter(function (v) {
        return v.created == jQuery('#mappingSelect').val();
    })[0].periodIndicatorReportGUID;

    var periodDates = getPeriodDates();

    var locationGUID = jQuery('#locationSelect').val();

    // fetch report data
    return jQuery.get(OMRS_WEBSERVICES_BASE_URL + "/ws/rest/v1/reportingrest/reportdata/" + reportGUID + "?startDate=" + jQuery.datepicker.formatDate('yy-mm-dd', periodDates.startDate) + "&endDate=" + jQuery.datepicker.formatDate('yy-mm-dd', periodDates.endDate) + "&location=" + locationGUID + "&v=custom:(dataSets)", function (data) {
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

    var mappingReport = mappings.filter(function (v) {
        return v.created == jQuery('#mappingSelect').val();
    })[0].periodIndicatorReportGUID;

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
    ret &= testNotEmpty('#locationSelect', 'Location cannot be empty');
    ret &= testNotEmpty('#orgUnitSelect', 'Organisational Unit cannot be empty');
    ret &= testNotEmpty('#periodSelector', 'Period cannot be empty');

    // Make sure mapping applies to report
    ret &= checkMappingAppliesToReport();

    return ret;
}

function getMappingForIndicator(indicator) {
    var mapping = mappings.filter(function (v) {
        return v.created == jQuery('#mappingSelect').val();
    })[0];

    var element = mapping.elements.filter(function (v) {
        return v.indicator == indicator;
    })[0];

    if (element == undefined) // There is no mapping for this indicator
        return null;

    return {
        dataElement: element.dataElement,
        comboOption: element.comboOption
    }
}

function buildDXFJSON() {
    dxfJSON = null;

    return getReportData().then(function () {
        dxfJSON = {};

        dxfJSON.dataSet = mappings.filter(function (v) {
            return v.created == jQuery('#mappingSelect').val();
        })[0].dataSetUID

        dxfJSON.period = jQuery('#periodSelector').val();

        dxfJSON.orgUnit = jQuery('#orgUnitSelect').val();

        var indicatorValues = reportData.dataSets[0].rows[0];
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
    jQuery('#responseRow').remove();
    var reponseRow = jQuery('<tr id="responseRow"><th class="runHeader">Reponse</th><td><pre><code class="JSON">' + JSON.stringify(json, null, 2) + '</code></pre></td></tr>');

    jQuery('#tableBody').append(reponseRow);
    jQuery('pre code').each(function (i, block) {
        hljs.highlightBlock(block);
    });
    reponseRow.hide().fadeIn("slow");
}

function downloadAdx() {
    if (validateForm()) {
        buildDXFJSON().then(function () {
            jQuery.ajax({
                type : "GET",
                url : "adxGenerator.form",
                data: { "dxfDataValueSet": JSON.stringify(dxfJSON) },
                datatype: "json",
                success : function(activityMonitorData) {
                    if(activityMonitorData)
                        createDownload(activityMonitorData, 'application/xml', '.adx.xml');
                }
            });
        });
    }
}

function sendDataToDHIS() {
    if (validateForm()) {
        buildDXFJSON().then(function () {
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

function createDownload(content, contentType, extension) {
    var dl = document.createElement('a');

    dl.setAttribute('href', 'data:' + contentType + ';charset=utf-8,' + encodeURIComponent(content));
    dl.setAttribute('download', slugify(jQuery('#reportSelect option:selected').text()) + '-' + slugify(jQuery('#orgUnitSelect option:selected').text()) + '-' + slugify(jQuery('#periodSelector').val()) + extension);

    dl.style.display = 'none';
    document.body.appendChild(dl);

    dl.click();

    document.body.removeChild(dl);
}

function generateDXFDownload() {
    if (validateForm()) {
        buildDXFJSON().then(function () {
            createDownload(JSON.stringify(dxfJSON), 'application/json', '.dxf.json');
        });
    }
}

jQuery(function () {
    populateReportsDropdown();
    populateMappingsDropdown();
    populateOpenMRSLocationsDropdown();
    populateDHISOrgUnitsDropdown();
    hljs.initHighlightingOnLoad();

    // Attach datepickers
    jQuery('#openmrs-start-date').datepicker();
    jQuery('#openmrs-end-date').datepicker();

    jQuery('#custom-range-option').click(function() {
        jQuery('#date-range-section').toggle();
    });
});
