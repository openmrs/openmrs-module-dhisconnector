package org.openmrs.module.dhisconnector.api.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
                fis.close();
            }
            zOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(zOut);
        }
        return path;
    }

    public static  List<File> unzipMappingBundle (ZipFile zipFile, String tempDirectory) throws IOException {
        List<File> files = new ArrayList<>();
        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
            ZipEntry zipEntry = e.nextElement();
            File output = new File(tempDirectory + zipEntry.getName());
            if (!output.exists()) {
                output.getParentFile().mkdirs();
                output.createNewFile();
            }
            BufferedInputStream inputStream = null;
            BufferedOutputStream outputStream = null;
            try {
                inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                outputStream = new BufferedOutputStream(new FileOutputStream(output));
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException exception) {
                log.error("Unable to unzip the file, caught IO exception", exception);
            } finally {
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(inputStream);
            }
            files.add(output);
        }
        return files;
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
