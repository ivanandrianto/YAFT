/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.analyzer;

import ivanandrianto.yaft.attacker.HTTPRequest;
import ivanandrianto.yaft.attacker.HTTPRequest.RequestHeader;
import ivanandrianto.yaft.attacker.HTTPRequestConfig;
import ivanandrianto.yaft.attacker.HTTPRequestConfig.ResponseExpect;
import ivanandrianto.yaft.attacker.HTTPResponse;
import ivanandrianto.yaft.attacker.HTTPResponse.ResponseHeader;
import ivanandrianto.yaft.utils.JsonParser;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * ResponseAnalyzer Class.
 * @author ivanandrianto
 */
public class ResponseAnalyzer {
    private HTTPRequest request;
    private HTTPResponse response;
    private String lastURI;
    private HTTPRequestConfig requestConfig;
    private Set<String> generationTypes;
    private String[] knownVulnerabilities = {"Active SQL Injection",
        "Passive SQL Injection", "LDAP Injection", "XML Injection",
        "XPath Injection", "SSI Injection", "XSS", "Path Traversal"};

    /**
     * Constructor.
     * @param request
     *      HTTPRequest yang dikirim
     * @param response
     *      HTTPResponse yang diperoleh
     * @param requestConfig
     *      Konfigurasi request
     * @param generationTypes
     *      Tipe pembangkitan input yang digunakan
     */
    public ResponseAnalyzer(HTTPRequest request, HTTPResponse response, HTTPRequestConfig
            requestConfig, Set<String> generationTypes) {
        this.request = request;
        this.response = response;
        this.requestConfig = requestConfig;
        this.generationTypes = generationTypes;
    };

