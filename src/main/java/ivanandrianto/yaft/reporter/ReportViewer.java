/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.reporter;

import ivanandrianto.yaft.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Report Viewer Class.
 * @author ivanandrianto
 */
public class ReportViewer {
    private String session;
    private String reportsFolder;

    /**
     * Constructor.
     * @param session
     *      Nama session
     */
    public ReportViewer(String session) {
        this.session = session;
        reportsFolder = "projects/" + session + "/reports/";
    }

    /**
     * Method ini berfungsi untuk mendapatkan daftar summary report dari setiap
     * sesi pengujian.
     * @return ArrayList<ReportSession>
     *      Daftar summary report dari setiap sesi pengujian.
     */
    public ArrayList<ReportSession> getReportSessionList() {
        ArrayList<ReportSession> reportSessions = new ArrayList<>();
        ArrayList<String> dirList = FileUtil.getDirectoryList(reportsFolder);
        for (int i = 0; i < dirList.size(); i++) {
            ReportSession rs = getReportSessionDetails(dirList.get(i));
            if (rs != null) {
                reportSessions.add(rs);
            }
        }
        return reportSessions;
    }

    /**
     * Method ini berfungsi untuk mendapatkan summary report untuk suatu report
     * session.
     * @param reportSessionFolder
     *      Nama folder dari report session
     * @return ReportSession
     *      Summary dari suatu report session
     */
    public ReportSession getReportSessionDetails(String
            reportSessionFolder) {
        String summaryFile = reportsFolder + reportSessionFolder + "/"
                + ReportGenerator.getSummaryReportName();
        try {
            String content = FileUtil.read(summaryFile);
            if (content == null) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(content);
            int totalRequests = jsonObject.getInt("count");
            int vulnerabilityCount = jsonObject.getInt("vulnerabilityCount");

            JSONArray vulnerabilitiesArr = jsonObject
                    .getJSONArray("vulnerabilities");
            Map<String, Integer> vulnerabilities = new HashMap<String,
                    Integer>();

            for (int i = 0; i < vulnerabilitiesArr.length(); i++) {
                JSONObject vulnerabilityObj = vulnerabilitiesArr
                        .getJSONObject(i);
                if ((vulnerabilityObj.has("name"))
                        && (vulnerabilityObj.has("count"))) {
                    String curVulnerabilityName = vulnerabilityObj
                        .getString("name");
                    int curVulnerabilityNumber = vulnerabilityObj
                            .getInt("count");
                    vulnerabilities.put(curVulnerabilityName,
                            curVulnerabilityNumber);
                }
            }

            Path summaryFilePath = Paths.get(summaryFile);
            BasicFileAttributes attr = Files.readAttributes(summaryFilePath,
                    BasicFileAttributes.class);
            Long timeLong = attr.creationTime().to(TimeUnit.MILLISECONDS);
            Date timeDate = new Date(timeLong);
            SimpleDateFormat df = new SimpleDateFormat("yyyy MMMM, dd - hh:mm");
            String timeStr = df.format(timeDate);

            ReportSession rs = new ReportSession(reportSessionFolder,
                totalRequests, vulnerabilityCount, vulnerabilities, timeStr);
            return rs;

        } catch (IOException ex) {
            return null;
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Method ini berfungsi untuk mendapatkan daftar detail singkat dari setiap
     * report untuk setiap request dalam suatu folder.
     * @param reportSessionFolder
     *      Nama folder dari report session
     * @return ArrayList<ReportDetails>
     *      Sekumpulan detail dari setiap report untuk setiap request
     */
    public ArrayList<ReportDetails> getReportDetailsList(String
            reportSessionFolder) {
        ArrayList<ReportDetails> reportDetails = new ArrayList<>();
        String reportSessionPath = reportsFolder + reportSessionFolder;
        ArrayList<String> fileList = FileUtil.getFileList(reportSessionPath,
                ".json", true);
        for (int i = 0; i < fileList.size(); i++) {
            ReportDetails rd = getReportDetails(reportSessionFolder,
                    fileList.get(i), true);
            if ((rd != null) && (!fileList.get(i).equals(ReportGenerator
                    .getSummaryReportName()))) {
                reportDetails.add(rd);
            }
        }
        return reportDetails;
    }

    /**
     * Mendapatkan detail dari suatu report untuk setiap request dalam bentuk
     * ReportDetails.
     * @param folderName
     *      Nama folder dari report session
     * @param fileName
     *      Nama file report
     * @param shortDetailsOnly
     *      Apakah hanya detail singkat (nama file, nama folder, apakah
     *      vulnerability ditemukan, dan jenis vulnerability yang diteukan)
     *      atau semua detail
     * @return ReportDetails
     *      Detail dari suatu report untuk setiap request
     */
    public ReportDetails getReportDetails(String folderName,
            String fileName, boolean shortDetailsOnly) {
        String reportFilePath = reportsFolder + folderName + "/"
                + fileName;

        String content = FileUtil.read(reportFilePath);
        if (content == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(content);

        String request = (jsonObject.has("request"))
                ? (String) jsonObject.get("request") : "-";

        String response = (jsonObject.has("response"))
                ? (String) jsonObject.get("response") : "-";
        
        String responseBody = (jsonObject.has("responseBody"))
                ? (String) jsonObject.get("responseBody") : "-";

        String lastURI = (jsonObject.has("lastURI"))
                ? (String) jsonObject.get("lastURI") : "-";

        String generationType = (jsonObject.has("generationType"))
                ? (String) jsonObject.get("generationType") : "-";

        String modifications = (jsonObject.has("modifications"))
                ? (String) jsonObject.get("modifications") : "-";

        boolean isVulnerabilityFound;
        if (jsonObject.has("isVulnerabilityFound")) {
            isVulnerabilityFound = jsonObject
                    .getBoolean("isVulnerabilityFound");
        } else {
            isVulnerabilityFound = false;
        }

        String vulnerability = (jsonObject.has("vulnerability"))
                ? (String) jsonObject.get("vulnerability") : "-";

        boolean isAnalyzeError;
        if (jsonObject.has("isAnalyzeError")) {
            isAnalyzeError = jsonObject.getBoolean("isAnalyzeError");
        } else {
            isAnalyzeError = false;
        }

        String description = (jsonObject.has("description"))
                ? (String) jsonObject.get("description") : "-";

        ReportDetails rd = new ReportDetails();
        rd.setFileName(fileName);
        rd.setFolderName(folderName);
        rd.setIsVulnerabilityFound(isVulnerabilityFound);
        rd.setVulnerability(vulnerability);
        rd.setGenerationType(generationType);
        rd.setModifications(modifications);
        if (!shortDetailsOnly) {
            rd.setDescription(description);
            rd.setIsAnalyzeError(isAnalyzeError);
            rd.setLastURI(lastURI);
            rd.setRequest(request);
            rd.setResponse(response);
            rd.setResponseBody(responseBody);
        }
        return rd;

    }

    /**
     * Mendapatkan isi dari suatu report untuk setiap request.
     * @param folderName
     *      Nama folder
     * @param fileName
     *      Nama file
     * @return String
     *      Isi dari suatu report untuk setiap request.
     */
    public String getReportDetailsContent(String folderName,
            String fileName) {
        ReportDetails rd = getReportDetails(folderName, fileName, false);
        String textTemplate = "Request:\n%s\n\n"
                + "LastUri:\n%s\n\n"
                + "Analyze Success:\n%s\n\n"
                + "Is Vulnerability Found:\n%s\n\n"
                + "Vulnerability Type:\n%s\n\n"
                + "Description:\n%s\n\n"
                + "Generation Type:\n%s\n\n"
                + "Modifications:\n%s\n\n"
                + "Response:\n%s\n\n"
                + "Response Body:\n%s\n\n";

        String[] values = {
            rd.getRequest(),
            rd.getLastURI(),
            String.valueOf((!rd.getIsAnalyzeError())),
            String.valueOf((rd.getIsVulnerabilityFound())),
            rd.getVulnerability(),
            rd.getDescription(),
            rd.getGenerationType(),
            rd.getModifications(),
            rd.getResponse(),
            rd.getResponseBody()
        };

        return String.format(textTemplate, (Object[]) values);
    }

    /**
     * Menghapus hasil dari suatu sesi pengujian.
     * @param folderName
     *      Nama folder
     */
    public void deleteReportSession(String folderName) {
        File dir = new File(reportsFolder + folderName);
        dir.delete();
    }
}
