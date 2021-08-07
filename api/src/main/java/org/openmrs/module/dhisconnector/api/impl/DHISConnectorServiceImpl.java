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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.FileUtils;
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
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.dhisconnector.Configurations;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
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
import org.openmrs.module.dhisconnector.api.util.DHISConnectorUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * It is a default implementation of {@link DHISConnectorService}.
 */
public class DHISConnectorServiceImpl extends BaseOpenmrsService implements DHISConnectorService {
	
	
	private DHISConnectorDAO dao;

	private static final Logger log = LoggerFactory.getLogger(DHISConnectorServiceImpl.class);
	
	public static final String DHISCONNECTOR_MAPPINGS_FOLDER = File.separator + "dhisconnector" + File.separator
	        + "mappings";
	
	public static final String DHISCONNECTOR_DHIS2BACKUP_FOLDER = File.separator + "dhisconnector" + File.separator
	        + "dhis2Backup";
	
	public static final String DHISCONNECTOR_TEMP_FOLDER = File.separator + "dhisconnector" + File.separator + "temp";
	
	public static final String DHISCONNECTOR_LOGS_FOLDER = File.separator + "dhisconnector" + File.separator + "logs";

	public static final String DHISCONNECTOR_MAPPING_FILE_SUFFIX = ".mapping.json";

	public static final String METADATA_FILE_SUFFIX = ".metadata.xml";

	public static final String ZIP_FILE_SUFFIX = ".zip";
	
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