    /**
     * Menjalankan proses analisis.
     * @return AnalysisResult
     *      Hasil analisis
     */
    public AnalysisResult analyze() {
        AnalysisResult analysisResult = null;
        if (response == null) {
            return new AnalysisResult(false, true, null, "Can't get response");
        }
        // Apabila status code nya 5xx, diasumsikan terjadi error karena
        // aplikasi tidak dapat menghandle request.
        int statusCode = response.getStatusCode();
        Set<String> vulnerabilityTypes = getVulnerabilityTypes();

        if (response.getIsTimeout()) {
            System.out.println("TIMEOUT");
            return new AnalysisResult(true, false, vulnerabilityTypes,
                    "Timeout\nGeneration used: " + generationTypes);
        }

        if ((statusCode >= 500) && (statusCode < 600)) {
            return new AnalysisResult(true, false, vulnerabilityTypes,
                    "Error 500\nGeneration used: " + generationTypes);
        }

        if (requestConfig.getResponseExpects() == null) {
            return new AnalysisResult(false, false, null, null);
        }

        for (int i = 0; i < requestConfig.getResponseExpects().size(); i++) {
            // Get Exception Type First
            ResponseExpect responseExpect = requestConfig.getResponseExpects()
                    .get(i);

            if (responseExpect == null) {

            }

            if (responseExpect.getExceptionType() != null) {
                String exceptionType = responseExpect.getExceptionType();
                if (exceptionType.equalsIgnoreCase("default")) {
                    if (generationTypes.equals("default")) {
                        continue;
                    }
                } else if (exceptionType.equalsIgnoreCase("requestBody")) {
                    if (passRequestBodyException(responseExpect
                            .getExceptionContent())) {
                        continue;
                    }
                } else if (exceptionType.equalsIgnoreCase("uri")) {
                    if (passRequestURIException(responseExpect
                            .getExceptionContent())) {
                        continue;
                    }
                } else if (exceptionType.equalsIgnoreCase("requestHeader")) {
                    if (passRequestHeaderException(responseExpect
                            .getExceptionContent())) {
                        continue;
                    }
                }
            }

            try {
                String responseExpectType = responseExpect.getValidationType();
                if (responseExpectType.equalsIgnoreCase("responseBody")) {
                    analysisResult = analyzeResponseBody(responseExpect);
                } else if (responseExpectType.equalsIgnoreCase("status")) {
                    analysisResult = analyzeStatus(responseExpect);
                } else if (responseExpectType.equalsIgnoreCase("uri")) {
                    analysisResult = analyzeURI(responseExpect);
                } else if (responseExpectType.equalsIgnoreCase("responseHeader")) {
                    analysisResult = analyzeResponseHeader(responseExpect);
                } else if (responseExpectType.equalsIgnoreCase("alert")) {
                    analysisResult = analyzeAlert(responseExpect);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (analysisResult != null) {
                if (analysisResult.isVulnerabilityFound() || analysisResult
                        .isAnalyzeError()) {
                    break;
                }
            }
        }

        if (analysisResult == null) {
            analysisResult = new AnalysisResult(false, false, null, null);
        }

        return analysisResult;
    }

    /**
     * Memperoleh jenis celah keamanan yang ditentukan berdasarkan pembangkitan
     * input yang digunakan
     * @return Set<String>
     *      List of vulnerability types
     */
    private Set<String> getVulnerabilityTypes() {
        Set<String> vulnerabilityTypes = new HashSet<String>();
        for (String generationType : generationTypes) {
            String vulnerabilityType = Arrays.asList(knownVulnerabilities)
                .contains(generationType) ? generationType : null;
            if (vulnerabilityType != null) {
                vulnerabilityTypes.add(vulnerabilityType);
            }
        }
        return vulnerabilityTypes;
    }

    /**
     * Menganalisis bagian URI.
     * @param responseExpect
     *      Expect dari response yang ditetapkan pada RCF
     * @return AnalysisResult
     *      Hasil analisis
     */
    private AnalysisResult analyzeURI(ResponseExpect responseExpect) {
        String uri = response.getLastUri();
        String expectedContent = responseExpect.getContent();
        boolean match = uri.matches(expectedContent);
        boolean mustMatch = responseExpect.getMatch();
        if (!mustMatch) {
            match = !match;
        }
        Set<String> vulnerabilityTypes = getVulnerabilityTypes();
        return new AnalysisResult(!match, false, vulnerabilityTypes,
                "Generation used: " + generationTypes);
    }

    /**
     * Menganalisis bagian status.
     * @param responseExpect
     *      Expect dari response yang ditetapkan pada RCF
     * @return AnalysisResult
     *      Hasil analisis
     */
    private AnalysisResult analyzeStatus(ResponseExpect responseExpect) {
        String status = String.valueOf(response.getStatusCode());
        String expectedContent = responseExpect.getContent();
        boolean match = status.matches(expectedContent);
        boolean mustMatch = responseExpect.getMatch();
        if (!mustMatch) {
            match = !match;
        }
        Set<String> vulnerabilityTypes = getVulnerabilityTypes();
        return new AnalysisResult(!match, false, vulnerabilityTypes,
                "Generation used: " + generationTypes);
    }

    /**
     * Menganalisis bagian response header.
     * @param responseExpect
     *      Expect dari response yang ditetapkan pada RCF
     * @return AnalysisResult
     *      Hasil analisis
     */
    private AnalysisResult analyzeResponseHeader(ResponseExpect
            responseExpect) {
        String expectedContent = responseExpect.getContent();
        String[] splitHeaderKeyValue = responseExpect.getContent().split(":");
        String key = splitHeaderKeyValue[0];
        String headerValue = null;
        boolean isHeaderExist = false;
        for (int i = 0; i < response.getResponseHeaders().size(); i++) {
            ResponseHeader responseHeader = response.getResponseHeaders()
                    .get(i);
            String currentKey = responseHeader.getHeaderField().toLowerCase();
            if (currentKey.equals("key")) {
                headerValue = responseHeader.getHeaderValue();
                isHeaderExist = true;
                break;
            }
        }
        boolean match = headerValue.matches(expectedContent);
        boolean mustMatch = responseExpect.getMatch();
        if (!mustMatch) {
            match = !match;
        }
        Set<String> vulnerabilityTypes = getVulnerabilityTypes();
        return new AnalysisResult(!match, false, vulnerabilityTypes,
                "Generation used: " + generationTypes);
    }

    /**
     * Menganalisis bagian body.
     * @param responseExpect
     *      Expect yang telah ditetapkan pada RCF
     * @return Analysis Result
     *      Hasil analisis
     * @throws ParseException
     *      Apabila terjadi kesalahan dalam mem-parse isi body
     */
    private AnalysisResult analyzeResponseBody(ResponseExpect responseExpect)
            throws ParseException {
        String responseBodyFormat = responseExpect.getResponseBodyFormat();
        if (responseBodyFormat.equalsIgnoreCase("HTML")) {
            return analyzeHTMLResponse(responseExpect);
        } else if (responseBodyFormat.equalsIgnoreCase("JSON")) {
            return analyzeJSONResponse(responseExpect);
        } else if (responseBodyFormat.equalsIgnoreCase("TEXT")) {
            return analyzeTextResponse(responseExpect);
        } else {
            return null;
        }
    }

    /**
     * Menganalisis response body dalam format HTML.
     * @param responseExpect
     *      Expect yang telah ditetapkan pada RCF
     * @return AnalysisResult
     *      Hasil analisis
     * @throws ParseException
     *      Apabila terjadi kesalahan dalam mem-parse HTML
     */
    private AnalysisResult analyzeHTMLResponse(ResponseExpect responseExpect)
            throws ParseException {
        String responseBody = response.getBody();
        Document doc = Jsoup.parse(responseBody);
        String element = responseExpect.getResponseBodyElement();
        String expectedContent = responseExpect.getContent();
        String elementContent = doc.select(element).text();
        boolean match = elementContent.matches(expectedContent);
        boolean mustMatch = responseExpect.getMatch();
        if (!mustMatch) {
            match = !match;
        }
        Set<String> vulnerabilityTypes = getVulnerabilityTypes();
        return new AnalysisResult(!match, false, vulnerabilityTypes,
                "Generation used: " + generationTypes);
    }

    /**
     * Menganalisis response body dalam format JSON.
     * @param responseExpect
     *      Expect yang telah ditetapkan pada RCF
     * @return AnalysisResult
     *      Hasil analisis
     * @throws ParseException
     *      Apabila terjadi kesalahan dalam mem-parse JSON
     */
    private AnalysisResult analyzeJSONResponse(ResponseExpect responseExpect)
            throws ParseException {
        String expectedContent = responseExpect.getContent();
        String element = responseExpect.getResponseBodyElement();
        JsonParser jsonParser = new JsonParser(response.getBody());
        String elementContent = jsonParser.parse(element);
        boolean match = elementContent.matches(expectedContent);
        boolean mustMatch = responseExpect.getMatch();
        if (!mustMatch) {
            match = !match;
        }
        Set<String> vulnerabilityTypes = getVulnerabilityTypes();
        return new AnalysisResult(!match, false, vulnerabilityTypes,
                "Generation used: " + generationTypes);
    }

    /**
     * Menganalisis response body dalam format teks.
     * @param responseExpect
     *      Expect yang telah ditetapkan pada RCF
     * @return AnalysisResult
     *      Hasil analisis
     */
    private AnalysisResult analyzeTextResponse(ResponseExpect responseExpect) {
        String textContent = response.getBody();
        String expectedContent = responseExpect.getContent();
        boolean match = textContent.matches(expectedContent);
        boolean mustMatch = responseExpect.getMatch();
        if (!mustMatch) {
            match = !match;
        }
        Set<String> vulnerabilityTypes = getVulnerabilityTypes();
        return new AnalysisResult(!match, false, vulnerabilityTypes,
                "Generation used: " + generationTypes);
    }

    /**
     * Menganalisis alert.
     * @param responseExpect
     *      Expect dari response yang ditetapkan pada RCF
     * @return AnalysisResult
     *      Hasil analisis
     */
    private AnalysisResult analyzeAlert(ResponseExpect responseExpect) {
        ArrayList<String> alerts = response.getAlerts();
        String expectedContent = responseExpect.getContent();
        boolean match = false;
        for (String alert : alerts) {
            if (alert.matches(expectedContent)) {
                match = true;
                break;
            }
        }
        boolean mustMatch = responseExpect.getMatch();
        if (!mustMatch) {
            match = !match;
        }
        Set<String> vulnerabilityTypes = getVulnerabilityTypes();
        return new AnalysisResult(!match, false, vulnerabilityTypes,
                "Generation used: " + generationTypes);
    }

    /**
     * Memeriksa apakah termasuk ke dalam exception berdasarkan URI.
     * @param exceptionContent
     *      Isi dari exception yang telah ditetapkan pada RCF
     * @return boolean
     *      Apakah termasuk ke dalam exception berdasarkan URI.
     */
    private boolean passRequestURIException(String exceptionContent) {
        if (exceptionContent == null) {
            return false;
        }
        String uri = request.getURI();
        return uri.matches(exceptionContent);
    }

    /**
     * Memeriksa apakah termasuk ke dalam exception berdasarkan body.
     * @param exceptionContent
     *      Isi dari exception yang telah ditetapkan pada RCF
     * @return booelan
     *      Apakah termasuk ke dalam exception berdasarkan URI.
     */
    private boolean passRequestBodyException(String exceptionContent) {
        if (exceptionContent == null) {
            return false;
        }
        String body = request.getBody();
        return body.matches(exceptionContent);
    }

    /**
     * Memeriksa apakah termasuk ke dalam exception berdasarkan header.
     * @param exceptionContent
     *      Isi dari exception yang telah ditetapkan pada RCF
     * @return booelan
     *      Apakah termasuk ke dalam exception berdasarkan header.
     */
    private boolean passRequestHeaderException(String exceptionContent) {
        if (exceptionContent == null) {
            return false;
        }
        String delim = ":";
        String regex = "(?<!\\\\)" + Pattern.quote(delim);
        String[] splitHeaderFieldValue = exceptionContent.split(regex);
        String field = splitHeaderFieldValue[0].toLowerCase();
        String value = splitHeaderFieldValue[1];
        String headerValue = null;
        boolean isHeaderExist = false;
        for (int i = 0; i < request.getHeaders().size(); i++) {
            RequestHeader requestHeader = request.getHeaders().get(i);
            String currentField = requestHeader.getHeaderField().toLowerCase();
            if (currentField.equals(field)) {
                headerValue = requestHeader.getHeaderValue();
                isHeaderExist = true;
                break;
            }
        }
        if (!isHeaderExist) {
            return false;
        }
        return headerValue.matches(value);
    }
}
