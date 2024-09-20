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
package org.openmrs.module.dhisconnector.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.Configurations;
import org.openmrs.module.dhisconnector.DHISServerConfiguration;
import org.openmrs.module.dhisconnector.DHISServerReportsToReceive;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.ReportToDataSetMapping;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.model.DHISDataValueSet;
import org.openmrs.module.dhisconnector.api.model.DHISLocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.api.model.DHISMapping;
import org.openmrs.module.dhisconnector.api.model.DHISOrganisationUnit;
import org.openmrs.module.dhisconnector.api.model.DHISServerConfigurationDTO;
import org.openmrs.module.dhisconnector.api.util.DHISConnectorUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.web.WebConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for the DHIS Connector Module admin pages
 */
@Controller
public class DHISConnectorController {

	protected final Log log = LogFactory.getLog(getClass());

	public static final String GLOBAL_PROPERTY_URL = "dhisconnector.url";

	public static final String GLOBAL_PROPERTY_USER = "dhisconnector.user";

	public static final String GLOBAL_PROPERTY_PASS = "dhisconnector.pass";

	public static final String GLOBAL_PROPERTY_START_DATE = "dhisconnector.startDate";

	public static final String GLOBAL_PROPERTY_END_DATE = "dhisconnector.endDate";
	
	public static String TYPE = "text/csv";

	static final List<String> SUPPORTED_AUTOMATION_PERIOD_TYPES = Arrays.asList("Daily", "Weekly", "WeeklySunday",
			"WeeklyWednesday", "WeeklyThursday", "WeeklySaturday", "BiWeekly", "Monthly", "BiMonthly", "Yearly",
			"Financial April", "Financial July", "Financial Oct", "SixMonthly", "SixMonthly April", "Quarterly");

	@RequestMapping(value = "/module/dhisconnector/manage.form", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		model.addAttribute("user", Context.getAuthenticatedUser());
	}

	@RequestMapping(value = "/module/dhisconnector/createMapping.form", method = RequestMethod.GET)
	public void createMapping(ModelMap model) {
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
	}

	@RequestMapping(value = "/module/dhisconnector/configureServer.form", method = RequestMethod.GET)
	public void configureServer(ModelMap model) {
		String url = Context.getAdministrationService().getGlobalProperty(GLOBAL_PROPERTY_URL);
		String user = Context.getAdministrationService().getGlobalProperty(GLOBAL_PROPERTY_USER);

		model.addAttribute("locations", Context.getLocationService().getAllLocations(true));
		model.addAttribute("url", url);
		model.addAttribute("user", user);
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
		model.addAttribute("servers", Context.getService(DHISConnectorService.class).getDHISServerConfigurations());
	}

	@RequestMapping(value = "/module/dhisconnector/getServers", method = RequestMethod.GET)
	public @ResponseBody List<String> getServersList(ModelMap model) {
		List<String> serversUuid = new ArrayList<>();
		List<DHISServerConfiguration> servers = Context.getService(DHISConnectorService.class)
				.getDHISServerConfigurations();
		for (DHISServerConfiguration dhisServerConfiguration : servers) {
			serversUuid.add(dhisServerConfiguration.getUuid());
		}
		return serversUuid;
	}

	@RequestMapping(value = "/module/dhisconnector/getServersByReportSelected", method = RequestMethod.GET)
	public @ResponseBody List<DHISServerConfigurationDTO> getServersListByReportSelected(
			@RequestParam(value = "sespReportUuid") String sespReportUuid) {

		List<DHISServerReportsToReceive> serversWithReports = Context.getService(DHISConnectorService.class)
				.getDHISServerReportsToReceive();

		List<String> serversUuids = new ArrayList<>();

		for (DHISServerReportsToReceive dhisServerReport : serversWithReports) {
			if (dhisServerReport.getSespReportUuid().equals(sespReportUuid))
				serversUuids.add(dhisServerReport.getDhisServerUuid());
		}

		List<DHISServerConfiguration> servers = Context.getService(DHISConnectorService.class)
				.getDHISServerConfigurations();

		List<DHISServerConfigurationDTO> serversForTheSelectedReport = new ArrayList<>();

		for (DHISServerConfiguration dhisServerConfiguration : servers) {
			for (String serverUuid : serversUuids) {
				if (serverUuid.equals(dhisServerConfiguration.getUuid()))
					serversForTheSelectedReport.add(new DHISServerConfigurationDTO(dhisServerConfiguration.getUrl(),
							dhisServerConfiguration.getUser(), dhisServerConfiguration.getPassword(),
							dhisServerConfiguration.getUuid()));
			}
		}
		return serversForTheSelectedReport;
	}

