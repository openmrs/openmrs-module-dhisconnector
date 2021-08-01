package org.openmrs.module.dhisconnector.api.util;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.dhisconnector.api.db.DHISConnectorDAO;
import org.openmrs.module.dhisconnector.api.db.hibernate.HibernateDHISConnectorDAO;
import org.openmrs.module.dhisconnector.api.model.DHISMapping;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DHISConnectorUtil {

    private static final Logger log = LoggerFactory.getLogger(DHISConnectorUtil.class);

    public static String zipMappingBundle (String tempDirectory, List<File> fileList) {
        String path = tempDirectory + "mappingBundle_" + (new Date()).getTime() + ".zip";
        ZipOutputStream zOut = null;
        FileInputStream fis = null;
        try (FileOutputStream fOut = new FileOutputStream(path)) {
            zOut = new ZipOutputStream(fOut);
            for (File file : fileList) {
                fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zOut.write(bytes, 0, length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(zOut);
        }
        return path;
    }

    public static boolean mappingExists(File[] filesList, String mapping) {
        log.info("Searching for the correct mapping");
        log.debug("Mapping: " + mapping);
        for (File file: filesList) {
            log.debug("File name: " + file.getName());
            if (file.getName().equals(mapping)) {
                return true;
            }
        }
        return false;
    }
}
