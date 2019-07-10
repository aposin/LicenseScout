/**
 * Copyright 2019 Association for the promotion of open-source insurance software and for the establishment of open interface standards in the insurance industry (Verein zur Förderung quelloffener Versicherungssoftware und Etablierung offener Schnittstellenstandards in der Versicherungsbranche)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aposin.licensescout.main;

import java.io.File;
import java.util.logging.Logger;

import org.aposin.licensescout.configuration.RunParameters;
import org.aposin.licensescout.exporter.GeneralStatistics;
import org.aposin.licensescout.exporter.HtmlExporter;
import org.aposin.licensescout.exporter.IDetectionStatusStatistics;
import org.aposin.licensescout.exporter.ILegalStatusStatistics;
import org.aposin.licensescout.exporter.OutputResult;
import org.aposin.licensescout.exporter.ReportConfiguration;
import org.aposin.licensescout.finder.AbstractFinder;
import org.aposin.licensescout.finder.FinderResult;
import org.aposin.licensescout.finder.JavaJarFinder;
import org.aposin.licensescout.license.LicenseStoreData;
import org.aposin.licensescout.license.LicenseUtil;
import org.aposin.licensescout.util.JavaUtilLog;

/**
 * Stand-alone entry point for the licenseScout.
 * 
 * <h1>Usage:</h1>
 * 
 * <code>Main -pathtoscan some_path_to_an_directory_to_scan -pathtoreport some_path_to_a_report_directory</code>
 * 
 * 
 */
public class Main {

    /**
     * Default output filename.
     */
    public static final String REPORT_NAME = "licensereport.html";

    private Main() {
        // DO NOTHING
    }

    /**
     * The main method.
     * 
     * @param args the arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File source;
        File target;
        LicenseStoreData licenseStoreData = null;
        final JavaUtilLog log = new JavaUtilLog(Logger.getGlobal());
        final RunParameters runParameters = new RunParameters();
        // TODO: set parameters in RunParameters
        // runParameters.setNexusCentralBaseUrl(nexusCentralBaseUrl);
        // runParameters.setConnectTimeout(connectTimeout);
        AbstractFinder finder = new JavaJarFinder(licenseStoreData, runParameters, log);

        File outputFile = null;
        int argsTargetLength = 4; // set required args length

        // arg 0 -> -pathtoscan
        if (args.length != argsTargetLength) {
            throw new IllegalArgumentException("Wrong count of parameters, must be " + Integer.toString(argsTargetLength));
        }

        int i = 0;
        while (i < args.length) {
            if ("-pathtoscan".equalsIgnoreCase(args[i])) {
                String searchPath = args[++i];
                source = new File(searchPath);
                if (!source.exists()) {
                    throw new IllegalArgumentException("This search path does not exist: " + searchPath);
                } else {
                    finder.setScanDirectory(source);
                    i++;
                }
                continue;
            }
            if ("-pathtoreport".equalsIgnoreCase(args[i])) {
                String savePath = args[++i];
                target = new File(savePath);
                if (!target.exists() || !target.isDirectory()) {
                    throw new IllegalArgumentException("This save path for the report does not exist: " + target);
                } else {
                    outputFile = new File(target, REPORT_NAME);
                    i++;
                }
                continue;
            }
        }

        final FinderResult finderResult = finder.findLicenses();
        final IDetectionStatusStatistics detectionStatusStatistics = LicenseUtil
                .calculateDetectionStatusStatistics(finderResult.getArchiveFiles());
        final ILegalStatusStatistics legalStatusStatistics = LicenseUtil
                .calculateLegalStatusStatistics(finderResult.getArchiveFiles());
        final GeneralStatistics generalStatistics = new GeneralStatistics();
        final OutputResult outputResult = new OutputResult();
        outputResult.setFinderResult(finderResult);
        outputResult.setGeneralStatistics(generalStatistics);
        outputResult.setDetectionStatusStatistics(detectionStatusStatistics);
        outputResult.setLegalStatusStatistics(legalStatusStatistics);
        final ReportConfiguration reportConfiguration = new ReportConfiguration();
        reportConfiguration.setOutputFile(outputFile);
        HtmlExporter.getInstance().export(outputResult, reportConfiguration);
    }

}