	@RequestMapping(value = "/module/dhisconnector/getServersAndReportsToReceive", method = RequestMethod.GET)
	public @ResponseBody List<String> getServersAndReportsToReceiveList(ModelMap model) {

		List<DHISServerReportsToReceive> serversWithReports = Context.getService(DHISConnectorService.class)
				.getDHISServerReportsToReceive();

		List<String> idsPerServerAndReports = new ArrayList<>();

		for (DHISServerReportsToReceive server : serversWithReports) {

			String idPerServerAndReport = server.getDhisServerUuid() + "" + server.getSespReportUuid();

			idsPerServerAndReports.add(idPerServerAndReport);
		}
		return idsPerServerAndReports;
	}

	@RequestMapping(value = "/module/dhisconnector/configureServer.form", params = "saveConfig", method = RequestMethod.POST)
	public void saveConfig(ModelMap model, @RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "pass", required = true) String pass, WebRequest req) throws ParseException {

		if (Context.getService(DHISConnectorService.class).testDHISServerDetails(url, user, pass)) {

			try {

				DHISServerConfiguration server = new DHISServerConfiguration(url, user, pass);

				Context.getService(DHISConnectorService.class).saveDHISServerConfiguration(server);

				req.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
						Context.getMessageSourceService().getMessage("dhisconnector.saveSuccess"),
						WebRequest.SCOPE_SESSION);

			} catch (Exception e) {
				req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						Context.getMessageSourceService().getMessage("dhisconnector.server.configuration.exist"),
						WebRequest.SCOPE_SESSION);
			}

			model.addAttribute("url", url);
			model.addAttribute("user", user);
			model.addAttribute("pass", pass);
			model.addAttribute("servers", Context.getService(DHISConnectorService.class).getDHISServerConfigurations());

		} else {
			req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					Context.getMessageSourceService().getMessage("dhisconnector.saveFailure"),
					WebRequest.SCOPE_SESSION);

			model.addAttribute("url", url);
			model.addAttribute("user", user);
			model.addAttribute("pass", pass);
			model.addAttribute("servers", Context.getService(DHISConnectorService.class).getDHISServerConfigurations());
			model.addAttribute("serversWithReports",
					Context.getService(DHISConnectorService.class).getDHISServerReportsToReceive());
		}
	}

	@RequestMapping(value = "/module/dhisconnector/saveDHISServerSespReportsToReceive", method = RequestMethod.POST)
	public @ResponseBody List<String> saveDHISServerSespReportsToReceive(ModelMap model, @RequestBody String[] payload,
			WebRequest req) throws ParseException {

		List<DHISServerReportsToReceive> serversWithReports = Context.getService(DHISConnectorService.class)
				.getDHISServerReportsToReceiveByServerUuid(payload[0]);

		Context.getService(DHISConnectorService.class).verifyDHISServerReportsToReceiveToBeDeleted(payload,
				serversWithReports);

		Context.getService(DHISConnectorService.class).saveDHISServerReportsToReceive(payload);

		req.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				Context.getMessageSourceService().getMessage("dhisconnector.saveSuccess"), WebRequest.SCOPE_SESSION);

		serversWithReports = Context.getService(DHISConnectorService.class).getDHISServerReportsToReceive();

		List<String> idsPerServerAndReports = new ArrayList<>();

		for (DHISServerReportsToReceive server : serversWithReports) {

			String idPerServerAndReport = server.getDhisServerUuid() + "" + server.getSespReportUuid();

			idsPerServerAndReports.add(idPerServerAndReport);
		}

		return idsPerServerAndReports;

	}

	@RequestMapping(value = "/module/dhisconnector/configureServer", params = "savePeriod", method = RequestMethod.POST)
	public void savePeriod(ModelMap model, @RequestParam(value = "startDate", required = true) String startDate,
			@RequestParam(value = "endDate", required = true) String endDate, WebRequest req) throws ParseException {

		AdministrationService as = Context.getAdministrationService();
		GlobalProperty startDateProperty = as.getGlobalPropertyObject(GLOBAL_PROPERTY_START_DATE);
		GlobalProperty endDateProperty = as.getGlobalPropertyObject(GLOBAL_PROPERTY_END_DATE);

		if (startDate == "" || endDate == "") {
			req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					Context.getMessageSourceService().getMessage("dhisconnector.periodParametersNotFilled"),
					WebRequest.SCOPE_SESSION);

			model.addAttribute("startDate", startDateProperty.getPropertyValue());
			model.addAttribute("endDate", endDateProperty.getPropertyValue());

		} else {
			startDateProperty.setPropertyValue(startDate);
			endDateProperty.setPropertyValue(endDate);

			as.saveGlobalProperty(startDateProperty);
			as.saveGlobalProperty(endDateProperty);

			req.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
					Context.getMessageSourceService().getMessage("dhisconnector.savePeriodSuccess"),
					WebRequest.SCOPE_SESSION);

			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
		}

	}

	@RequestMapping(value = "/module/dhisconnector/configureServer.form", params = "testConfig", method = RequestMethod.POST)
	public void testConfig(ModelMap model, WebRequest req) throws ParseException {

		AdministrationService as = Context.getAdministrationService();
		String url = as.getGlobalPropertyObject(GLOBAL_PROPERTY_URL).getPropertyValue();
		String user = as.getGlobalPropertyObject(GLOBAL_PROPERTY_USER).getPropertyValue();
		String pass = as.getGlobalPropertyObject(GLOBAL_PROPERTY_PASS).getPropertyValue();

		if (Context.getService(DHISConnectorService.class).testDHISServerDetails(url, user, pass)) {
			req.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
					Context.getMessageSourceService().getMessage("dhisconnector.connectSuccess"),
					WebRequest.SCOPE_SESSION);
		} else {
			req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					Context.getMessageSourceService().getMessage("dhisconnector.connectFailure"),
					WebRequest.SCOPE_SESSION);
		}

		model.addAttribute("url", url);
		model.addAttribute("user", user);
	}

	@RequestMapping(value = "/module/dhisconnector/runReports.form", method = RequestMethod.GET)
	public void showRunReports(ModelMap model) {

		AdministrationService as = Context.getAdministrationService();
		GlobalProperty startDateProperty = as.getGlobalPropertyObject(GLOBAL_PROPERTY_START_DATE);
		GlobalProperty endDateProperty = as.getGlobalPropertyObject(GLOBAL_PROPERTY_END_DATE);

		model.addAttribute("globalPropertyStartDate", startDateProperty.getPropertyValue());
		model.addAttribute("globalPropertyEndDate", endDateProperty.getPropertyValue());
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
	}

	@RequestMapping(value = "/module/dhisconnector/runReports.form", method = RequestMethod.POST)
	public void runReport(ModelMap model, @RequestParam(value = "report", required = true) String reportMappingFilename,
			@RequestParam(value = "location", required = true) Date date,
			@RequestParam(value = "date", required = true) Integer locationId, WebRequest req) throws ParseException {
		DHISConnectorService dcs = Context.getService(DHISConnectorService.class);

		List<PeriodIndicatorReportDefinition> reportsWithMappings = dcs.getReportWithMappings(dcs.getMappings());

		model.addAttribute("reports", reportsWithMappings);
	}

	private void passOnUploadingFeedback(ModelMap model, String successMessage, String failedMessage) {
		model.addAttribute("failureWhileUploading", failedMessage);
		model.addAttribute("successWhileUploading", successMessage);
	}

	@RequestMapping(value = "/module/dhisconnector/exportMappings.form", method = RequestMethod.GET)
	public void exportMapping(ModelMap model) {
		passOnExportedFeedback(model, "", "");
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
	}

	private void passOnExportedFeedback(ModelMap model, String failureWhileExporting, String successWhileExporting) {
		model.addAttribute("failureWhileExporting", failureWhileExporting);
		model.addAttribute("successWhileExporting", successWhileExporting);
	}

	@RequestMapping(value = "/module/dhisconnector/exportMappings.form", method = RequestMethod.POST)
	public void exportMapping(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		boolean shouldIncludeMetadata = true;
		String dontIncludeParameter = request.getParameter("dontIncludeMetadata");
		if (dontIncludeParameter != null) {
			shouldIncludeMetadata = !dontIncludeParameter.equals("on");
		}
		String[] selectedMappings = request.getParameter("selectedMappings") != null
				? request.getParameter("selectedMappings").split("<:::>")
				: null;
		String msg = "";

		if (selectedMappings != null) {
			try {
				String[] exported = Context.getService(DHISConnectorService.class)
						.exportSelectedMappings(selectedMappings);
				msg = exported[0];
				int BUFFER_SIZE = 4096;
				String fullPath = exported[1];// contains path to
												// backedupMappings

				if (StringUtils.isNotBlank(msg) && msg.startsWith("Successfully")) {
					exportZipFile(response, BUFFER_SIZE, fullPath);
					passOnExportedFeedback(model, "", msg);
				} else {
					passOnExportedFeedback(model, msg, "");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			msg = Context.getMessageSourceService().getMessage("dhisconnector.exportMapping.noMappingsFound");
			passOnExportedFeedback(model, "", msg);
		}
		try {
			response.sendRedirect("/module/dhisconnector/manageMappings.form");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * fullPath must be a temporally stored file path since it's deleted after being
	 * exported
	 */
	private void exportZipFile(HttpServletResponse response, int BUFFER_SIZE, String fullPath)
			throws FileNotFoundException, IOException {
		if (fullPath != null) {
			File downloadFile = new File(fullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);
			String mimeType = "application/octet-stream";

			System.out.println("MIME type: " + mimeType);
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());

			response.setHeader(headerKey, headerValue);

			OutputStream outStream = response.getOutputStream();
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			outStream.close();
			(new File(fullPath)).delete();
		}
	}
	
	@RequestMapping(value = "/module/dhisconnector/configureServer", params = "export", method = RequestMethod.POST)
	public void exportServerConfigurations(ModelMap model, HttpServletRequest request, HttpServletResponse response) {

			try {
				String filePath = Context.getService(DHISConnectorService.class).exportServerConfigurations();
				int BUFFER_SIZE = 4096;
					exportZipFile(response, BUFFER_SIZE, filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		try {
			response.sendRedirect("/module/dhisconnector/configureServer");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/module/dhisconnector/dhis2BackupExport.form", method = RequestMethod.GET)
	public void backupDHIS2APIExport(ModelMap model) {
		failureOrSuccessFeedback(model, "", "");
		model.addAttribute("dhis2BackupExists", Context.getService(DHISConnectorService.class).dhis2BackupExists());
		model.addAttribute("lastSyncedAt", Context.getService(DHISConnectorService.class).getLastSyncedAt());
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
	}

	@RequestMapping(value = "/module/dhisconnector/dhis2BackupImport.form", method = RequestMethod.GET)
	public void backupDHIS2(ModelMap model) {
		failureOrSuccessFeedback(model, "", "");
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
	}

	@RequestMapping(value = "/module/dhisconnector/dhis2BackupExport.form", method = RequestMethod.POST)
	public void backupDHIS2APIImport(ModelMap model, HttpServletResponse response) {
		String path = Context.getService(DHISConnectorService.class).getDHIS2APIBackupPath();

		if (Context.getService(DHISConnectorService.class).dhis2BackupExists() && StringUtils.isNotBlank(path)) {
			try {
				exportZipFile(response, 4096, path);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@RequestMapping(value = "/module/dhisconnector/dhis2BackupImport.form", method = RequestMethod.POST)
	public void backupDHIS2APIImport(ModelMap model,
			@RequestParam(value = "dhis2APIbBackup", required = false) MultipartFile dhis2APIbBackup) {
		if (StringUtils.isNotBlank(dhis2APIbBackup.getOriginalFilename())
				&& dhis2APIbBackup.getOriginalFilename().endsWith(".zip")) {
			String msg = Context.getService(DHISConnectorService.class).uploadDHIS2APIBackup(dhis2APIbBackup);

			if (msg.startsWith("Successfully")) {
				failureOrSuccessFeedback(model, "", msg);
			} else {
				failureOrSuccessFeedback(model, msg, "");
			}
		} else {
			failureOrSuccessFeedback(model,
					Context.getMessageSourceService().getMessage("dhisconnector.dhis2backup.wrongUpload"), "");
		}
	}

	private void failureOrSuccessFeedback(ModelMap model, String failureEncountered, String successEncountered) {
		model.addAttribute("failureEncountered", failureEncountered);
		model.addAttribute("successEncountered", successEncountered);
	}

	@RequestMapping(value = "/module/dhisconnector/manageMappings.form", method = RequestMethod.GET)
	public void manageMappings(ModelMap model) {
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
	}

	@RequestMapping(value = "/module/dhisconnector/manageMappings.form", method = RequestMethod.POST)
	public void manageMappings(ModelMap model, @RequestParam(value = "mapping", required = false) MultipartFile mapping,
			@RequestParam(value = "shouldReplaceMetadata", required = false) boolean shouldReplaceMetadata)
			throws IOException {
		String successMessage = "";
		String failedMessage = "";

		if (!mapping.isEmpty()) {
			// String msg =
			// Context.getService(DHISConnectorService.class).importMappings(mapping,
			// shouldReplaceMetadata);
			String msg = Context.getService(DHISConnectorService.class).uploadMappings(mapping);

			if (msg.startsWith("Successfully") || msg.startsWith("Carregado")) {
				successMessage = msg;
				failedMessage = "";
			} else {
				failedMessage = msg;
				successMessage = "";
			}
		} else {
			failedMessage = Context.getMessageSourceService().getMessage("dhisconnector.uploadMapping.mustSelectFile");
		}
		passOnUploadingFeedback(model, successMessage, failedMessage);
	}
	
	@RequestMapping(value = "/module/dhisconnector/configureServer", params = "import", method = RequestMethod.POST)
	public void importDHISConfigurationsServer(ModelMap model, @RequestParam(value = "configuration", required = false) MultipartFile configuration, WebRequest req)
			throws IOException {
		
		String url = Context.getAdministrationService().getGlobalProperty(GLOBAL_PROPERTY_URL);
		String user = Context.getAdministrationService().getGlobalProperty(GLOBAL_PROPERTY_USER);

		String msg = "";

		if (!configuration.isEmpty()) {
			
			if(hasCSVFormat(configuration)) {

			msg = Context.getService(DHISConnectorService.class).uploadDHISServerConfigurations(configuration);

			if (msg.startsWith("Successfully") || msg.startsWith("Carregado")) {
				req.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
						Context.getMessageSourceService().getMessage(msg),
						WebRequest.SCOPE_SESSION);
			} else {
				req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						Context.getMessageSourceService().getMessage(msg),
						WebRequest.SCOPE_SESSION);
			}
		} else {
			req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					Context.getMessageSourceService().getMessage("dhisconnector.uploadMapping.server.file.format"),
					WebRequest.SCOPE_SESSION);
		}
		
		} else {
			req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					Context.getMessageSourceService().getMessage("dhisconnector.uploadMapping.server.file.not.selected"),
					WebRequest.SCOPE_SESSION);
		}
		
		model.addAttribute("locations", Context.getLocationService().getAllLocations(true));
		model.addAttribute("url", url);
		model.addAttribute("user", user);
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
		model.addAttribute("servers", Context.getService(DHISConnectorService.class).getDHISServerConfigurations());

	}

	@RequestMapping(value = "/module/dhisconnector/adxGenerator.form", method = RequestMethod.GET)
	public @ResponseBody String adxGenerator(@RequestParam(value = "dxfDataValueSet") String dxfDataValueSet) {
		String adx = null;
		ObjectMapper mapper = new ObjectMapper();
		DHISDataValueSet dvs = null;
		try {
			if (StringUtils.isNotBlank(dxfDataValueSet)) {
				dvs = mapper.readValue(dxfDataValueSet, DHISDataValueSet.class);

				return Context.getService(DHISConnectorService.class).getAdxFromDxf(dvs);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return adx;
	}

	@RequestMapping(value = "/module/dhisconnector/failedData", method = RequestMethod.GET)
	public void failedDataRender(ModelMap model) {
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
		model.addAttribute("nunmberOfFailedPostAttempts",
				Context.getService(DHISConnectorService.class).getNumberOfFailedDataPosts());
	}

	@RequestMapping(value = "/module/dhisconnector/failedReportDataRender", method = RequestMethod.GET)
	public @ResponseBody List<String> failedReportDataRender(ModelMap model) {
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
		List<String> failedReportDataNames = Context.getService(DHISConnectorService.class)
				.getFileNameOfFailedDataPosts();
		return failedReportDataNames;
	}

	@RequestMapping(value = "/module/dhisconnector/failedData.form", params = "pushAgain", method = RequestMethod.POST)
	public void failedData(ModelMap model, HttpServletRequest request) {
		// TODO be specific which post went well and if any failed which one
		Context.getService(DHISConnectorService.class).postPreviouslyFailedData();
		model.addAttribute("nunmberOfFailedPostAttempts",
				Context.getService(DHISConnectorService.class).getNumberOfFailedDataPosts());
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Completed successfully!");
	}

	@RequestMapping(value = "/module/dhisconnector/resendReportData", method = RequestMethod.POST)
	public @ResponseBody Object resendReportDataToDHIS(ModelMap model, @RequestBody Map<String, String> payload,
			HttpServletRequest request) {
		String reportNameToResend = DHISConnectorUtil.putUnderScoreInReportName(payload.get("selectedReportName"));
		String finalReportName = reportNameToResend + "_" + new SimpleDateFormat("ddMMyyy").format(new Date()) + "_"
				+ payload.get("selectedPeriod");
		return Context.getService(DHISConnectorService.class).reSendReportToDHIS(finalReportName);
	}

	@RequestMapping(value = "/module/dhisconnector/resendFailedReportData", method = RequestMethod.POST)
	public @ResponseBody Object resendFailedReportData(ModelMap model, @RequestBody Map<String, String> payload,
			HttpServletRequest request) {
		String reportName = payload.get("selectedReportName");
		return Context.getService(DHISConnectorService.class)
				.reSendReportToDHIS(reportName.substring(0, reportName.length() - 5));
	}

	@RequestMapping(value = "/module/dhisconnector/automation.form", method = RequestMethod.GET)
	public void renderAutomationPage(ModelMap model) {
		initialiseAutomation(model, new Configurations().automationEnabled(), new ArrayList<String>());
	}

	private void initialiseAutomation(ModelMap model, boolean automationEnabled, List<String> postResponse) {
		// Filter the mappings with supported period types
		List<DHISMapping> supportedMappings = Context.getService(DHISConnectorService.class).getMappings().stream()
				.filter(mapping -> SUPPORTED_AUTOMATION_PERIOD_TYPES.contains(mapping.getPeriodType()))
				.collect(Collectors.toList());
		List<DHISOrganisationUnit> orgUnits = Context.getService(DHISConnectorService.class).getDHISOrgUnits();

		model.addAttribute("mappings", supportedMappings);
		model.addAttribute("reportToDataSetMappings",
				Context.getService(DHISConnectorService.class).getAllReportToDataSetMappings());
		model.addAttribute("automationEnabled", automationEnabled);
		model.addAttribute("postResponse", postResponse);
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
	}

	@RequestMapping(value = "/module/dhisconnector/automation.form", params = "run", method = RequestMethod.POST)
	public void postAutomationPage(ModelMap model, HttpServletRequest request) {
		String response = "";
		Configurations configs = new Configurations();
		List<String> postResponse = new ArrayList<String>();
		List<String> toBeRan = new ArrayList<String>();

		if (request.getParameterValues("mappingIds") != null) {
			for (String s : request.getParameterValues("mappingIds")) {
				ReportToDataSetMapping r2d = Context.getService(DHISConnectorService.class)
						.getReportToDataSetMappingByUuid(s);

				if (r2d != null) {
					toBeRan.add(s);
					r2d.setLastRun(null);
					Context.getService(DHISConnectorService.class).saveReportToDataSetMapping(r2d);
				}
			}
		}

		if (toBeRan.size() > 0) {
			for (String s : toBeRan) {
				ReportToDataSetMapping r2d = Context.getService(DHISConnectorService.class)
						.getReportToDataSetMappingByUuid(s);

				if (r2d != null) {
					List<String> resp = Context.getService(DHISConnectorService.class).runAndPushReportToDHIS(r2d);
					if (!resp.isEmpty())
						postResponse.addAll(resp);
				}
			}
		}
		if (postResponse.size() > 0)
			response += " -> Run was successful";

		initialiseAutomation(model, configs.automationEnabled(), postResponse);
		if (StringUtils.isNotBlank(response))
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, response);
	}

	@RequestMapping(value = "/module/dhisconnector/automation.form", params = "saveAutomationToggle", method = RequestMethod.POST)
	public void toggleAutomation(ModelMap model, HttpServletRequest request) {
		String response = "";
		Configurations configs = new Configurations();

		if (request.getParameter("toggleAutomation") != null) {
			configs.toggleAutomation(true);
			response += " -> Successfully turned on automation";
		} else {
			configs.toggleAutomation(false);
			response += " -> Successfully turned off automation";
		}

		initialiseAutomation(model, configs.automationEnabled(), new ArrayList<String>());
		if (StringUtils.isNotBlank(response))
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, response);
	}

	@RequestMapping(value = "/module/dhisconnector/automation.form", params = "delete", method = RequestMethod.POST)
	public void deleteSelectedMappings(ModelMap model, HttpServletRequest request) {
		String response = "";
		Configurations configs = new Configurations();
		List<String> postResponse = new ArrayList<String>();

		if (request.getParameterValues("mappingIds") != null) {
			for (String s : request.getParameterValues("mappingIds")) {
				Context.getService(DHISConnectorService.class).deleteReportToDataSetMapping(s);
			}
			response += " -> Delete was successful";
		}

		initialiseAutomation(model, configs.automationEnabled(), postResponse);
		if (StringUtils.isNotBlank(response))
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, response);
	}

	@RequestMapping(value = "/module/dhisconnector/automation.form", params = "addMapping", method = RequestMethod.POST)
	public void addMappingToAutomation(ModelMap model, HttpServletRequest request) {
		String response = "";
		String mapping = request.getParameter("mapping");
		Configurations configs = new Configurations();
		List<String> postResponse = new ArrayList<String>();

		if (StringUtils.isNotBlank(mapping)) {
			Context.getService(DHISConnectorService.class)
					.saveReportToDataSetMapping(new ReportToDataSetMapping(mapping));
			response += " -> Mapping added successfully";
		}

		initialiseAutomation(model, configs.automationEnabled(), postResponse);
		if (StringUtils.isNotBlank(response))
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, response);
	}

	@RequestMapping(value = "/module/dhisconnector/locationMapping.form", method = RequestMethod.GET)
	public void showLocationMappings(ModelMap model) {
		List<DHISOrganisationUnit> orgUnits = Context.getService(DHISConnectorService.class).getDHISOrgUnits();

		List<LocationToOrgUnitMapping> locationToOrgUnitMappings = Context.getService(DHISConnectorService.class)
				.getAllLocationToOrgUnitMappings();

		List<DHISLocationToOrgUnitMapping> dhisLocationToOrgUnitMappings = new ArrayList<>();

		for (LocationToOrgUnitMapping locationToOrgUnitMapping : locationToOrgUnitMappings) {

			DHISServerConfiguration server = Context.getService(DHISConnectorService.class)
					.getDHISServerByUuid(locationToOrgUnitMapping.getServerUuid());

			DHISLocationToOrgUnitMapping dhisLocationToOrgUnitMapping = new DHISLocationToOrgUnitMapping();

			dhisLocationToOrgUnitMapping.setLocation(locationToOrgUnitMapping.getLocation());
			dhisLocationToOrgUnitMapping.setServerUrl(server.getUrl());
			dhisLocationToOrgUnitMapping.setServerUuid(server.getUuid());

			for (DHISOrganisationUnit orgUnit : orgUnits) {
				if (orgUnit.getId().equals(locationToOrgUnitMapping.getOrgUnitUid())) {
					dhisLocationToOrgUnitMapping.setOrgUnitName(orgUnit.getName());
					dhisLocationToOrgUnitMapping.setOrgUnitUid(orgUnit.getId());
				}
			}
			dhisLocationToOrgUnitMappings.add(dhisLocationToOrgUnitMapping);
		}
		model.addAttribute("locations", Context.getLocationService().getAllLocations(true));
		model.addAttribute("servers", Context.getService(DHISConnectorService.class).getDHISServerConfigurations());
		model.addAttribute("orgUnits", orgUnits);
		model.addAttribute("locationToOrgUnitMappings", locationToOrgUnitMappings);
		model.addAttribute("dhisLocationToOrgUnitMappings", dhisLocationToOrgUnitMappings);
		model.addAttribute("showLogin", (Context.getAuthenticatedUser() == null) ? true : false);
	}

	@RequestMapping(value = "/module/dhisconnector/orgUnitsByServer", method = RequestMethod.GET)
	public @ResponseBody List<DHISOrganisationUnit> getOrgUnitsByServer(ModelMap model,
			@RequestParam(value = "serverUuid") String serverUuid) {

		DHISServerConfiguration server = Context.getService(DHISConnectorService.class).getDHISServerByUuid(serverUuid);

		List<DHISOrganisationUnit> orgUnits = Context.getService(DHISConnectorService.class)
				.getDHISOrgUnitsByServer(server);

		return orgUnits;
	}

	@RequestMapping(value = "/module/dhisconnector/locationMapping", method = RequestMethod.POST)
	public void postLocationOrgUnitsMappings(ModelMap model, HttpServletRequest request, WebRequest req) {
		String response = "";

		if (!request.getParameter("locationOrgUnitsMappings").isEmpty()) {
			String[] locationMappings = request.getParameter("locationOrgUnitsMappings").split(",");

			String serverUuid = locationMappings[0];
			String orgUnitUId = locationMappings[1];
			String locationUuid = locationMappings[2];
			String orgUnitName = locationMappings[3];

			if (StringUtils.isNotBlank(serverUuid) && StringUtils.isNotBlank(orgUnitUId)
					&& StringUtils.isNotBlank(locationUuid)) {
				Location location = Context.getLocationService().getLocationByUuid(locationUuid);
				LocationToOrgUnitMapping locationToOrg = Context.getService(DHISConnectorService.class)
						.getLocationToOrgUnitMappingByLocationAndOrgUnitIdAndServerUuid(location, orgUnitUId,
								serverUuid);
				if (locationToOrg != null) {
					req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, Context.getMessageSourceService()
							.getMessage("dhisconnector.locationMapping.per.server.exist"), WebRequest.SCOPE_SESSION);
				} else {
					Context.getService(DHISConnectorService.class).saveLocationToOrgUnitMapping(
							new LocationToOrgUnitMapping(location, orgUnitUId, serverUuid, orgUnitName));
					response += Context.getMessageSourceService()
							.getMessage("dhisconnector.locationMapping.per.server.saved");
				}
			} else {
				req.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, Context.getMessageSourceService().getMessage(
						"dhisconnector.locationMapping.per.server.value.notfilled"), WebRequest.SCOPE_SESSION);
			}
		}

		model.addAttribute(response);

		showLocationMappings(model);
		if (StringUtils.isNotBlank(response))
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, response);
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/module/dhisconnector/deleteLocationMapping", method = RequestMethod.POST)
	public void deleteLocationOrgUnitsMappings(ModelMap model,
			@RequestParam(value = "mapping", required = true) String mapping, WebRequest req) throws ParseException {

		String[] locationMappings = mapping.split(",");

		String serverUuid = locationMappings[0];
		String orgUnitUId = locationMappings[1];
		String locationUuid = locationMappings[2];

		Location location = Context.getLocationService().getLocationByUuid(locationUuid);

		Context.getService(DHISConnectorService.class)
				.deleteLocationToOrgUnitMappingsByLocationAndServerUuidAndOrgUnitUid(location, serverUuid, orgUnitUId);

		req.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				Context.getMessageSourceService()
						.getMessage(Context.getMessageSourceService()
								.getMessage("dhisconnector.locationMapping.per.server.deleted")),
				WebRequest.SCOPE_SESSION);
	}

	@RequestMapping(value = "/module/dhisconnector/deleteServer", method = RequestMethod.POST)
	public void deleteConfig(ModelMap model, @RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "location", required = true) String locationUuid, WebRequest req)
			throws ParseException {

		DHISServerConfiguration server = new DHISServerConfiguration(url, null, null);

		Context.getService(DHISConnectorService.class).permanentlyDHISServerConfiguration(server);

		req.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				Context.getMessageSourceService().getMessage(
						Context.getMessageSourceService().getMessage("dhisconnector.server.configuration.removed")),
				WebRequest.SCOPE_SESSION);

	}
	
	  public static boolean hasCSVFormat(MultipartFile file) {
		    if (TYPE.equals(file.getContentType())
		    		|| file.getContentType().equals("application/vnd.ms-excel")) {
		      return true;
		    }

		    return false;
		  }

}
