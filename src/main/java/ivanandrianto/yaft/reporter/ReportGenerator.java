/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.reporter;

import ivanandrianto.yaft.analyzer.AnalysisResult;
import ivanandrianto.yaft.attacker.HTTPRequest;
import ivanandrianto.yaft.attacker.HTTPResponse;
import ivanandrianto.yaft.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Report Generator Class.
 * @author ivanandrianto
 */
public class ReportGenerator {
    private String session;
    private String reportsFolder;
    private String currentTestingSessionFolder;
    private String txtOutput;
    private int count;
    private int vulnerabilityCount;
    private Map<String, Integer> vulnerabilities;
    private long startTime;

    private static final String SUMMARY_REPORT_NAME = "summary.json";

    /**
     * Constructor.
     * @param session
     *      Nama session
     * @param txtOutput
     *      Output yang berisi path menuju summary, terutama berfungsi ketika
     *      dijalankan oleh Jenkins
     */
    public ReportGenerator(String session, String txtOutput) {
        this.session = session;
        reportsFolder = "projects/" + session + "/reports/";
        FileUtil.createDirectoryIfNotExists(reportsFolder);
        startTime = System.currentTimeMillis();
        currentTestingSessionFolder = reportsFolder + String.valueOf(startTime)
                + "/";
        FileUtil.createDirectoryIfNotExists(currentTestingSessionFolder);
        count = 0;
        vulnerabilityCount = 0;
        vulnerabilities = new HashMap<String, Integer>();
        this.txtOutput = txtOutput;
    }

    /**
     * Membuat report untuk setiap HTTP request yang dikirim.
     * @param request
     *      HTTPRequest
     * @param response
     *      HTTPResponse
     * @param Set<String>
     *      Tipe pembangkitan input
     * @param modifications
     *      Modifikasi yang diterapkan
     * @param analysisResult
     *      Hasil analisis
     */
    public void addReport(HTTPRequest request, HTTPResponse response,
            Set<String> generationTypes, String modifications,
            AnalysisResult analysisResult) {
        JSONObject reportObj = new JSONObject();
        reportObj.put("request", request.toString());
        if (response != null) {
            reportObj.put("response", response.getResponseWithoutBody());
            reportObj.put("responseBody", response.getBody());
            reportObj.put("lastURI", response.getLastUri());
        }
        reportObj.put("isVulnerabilityFound", analysisResult
                .isVulnerabilityFound());
        reportObj.put("isAnalyzeError", analysisResult.isAnalyzeError());
        String generationTypesStr = "";
        for (String s : generationTypes) {
            generationTypesStr += s + ",";
        }
        generationTypesStr = generationTypesStr.substring(0,
                generationTypesStr.length() - 1);
        reportObj.put("generationType", generationTypesStr);
        reportObj.put("modifications", modifications);
        reportObj.put("description", analysisResult.getDescription());

        if (analysisResult.isVulnerabilityFound()) {
            Set<String> vulnerabilityTypes = analysisResult
                    .getVulnerabilityTypes();

            vulnerabilityCount++;
            if (vulnerabilityTypes.size() == 0) {
                vulnerabilityTypes.add("unknown");
            }

            String vulnerabilityList = "";
            for (String vulnerabilityType : vulnerabilityTypes) {
                vulnerabilityList += vulnerabilityType + ",";
                if (vulnerabilities.containsKey(vulnerabilityType)) {
                    vulnerabilities.put(vulnerabilityType,
                        vulnerabilities.get(vulnerabilityType) + 1);
                } else {
                    vulnerabilities.put(vulnerabilityType, 1);
                }
            }
            vulnerabilityList = vulnerabilityList.substring(0,
                    vulnerabilityList.length() - 1);
            reportObj.put("vulnerability", vulnerabilityList);
        }

        count++;
        String filePath = currentTestingSessionFolder + count  + ".json";
        FileUtil.createNewFile(filePath, reportObj.toString(4));
    }

    /**
     * Membuat summary report untuk setiap sesi pengujian.
     */
    public void generateSummary() {
        String filePath = currentTestingSessionFolder + SUMMARY_REPORT_NAME;
        JSONObject summaryObj = new JSONObject();
        Date timeDate = new Date(startTime);
        SimpleDateFormat df = new SimpleDateFormat("yyyy MMMM, dd - hh:mm");
        String time = df.format(timeDate);
        summaryObj.put("time", time);
        summaryObj.put("count", count);
        summaryObj.put("vulnerabilityCount", vulnerabilityCount);
        JSONArray vulnerabilitiesArray = new JSONArray();
        for (HashMap.Entry<String, Integer> e : vulnerabilities.entrySet()) {
            JSONObject vulnerabilitiesObj = new JSONObject();
            vulnerabilitiesObj.put("name", e.getKey());
            vulnerabilitiesObj.put("count", e.getValue());
            vulnerabilitiesArray.put(vulnerabilitiesObj);
        }
        summaryObj.put("vulnerabilities", vulnerabilitiesArray);

        FileUtil.createNewFile(filePath, summaryObj.toString(4));

        String txtOutputFilePath = reportsFolder + txtOutput;
        if (!txtOutputFilePath.endsWith(".txt")) {
            txtOutputFilePath += ".txt";
        }

        System.out.println("Fuzzing done");
        File f = new File(txtOutputFilePath);
        try {
            if (f.exists()) {
                f.delete();
            }

            if (f.createNewFile()) {
                StringBuilder sb = new StringBuilder();
                sb.append(currentTestingSessionFolder);
                Files.write(Paths.get(txtOutputFilePath), sb.toString()
                        .getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }

    /**
     * Mendapatkan nama file summary report.
     * summary report.
     * @return String
     *      Nama file summary report
     */
    public static String getSummaryReportName() {
        return SUMMARY_REPORT_NAME;
    }
}
