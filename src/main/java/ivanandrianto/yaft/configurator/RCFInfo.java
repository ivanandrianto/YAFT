/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.configurator;

/**
 *
 * @author ivanandrianto
 */
/**
* RCF Info Class.
*/
public class RCFInfo {
    private String method;
    private String uri;
    private String fileName;

    /**
     * Constructor.
     * @param method
     *      Method dari request pada request configuration file
     * @param uri
     *      URI dari request pada request configuration file
     * @param fileName
     *      Nama file RCF
     */
    public RCFInfo(String method, String uri,
            String fileName) {
        this.method = method;
        this.uri = uri;
        this.fileName = fileName;
    }

    /**
     * Mendapatkan method dari request pada request configuration file.
     * @return String
     *      Method dari request pada RCF
     */
    public String getMethod() {
        return method;
    }

    /**
     * Mendapatkan URI dari request pada request configuration file.
     * @return String
     *      URI dari request pada request configuration file
     */
    public String getURI() {
        return uri;
    }

    /**
     * Mendapatkan nama request configuration file.
     * @return String
     *      Nama request configuration file
     */
    public String getFileName() {
        return fileName;
    }
}
