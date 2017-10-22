/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import java.util.ArrayList;
import java.util.Map;

/**
 * Request Config Class.
 * @author ivanandrianto
 */
public class HTTPRequestConfig {
    /**
     * Request Headers Config Class.
     */
    private static final String[] responseBodyFormats = {"html", "json"};
    private static final String[] validationTypes = {"responseBody", "status",
        "uri", "reponseHeader"};
    private static final String[] exceptionTypes = {"default", "requestBody",
        "uri", "requestHeader"};

    public static class RequestHeadersConfig {
        private HTTPRequestPart field;
        private HTTPRequestPart value;

        /**
         * Constructor.
         * @param field
         *      Nama header
         * @param value
         *      Value header
         */
        public RequestHeadersConfig(HTTPRequestPart field, HTTPRequestPart value) {
            this.field = field;
            this.value = value;
        }

        /**
         * Constructor.
         */
        public RequestHeadersConfig() {

        }

        /**
         * Menetapkan konfigurasi untuk field dari suatu header.
         * @param field
         *      Konfigurasi untuk field dari suatu header
         */
        public void setHeaderField(HTTPRequestPart field) {
            this.field = field;
        }

        /**
         * Mendapatkan konfigurasi untuk field dari suatu header.
         * @return RequestHeader
         *      Konfigurasi untuk field dari suatu header
         */
        public HTTPRequestPart getHeaderField() {
            return field;
        }

        /**
         * Menetapkan konfigurasi untuk isi dari suatu header.
         * @param value
         *      Konfigurasi untuk isi dari suatu header
         */
        public void setHeaderValue(HTTPRequestPart value) {
            this.value = value;
        }

        /**
         * Mendapatkan konfigurasi untuk isi dari suatu header.
         * @return HTTPRequestPart
                Konfigurasi isi isi dari suatu header
         */
        public HTTPRequestPart getHeaderValue() {
            return value;
        }

    }

    /**
     * Response Expect Class.
     */
    public static class ResponseExpect {
        /**
         * Response Body Format enum.
         */
        public enum ResponseBodyFormat {
            /**
             * HTML.
             */
            HTML,
            /**
             * JSON.
             */
            JSON
        }

        /**
         * Validation Type enum.
         */
        public enum ValidationType {
            /**
             * Response Body.
             */
            RESPONSE_BODY,
            /**
             * Status.
             */
            STATUS,
            /**
             * URI.
             */
            URI,
            /**
             * Response Header.
             */
            RESPONSE_HEADER
        }

        /**
         * Exception Type enum.
         */
        public enum ExceptionType {
            /**
             * Default.
             */
            DEFAULT,
            /**
             * Request body.
             */
            REQUEST_BODY,
            /**
             * URI.
             */
            URI,
            /**
             * Request header.
             */
            REQUEST_HEADER
        }

        private String responseBodyFormat;
        private String responseBodyElement;
        private String validationType;
        private String content;
        private boolean mustMatch;
        private String exceptionType;
        private String exceptionContent;

        /**
         * Constructor.
         * @param responseBodyFormat
         *      Format response body
         * @param responseBodyElement
         *      Elemen dari response body yang ingin diperoleh
         * @param validationType
         *      Tipe validasi
         * @param content
         *      Isi yang ingin dibandingkan dengan suatu bagian dari response
         * @param exist
         *      Menetapkan apakah content harus ada atau tidak boleh ada
         * @param exceptionType
         *      Tipe exception, validasi tidak dilakukan apabila
         *      memenuhi exception
         * @param exceptionContent
         *      Isi exception
         */
        public ResponseExpect(String responseBodyFormat, String
                responseBodyElement, String validationType, String
                content, boolean exist, String exceptionType, String
                exceptionContent) {
            this.responseBodyFormat = responseBodyFormat;
            this.responseBodyElement = responseBodyElement;
            this.validationType = validationType;
            this.content = content;
            this.mustMatch = exist;
            this.exceptionType = exceptionType;
            this.exceptionContent = exceptionContent;
        }

        /**
         * Constructor.
         */
        public ResponseExpect() {

        }

        /**
         * Menetapkan format response body.
         * @param responseBodyFormat
         *      Format response body
         */
        public void setResponseBodyFormat(String responseBodyFormat) {
            this.responseBodyFormat = responseBodyFormat;
        }

        /**
         * Mendapatkan format response body.
         * @return String
         *      Format response body
         */
        public String getResponseBodyFormat() {
            return responseBodyFormat;
        }