	@Override
	public DHISDataSet getDHISDataSetById(String id) {
		DHISDataSet dataSet = new DHISDataSet();
		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = new String();
		JsonNode node;

		jsonResponse = getDataFromDHISEndpoint(DATASETS_PATH+"/"+id);

		try {
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			node = mapper.readTree(jsonResponse);
			dataSet = mapper.readValue(node.toString(), DHISDataSet.class);
		}
		catch (Exception ex) {
			System.out.print(ex.getMessage());
		}

		return dataSet;
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
	
	@Override
	public String importMappings (MultipartFile mappingBundle) throws IOException {
		String sourceDirectory = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_MAPPINGS_FOLDER + File.separator;
		String tempDirectory = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_TEMP_FOLDER + File.separator;
		(new File(sourceDirectory)).mkdirs();
		(new File(tempDirectory)).mkdirs();
		ObjectMapper mapper = new ObjectMapper();
		XmlMapper xmlMapper = new XmlMapper();

		if (mappingBundle.getOriginalFilename().endsWith(ZIP_FILE_SUFFIX)) {
			File tempBundle = new File(tempDirectory + mappingBundle.getOriginalFilename());
			if (!tempBundle.exists()) {
				tempBundle.createNewFile();
			}
			mappingBundle.transferTo(tempBundle);
			ZipFile zipFile = new ZipFile(tempBundle);
			List<File> fileList = DHISConnectorUtil.unzipMappingBundle(zipFile, tempDirectory);
			for (File f: fileList) {
				log.info("Processing " + f.getName());
				if (f.getName().endsWith(DHISCONNECTOR_MAPPING_FILE_SUFFIX)) {
					DHISMapping dhisMapping = mapper.readValue(f, DHISMapping.class);
					this.saveMapping(dhisMapping);
				} else if (f.getName().endsWith(METADATA_FILE_SUFFIX)) {
					SerializedObject serializedObject = xmlMapper.readValue(f, SerializedObject.class);
					// Check if the system already has an object with the same uuid
					SerializedObject existing = dao.getSerializedObjectByUuid(serializedObject.getUuid());
					if (existing != null) {
						log.info(serializedObject.getName() + " already exists in the system. Ignored");
						continue;
					}
					// Set serialized object creator to the current user and date created to the current day
					serializedObject.setCreator(Context.getAuthenticatedUser());
					serializedObject.setChangedBy(Context.getAuthenticatedUser());
					serializedObject.setRetiredBy(null);
					Date currentDate = new Date();
					serializedObject.setDateCreated(currentDate);
					serializedObject.setDateChanged(currentDate);
					dao.saveSerializedObject(serializedObject);
				}
			}
			return "Successfully imported the mapping files";
		} else {
			return "Unsupported file format";
		}
	}

	@Override
	public String[] exportMappings(String[] mappings, boolean shouldIncludeMetadata) throws IOException {
		String sourceDirectory = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_MAPPINGS_FOLDER + File.separator;
		String tempDirectory = OpenmrsUtil.getApplicationDataDirectory() + DHISCONNECTOR_TEMP_FOLDER + File.separator;
		(new File(tempDirectory)).mkdirs();

		String[] output = new String[2];
		String msg = "";
		String path = "";

		XmlMapper xmlMapper = new XmlMapper();
		List<File> fileList = new ArrayList<>();
		Set<SerializedObject> metadataSet = new HashSet<>();

		File mappingsDirectory = new File(sourceDirectory);
		File[] mappingFiles = mappingsDirectory.listFiles();
		assert mappingFiles != null;
		for (String mapping: mappings) {
			mapping += DHISCONNECTOR_MAPPING_FILE_SUFFIX;
			if (DHISConnectorUtil.mappingExists(mappingFiles, mapping)) {
				// Add mapping file into the file list
				String mappingPath = sourceDirectory + mapping;
				File mappingFile = new File(mappingPath);
				fileList.add(mappingFile);
				// Extract and store related metadata in the metadataSet
				if (shouldIncludeMetadata) {
					Set<SerializedObject> extractedMetadata = extractMetadataFromMapping(mappingPath);
					metadataSet.addAll(extractedMetadata);
				}
			} else {
				log.error("The requested mapping doesn't exist: " + mapping);
			}
		}

		// Add metadata array xml into the file list
		for (SerializedObject metadata: metadataSet) {
			if (metadata != null) {
				metadata.setCreator(null);
				metadata.setChangedBy(null);
				File metadataFile = new File(tempDirectory + metadata.getName() + METADATA_FILE_SUFFIX);
				FileWriter fileWriter = new FileWriter(metadataFile);
				fileWriter.write(xmlMapper.writeValueAsString(metadata));
				fileWriter.close();
				fileList.add(metadataFile);
			}
		}

		if (fileList.size() > 0) {
			path = DHISConnectorUtil.zipMappingBundle(tempDirectory, fileList);
			msg = "Successfully bundled the mapping with the metadata";
		} else {
			msg = "Error, no dhis mapping files were found";
		}

		output[0] = msg;
		output[1] = path;
		return output;
	}

	private Set<SerializedObject> extractMetadataFromMapping(String mappingPath) throws IOException {
		Set<SerializedObject> metadataSet = new HashSet<>();
		ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
		ObjectMapper mapper = new ObjectMapper();
		File mappingFile = new File(mappingPath);

		// Add period indicator report into the metadata set
		DHISMapping mappingObject = mapper.readValue(mappingFile, DHISMapping.class);
		SerializedObject serializedReport =
				dao.getSerializedObjectByUuid(mappingObject.getPeriodIndicatorReportGUID());
		metadataSet.add(serializedReport);

		PeriodIndicatorReportDefinition periodIndicatorReportDefinition =
				(PeriodIndicatorReportDefinition)
						reportDefinitionService.getDefinitionByUuid(mappingObject.getPeriodIndicatorReportGUID());

		// Add indicator data definition into the metadata set
		CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition =
				periodIndicatorReportDefinition.getIndicatorDataSetDefinition();
		if (cohortIndicatorDataSetDefinition != null) {
			metadataSet.add(dao.getSerializedObjectByUuid(cohortIndicatorDataSetDefinition.getUuid()));

			// Add dimensions of the indicator into the metadata set
			Map<String, Mapped<CohortDefinitionDimension>> dimensions =
					cohortIndicatorDataSetDefinition.getDimensions();
			for (String key: dimensions.keySet()) {
				Mapped<CohortDefinitionDimension> dimension = dimensions.get(key);
				metadataSet.add(dao.getSerializedObjectByUuid(dimension.getUuidOfMappedOpenmrsObject()));
				Map<String, Mapped<CohortDefinition>> cohortQueries =
						dimension.getParameterizable().getCohortDefinitions();
				for (String cohortKey: cohortQueries.keySet()) {
					CohortDefinition cohortQuery = cohortQueries.get(cohortKey).getParameterizable();
					if (cohortQuery != null) {
						metadataSet.add(dao.getSerializedObjectByUuid(cohortQuery.getUuid()));
					}
				}
			}

			// Add columns metadata into the metadata set
			List<CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn> columns =
					cohortIndicatorDataSetDefinition.getColumns();
			for (CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn column: columns) {
				CohortIndicator indicator = column.getIndicator().getParameterizable();
				metadataSet.add(dao.getSerializedObjectByUuid(indicator.getUuid()));
				// Add cohort definition, denominator, and location filter metadata into the metadata set
				Mapped<? extends CohortDefinition> indicatorCohort = indicator.getCohortDefinition();
				if (indicatorCohort != null) {
					metadataSet.add(dao.getSerializedObjectByUuid(indicatorCohort.getUuidOfMappedOpenmrsObject()));
				}
				Mapped<? extends CohortDefinition> indicatorDenominator = indicator.getDenominator();
				if (indicatorDenominator != null) {
					metadataSet.add(dao.getSerializedObjectByUuid(indicatorDenominator.getUuidOfMappedOpenmrsObject()));
				}
				Mapped<? extends CohortDefinition> indicatorLocationFilter = indicator.getLocationFilter();
				if (indicatorLocationFilter != null) {
					metadataSet.add(dao.getSerializedObjectByUuid(
							indicatorLocationFilter.getUuidOfMappedOpenmrsObject()));
				}
			}
		}

		// Add cohort definition into the metadata set
		Mapped<? extends CohortDefinition> cohortDefinition =
				periodIndicatorReportDefinition.getBaseCohortDefinition();
		if (cohortDefinition != null) {
			metadataSet.add(dao.getSerializedObjectByUuid(
					cohortDefinition.getUuidOfMappedOpenmrsObject()));
		}

		// Add data set definitions into the metadata set
		Map<String, Mapped<? extends DataSetDefinition>> dataSetDefinitions =
				periodIndicatorReportDefinition.getDataSetDefinitions();
		if (dataSetDefinitions != null) {
			for (String key: dataSetDefinitions.keySet()) {
				DataSetDefinition dataSetDefinition = dataSetDefinitions.get(key).getParameterizable();
				metadataSet.add(dao.getSerializedObjectByUuid(dataSetDefinition.getUuid()));
			}
		}
		return metadataSet;
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
	public List<String> runAndPushReportToDHIS(ReportToDataSetMapping reportToDatasetMapping) {
		List<String> responseString = new ArrayList<>();
		if (reportToDatasetMapping != null) {
			Calendar startDate = Calendar.getInstance(Context.getLocale());
			Calendar endDate = Calendar.getInstance(Context.getLocale());
			DHISMapping mapping = getMapping(reportToDatasetMapping.getMapping());
			
			if (mapping != null) {
				DHISDataSet dataSet = getDHISDataSetById(mapping.getDataSetUID());
				String periodType = mapping.getPeriodType();
				PeriodIndicatorReportDefinition ranReportDef = (PeriodIndicatorReportDefinition) Context
						.getService(ReportDefinitionService.class)
						.getDefinitionByUuid(mapping.getPeriodIndicatorReportGUID());
				Date lastRun = reportToDatasetMapping.getLastRun();
				String period = transformToDHISPeriod(startDate, endDate, periodType, lastRun);

				List<DHISOrganisationUnit> orgs = dataSet.getOrganisationUnits();
				for (DHISOrganisationUnit takenOrgUnit: orgs){
					String orgUnitUid = takenOrgUnit.getId();
					LocationToOrgUnitMapping locationToOrgUnitMapping = Context.getService(DHISConnectorService.class)
							.getLocationToOrgUnitMappingByOrgUnitUid(orgUnitUid);

					if (locationToOrgUnitMapping != null && ranReportDef != null) {
						Location location = locationToOrgUnitMapping.getLocation();
						if (StringUtils.isNotBlank(period)) {
							Report ranReport = runPeriodIndicatorReport(ranReportDef, startDate.getTime(), endDate.getTime(), location);
							if (ranReport != null) {
								Object response = sendReportDataToDHIS(ranReport, mapping, period, orgUnitUid);

								if (response != null) {
									reportToDatasetMapping.setLastRun(endDate.getTime());
									saveReportToDataSetMapping(reportToDatasetMapping);

									responseString.add(location.getName() + " => " + getPostSummary(response));
								}
							}
						}
					}
				}
				return responseString;
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
			startDate.setFirstDayOfWeek(Calendar.MONDAY);
			endDate.setFirstDayOfWeek(Calendar.MONDAY);
			lastRan.setFirstDayOfWeek(Calendar.MONDAY);
			startDate.add(Calendar.WEEK_OF_YEAR, -1);
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "W" + lastRan.get(Calendar.WEEK_OF_YEAR))
			        .equals(endDate.get(Calendar.YEAR) + "W" + endDate.get(Calendar.WEEK_OF_YEAR)))
				period = startDate.get(Calendar.YEAR) + "W" + startDate.get(Calendar.WEEK_OF_YEAR);
		} else if (ReportingPeriodType.WeeklySunday.name().equals(periodType)) {
			startDate.setFirstDayOfWeek(Calendar.SUNDAY);
			endDate.setFirstDayOfWeek(Calendar.SUNDAY);
			lastRan.setFirstDayOfWeek(Calendar.SUNDAY);
			startDate.add(Calendar.WEEK_OF_YEAR, -1);
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "SunW" + lastRan.get(Calendar.WEEK_OF_YEAR))
					.equals(endDate.get(Calendar.YEAR) + "SunW" + endDate.get(Calendar.WEEK_OF_YEAR)))
				period = startDate.get(Calendar.YEAR) + "SunW" + startDate.get(Calendar.WEEK_OF_YEAR);
		} else if (ReportingPeriodType.WeeklyWednesday.name().equals(periodType)) {
			startDate.setFirstDayOfWeek(Calendar.WEDNESDAY);
			endDate.setFirstDayOfWeek(Calendar.WEDNESDAY);
			lastRan.setFirstDayOfWeek(Calendar.WEDNESDAY);
			startDate.add(Calendar.WEEK_OF_YEAR, -1);
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "WedW" + lastRan.get(Calendar.WEEK_OF_YEAR))
					.equals(endDate.get(Calendar.YEAR) + "WedW" + endDate.get(Calendar.WEEK_OF_YEAR)))
				period = startDate.get(Calendar.YEAR) + "WedW" + startDate.get(Calendar.WEEK_OF_YEAR);
		} else if (ReportingPeriodType.WeeklyThursday.name().equals(periodType)) {
			startDate.setFirstDayOfWeek(Calendar.THURSDAY);
			endDate.setFirstDayOfWeek(Calendar.THURSDAY);
			lastRan.setFirstDayOfWeek(Calendar.THURSDAY);
			startDate.add(Calendar.WEEK_OF_YEAR, -1);
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "ThuW" + lastRan.get(Calendar.WEEK_OF_YEAR))
					.equals(endDate.get(Calendar.YEAR) + "ThuW" + endDate.get(Calendar.WEEK_OF_YEAR)))
				period = startDate.get(Calendar.YEAR) + "ThuW" + startDate.get(Calendar.WEEK_OF_YEAR);
		} else if (ReportingPeriodType.WeeklySaturday.name().equals(periodType)) {
			startDate.setFirstDayOfWeek(Calendar.SATURDAY);
			endDate.setFirstDayOfWeek(Calendar.SATURDAY);
			lastRan.setFirstDayOfWeek(Calendar.SATURDAY);
			startDate.add(Calendar.WEEK_OF_YEAR, -1);
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "SatW" + lastRan.get(Calendar.WEEK_OF_YEAR))
					.equals(endDate.get(Calendar.YEAR) + "SatW" + endDate.get(Calendar.WEEK_OF_YEAR)))
				period = startDate.get(Calendar.YEAR) + "SatW" + startDate.get(Calendar.WEEK_OF_YEAR);
		} else if (ReportingPeriodType.BiWeekly.name().equals(periodType)) {
			startDate.setFirstDayOfWeek(Calendar.MONDAY);
			endDate.setFirstDayOfWeek(Calendar.MONDAY);
			lastRan.setFirstDayOfWeek(Calendar.MONDAY);
			startDate.add(Calendar.WEEK_OF_YEAR, -2);
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.add(Calendar.WEEK_OF_YEAR, 1);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "BiW" + lastRan.get(Calendar.WEEK_OF_YEAR))
					.equals(endDate.get(Calendar.YEAR) + "BiW" + endDate.get(Calendar.WEEK_OF_YEAR)))
				period = startDate.get(Calendar.YEAR) + "BiW" + startDate.get(Calendar.WEEK_OF_YEAR);
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
		} else if (ReportingPeriodType.FinancialApril.name().equals(periodType)) {
			sdf = new SimpleDateFormat("yyyy");
			// Set the start date to year of last Financial April period
			startDate.add(Calendar.YEAR, startDate.get(Calendar.MONTH) < Calendar.APRIL ? -2 : -1);
			// Set the start date to 1st of April
			startDate.set(Calendar.MONTH, Calendar.APRIL);
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			// Set the end date to 31st of March
			endDate.add(Calendar.YEAR, 1);
			endDate.set(Calendar.MONTH, Calendar.MARCH);
			endDate.set(Calendar.DAY_OF_MONTH, 31);
			if (lastRun == null || !sdf.format(lastRun).equals(sdf.format(endDate.getTime())))
				period = startDate.get(Calendar.YEAR) + "April";
		} else if (ReportingPeriodType.FinancialJuly.name().equals(periodType)) {
			sdf = new SimpleDateFormat("yyyy");
			// Set the start date to year of last Financial July period
			startDate.add(Calendar.YEAR, startDate.get(Calendar.MONTH) < Calendar.JULY ? -2 : -1);
			// Set the start date to 1st of July
			startDate.set(Calendar.MONTH, Calendar.JULY);
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			// Set the end date to 30th of June
			endDate.add(Calendar.YEAR, 1);
			endDate.set(Calendar.MONTH, Calendar.JUNE);
			endDate.set(Calendar.DAY_OF_MONTH, 30);
			if (lastRun == null || !sdf.format(lastRun).equals(sdf.format(endDate.getTime())))
				period = startDate.get(Calendar.YEAR) + "July";
		} else if (ReportingPeriodType.FinancialOct.name().equals(periodType)) {
			sdf = new SimpleDateFormat("yyyy");
			// Set the start date to year of last Financial October period
			startDate.add(Calendar.YEAR, startDate.get(Calendar.MONTH) < Calendar.OCTOBER ? -2 : -1);
			// Set the start date to 1st of October
			startDate.set(Calendar.MONTH, Calendar.OCTOBER);
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			// Set the end date to 30th of September
			endDate.add(Calendar.YEAR, 1);
			endDate.set(Calendar.MONTH, Calendar.SEPTEMBER);
			endDate.set(Calendar.DAY_OF_MONTH, 30);
			if (lastRun == null || !sdf.format(lastRun).equals(sdf.format(endDate.getTime())))
				period = startDate.get(Calendar.YEAR) + "Oct";
		} else if (ReportingPeriodType.FinancialNov.name().equals(periodType)) {
			sdf = new SimpleDateFormat("yyyy");
			// Set the start date to year of last Financial November period
			startDate.add(Calendar.YEAR, startDate.get(Calendar.MONTH) < Calendar.NOVEMBER ? -2 : -1);
			// Set the start date to 1st of November
			startDate.set(Calendar.MONTH, Calendar.NOVEMBER);
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			// Set the end date to 31st of October
			endDate.add(Calendar.YEAR, 1);
			endDate.set(Calendar.MONTH, Calendar.OCTOBER);
			endDate.set(Calendar.DAY_OF_MONTH, 31);
			if (lastRun == null || !sdf.format(lastRun).equals(sdf.format(endDate.getTime())))
				period = startDate.get(Calendar.YEAR) + "Nov";
		} else if (ReportingPeriodType.SixMonthly.name().equals(periodType)) {
			// Set the start date to start date of last SixMonthly period
			if (startDate.get(Calendar.MONTH) < Calendar.JULY) {
				startDate.add(Calendar.YEAR, -1);
				startDate.set(Calendar.MONTH, Calendar.JULY);
				startDate.set(Calendar.DAY_OF_MONTH, 1);
			} else {
				startDate.set(Calendar.DAY_OF_YEAR, 1);
			}
			setBasicsStartsAndEnds(startDate, endDate);
			// Set the end date to the last day of SixMonthly period
			endDate.add(Calendar.MONTH, 5);
			endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "S" + getHalfYear(lastRan))
					.equals(endDate.get(Calendar.YEAR) + "S" + getHalfYear(endDate)))
				period = startDate.get(Calendar.YEAR) + "S" + getHalfYear(startDate);
		} else if (ReportingPeriodType.SixMonthlyApril.name().equals(periodType)) {
			// Set the start date to start date of last SixMonthlyApril period
			if ((startDate.get(Calendar.MONTH) < Calendar.OCTOBER) && (startDate.get(Calendar.MONTH) >= Calendar.APRIL)) {
				startDate.add(Calendar.YEAR, -1);
				startDate.set(Calendar.MONTH, Calendar.OCTOBER);
			} else {
				startDate.set(Calendar.MONTH, Calendar.APRIL);
			}
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			// Set the end date to the last day of SixMonthlyApril period
			endDate.add(Calendar.MONTH, 5);
			endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			if (lastRun == null || !(lastRan.get(Calendar.YEAR) + "S" + getHalfYearApril(lastRan))
					.equals(endDate.get(Calendar.YEAR) + "S" + getHalfYearApril(endDate)))
				period = startDate.get(Calendar.YEAR) + "S" + getHalfYearApril(startDate);
		} else if (ReportingPeriodType.Quarterly.name().equals(periodType)) {
			// Set the correct start date of the last Quarterly period
			if (startDate.get(Calendar.MONTH) < Calendar.APRIL) {
				startDate.add(Calendar.YEAR, -1);
				startDate.set(Calendar.MONTH, Calendar.OCTOBER);
			} else if (startDate.get(Calendar.MONTH) < Calendar.JULY) {
				startDate.set(Calendar.MONTH, Calendar.JANUARY);
			} else if (startDate.get(Calendar.MONTH) < Calendar.OCTOBER) {
				startDate.set(Calendar.MONTH, Calendar.APRIL);
			} else {
				startDate.set(Calendar.MONTH, Calendar.JULY);
			}
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			// set the end date to the last day of the current period
			endDate.add(Calendar.MONTH, 2);
			endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			if (lastRan == null || !(lastRan.get(Calendar.YEAR) + "Q" + getQuarterNumber(lastRan))
					.equals(endDate.get(Calendar.YEAR) + "Q" + getQuarterNumber(endDate))) {
				period = startDate.get(Calendar.YEAR) + "Q" + getQuarterNumber(startDate);
			}
		} else if (ReportingPeriodType.BiMonthly.name().equals(periodType)) {
			sdf = new SimpleDateFormat("yyyyMM");
			startDate.add(Calendar.MONTH, -2);
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			setBasicsStartsAndEnds(startDate, endDate);
			endDate.add(Calendar.MONTH, 1);
			endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			if (lastRun == null || !sdf.format(lastRun).equals(sdf.format(endDate.getTime())))
				period = new SimpleDateFormat("yyyyMM").format(startDate.getTime()) + "B";
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

	/**
	 * @return the half year number
	 */
	private int getHalfYear(Calendar date) {
		return date.get(Calendar.MONTH) < Calendar.JULY ? 1 : 2;
	}

	/**
	 * @return the half year number from a year starts with April
	 */
	private int getHalfYearApril(Calendar date) {
		return date.get(Calendar.MONTH) < Calendar.OCTOBER && date.get(Calendar.MONTH) >= Calendar.APRIL ? 1 : 2;
	}

	/**
	 * @return the quarter number
	 */
	private int getQuarterNumber(Calendar date) {
		if (date.get(Calendar.MONTH) < Calendar.APRIL) {
			return 1;
		} else if (date.get(Calendar.MONTH) < Calendar.JULY) {
			return 2;
		} else if (date.get(Calendar.MONTH) < Calendar.OCTOBER) {
			return 3;
		} else {
			return 4;
		}
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
	public ArrayList<List<String>> runAllAutomatedReportsAndPostToDHIS() {
		ArrayList<List<String>> responses = new ArrayList<>();
		List<ReportToDataSetMapping> mps = getAllReportToDataSetMappings();
		
		if (mps != null) {
			for (ReportToDataSetMapping m : mps) {
				List<String> resp = runAndPushReportToDHIS(m);
				
				if (!resp.isEmpty())
					responses.add(resp);
			}
		}
		
		return responses;
	}

	@Override
	public List<LocationToOrgUnitMapping> getAllLocationToOrgUnitMappings() {
		return getDao().getAllLocationToOrgUnitMappings();
	}

	@Override
	public LocationToOrgUnitMapping getLocationToOrgUnitMappingByUuid(String uuid) {
		return getDao().getLocationToOrgUnitMappingByUuid(uuid);
	}

	@Override
	public LocationToOrgUnitMapping getLocationToOrgUnitMappingByOrgUnitUid(String orgUnitUid) {
		return getDao().getLocationToOrgUnitMappingByOrgUnitUid(orgUnitUid);
	}

	@Override
	public void saveLocationToOrgUnitMapping(LocationToOrgUnitMapping locationToOrgUnitMapping) {
		getDao().saveLocationToOrgUnitMapping(locationToOrgUnitMapping);
	}

	@Override
	public void deleteLocationToOrgUnitMappingsByLocation(Location location) {
		getDao().deleteLocationToOrgUnitMappingsByLocation(location);
	}
}
