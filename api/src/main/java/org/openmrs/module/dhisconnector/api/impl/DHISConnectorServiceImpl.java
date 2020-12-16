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
package org.openmrs.module.dhisconnector.api.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.dhisconnector.Configurations;
import org.openmrs.module.dhisconnector.ReportToDataSetMapping;
import org.openmrs.module.dhisconnector.ReportToDataSetMapping.ReportingPeriodType;
import org.openmrs.module.dhisconnector.adx.AdxDataValue;
import org.openmrs.module.dhisconnector.adx.AdxDataValueGroup;
import org.openmrs.module.dhisconnector.adx.AdxDataValueGroupPeriod;
import org.openmrs.module.dhisconnector.adx.AdxDataValueSet;
import org.openmrs.module.dhisconnector.adx.AdxObjectFactory;
import org.openmrs.module.dhisconnector.adx.importsummary.ImportSummaries;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.db.DHISConnectorDAO;
import org.openmrs.module.dhisconnector.api.model.DHISCategoryCombo;
import org.openmrs.module.dhisconnector.api.model.DHISCategoryOptionCombo;
import org.openmrs.module.dhisconnector.api.model.DHISDataElement;
import org.openmrs.module.dhisconnector.api.model.DHISDataSet;
import org.openmrs.module.dhisconnector.api.model.DHISDataValue;
import org.openmrs.module.dhisconnector.api.model.DHISDataValueSet;
import org.openmrs.module.dhisconnector.api.model.DHISImportSummary;
import org.openmrs.module.dhisconnector.api.model.DHISMapping;
import org.openmrs.module.dhisconnector.api.model.DHISMappingElement;
import org.openmrs.module.dhisconnector.api.model.DHISOrganisationUnit;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.DefaultWebRenderer;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * It is a default implementation of {@link DHISConnectorService}.
 */
public class DHISConnectorServiceImpl extends BaseOpenmrsService implements DHISConnectorService {
	
	
	private DHISConnectorDAO dao;
	
	public static final String DHISCONNECTOR_MAPPINGS_FOLDER = File.separator + "dhisconnector" + File.separator
	        + "mappings";
	
	public static final String DHISCONNECTOR_DHIS2BACKUP_FOLDER = File.separator + "dhisconnector" + File.separator
	        + "dhis2Backup";
	
	public static final String DHISCONNECTOR_TEMP_FOLDER = File.separator + "dhisconnector" + File.separator + "temp";
	
	public static final String DHISCONNECTOR_LOGS_FOLDER = File.separator + "dhisconnector" + File.separator + "logs";
	
	public static final String DHISCONNECTOR_MAPPING_FILE_SUFFIX = ".mapping.json";
	
	public static final String DHISCONNECTOR_ORGUNIT_RESOURCE = "/api/organisationUnits.json?paging=false&fields=:identifiable,displayName";
	
	public static final String DATAVALUESETS_PATH = "/api/dataValueSets";
	
	public static final String DATASETS_PATH = "/api/dataSets/";
	
	public static final String ORGUNITS_PATH = "/api/organisationUnits/";
	
	public static String JSON_POST_FIX = ".json?paging=false";
	
	private String DATA_ELEMETS_PATH = "/api/dataElements/";
	
	private String CAT_OPTION_COMBOS_PATH = "/api/categoryOptionCombos/";
	
	public static final String DHISCONNECTOR_DATA_FOLDER = File.separator + "dhisconnector" + File.separator + "data";
	
	private Configurations configs = new Configurations();
	