        /**
         * Menetapkan elemen dari response body yang ingin diperiksa.
         * @param responseBodyElement
         *      Elemen dari response body yang ingin diperiksa
         */
        public void setResponseBodyElement(String responseBodyElement) {
            this.responseBodyElement = responseBodyElement;
        }

        /**
         * Mendapatkan elemen dari response body yang ingin diperiksa.
         * @return String
         *      Elemen dari response body yang ingin diperiksa
         */
        public String getResponseBodyElement() {
            return responseBodyElement;
        }

        /**
         * Menetapkan tipe validasi.
         * @param validationType
         *      Tipe validasi
         */
        public void setValidationType(String validationType) {
            this.validationType = validationType;
        }

        /**
         * Mendapatkan tipe validasi.
         * @return String
         *      Tipe validasi
         */
        public String getValidationType() {
            return validationType;
        }

        /**
         * Menetapkan konten dari expect.
         * @param content
         *      Konten dari expect
         */
        public void setContent(String content) {
            this.content = content;
        }

        /**
         * Mendapatkan konten dari expect.
         * @return String
         *      Konten dari expect
         */
        public String getContent() {
            return content;
        }

        /**
         * Menetapkan apakah konten harus ada atau tidak boleh ada.
         * @param exist
         *      Apakah konten harus ada atau tidak boleh ada
         */
        public void setMatch(boolean mustMatch) {
            this.mustMatch = mustMatch;
        }

        /**
         * Mendapatkan apakah konten harus ada atau tidak boleh ada.
         * @return boolean
         *      Apakah content harus ada atau tidak boleh ada
         */
        public boolean getMatch() {
            return mustMatch;
        }

        /**
         * Menetapkan tipe exception.
         * @param exceptionType
         *      Tipe exception
         */
        public void setExceptionType(String exceptionType) {
            this.exceptionType = exceptionType;
        }

        /**
         * Mendapatkan tipe exception.
         * @return String
         *      Tipe exception
         */
        public String getExceptionType() {
            return exceptionType;
        }

        /**
         * Menetapkan isi exception.
         * @param exceptionContent
         *      Isi exception
         */
        public void setExceptionContent(String exceptionContent) {
            this.exceptionContent = exceptionContent;
        }

        /**
         * Mendapatkan isi exception.
         * @return String
         *      Isi exception
         */
        public String getExceptionContent() {
            return exceptionContent;
        }
    }

    /**
     * Macro Config Class.
     */
    public static class MacroConfig {
        private String id;
        private String fileName;
        private String part;

        /**
         * Constructor.
         */
        public MacroConfig() {

        }

        /**
         * Constructor.
         * @param id
         *      ID macro
         * @param fileName
         *      Nama file macro
         * @param part
         *      Bagian dari file macro yang ingin diperoleh
         */
        public MacroConfig(String id, String fileName, String part) {
            this.id = id;
            this.fileName = fileName;
            this.part = part;
        }

        /**
         * Menetapkan ID macro.
         * @param id
         *      ID macro
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Mendapatkan ID macro.
         * @return String
         *      ID macro
         */
        public String getId() {
            return id;
        }

        /**
         * Menetapkan nama file macro yang ingin digunakan.
         * @param fileName
         *      Nama file macro yang ingin digunakan
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * Memperoleh nama file macro yang ingin digunakan.
         * @return String
         *      Nama file macro yang ingin digunakan
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Menetapkan bagian dari macro yang ingin diperoleh.
         * @param part
         *      Bagian dari macro yang ingin diperoleh
         */
        public void setPart(String part) {
            this.part = part;
        }

        /**
         * Memperoleh bagian dari macro yang ingin diperoleh.
         * @return String
         *      bagian dari macro yang ingin diperoleh
         */
        public String getPart() {
            return part;
        }
    }

    private ArrayList<MacroConfig> macrosConfig;
    private HTTPRequestPart methodConfig;
    private HTTPRequestPart uriConfig;
    private HTTPRequestPart httpVersionConfig;
    private ArrayList<RequestHeadersConfig> headersConfig;
    private HTTPRequestPart bodyConfig;
    private ArrayList<ResponseExpect> responseExpects;
    private int timeout;
    private Map<String, Integer> combinations;

