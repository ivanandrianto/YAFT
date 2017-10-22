/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.reporter;

import java.util.Map;

/**
 * Report Session Class.
 * @author ivanandrianto
 */
public class ReportSession {
    private String folder;
    private int totalRequests;
    private int vulnerabilityFound;
    private Map<String, Integer> vulnerabilities;
    private String time;

    /**
     * Constructor.
     * @param folder
     *      Folder dari suatu report session
     * @param totalRequests
     *      Total request yang dieksekusi
     * @param vulnerabilityFound
     *      Jumlah celah keamanan yang ditemukan
     * @param vulnerabilities
     *      Daftar celah keamanan yang ditemukan
     * @param time
     *      Waktu report dihasilkan
     */
    public ReportSession(String folder, int totalRequests, int
            vulnerabilityFound, Map<String, Integer> vulnerabilities, String
            time) {
        this.folder = folder;
        this.totalRequests = totalRequests;
        this.vulnerabilityFound = vulnerabilityFound;
        this.vulnerabilities = vulnerabilities;
        this.time = time;
    }

    /**
     * Mendapatkan nama folder dari suatu sesi pengujian.
     * @return String
     *      Nama folder dari report session
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Mendapatkan jumlah request yang dikirim dari suatu sesi pengujian.
     * @return int
     *      Total request yang dieksekusi
     */
    public int getTotalRequests() {
        return totalRequests;
    }

    /**
     * Mendapatkan jumlah celah keamanan yang ditemukan pada suatu sesi
     * pengujian.
     * @return int
     *      Jumlah celah keamanan yang ditemukan
     */
    public int getVulnerabilityFound() {
        return vulnerabilityFound;
    }

    /**
     * Mendapatkan daftar celah keamanan yang ditemukan pada suatu sesi
     * pengujian beserta jumlah dari setiap celah keamanan yang ditemukan.
     * @return Map<String, Integer>
     *      Daftar celah keamanan yang ditemukan dan jumlahnya masing-masing
     */
    public Map<String, Integer> getVulnerabilities() {
        return vulnerabilities;
    }

    /**
     * Mendapatkan waktu report dihasilkan.
     * @return String
     *      Waktu report dihasilkan
     */
    public String getTime() {
        return time;
    }
}