	private AdxObjectFactory factory = new AdxObjectFactory();
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(DHISConnectorDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public DHISConnectorDAO getDao() {
		return dao;
	}
	
	private String getFromBackUp(String path) {
		String backupFilePath = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DHIS2BACKUP_FOLDER + path;
		
		File backupFile = new File(backupFilePath);
		
		if (backupFile.exists()) {
			try {
				return FileUtils.readFileToString(backupFile);
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
	private void saveToBackUp(String path, String jsonResponse) {
		String backUpDirecoryPath = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DHIS2BACKUP_FOLDER;
		
		File backUpDirecory = new File(backUpDirecoryPath);
		
		if (!backUpDirecory.exists()) {
			try {
				if (!backUpDirecory.mkdirs()) {
					return;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		String directoryStructure = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DHIS2BACKUP_FOLDER
		        + path.substring(0, path.lastIndexOf(File.separator));
		
		File directory = new File(directoryStructure);
		
		if (!directory.exists()) {
			try {
				if (!directory.mkdirs()) {
					return;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		try {
			PrintWriter enpointBackUp = new PrintWriter(
			        OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DHIS2BACKUP_FOLDER + path, "utf-8");
			enpointBackUp.write(jsonResponse);
			enpointBackUp.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		return;
	}
	
	@Override
	public String getDataFromDHISEndpoint(String endpoint) {
		String url = Context.getAdministrationService().getGlobalProperty("dhisconnector.url");
		String user = Context.getAdministrationService().getGlobalProperty("dhisconnector.user");
		String pass = Context.getAdministrationService().getGlobalProperty("dhisconnector.pass");
		
		DefaultHttpClient client = null;
		String payload = "";
		
		if (StringUtils.isNotBlank(endpoint)) {
			try {
				if (endpoint.contains("fields=")) {
					String stringToEncode = endpoint.split("fields=")[1];
					if (StringUtils.isNotBlank(stringToEncode)) {
						String encodedString = URLEncoder.encode(stringToEncode, "UTF-8");
						endpoint = endpoint.replace(stringToEncode, encodedString);
					}
				}
				URL dhisURL = new URL(url);
				String host = dhisURL.getHost();
				int port = dhisURL.getPort();
				
				HttpHost targetHost = new HttpHost(host, port, dhisURL.getProtocol());
				client = new DefaultHttpClient();
				BasicHttpContext localcontext = new BasicHttpContext();
				
				HttpGet httpGet = new HttpGet(dhisURL.getPath() + endpoint);
				Credentials creds = new UsernamePasswordCredentials(user, pass);
				Header bs = new BasicScheme().authenticate(creds, httpGet, localcontext);
				httpGet.addHeader("Authorization", bs.getValue());
				httpGet.addHeader("Content-Type", "application/json");
				httpGet.addHeader("Accept", "application/json");
				HttpResponse response = client.execute(targetHost, httpGet, localcontext);
				HttpEntity entity = response.getEntity();
				
				if (entity != null && response.getStatusLine().getStatusCode() == 200) {
					payload = EntityUtils.toString(entity);
					saveToBackUp(endpoint, payload);
				} else {
					payload = getFromBackUp(endpoint);
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				payload = getFromBackUp(endpoint);
			}
			finally {
				if (client != null) {
					client.getConnectionManager().shutdown();
				}
			}
		}
		
		return payload;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getCodeFromClazz(Class clazz, String endPoint) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = getDataFromDHISEndpoint(endPoint);
		String code = null;
		
		try {
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (StringUtils.isNotBlank(jsonResponse)) {
				Object obj = mapper.readValue(jsonResponse, clazz);
				
				if (obj instanceof DHISDataSet)
					code = ((DHISDataSet) obj).getCode();
				else if (obj instanceof DHISOrganisationUnit)
					code = ((DHISOrganisationUnit) obj).getCode();
				else if (obj instanceof DHISDataElement)
					code = ((DHISDataElement) obj).getCode();
				else if (obj instanceof DHISCategoryOptionCombo) {
					code = ((DHISCategoryOptionCombo) obj).getCode();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return code != null ? code : "";
	}
	
	private DHISCategoryOptionCombo getCategoryOptionCombo(String categoryOptionComboId) {
		String data = getDataFromDHISEndpoint(CAT_OPTION_COMBOS_PATH + categoryOptionComboId + JSON_POST_FIX);
		
		if (StringUtils.isNotBlank(data)) {
			try {
				return new ObjectMapper().readValue(data, DHISCategoryOptionCombo.class);
				
			}
			catch (JsonParseException e) {
				e.printStackTrace();
			}
			catch (JsonMappingException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private DHISCategoryCombo getCategoryComboFromOption(String categoryOptionComboId) {
		String data = getDataFromDHISEndpoint(CAT_OPTION_COMBOS_PATH + categoryOptionComboId + JSON_POST_FIX);
		DHISCategoryOptionCombo optionCombo;
		
		if (StringUtils.isNotBlank(data)) {
			try {
				optionCombo = new ObjectMapper().readValue(data, DHISCategoryOptionCombo.class);
				
				if (optionCombo != null)
					return optionCombo.getCategoryCombo();
			}
			catch (JsonParseException e) {
				e.printStackTrace();
			}
			catch (JsonMappingException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private AdxDataValueSet convertDHISDataValueSetToAdxDataValueSet(DHISDataValueSet valueSet) {
		AdxDataValueSet adx = null;
		
		if (valueSet != null) {
			try {
				String dataSet = getCodeFromClazz(DHISDataSet.class, DATASETS_PATH + valueSet.getDataSet() + JSON_POST_FIX);
				String orgUnit = getCodeFromClazz(DHISOrganisationUnit.class,
				    ORGUNITS_PATH + valueSet.getOrgUnit() + JSON_POST_FIX);
				String period = valueSet.getPeriod();
				AdxDataValueGroup group = new AdxDataValueGroup();
				XMLGregorianCalendar exported = DatatypeFactory.newInstance()
				        .newXMLGregorianCalendar(new GregorianCalendar());
				AdxDataValueGroupPeriod adxPeriod = new AdxDataValueGroupPeriod(period);
				
				adx = new AdxDataValueSet();
				adx.setExported(exported);
				
				group.setOrgUnit(orgUnit);
				group.setDataSet(dataSet);
				group.setPeriod(adxPeriod);
				group.setCompleteDate(adxPeriod.getdHISAdxEndDate());
				
				for (DHISDataValue dv : valueSet.getDataValues()) {
					AdxDataValue adxDv = new AdxDataValue();
					String dataElement = getCodeFromClazz(DHISDataElement.class,
					    DATA_ELEMETS_PATH + dv.getDataElement() + JSON_POST_FIX);
					
					if (StringUtils.isNotBlank(dataElement)) {
						adxDv.setDataElement(dataElement);
						adxDv.setValue(new BigDecimal(Integer.parseInt(dv.getValue())));
						
						if (StringUtils.isNotBlank(dv.getCategoryOptionCombo())) {
							DHISCategoryCombo c = getCategoryComboFromOption(dv.getCategoryOptionCombo());
							DHISCategoryOptionCombo oc = getCategoryOptionCombo(dv.getCategoryOptionCombo());
							
							if (c != null && oc != null)
								adxDv.getOtherAttributes().put(
								    new QName(StringUtils.isNotBlank(c.getCode()) ? c.getCode() : c.getId()),
								    StringUtils.isNotBlank(oc.getCode()) ? oc.getCode() : oc.getId());
						}
						group.getDataValues().add(adxDv);
					}
				}
				adx.getGroups().add(group);
			}
			catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
			
		}
		return adx;
	}
	
	/**
	 * TODO this should support selection of a failed attempt(s) to push again
	 */
	@Override
	public void postPreviouslyFailedData() {
		subDirectoryJSONAndXMLFilePost(new File(OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DATA_FOLDER));
	}
	
	private void subDirectoryJSONAndXMLFilePost(File file) {
		if (file != null && file.exists()) {
			if (file.isFile() && (file.getName().endsWith(".json") || file.getName().endsWith(".xml"))) {
				try {
					String data = FileUtils.readFileToString(file);
					String endPoint = file.getPath()
					        .replace(OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DATA_FOLDER, "")
					        .replace(File.separator + file.getName(), "");
					
					if (StringUtils.isNotBlank(data) && StringUtils.isNotBlank(endPoint)) {
						file.delete();
						postDataToDHISEndpoint(endPoint, data);
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			} else if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					subDirectoryJSONAndXMLFilePost(f);
				}
			}
		}
	}
	
	@Override
	public Integer getNumberOfFailedDataPosts() {
		File dataDir = new File(OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DATA_FOLDER);
		
		int count = 0;
		if (dataDir.exists() && dataDir.isDirectory()) {
			for (File f : dataDir.listFiles()) {
				count += subDirectoryJSONAndXMLFileCount(f);
			}
		}
		return count;
	}
	
	private int subDirectoryJSONAndXMLFileCount(File dataDir) {
		int count = 0;
		if (dataDir != null && dataDir.exists()) {
			if (dataDir.isFile() && (dataDir.getName().endsWith(".json") || dataDir.getName().endsWith(".xml")))
				count++;
			else if (dataDir.isDirectory()) {
				for (File f : dataDir.listFiles()) {
					count += subDirectoryJSONAndXMLFileCount(f);
				}
			}
		}
		
		return count;
	}
	
	private void backUpData(String endPoint, String data, String extension) {
		if (StringUtils.isNotBlank(endPoint) && StringUtils.isNotBlank(data)) {
			if (StringUtils.isBlank(extension))
				extension = ".json";
			if (!endPoint.startsWith(File.separator))
				endPoint = File.separator + endPoint;
			
			String dataLocation = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DATA_FOLDER + endPoint;
			File dataFile = new File(dataLocation);
			
			if (!dataFile.exists())
				dataFile.mkdirs();
			
			String datafileLocation = dataFile.getPath() + File.separator
			        + new SimpleDateFormat("ddMMyyy_hhmmss").format(new Date()) + extension;
			File datafile = new File(datafileLocation);
			
			if (!datafile.exists()) {
				try {
					FileUtils.writeStringToFile(datafile, data);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public String postDataToDHISEndpoint(String endpoint, String data) {
		String url = Context.getAdministrationService().getGlobalProperty("dhisconnector.url");
		String user = Context.getAdministrationService().getGlobalProperty("dhisconnector.user");
		String pass = Context.getAdministrationService().getGlobalProperty("dhisconnector.pass");
		
		DefaultHttpClient client = null;
		String payload = "";
		String extension = ".json";
		
		try {
			URL dhisURL = new URL(url);
			
			String host = dhisURL.getHost();
			int port = dhisURL.getPort();
			
			HttpHost targetHost = new HttpHost(host, port, dhisURL.getProtocol());
			client = new DefaultHttpClient();
			BasicHttpContext localcontext = new BasicHttpContext();
			
			HttpPost httpPost = new HttpPost(
			        dhisURL.getPath() + endpoint + (configs.useAdxInsteadOfDxf() ? (endpoint.indexOf("?") > -1 ? "&"
			                : "?" + "dataElementIdScheme=CODE&orgUnitIdScheme=CODE&idScheme=CODE") : ""));
			
			Credentials creds = new UsernamePasswordCredentials(user, pass);
			Header bs = new BasicScheme().authenticate(creds, httpPost, localcontext);
			
			httpPost.addHeader("Authorization", bs.getValue());
			if (configs.useAdxInsteadOfDxf()) {
				extension = ".xml";
				httpPost.addHeader("Content-Type", "application/xml+adx");
				httpPost.addHeader("Accept", "application/xml");
			} else {
				httpPost.addHeader("Content-Type", "application/json");
				httpPost.addHeader("Accept", "application/json");
			}
			
			httpPost.setEntity(new StringEntity(data));
			
			HttpResponse response = client.execute(targetHost, httpPost, localcontext);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				payload = EntityUtils.toString(entity);
				logPayload(payload);
			} else {
				backUpData(endpoint, data, extension);
				System.out.println("Failed to get entity from dhis2 server, network failure!");
			}
		}
		catch (Exception ex) {
			backUpData(endpoint, data, extension);
			ex.printStackTrace();
		}
		finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		
		return payload;
	}
	
	private void logPayload(String payload) {
		File logFolder = new File(OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_LOGS_FOLDER);
		String endpoint = payload.startsWith("<?xml") ? ".xml" : ".json";
		
		if (!logFolder.exists())
			logFolder.mkdirs();
		try {
			FileUtils.writeStringToFile(new File(logFolder.getAbsolutePath() + File.separator + "dhisResponse-"
			        + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + endpoint),
			    payload);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean testDHISServerDetails(String url, String user, String pass) {
		URL testURL;
		Boolean success = true;
		
		// Check if the URL makes sense
		try {
			testURL = new URL(url + "/api/resources"); // Add the root API
			                                           // endpoint to the URL
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
		
		HttpHost targetHost = new HttpHost(testURL.getHost(), testURL.getPort(), testURL.getProtocol());
		DefaultHttpClient httpclient = new DefaultHttpClient();
		BasicHttpContext localcontext = new BasicHttpContext();
		
		try {
			HttpGet httpGet = new HttpGet(testURL.toURI());
			Credentials creds = new UsernamePasswordCredentials(user, pass);
			Header bs = new BasicScheme().authenticate(creds, httpGet, localcontext);
			httpGet.addHeader("Authorization", bs.getValue());
			httpGet.addHeader("Content-Type", "application/json");
			httpGet.addHeader("Accept", "application/json");
			
			//execute the test query
			HttpResponse response = httpclient.execute(targetHost, httpGet, localcontext);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				success = false;
				
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			success = false;
		}
		finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		return success;
	}
	
	@Override
	public Object saveMapping(DHISMapping mapping) {
		String mappingsDirecoryPath = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_MAPPINGS_FOLDER;
		
		File mappingsDirecory = new File(mappingsDirecoryPath);
		
		if (!mappingsDirecory.exists()) {
			try {
				if (!mappingsDirecory.mkdirs()) {
					return null;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return e;
			}
		}
		
		String filename = mapping.getName() + "." + mapping.getCreated() + ".mapping.json";
		
		File newMappingFile = new File(mappingsDirecoryPath + File.separator + filename);
		
		if (newMappingFile.exists()) {//user is trying to edit a mapping, delete previous copy first
			newMappingFile.delete();
		}
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValue(newMappingFile, mapping);
		}
		catch (Exception e) {
			e.printStackTrace();
			return e;
		}
		
		return mapping;
	}
	
	@Override
	public String getAdxFromDxf(DHISDataValueSet dataValueSet) {
		return beautifyXML(
		    factory.translateAdxDataValueSetIntoString(convertDHISDataValueSetToAdxDataValueSet(dataValueSet)));
	}
	
	@Override
	public Object postDataValueSet(DHISDataValueSet dataValueSet) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonOrXmlString;
		String responseString;
		
		try {
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			jsonOrXmlString = configs.useAdxInsteadOfDxf()
			        ? factory.translateAdxDataValueSetIntoString(convertDHISDataValueSetToAdxDataValueSet(dataValueSet))
			        : mapper.writeValueAsString(dataValueSet);
			responseString = postDataToDHISEndpoint(DATAVALUESETS_PATH, jsonOrXmlString);
			
			if (StringUtils.isNotBlank(responseString)) {
				if (configs.useAdxInsteadOfDxf()) {
					JAXBContext jaxbImportSummaryContext = JAXBContext.newInstance(ImportSummaries.class);
					Unmarshaller importSummaryUnMarshaller = jaxbImportSummaryContext.createUnmarshaller();
					
					return (ImportSummaries) importSummaryUnMarshaller.unmarshal(new StringReader(responseString));
				} else {
					return mapper.readValue(responseString, DHISImportSummary.class);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<DHISMapping> getMappings() {
		List<DHISMapping> mappings = new ArrayList<DHISMapping>();
		
		ObjectMapper mapper = new ObjectMapper();
		
		String mappingsDirecoryPath = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_MAPPINGS_FOLDER;
		
		File mappingsDirecory = new File(mappingsDirecoryPath);
		
		File[] files = mappingsDirecory.listFiles(new FilenameFilter() {
			
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(DHISCONNECTOR_MAPPING_FILE_SUFFIX);
			}
		});
		
		if (files == null)
			return null;
		
		for (File f : files) {
			try {
				mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				mappings.add(mapper.readValue(f, DHISMapping.class));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return mappings;
	}
	
	@Override
	public List<PeriodIndicatorReportDefinition> getReportWithMappings(List<DHISMapping> mappings) {
		List<ReportDefinition> all = Context.getService(ReportDefinitionService.class).getAllDefinitions(false);
		
		List<PeriodIndicatorReportDefinition> pireports = new ArrayList<PeriodIndicatorReportDefinition>();
		
		for (ReportDefinition r : all) {
			if (r instanceof PeriodIndicatorReportDefinition && mappingsHasGUID(mappings, r.getUuid())) {
				pireports.add((PeriodIndicatorReportDefinition) r);
			}
		}
		
		return pireports;
	}
	
	@Override
	public List<DHISOrganisationUnit> getDHISOrgUnits() {
		List<DHISOrganisationUnit> orgUnits = new ArrayList<DHISOrganisationUnit>();
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = new String();
		JsonNode node;
		
		jsonResponse = getDataFromDHISEndpoint(DHISCONNECTOR_ORGUNIT_RESOURCE);
		
		try {
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			node = mapper.readTree(jsonResponse);
			orgUnits = Arrays
			        .asList(mapper.readValue(node.get("organisationUnits").toString(), DHISOrganisationUnit[].class));
		}
		catch (Exception ex) {
			System.out.print(ex.getMessage());
		}
		
		return orgUnits;
	}
	
	private boolean mappingsHasGUID(List<DHISMapping> mappings, String GUID) {
		if (mappings == null)
			return false;
		
		for (DHISMapping mapping : mappings) {
			if (mapping.getPeriodIndicatorReportGUID().equals(GUID)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public String uploadMappings(MultipartFile mapping) {
		String msg = "";
		String tempFolderName = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_TEMP_FOLDER + File.separator;
		String mappingFolderName = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_MAPPINGS_FOLDER
		        + File.separator;
		String mappingName = mapping.getOriginalFilename();
		
		if (mappingName.endsWith(".zip")) {
			boolean allFailed = true;
			File tempMappings = new File(tempFolderName + mappingName);
			
			(new File(tempFolderName)).mkdirs();
			try {
				mapping.transferTo(tempMappings);
				
				try {
					ZipFile zipfile = new ZipFile(tempMappings);
					
					for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
						ZipEntry entry = (ZipEntry) e.nextElement();
						
						if (entry.isDirectory()) {
							System.out.println(
							    "Incorrect file (Can't be a folder instead): " + entry.getName() + " has been ignored");
						} else if (entry.getName().endsWith(DHISCONNECTOR_MAPPING_FILE_SUFFIX)) {
							File outputFile = new File(mappingFolderName, entry.getName());
							
							if (outputFile.exists()) {
								System.out.println("File: " + outputFile.getName() + " already exists and has been ignored");
							} else {
								BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
								BufferedOutputStream outputStream = new BufferedOutputStream(
								        new FileOutputStream(outputFile));
								
								try {
									System.out.println("Extracting: " + entry);
									IOUtils.copy(inputStream, outputStream);
									allFailed = false;
								}
								finally {
									outputStream.close();
									inputStream.close();
								}
							}
						} else {
							System.out.println("Incorrect file: " + entry.getName() + " has been ignored");
						}
					}
					if (!allFailed) {
						msg = Context.getMessageSourceService().getMessage("dhisconnector.uploadMapping.groupSuccess");
					} else {
						msg = Context.getMessageSourceService().getMessage("dhisconnector.uploadMapping.allFailed");
					}
					FileUtils.deleteDirectory(new File(tempFolderName));
				}
				catch (Exception e) {
					System.out.println("Error while extracting file:" + mapping.getName() + " ; " + e);
				}
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		} else if (mappingName.endsWith(DHISCONNECTOR_MAPPING_FILE_SUFFIX)) {
			try {
				File uploadedMapping = new File(mappingFolderName + mappingName);
				if (uploadedMapping.exists()) {
					msg = Context.getMessageSourceService().getMessage("dhisconnector.uploadMapping.exists");
				} else {
					mapping.transferTo(uploadedMapping);
					msg = Context.getMessageSourceService().getMessage("dhisconnector.uploadMapping.singleSuccess");
				}
				
			}
			catch (IllegalStateException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			msg = Context.getMessageSourceService().getMessage("dhisconnector.uploadMapping.wrongType");
		}
		
		return msg;
	}
	
	@Override
	public String[] exportSelectedMappings(String[] selectedMappings) {
		String[] cleanedSelectedMappings = cleanSelectedMappings(selectedMappings);
		String msg = "";
		String[] returnStr = new String[2];
		String path = null;
		
		try {
			byte[] buffer = new byte[1024];
			String sourceDirectory = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_MAPPINGS_FOLDER
			        + File.separator;
			String tempFolderName = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_TEMP_FOLDER + File.separator;
			String suffix = ".mapping.json";
			String zipFile = tempFolderName + "exported-mappings_" + (new Date()).getTime() + ".zip";
			
			(new File(tempFolderName)).mkdirs();
			
			FileOutputStream fout = new FileOutputStream(zipFile);
			ZipOutputStream zout = new ZipOutputStream(fout);
			File dir = new File(sourceDirectory);
			
			if (!dir.isDirectory()) {
				System.out.println(sourceDirectory + " is not a directory");
			} else {
				File[] files = dir.listFiles();
				String mappings = "";
				
				if (files.length == 0) {
					msg = Context.getMessageSourceService().getMessage("dhisconnector.exportMapping.noMappingsFound");
				} else {
					for (int i = 0; i < files.length; i++) {
						if (files[i].getName().endsWith(suffix)) {
							FileInputStream fin = new FileInputStream(files[i]);
							
							mappings += files[i].getName() + "<:::>";
							System.out.println("Compressing " + files[i].getName());
							if (cleanedSelectedMappings.length == 0) {
								copyToZip(buffer, zout, files, i, fin);
							} else {
								if (selectedMappingsIncludes(cleanedSelectedMappings, files[i].getName())) {
									copyToZip(buffer, zout, files, i, fin);
								}
							}
							msg = Context.getMessageSourceService().getMessage("dhisconnector.exportMapping.success");
							zout.closeEntry();
							fin.close();
						}
					}
					if (mappings.split("<:::>").length == 0) {
						msg = Context.getMessageSourceService().getMessage("dhisconnector.exportMapping.noMappingsFound");
					}
					path = zipFile;
				}
			}
			zout.close();
			System.out.println("Zip file has been created!");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		returnStr[0] = msg;
		returnStr[1] = path;
		return returnStr;
	}
	
	private String[] cleanSelectedMappings(String[] selectedMappings) {
		int r, w;
		final int n = r = w = selectedMappings.length;
		while (r > 0) {
			final String s = selectedMappings[--r];
			if (!s.equals("null")) {
				selectedMappings[--w] = s;
			}
		}
		return Arrays.copyOfRange(selectedMappings, w, n);
	}
	
	private void copyToZip(byte[] buffer, ZipOutputStream zout, File[] files, int i, FileInputStream fin)
	        throws IOException {
		zout.putNextEntry(new ZipEntry(files[i].getName()));
		int length;
		while ((length = fin.read(buffer)) > 0) {
			zout.write(buffer, 0, length);
		}
	}
	
	private boolean selectedMappingsIncludes(String[] selectedMappings, String name) {
		boolean contains = false;
		
		for (int i = 0; i < selectedMappings.length; i++) {
			if ((selectedMappings[i] + DHISCONNECTOR_MAPPING_FILE_SUFFIX).equals(name)) {
				contains = true;
			}
		}
		return contains;
	}
	
	@Override
	public boolean dhis2BackupExists() {
		File backup = new File(
		        OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DHIS2BACKUP_FOLDER + File.separator + "api");
		
		if (backup.exists() && backup.isDirectory() && backup.list().length > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String getLastSyncedAt() {
		File backup = new File(
		        OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DHIS2BACKUP_FOLDER + File.separator + "api");
		
		if (dhis2BackupExists()) {
			Date lastModified = new Date(backup.lastModified());
			
			return Context.getDateFormat().format(lastModified) + " " + lastModified.getHours() + ":"
			        + lastModified.getMinutes() + ":" + lastModified.getSeconds();
		} else {
			return "";
		}
	}
	
	@Override
	public String getDHIS2APIBackupPath() {
		String zipFile = null;
		String sourceDirectory = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DHIS2BACKUP_FOLDER
		        + File.separator;
		String tempFolderName = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_TEMP_FOLDER + File.separator;
		File temp = new File(tempFolderName);
		
		if (!temp.exists()) {
			temp.mkdirs();
		}
		zipFile = tempFolderName + "exported-dhis2APIBackup_" + (new Date()).getTime() + ".zip";
		
		File dirObj = new File(sourceDirectory);
		ZipOutputStream out;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipFile));
			
			System.out.println("Creating : " + zipFile);
			addDHIS2APIDirectories(dirObj, out, sourceDirectory);
			out.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return zipFile;
	}
	
	private void addDHIS2APIDirectories(File dirObj, ZipOutputStream out, String sourceDirectory) {
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[1024];
		
		for (int i = 0; i < files.length; i++) {
			if (matchingDHIS2APIBackUpStructure(files[i])) {
				if (files[i].isDirectory()) {
					addDHIS2APIDirectories(files[i], out, sourceDirectory);
					continue;
				}
				try {
					FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
					String entryPath = (new File(sourceDirectory)).toURI().relativize(files[i].toURI()).getPath();
					
					System.out.println("Adding: " + entryPath);
					out.putNextEntry(new ZipEntry(entryPath));
					
					int len;
					while ((len = in.read(tmpBuf)) > 0) {
						out.write(tmpBuf, 0, len);
					}
					out.closeEntry();
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean matchingDHIS2APIBackUpStructure(File file) {
		return StringUtils.equals(file.getName(), "api") || StringUtils.equals(file.getName(), "categoryCombos")
		        || StringUtils.equals(file.getName(), "dataElements") || StringUtils.equals(file.getName(), "dataSets")
		        || file.getName().indexOf(".json") > 0;
	}
	
	@Override
	public String uploadDHIS2APIBackup(MultipartFile dhis2APIBackup) {
		String msg = "";
		String outputFolder = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_TEMP_FOLDER;
		File temp = new File(outputFolder);
		File dhis2APIBackupRootDir = new File(OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_DHIS2BACKUP_FOLDER);
		
		if (!temp.exists()) {
			temp.mkdirs();
		}
		
		File dest = new File(outputFolder + File.separator + dhis2APIBackup.getOriginalFilename());
		
		if (!dhis2APIBackup.isEmpty() && dhis2APIBackup.getOriginalFilename().endsWith(".zip")) {
			try {
				dhis2APIBackup.transferTo(dest);
				
				if (dest.exists() && dest.isFile()) {
					File unzippedAt = new File(outputFolder + File.separator + "api");
					File api = new File(dhis2APIBackupRootDir.getPath() + File.separator + "api");
					
					unZipDHIS2APIBackupToTemp(dest.getCanonicalPath());
					if ((new File(outputFolder)).list().length > 0 && unzippedAt.exists()) {
						if (!dhis2APIBackupRootDir.exists()) {
							dhis2APIBackupRootDir.mkdirs();
						}
						
						if (FileUtils.sizeOfDirectory(dhis2APIBackupRootDir) > 0 && unzippedAt.exists()
						        && unzippedAt.isDirectory()) {
							if (checkIfDirContainsFile(dhis2APIBackupRootDir, "api")) {
								
								FileUtils.deleteDirectory(api);
								api.mkdir();
								msg = Context.getMessageSourceService()
								        .getMessage("dhisconnector.dhis2backup.replaceSuccess");
							} else {
								msg = Context.getMessageSourceService()
								        .getMessage("dhisconnector.dhis2backup.import.success");
							}
							FileUtils.copyDirectory(unzippedAt, api);
							FileUtils.deleteDirectory(temp);
						}
					}
				}
			}
			catch (IllegalStateException e) {
				msg = Context.getMessageSourceService().getMessage("dhisconnector.dhis2backup.failure");
				e.printStackTrace();
			}
			catch (IOException e) {
				msg = Context.getMessageSourceService().getMessage("dhisconnector.dhis2backup.failure");
				e.printStackTrace();
			}
		} else {
			msg = Context.getMessageSourceService().getMessage("dhisconnector.dhis2backup.failure");
		}
		
		return msg;
	}
	
	private boolean checkIfDirContainsFile(File dir, String fileName) {
		boolean contains = false;
		
		if (dir.exists() && dir.isDirectory()) {
			for (File d : dir.listFiles()) {
				if (d.getName().equals(fileName))//can be directory still
					contains = true;
			}
		}
		return contains;
	}
	
	private void unZipDHIS2APIBackupToTemp(String zipFile) {
		byte[] buffer = new byte[1024];
		String outputFolder = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_TEMP_FOLDER;
		
		try {
			File destDir = new File(outputFolder);
			if (!destDir.exists()) {
				destDir.mkdir();
			}
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = zipIn.getNextEntry();
			
			while (entry != null) {
				String filePath = outputFolder + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					if (!(new File(filePath)).getParentFile().exists()) {
						(new File(filePath)).getParentFile().mkdirs();
					}
					(new File(filePath)).createNewFile();
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
					byte[] bytesIn = buffer;
					int read = 0;
					while ((read = zipIn.read(bytesIn)) != -1) {
						bos.write(bytesIn, 0, read);
					}
					bos.close();
				} else {
					// if the entry is a directory, make the directory
					File dir = new File(filePath);
					dir.mkdir();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
			zipIn.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public DHISMapping getMapping(String s) {
		if (StringUtils.isNotBlank(s)) {
			String mapping = s.replace("[@]",
			    ".");/*meant to be uuid, however we are hacking it to contain what we want (mappingName<@>dateTimeStampWhenCreated)*/
			
			for (DHISMapping m : getMappings()) {
				if (mapping.equals(m.getName() + "." + m.getCreated().toString())) {
					return m;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public boolean permanentlyDeleteMapping(DHISMapping mapping) {
		File mappingsFolder = new File(OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_MAPPINGS_FOLDER);
		boolean deleted = false;
		
		if (mapping != null) {
			String mappingFileName = mapping.getName() + "." + mapping.getCreated() + DHISCONNECTOR_MAPPING_FILE_SUFFIX;
			
			if (checkIfDirContainsFile(mappingsFolder, mappingFileName)) {
				try {
					if ((new File(mappingsFolder.getCanonicalPath() + File.separator + mappingFileName)).delete()) {
						deleted = true;
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return deleted;
	}
	
	private String beautifyXML(String xml) {
		if (StringUtils.isNotBlank(xml)) {
			try {
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				        .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
				Transformer tf = TransformerFactory.newInstance().newTransformer();
				Writer out = new StringWriter();
				
				tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				tf.setOutputProperty(OutputKeys.INDENT, "yes");
				tf.transform(new DOMSource(document), new StreamResult(out));
				
				return out.toString();
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			catch (SAXException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			catch (TransformerException e) {
				e.printStackTrace();
			}
		}
		return xml;
	}
	
	@Override
	public List<ReportToDataSetMapping> getAllReportToDataSetMappings() {
		return getDao().getAllReportToDataSetMappings();
	}
	
	@Override
	public ReportToDataSetMapping getReportToDataSetMappingByUuid(String uuid) {
		return getDao().getReportToDataSetMappingByUuid(uuid);
	}
	
	@Override
	public ReportToDataSetMapping getReportToDataSetMapping(Integer id) {
		return getDao().getReportToDataSetMapping(id);
	}
	
	@Override
	public void deleteReportToDataSetMapping(ReportToDataSetMapping reportToDataSetMapping) {
		getDao().deleteReportToDataSetMapping(reportToDataSetMapping);
	}
	
	@Override
	public void saveReportToDataSetMapping(ReportToDataSetMapping reportToDataSetMapping) {
		getDao().saveReportToDataSetMapping(reportToDataSetMapping);
	}
	
	@Override
	public void deleteReportToDataSetMapping(Integer reportToDataSetMappingId) {
		deleteReportToDataSetMapping(getReportToDataSetMapping(reportToDataSetMappingId));
	}
	
	@Override
	public String runAndPushReportToDHIS(ReportToDataSetMapping reportToDatasetMapping) {
		if (reportToDatasetMapping != null) {
			Calendar startDate = Calendar.getInstance(Context.getLocale());
			Calendar endDate = Calendar.getInstance(Context.getLocale());
			DHISMapping mapping = getMapping(reportToDatasetMapping.getMapping());
			
			if (mapping != null) {
				Location location = reportToDatasetMapping.getLocation();
				String periodType = mapping.getPeriodType();
				PeriodIndicatorReportDefinition ranReportDef = (PeriodIndicatorReportDefinition) Context
				        .getService(ReportDefinitionService.class)
				        .getDefinitionByUuid(mapping.getPeriodIndicatorReportGUID());
				String orgUnitUid = reportToDatasetMapping.getOrgUnitUid();
				Date lastRun = reportToDatasetMapping.getLastRun();
				if (location != null && ranReportDef != null) {
					String period = transformToDHISPeriod(startDate, endDate, periodType, lastRun);
					
					if (StringUtils.isNotBlank(period)) {
						Report ranReport = runPeriodIndicatorReport(ranReportDef, startDate.getTime(), endDate.getTime(),
						    location);
						if (ranReport != null) {
							Object response = sendReportDataToDHIS(ranReport, mapping, period, orgUnitUid);
							
							if (response != null) {
								reportToDatasetMapping.setLastRun(endDate.getTime());
								saveReportToDataSetMapping(reportToDatasetMapping);
								
								return getPostSummary(response);
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	// TODO support more period types besides, daily, weekly, monthly and yearly
	/**
	 * This now uses last (day, week, month, quarter, year etc), it should take set startDate and
	 * endDate using periodType and return formated DHIS2 period only if lastRan isn't for returned
	 * period
	 */
	@Override
	public String transformToDHISPeriod(Calendar startDate, Calendar endDate, String periodType, Date lastRun) {
		String period = null;
		SimpleDateFormat sdf = null;
		Calendar lastRan = Calendar.getInstance();
		
		if (lastRun != null)
			lastRan.setTime(lastRun);
		endDate.setTime(startDate.getTime());
		if (ReportingPeriodType.Daily.name().equals(periodType)) {
			sdf = new SimpleDateFormat("yyyyMMdd");
			startDate.add(Calendar.DAY_OF_YEAR, -1);
			setBasicsStartsAndEnds(startDate, endDate);
			if (lastRun == null || !sdf.format(lastRun).equals(sdf.format(endDate.getTime())))
				period = sdf.format(startDate.getTime());
		} else if (ReportingPeriodType.Weekly.name().equals(periodType)) {
			startDate.add(Calendar.WEEK_OF_YEAR, -1);
			startDate.set(Calendar.DAY_OF_WEEK, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.set(Calendar.DAY_OF_WEEK, 7);
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "W" + lastRan.get(Calendar.WEEK_OF_YEAR))
			        .equals(endDate.get(Calendar.YEAR) + "W" + endDate.get(Calendar.WEEK_OF_YEAR)))
				period = startDate.get(Calendar.YEAR) + "W" + startDate.get(Calendar.WEEK_OF_YEAR);
		} else if (ReportingPeriodType.Monthly.name().equals(periodType)) {
			sdf = new SimpleDateFormat("yyyyMM");
			startDate.add(Calendar.MONTH, -1);
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			if (lastRun == null || !sdf.format(lastRun).equals(sdf.format(endDate.getTime())))
				period = new SimpleDateFormat("yyyyMM").format(startDate.getTime());
		} else if (ReportingPeriodType.Yearly.name().equals(periodType)) {
			sdf = new SimpleDateFormat("yyyy");
			startDate.add(Calendar.YEAR, -1);
			startDate.set(Calendar.DAY_OF_YEAR, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.set(Calendar.DAY_OF_YEAR, startDate.getActualMaximum(Calendar.DAY_OF_YEAR));
			if (lastRun == null || !sdf.format(lastRun).equals(sdf.format(endDate.getTime())))
				period = new SimpleDateFormat("yyyy").format(startDate.getTime());
		}
		return period;
	}
	
	private void setBasicsStartsAndEnds(Calendar startDate, Calendar endDate) {
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		endDate.setTime(startDate.getTime());
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
	}
	
	private String getPostSummary(Object o) {
		String s = "";
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		if (o != null) {
			try {
				if (o instanceof ImportSummaries)
					s += mapper.writeValueAsString(mapper.readTree(mapper.writeValueAsString((ImportSummaries) o)));
				else if (o instanceof DHISImportSummary)
					s += mapper.writeValueAsString((DHISImportSummary) o);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return s;
	}
	
	public Object sendReportDataToDHIS(Report ranReport, DHISMapping mapping, String dhisPeriod, String orgUnitUid) {
		DHISDataValueSet dataValueSet = new DHISDataValueSet();
		DataSet ds = ranReport.getReportData().getDataSets().get("defaultDataSet");
		List<DataSetColumn> columns = ds.getMetaData().getColumns();
		DataSetRow row = ds.iterator().next();
		List<DHISDataValue> dataValues = new ArrayList<DHISDataValue>();
		String dataSetId = mapping.getDataSetUID();
		
		for (int i = 0; i < columns.size(); i++) {
			DHISDataValue dv = new DHISDataValue();
			String column = columns.get(i).getName();
			
			if (StringUtils.isNotBlank(column)) {
				DHISMappingElement de = getDataElementForIndicator(column, mapping.getElements());
				String value = row.getColumnValue(column).toString();
				
				if (mapping != null && de != null && StringUtils.isNotBlank(value)) {
					dv.setValue(value);
					dv.setComment(column);
					dv.setDataElement(de.getDataElement());
					dv.setCategoryOptionCombo(de.getComboOption());
					dataValues.add(dv);
				}
			}
		}
		dataValueSet.setDataValues(dataValues);
		dataValueSet.setOrgUnit(orgUnitUid);
		dataValueSet.setPeriod(dhisPeriod);
		dataValueSet.setDataSet(dataSetId);
		
		if (!dataValueSet.getDataValues().isEmpty())
			return postDataValueSet(dataValueSet);
		
		return null;
	}
	
	private DHISMappingElement getDataElementForIndicator(String indicator, List<DHISMappingElement> list) {
		if (StringUtils.isNotBlank(indicator) && list != null) {
			for (DHISMappingElement de : list) {
				if (StringUtils.isNotBlank(de.getIndicator()) && de.getIndicator().equals(indicator)) {
					return de;
				}
			}
		}
		return null;
	}
	
	public Report runPeriodIndicatorReport(PeriodIndicatorReportDefinition reportDef, Date startDate, Date endDate,
	        Location location) {
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(reportDef, null), null,
		        new RenderingMode(new DefaultWebRenderer(), "Web", null, 100), Priority.HIGHEST, null);
		
		request.getReportDefinition().addParameterMapping("startDate", startDate);
		request.getReportDefinition().addParameterMapping("endDate", endDate);
		request.getReportDefinition().addParameterMapping("location", location);
		request.setStatus(Status.PROCESSING);
		request = Context.getService(ReportService.class).saveReportRequest(request);
		
		return Context.getService(ReportService.class).runReport(request);
	}
	
	@Override
	public String runAllAutomatedReportsAndPostToDHIS() {
		String responses = "";
		List<ReportToDataSetMapping> mps = getAllReportToDataSetMappings();
		
		if (mps != null) {
			for (ReportToDataSetMapping m : mps) {
				String resp = runAndPushReportToDHIS(m);
				
				if (StringUtils.isNotBlank(resp))
					responses += " => " + resp;
			}
		}
		
		return responses;
	}
}