    /**
     * Constructor.
     * @param macrosConfig
     *      Konfigurasi macro
     * @param methodConfig
     *      Konfigurasi method
     * @param uriConfig
     *      Konfigurasi URI
     * @param httpVersionConfig
     *      Konfigurasi versi HTTP
     * @param headersConfig
     *      Konfigurasi headers
     * @param bodyConfig
     *      Konfigurasi body
     * @param combinations
     *      Kombinasi pembangkitan input
     */
    public HTTPRequestConfig(ArrayList<MacroConfig> macrosConfig,
            HTTPRequestPart methodConfig, HTTPRequestPart uriConfig,
            HTTPRequestPart httpVersionConfig, ArrayList<RequestHeadersConfig>
            headersConfig, HTTPRequestPart bodyConfig, Map<String, Integer>
            combinations) {
        this.macrosConfig = macrosConfig;
        this.methodConfig = methodConfig;
        this.uriConfig = uriConfig;
        this.httpVersionConfig = httpVersionConfig;
        this.headersConfig = headersConfig;
        this.bodyConfig = bodyConfig;
        this.combinations = combinations;
        timeout = -1;
    }

    /**
     * Constructor.
     */
    public HTTPRequestConfig() {
        timeout = -1;
    }

    /**
     * Menetapkan konfigurasi macro.
     * @param macrosConfig
     *      Konfigurasi macro
     */
    public void setMacrosConfig(ArrayList<MacroConfig> macrosConfig) {
        this.macrosConfig = macrosConfig;
    }

    /**
     * Mendapatkan konfigurasi macro.
     * @return ArrayList<MacroConfig>
     *      Konfigurasi macro.
     */
    public ArrayList<MacroConfig> getMacrosConfig() {
        return macrosConfig;
    }

    /**
     * Menetapkan konfigurasi method.
     * @param methodConfig
     *      Konfigurasi method
     */
    public void setMethodConfig(HTTPRequestPart methodConfig) {
        this.methodConfig = methodConfig;
    }

    /**
     * Mendapatkan konfigurasi method.
     * @return HTTPRequestPart
      Konfigurasi method
     */
    public HTTPRequestPart getMethodConfig() {
        return methodConfig;
    }

    /**
     * Menetapkan konfigurasi URI.
     * @param uriConfig
     *      Konfigurasi URI
     */
    public void setUriConfig(HTTPRequestPart uriConfig) {
        this.uriConfig = uriConfig;
    }

    /**
     * Mendapatkan konfigurasi URI.
     * @return HTTPRequestPart
      Konfigurasi URI
     */
    public HTTPRequestPart getUriConfig() {
        return uriConfig;
    }

    /**
     * Menetapkan konfigurasi versi HTTP.
     * @param httpVersionConfig
     *      Konfigurasi versi HTTP
     */
    public void setHttpVersionConfig(HTTPRequestPart httpVersionConfig) {
        this.httpVersionConfig = httpVersionConfig;
    }

    /**
     * Mendapatkan konfigurasi versi HTTP.
     * @return HTTPRequestPart
      Konfigursai veresi HTTP
     */
    public HTTPRequestPart getHttpVersionConfig() {
        return httpVersionConfig;
    }

    /**
     * Menetapkan konfigursi headers.
     * @param headersConfig
     *      Konfigurasi headers
     */
    public void setHeadersConfig(ArrayList<RequestHeadersConfig>
            headersConfig) {
        this.headersConfig = headersConfig;
    }

    /**
     * Mendapatkan konfigurasi headers.
     * @return ArrayList<RequestHeadersConfig>
     *      Konfigurasi headers
     */
    public ArrayList<RequestHeadersConfig> getHeadersConfig() {
        return headersConfig;
    }

    /**
     * Menetapkan konfigurasi body.
     * @param bodyConfig
     *      Konfigurasi body
     */
    public void setBodyConfig(HTTPRequestPart bodyConfig) {
        this.bodyConfig = bodyConfig;
    }

    /**
     * Mendapatkan konfigurasi body.
     * @return HTTPRequestPart
      Konfigurasi body
     */
    public HTTPRequestPart getBodyConfig() {
        return bodyConfig;
    }

    /**
     * Menetapkan sekumpulan expect untuk response.
     * @param responseExpects
     *      Sekumpulan exepect untuk response
     */
    public void setResponseExpect(ArrayList<ResponseExpect> responseExpects) {
        this.responseExpects = responseExpects;
    }

    /**
     * Mendapatkan sekumpulan expect untuk response.
     * @return ArrayList<ResponseExpect>
     *      Sekumpulan exepect untuk response
     */
    public ArrayList<ResponseExpect> getResponseExpects() {
        return responseExpects;
    }

    /**
     * Menetapkan timeout.
     * @param timeout
     *      Timeout yang ditentukan
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Mendapatkan timeout.
     * @return int
     *      Timeout yang ditentukan
     */
    public int getTimeout() {
        return timeout;
    }
}
