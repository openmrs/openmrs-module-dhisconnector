package org.openmrs.module.dhisconnector.api.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class DHISConnectorUtil {

    public static String zipMappingBundle (String tempDirectory, List<File> fileList) throws IOException {
        String path = tempDirectory + "mappingBundle_" + (new Date()).getTime() + ".zip";
        FileOutputStream fOut = new FileOutputStream(path);
        ZipOutputStream zOut = new ZipOutputStream(fOut);
        for (File file: fileList) {
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zOut.write(bytes, 0 , length);
            }
            fis.close();
        }
        zOut.close();
        fOut.close();
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
            BufferedInputStream inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(output));
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();
            files.add(output);
        }
        return files;
    }

    public static boolean doesMappingInclude (File[] filesList, String mapping) {
        for (File file: filesList) {
            System.out.println("file name = " + file.getName());
            System.out.println("mapping name = " + mapping);
            if (file.getName().equals(mapping)) {
                return true;
            }
        }
        return false;
    }
}
