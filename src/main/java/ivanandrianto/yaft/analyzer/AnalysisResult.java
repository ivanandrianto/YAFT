/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.analyzer;

import java.util.Set;

/**
 * Analysis Result Class.
 * @author ivanandrianto
 */
public class AnalysisResult {
    private boolean vulnerabilityFound;
    private boolean analyzeError;
    private Set<String> vulnerabilityTypes;
    private String description;

    /**
     * Constructor.
     * @param vulnerabilityFound
     *      Apakah ada celah keamanan yang ditemukan atau tidak
     * @param analyzeError
     *      Apakah analisis error atau tidak
     * @param vulnerabilityTypes
     *      Tipe celah keamanan yang ditemukan
     * @param description
     *      Deskripsi dari hasil analisis
     */
    public AnalysisResult(boolean vulnerabilityFound, boolean analyzeError,
            Set<String> vulnerabilityTypes, String description) {
        this.vulnerabilityFound = vulnerabilityFound;
        this.analyzeError = analyzeError;
        this.vulnerabilityTypes = vulnerabilityTypes;
        this.description = description;
    }

    /**
     * Mendapatkan apakah ada celah keamanan yang ditemukan atau tidak.
     * @return boolean
     *      Apakah ada celah keamanan yang ditemukan atau tidak
     */
    public boolean isVulnerabilityFound() {
        return vulnerabilityFound;
    }

    /**
     * Mendapatkan apakah analisis error atau tidak.
     * @return boolean
     *      Apakah analisis error atau tidak
     */
    public boolean isAnalyzeError() {
        return analyzeError;
    }

    /**
     * Mendapatkan tipe celah keamanan yang ditemukan.
     * @return String
     *      Tipe celah keamanan yang ditemukan
     */
    public Set<String> getVulnerabilityTypes() {
        return vulnerabilityTypes;
    }

    /**
     * Mendapatkan deskripsi dari hasil analisis.
     * @return String
     *      Deskripsi dari hasil analisis
     */
    public String getDescription() {
        return description;
    }
}
