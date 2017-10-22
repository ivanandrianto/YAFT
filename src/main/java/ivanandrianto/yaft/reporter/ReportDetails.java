/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.reporter;

/**
 * Report Details Class.
 * @author ivanandrianto
 */
public class ReportDetails {
    private String fileName;
    private String folderName;
    private String request;
    private String response;
    private String responseBody;
    private String lastURI;
    private boolean isVulnerabilityFound;
    private String vulnerability;
    private boolean isAnalyzeError;
    private String description;
    private String generationType;
    private String modifications;

    /**
     * Constructor.
     */
    public ReportDetails() {

    }

    /**
     * Menetapkan nama folder yang menyimpan report.
     * @param folderName
     *      Nama folder yang menyimpan report
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * Mendapatkan nama folder yang menyimpan report.
     * @return String
     *      Nama folder yang menyimpan report
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Menetapkan nama suatu file report.
     * @param fileName
     *      Nama suatu file report
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Mendapatkan nama suatu file report.
     * @return String
     *      Nama suatu file report
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Menetapkan isi request.
     * @param request
     *      Isi request
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * Mendapatkan isi request.
     * @return String
     *      Isi request
     */
    public String getRequest() {
        return request;
    }

    /**
     * Menetapkan isi response.
     * @param response
     *      Isi response
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * Mendapatkan risi esponse.
     * @return String
     *      Isi response
     */
    public String getResponse() {
        return response;
    }

    /**
     * Menetapkan isi response body.
     * @param response body
     *      Isi response body
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * Mendapatkan response body.
     * @return String
     *      Isi response body
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Menetapkan URI terakhir.
     * @param lastURI
     *      URI terakhir
     */
    public void setLastURI(String lastURI) {
        this.lastURI = lastURI;
    }

    /**
     * Mendapatkan URI terakhir.
     * @return String
     *      URI terakhir
     */
    public String getLastURI() {
        return lastURI;
    }

    /**
     * Menetapkan apakah celah keamanan ditemukan.
     * @param isVulnerabilityFound
     *      Apakah celah keamanan ditemukan
     */
    public void setIsVulnerabilityFound(boolean isVulnerabilityFound) {
        this.isVulnerabilityFound = isVulnerabilityFound;
    }

    /**
     * Mendapatkan apakah celah keamanan ditemukan.
     * @return boolean
     *      Apakah celah keamanan ditemukan
     */
    public boolean getIsVulnerabilityFound() {
        return isVulnerabilityFound;
    }

    /**
     * Menetapkan jenis celah keamanan yang ditemukan.
     * @param vulnerability
     *      Jenis celah keamanan yang ditemukan
     */
    public void setVulnerability(String vulnerability) {
        this.vulnerability = vulnerability;
    }

    /**
     * Mendapatkan jenis celah keamanan yang ditemukan.
     * @return String
     *      Jenis celah keamanan yang ditemukan
     */
    public String getVulnerability() {
        return vulnerability;
    }

    /**
     * Menetapkan apakah analisis error.
     * @param isAnalyzeError
     *      Apakah analisis error atau tidak
     */
    public void setIsAnalyzeError(boolean isAnalyzeError) {
        this.isAnalyzeError = isAnalyzeError;
    }

    /**
     * Mendapatkan apakah analisis error.
     * @return boolean
     *      Apakah analisis error atau tidak
     */
    public boolean getIsAnalyzeError() {
        return isAnalyzeError;
    }

    /**
     * Menetapkan isi deskripsi.
     * @param description
     *      Isi deskripsi
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Mendapatkan isi deskripsi.
     * @return String
     *      Isi deskripsi
     */
    public String getDescription() {
        return description;
    }

        /**
     * Set the generation type.
     * @param generationType
     *      The generation type
     */
    public void setGenerationType(String generationType) {
        this.generationType = generationType;
    }

    /**
     * Get the generation type.
     * @return String
     *      The generation type
     */
    public String getGenerationType() {
        return generationType;
    }

    /**
     * Set the modifications.
     * @param modifications
     *      The modifications
     */
    public void setModifications(String modifications) {
        this.modifications = modifications;
    }

    /**
     * Get the modifications.
     * @return String
     *      The modifications
     */
    public String getModifications() {
        return modifications;
    }
}
