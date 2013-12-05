/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util.importer.job.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipDirectory {

    private File destDirectory;
    private ZipOutputStream zos;
    private List<String> fileList;
    private String srcDirectoryName;
    private String srcDirectoryPath;
    private Boolean includeDirectoryName = false;


    public ZipDirectory(File destDirectory) throws FileNotFoundException {
        this.destDirectory = destDirectory;
    }

    public void zipIt(List<File> srcDirectories, Boolean includeDirectoryName) throws IOException{

        this.includeDirectoryName = includeDirectoryName;
        FileOutputStream fos = new FileOutputStream(destDirectory);
        zos = new ZipOutputStream(fos);
        for(File srcDirectory: srcDirectories){
            addDirectory(srcDirectory);
        }
        zos.closeEntry();
        zos.close();
    }

    public void zipIt(File srcDirectory) throws IOException{
        FileOutputStream fos = new FileOutputStream(destDirectory);
        zos = new ZipOutputStream(fos);
        addDirectory(srcDirectory);
        zos.closeEntry();
        zos.close();
    }

    private void addDirectory(File srcDirectory) throws IOException {
        byte[] buffer = new byte[1024];
        fileList = new ArrayList<String>();
        srcDirectoryPath = srcDirectory.getAbsolutePath();
        srcDirectoryName = srcDirectory.getName();

        generateFileList(srcDirectory);

        for (String file : fileList) {

            ZipEntry ze = new ZipEntry(addSrcDirectoryName() + file);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(srcDirectory.getAbsolutePath() + File.separator + file);
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
        }
    }

    private List<String> generateFileList(File src) {
        if (src.isFile()) {
            fileList.add(generateZipEntry(src.toString()));
        }

        if (src.isDirectory()) {
            String[] subNote = src.list();
            for (String filename : subNote) {
                generateFileList(new File(src, filename));
            }
        }
        return fileList;
    }

    private String generateZipEntry(String fileAbsolutePath) {
        return fileAbsolutePath.substring(srcDirectoryPath.length() + 1,
                fileAbsolutePath.length());
    }


    private String addSrcDirectoryName(){
        if(includeDirectoryName)
            return srcDirectoryName + File.separator;
        return "";
    }
}