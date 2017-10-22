/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import ivanandrianto.yaft.analyzer.ResponseAnalyzer;
import ivanandrianto.yaft.configurator.MacroManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import ivanandrianto.yaft.inputGenerator.GeneratedInput;
import ivanandrianto.yaft.attacker.HTTPRequest.RequestHeader;
import ivanandrianto.yaft.attacker.HTTPResponse.ResponseHeader;
import ivanandrianto.yaft.inputGenerator.InputGenerator;
import ivanandrianto.yaft.analyzer.AnalysisResult;
import ivanandrianto.yaft.attacker.FuzzedPart.Generation;
import ivanandrianto.yaft.inputGenerator.GeneratedInput;
import ivanandrianto.yaft.attacker.HTTPRequestContent.RequestHeaderContent;
import ivanandrianto.yaft.attacker.HTTPRequestPart.Content;
import ivanandrianto.yaft.reporter.ReportGenerator;
import ivanandrianto.yaft.utils.FileUtil;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * WebAppAttacker Class.
 * @author ivanandrianto
 */
public class WebAppAttacker {

    private String session;
    private String fuzzFolder;
    private ReportGenerator reporter;
    private WebClient webClient;
    private final int defaultTimeout = 30000;
    private MacroManager macroManager;

    /**
     * Constructor.
     * @param project
     *      Nama session
     * @param txtOutput
     *      Nama file txt yang berisi path menuju folder report, berguna
     *      ketika dijalankan menggunakan Jenkins
     */
    public WebAppAttacker(String project, String txtOutput) {
        this.session = project;
        txtOutput.replaceAll("/.txt$/", "");
        reporter = new ReportGenerator(project, txtOutput);
        fuzzFolder = "projects/" + project + "/fuzzRequests/";
    }

    /**
     * Menjalankan proses fuzzing.
     */
    public void run() {
        webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);     
        webClient.getOptions().setTimeout(defaultTimeout);
        macroManager = new MacroManager(session);

        FileInputStream fstream;
        try {
            fstream = new FileInputStream(fuzzFolder + "/list.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WebAppAttacker.class.getName()).log(
                    Level.SEVERE, null, ex);
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String fileName;
        try {
            while ((fileName = br.readLine()) != null)   {
                String filePath = fuzzFolder + fileName;
                if (!FileUtil.isFileExists(filePath)) {
                    continue;
                }

                RCFReader rcf = new RCFReader(filePath);
                HTTPRequestConfig requestConfig = rcf.read();

                // Reset timeout to default while running macros
                webClient.getOptions().setTimeout(defaultTimeout);

                // macros
                ArrayList<HTTPRequestConfig.MacroConfig> macrosConfig =
                        requestConfig.getMacrosConfig();

                // Set timeout
                int timeout = requestConfig.getTimeout();
                if (timeout != -1) {
                    webClient.getOptions().setTimeout(timeout);
                }

                // default
                HTTPRequestContent defaultRequestContent = generateDefaultRequest(requestConfig);
                HTTPRequest defaultRequest = defaultRequestContent.getRequest();
                ArrayList<MacroResponse> macroResponses = runMacros(
                        macroManager, macrosConfig);

                // method
                HTTPRequestPart methodConfig = requestConfig.getMethodConfig();
                ArrayList<GeneratedPart> generatedMethod =
                        generateModifiedRequest(methodConfig, macroResponses);
                for (GeneratedPart s : generatedMethod) {
                    HTTPRequestContent rcCopy = new HTTPRequestContent(defaultRequestContent);
                    rcCopy.setMethod(s.getContents());
                    HTTPRequest modifiedRequest = rcCopy.getRequest();
                    GeneratedInput gi = s.getGeneratedInput();
                    modifiedRequest.setMethod(gi.getContent());
                    fillMacroParts(requestConfig, rcCopy);
                    modifiedRequest = rcCopy.getRequest();
                    HTTPResponse response = sendHttpRequest(webClient,
                            modifiedRequest);
                    Set<String> generationTypes = new HashSet<String>();
                    generationTypes.add(gi.getType());
                    callAnalyzer(modifiedRequest, response, requestConfig,
                            generationTypes, gi.getModifiations());
                }

                // URI
                HTTPRequestPart uriConfig = requestConfig.getUriConfig();
                ArrayList<GeneratedPart> generatedURI =
                        generateModifiedRequest(uriConfig, macroResponses);
                for (GeneratedPart s : generatedURI) {
                    HTTPRequestContent rcCopy = new HTTPRequestContent(defaultRequestContent);
                    rcCopy.setURI(s.getContents());
                    HTTPRequest modifiedRequest = rcCopy.getRequest();
                    GeneratedInput gi = s.getGeneratedInput();
                    modifiedRequest.setURI(gi.getContent());
                    fillMacroParts(requestConfig, rcCopy);
                    modifiedRequest = rcCopy.getRequest();
                    HTTPResponse response = sendHttpRequest(webClient,
                            modifiedRequest);
                    Set<String> generationTypes = new HashSet<String>();
                    generationTypes.add(gi.getType());
                    callAnalyzer(modifiedRequest, response, requestConfig,
                            generationTypes, gi.getModifiations());
                }

                // HTTP version
                HTTPRequestPart httpVersionConfig = requestConfig
                        .getHttpVersionConfig();
                ArrayList<GeneratedPart> generatedHttpVersion =
                        generateModifiedRequest(httpVersionConfig,
                                macroResponses);
                for (GeneratedPart s : generatedHttpVersion) {
                    HTTPRequestContent rcCopy = new HTTPRequestContent(defaultRequestContent);
                    rcCopy.setHttpVersion(s.getContents());
                    HTTPRequest modifiedRequest = rcCopy.getRequest();
                    GeneratedInput gi = s.getGeneratedInput();
                    modifiedRequest.setHttpVersion(gi.getContent());
                    fillMacroParts(requestConfig, rcCopy);
                    modifiedRequest = rcCopy.getRequest();
                    HTTPResponse response = sendHttpRequest(webClient,
                            modifiedRequest);
                    Set<String> generationTypes = new HashSet<String>();
                    generationTypes.add(gi.getType());
                    callAnalyzer(modifiedRequest, response, requestConfig,
                            generationTypes, gi.getModifiations());
                }

                // Headers
                ArrayList<HTTPRequestConfig.RequestHeadersConfig> headersConfig =
                        requestConfig.getHeadersConfig();
                for (int i = 0; i < headersConfig.size(); i++) {
                    HTTPRequestConfig.RequestHeadersConfig headerConfig =
                            requestConfig.getHeadersConfig().get(i);

                    // key
                    HTTPRequestPart headerKeyConfig = headerConfig.getHeaderField();
                    ArrayList<GeneratedPart> generatedHeaderKey =
                            generateModifiedRequest(headerKeyConfig,
                                    macroResponses);
                    for (GeneratedPart s : generatedHeaderKey) {
                        HTTPRequestContent rcCopy = new HTTPRequestContent(defaultRequestContent);
                        RequestHeaderContent rhc = rcCopy.getHeaders().get(i);
                        rhc.setHeaderField(s.getContents());
                        HTTPRequest modifiedRequest = rcCopy.getRequest();
                        GeneratedInput gi = s.getGeneratedInput();
                        fillMacroParts(requestConfig, rcCopy);
                        modifiedRequest = rcCopy.getRequest();
                        HTTPResponse response = sendHttpRequest(webClient,
                                modifiedRequest);
                        Set<String> generationTypes = new HashSet<String>();
                        generationTypes.add(gi.getType());
                        callAnalyzer(modifiedRequest, response, requestConfig,
                                generationTypes, gi.getModifiations());
                    }

                    // value
                    HTTPRequestPart headerValueConfig = headerConfig
                            .getHeaderValue();
                    ArrayList<GeneratedPart> generatedHeaderValue =
                            generateModifiedRequest(headerValueConfig,
                                    macroResponses);
                    for (GeneratedPart s : generatedHeaderValue) {
                        HTTPRequestContent rcCopy = new HTTPRequestContent(
                                defaultRequestContent);
                        RequestHeaderContent rhc = rcCopy.getHeaders().get(i);
                        rhc.setHeaderValue(s.getContents());
                        HTTPRequest modifiedRequest = rcCopy.getRequest();
                        GeneratedInput gi = s.getGeneratedInput();
                        fillMacroParts(requestConfig, rcCopy);
                        modifiedRequest = rcCopy.getRequest();
                        HTTPResponse response = sendHttpRequest(webClient,
                                modifiedRequest);
                        Set<String> generationTypes = new HashSet<String>();
                        generationTypes.add(gi.getType());
                        callAnalyzer(modifiedRequest, response, requestConfig,
                                generationTypes, gi.getModifiations());
                    }
                }

                // Body
                HTTPRequestPart bodyConfig = requestConfig.getBodyConfig();
                ArrayList<GeneratedPart> generatedBody =
                        generateModifiedRequest(
                        bodyConfig, macroResponses);
                for (GeneratedPart s : generatedBody) {
                    HTTPRequestContent rcCopy = new HTTPRequestContent(
                            defaultRequestContent);
                    rcCopy.setBody(s.getContents());
                    HTTPRequest modifiedRequest = rcCopy.getRequest();
                    GeneratedInput gi = s.getGeneratedInput();
                    modifiedRequest.setBody(gi.getContent());
                    fillMacroParts(requestConfig, rcCopy);
                    modifiedRequest = rcCopy.getRequest();
                    HTTPResponse response = sendHttpRequest(webClient,
                            modifiedRequest);
                    Set<String> generationTypes = new HashSet<String>();
                    generationTypes.add(gi.getType());
                    callAnalyzer(modifiedRequest, response, requestConfig,
                            generationTypes, gi.getModifiations());
                }

                //Combination
                Map<String, Integer> combinations = rcf.getCombinations();
                for (HashMap.Entry<String, Integer> e : combinations
                        .entrySet()) {
                    String cname = e.getKey();
                    generateCombinations(requestConfig, cname,
                            generateAllCombinationInputs(requestConfig, cname,
                            macroResponses), null, e.getValue());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(WebAppAttacker.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        System.out.println("GENERATE SUMMARY");
        reporter.generateSummary();
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(WebAppAttacker.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    /**
     * Mengisi bagian dari request yang memerlukan hasil eksekusi macro.
     * @param requestConfig
     * @param rc 
     */
    private void fillMacroParts(HTTPRequestConfig requestConfig,
            HTTPRequestContent rc) {
        MacroManager macroManager = new MacroManager(session);
                ArrayList<HTTPRequestConfig.MacroConfig> macrosConfig =
                        requestConfig.getMacrosConfig();
        ArrayList<MacroResponse> macroResponses = runMacros(macroManager,
                macrosConfig);

        // Method
        HTTPRequestPart methodRequestPart = rc.getMethod();
        getMacroContents(methodRequestPart, macroResponses);
        rc.setMethod(methodRequestPart);

        // URI
        HTTPRequestPart uriRequestPart = rc.getURI();
        getMacroContents(uriRequestPart, macroResponses);
        rc.setURI(uriRequestPart);

        // HTTP Version
        HTTPRequestPart httpVersionRequestPart = rc.getHttpVersion();
        getMacroContents(httpVersionRequestPart, macroResponses);
        rc.setHttpVersion(httpVersionRequestPart);

        ArrayList<HTTPRequestConfig.RequestHeadersConfig> headersConfig =
                        requestConfig.getHeadersConfig();
        for (int i = 0; i < headersConfig.size(); i++) {
            HTTPRequestConfig.RequestHeadersConfig headerConfig =
                    requestConfig.getHeadersConfig().get(i);

            RequestHeaderContent reqHeadCont = rc.getHeaders().get(i);

            // field
            HTTPRequestPart headerFieldConfig = reqHeadCont
                    .getHeaderField();
            getMacroContents(headerFieldConfig, macroResponses);
            reqHeadCont.setHeaderField(headerFieldConfig);

            // value
            HTTPRequestPart headerValueConfig = reqHeadCont
                    .getHeaderValue();
            getMacroContents(headerValueConfig, macroResponses);
            reqHeadCont.setHeaderValue(headerValueConfig);
        }

        //Body
        HTTPRequestPart bodyRequestPart = rc.getBody();
        getMacroContents(bodyRequestPart, macroResponses);
        rc.setBody(bodyRequestPart);
    }

    /**
     * Mendapatkan sekumpulan isi macro yang diperlukan.
     * @param rp
     *      Suatu bagian dari HTTP request.
     * @param macroResponses 
     *      Sekumpulan response dari macro yang telah dijalankan
     */
    
    private void getMacroContents(HTTPRequestPart rp,
            ArrayList<MacroResponse> macroResponses) {
        if (rp == null) {
            return;
        }
        ArrayList<Content> contents = rp.getContents();

        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < contents.size(); i++) {
            if(contents.get(i).getFuzzedPart() == null) {
                continue;
            }
            String generationType = contents.get(i).getFuzzedPart().getGenerationType()
                    .getType();
            if (generationType.matches("macro")) {
                String macroContent = getContentFromMacroResponses(macroResponses,
                        contents.get(i).getFuzzedPart().getGenerationType());
                rp.setModifiedInput(i, macroContent);
            }
        }
    }

    /**
     * Mendapatkan isi macro dari sekumpulan response macro.
     * @param macroResponses
     *      Sekumpulan response dari macro yang telah dijalankan
     * @param generationType
     *      Pembangkitan input
     * @return 
     */
    private String getContentFromMacroResponses(ArrayList<MacroResponse>
            macroResponses, Generation generationType) {
        String partContent = null;
        String macroId = (String) generationType.getOptions().get("macroId");
        for (int j = 0; j < macroResponses.size(); j++) {
            MacroResponse macroResponse = macroResponses.get(j);
            if (macroResponse.getId().equals(macroId)) {
                partContent = macroResponse.getPartContent();
                break;
            }
        }
        return partContent;
    }

    /**
     * Menjalankan sekumpulan macro.
     * @param macroManager
     *      Macro manager
     * @param macrosConfig
     *      Macro config
     * @return ArrayList<MacroResponse>
     *      Macro responses
     */
    private ArrayList<MacroResponse> runMacros(MacroManager macroManager,
            ArrayList<HTTPRequestConfig.MacroConfig> macrosConfig) {
        ArrayList<MacroResponse> macroResponses =
                        new ArrayList<MacroResponse>();
        if (macrosConfig == null) {
            return null;
        }
        for (int i = 0; i < macrosConfig.size(); i++) {
            MacroReader macroReader = new MacroReader(macroManager
                    .getMacroFolder() + macrosConfig.get(i).getFileName());
            ArrayList<HTTPRequest> macroRequests = macroReader
                    .getMacroRequests();
            HTTPResponse response = null;
            for (int j = 0; j < macroRequests.size(); j++) {
                response = sendHttpRequest(webClient,
                        macroRequests.get(j));
            }
            MacroResponse macroResponse;
            try {
                macroResponse = new MacroResponse(response,
                        macrosConfig.get(i));
                macroResponses.add(macroResponse);
            } catch (ParseException ex) {
                Logger.getLogger(WebAppAttacker.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        return macroResponses;
    }

    /**
     * Membangkitkan seluruh input yang memerlukan kombinasi.
     * @param rc
     *      Konfigurasi request
     * @param cname
     *      Nama kombinasi
     * @param macroResponses
     *      HTTPResponse macro yang telah dieksekusi
     * @return ArrayList<ArrayList<GeneratedInput>>
     *      Input yang dibangkitkan
     */
    private ArrayList<ArrayList<GeneratedInput>> generateAllCombinationInputs(
            HTTPRequestConfig rc, String cname, ArrayList<MacroResponse>
            macroResponses) {
        ArrayList<ArrayList<GeneratedInput>> generatedInputs =
                new ArrayList<ArrayList<GeneratedInput>>();

        // RequestLine
        generatedInputs.addAll(generateCombinationInputs(rc.getUriConfig(),
                cname, macroResponses));
        generatedInputs.addAll(generateCombinationInputs(rc.getMethodConfig(),
                cname, macroResponses));

        // Header
        for (int i = 0; i < rc.getHeadersConfig().size(); i++) {
            generatedInputs.addAll(generateCombinationInputs(rc
                    .getHeadersConfig().get(i).getHeaderField(), cname,
                    macroResponses));
            generatedInputs.addAll(generateCombinationInputs(rc
                    .getHeadersConfig().get(i).getHeaderValue(), cname,
                    macroResponses));
        }

        // Body
        if (rc.getBodyConfig() != null) {
            generatedInputs.addAll(generateCombinationInputs(rc.getBodyConfig(),
                cname, macroResponses));
        }

        return generatedInputs;
    }

    /**
     * Membangkitkan input yang memerlukan kombinasi pada suatu bagian tertentu
     * dari HTTP request.
     * @param rp
     *      Konfigurasi untuk suatu bagian request
     * @param cname
     *      Nama kombinasi
     * @param macroResponses
     *      HTTPResponse dari macro yang telah dieksekusi
     * @return ArrayList<ArrayList<GeneratedInput>>
     *      Input yang dibangkitkan
     */
    private ArrayList<ArrayList<GeneratedInput>> generateCombinationInputs(
            HTTPRequestPart rp, String cname, ArrayList<MacroResponse>
                    macroResponses) {
        ArrayList<ArrayList<GeneratedInput>> allGeneratedInputs =
                new ArrayList<ArrayList<GeneratedInput>>();

        ArrayList<Content> contents = rp.getContents();
        for (int i = 0; i < contents.size(); i++) {
            FuzzedPart fuzzedPart = contents.get(i).getFuzzedPart();
            if ((fuzzedPart == null) || (fuzzedPart.getGenerationType()
                    .getType().equals("macro"))) {
                continue;
            }
            String defaultValue = fuzzedPart.getDefaultValue();
            if ((fuzzedPart.getCombinationName() != null)
                    && (fuzzedPart.getCombinationName().equals(cname))) {
                try {
                    ArrayList<GeneratedInput> generatedInputs =
                            InputGenerator.generate(fuzzedPart
                            .getGenerationType(), defaultValue);
                    ArrayList<GeneratedPart> generatedParts =
                            new ArrayList<GeneratedPart>();
                    for (int j = 0; j < generatedInputs.size(); j++) {
                        GeneratedInput gi = generatedInputs.get(j);
                        String content = gi.getContent();
                        HTTPRequestPart rp2 = new HTTPRequestPart(rp);
                        rp2.setModifiedInput(i, content);
                        generatedParts.add(new GeneratedPart(gi, rp2));
                    }
                    allGeneratedInputs.add(generatedInputs);
                } catch (IOException ex) {
                    Logger.getLogger(WebAppAttacker.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        }

        return allGeneratedInputs;
    }

    /**
     * Membangkitkan input yang melibatkan kombinasi beberapa bagian request.
     * @param generatedInputs
     *      Input yang dihasilkan berdasarkan masing-masing mekanisme
     *      pembangkitan input yang ditetapkan
     * @param combinations
     *      Sekumpulan input yang dibangkitkan, masing-masing untuk suatu bagian
     *      yang memerlukan kombinasi.
     * @param n
     *      Jumlah bagian request yang terlibat
     */
    private void generateCombinations(HTTPRequestConfig rc, String cname,
            ArrayList<ArrayList<GeneratedInput>> generatedInputs,
            ArrayList<GeneratedInput> combinations, int n) {
        ArrayList<GeneratedInput> generatedParts = generatedInputs.get(
                generatedInputs.size() - n);

        if (n > 1) {
            for (int i = 0; i < generatedParts.size(); i++) {
                if (n == generatedInputs.size()) {
                    combinations = new ArrayList<GeneratedInput>();
                }
                combinations.add(generatedParts.get(i));
                generateCombinations(rc, cname, generatedInputs, combinations,
                        n - 1);
                combinations.remove(combinations.size() - 1);
            }
        } else if (n == 1) {
            if (combinations == null) {
                combinations = new ArrayList<GeneratedInput>();
            }
            for (int i = 0; i < generatedParts.size(); i++) {
                combinations.add(generatedParts.get(i));
                generateAndSendCombinationRequest(rc, cname, combinations);
                combinations.remove(combinations.size() - 1);
            }
        }
    }

    /**
     * Menghaslkan dan mengirim request hasil kombinasi.
     * @param rc
     *      Konfigurasi request
     * @param cname
     *      Nama kombinasi
     * @param combinations
     *      Sekumpulan input yang dibangkitkan, masing-masing untuk suatu bagian
     *      yang memerlukan kombinasi.
     */
    private void generateAndSendCombinationRequest(HTTPRequestConfig rc, String
            cname, ArrayList<GeneratedInput> combinations) {
        
        HTTPRequestContent requestContent = new HTTPRequestContent();

        HTTPRequestPart methodConfig = rc.getMethodConfig();
        generateCombinationRequestPart(methodConfig, cname, combinations);
        requestContent.setMethod(methodConfig);

        HTTPRequestPart uriConfig = rc.getUriConfig();
        generateCombinationRequestPart(uriConfig, cname, combinations);
        requestContent.setURI(uriConfig);

        HTTPRequestPart httpVersionConfig = rc.getHttpVersionConfig();
        generateCombinationRequestPart(httpVersionConfig, cname, combinations);
        requestContent.setHttpVersion(httpVersionConfig);

        generateCombinationRequestPart(
                rc.getMethodConfig(), cname, combinations);
        ArrayList<GeneratedInput> generatedHeaders =
                new ArrayList<GeneratedInput>();
        ArrayList<RequestHeader> requestHeaders =
                new ArrayList<RequestHeader>();
        ArrayList<HTTPRequestContent.RequestHeaderContent> headers =
                new ArrayList<HTTPRequestContent.RequestHeaderContent>();
        for (int i = 0; i < rc.getHeadersConfig().size(); i++) {
            HTTPRequestPart headerFieldConfig = rc.getHeadersConfig().get(i).getHeaderField();
            generateCombinationRequestPart(headerFieldConfig, cname, combinations);
            
            HTTPRequestPart headerValueConfig = rc.getHeadersConfig().get(i).getHeaderValue();
            generateCombinationRequestPart(headerValueConfig, cname, combinations);
        
            HTTPRequestContent.RequestHeaderContent rhc = new HTTPRequestContent
                    .RequestHeaderContent();
            rhc.setHeaderField(headerFieldConfig);
            rhc.setHeaderValue(headerValueConfig);
            headers.add(rhc);
        }
        requestContent.setHeaders(headers);

        HTTPRequestPart bodyConfig = rc.getBodyConfig();
        generateCombinationRequestPart(bodyConfig, cname, combinations);
        requestContent.setBody(bodyConfig);

        fillMacroParts(rc, requestContent);
        HTTPRequest request = requestContent.getRequest();
        HTTPResponse response = sendHttpRequest(webClient, request);
        Set<String> generationTypes = new HashSet<String>();
        for (GeneratedInput gi : combinations) {
            generationTypes.add(gi.getType());
        }
        callAnalyzer(request, response, rc, generationTypes, "");
    }

    /**
     * Menghasilkan suatu bagian request yang memerlukan kombinasi.
     * @param rp
     *      Konfigurasi untuk suatu bagian request
     * @param cname
     *      Nama kombinasi.
     * @param combinations
     *      Sekumpulan input yang dibangkitkan, masing-masing untuk suatu bagian
     *      yang memerlukan kombinasi.
     * @return GeneratedInput
     *      Input yang dibangkitkan.
     */
    private void generateCombinationRequestPart(HTTPRequestPart rp,
            String cname,
            ArrayList<GeneratedInput> combinations) {
        if (rp == null) {
            return;
        }
        ArrayList<Content> contents = rp.getContents();
//        String[] valueArr = new String[fuzzedParts.size()];
//        String content = rp.getContent();
        for (int i = 0; i < contents.size(); i++) {
            FuzzedPart fuzzedPart = contents.get(i).getFuzzedPart();
            if ((fuzzedPart == null) || (fuzzedPart.getCombinationName() == null)){
                continue;
            }
            if (fuzzedPart.getCombinationName().equals(cname)) {
                int cid = fuzzedPart.getCid();
                String content = combinations.get(cid - 1).getContent();
                rp.setModifiedInput(i, content);
            }
        }
    }

    /**
     * Memanggil analyzer.
     * @param request
     *      HTTPRequest yang dikirim
     * @param response
     *      HTTPResponse yang diperoleh
     * @param requestConfig
     *      Konfigurasi request
     * @param types
     *      Tipe-tipe pembangkitan input yang digunakan
     * @param modifications
     *      Modifikasi input yang digunakan
     */
    private void callAnalyzer(HTTPRequest request, HTTPResponse response, HTTPRequestConfig
            requestConfig, Set<String> types, String modifications) {
        ResponseAnalyzer analyzer = new ResponseAnalyzer(request, response, requestConfig, types);
        AnalysisResult analysisResult = analyzer.analyze();
        reporter.addReport(request, response, types, modifications,
                analysisResult);
    }

    /**
     * Menghasilkan sutau bagian request dengan bagian yang telah dimodifikasi
     * berdasarkan pembangkitan input yang digunakan.
     * @param rp
     *      Konfigurasi untu suatu bagian request
     * @param macroResponses
     *      Resposne dari macro yagn telah dieksekusi
     * @return ArrayList<GeneratedInput>
     *      Input yang dibangkitkan
     */
    private ArrayList<GeneratedPart> generateModifiedRequest(HTTPRequestPart rp,
            ArrayList<MacroResponse> macroResponses) {
        if (rp == null) {
            return new ArrayList<GeneratedPart>();
        }

        ArrayList<GeneratedPart> generatedPart =
                new ArrayList<GeneratedPart>();
        ArrayList<Content> contents = rp.getContents();

        for (int i = 0; i < contents.size(); i++) {
            FuzzedPart fp = contents.get(i).getFuzzedPart();
            if ((fp == null) || (fp.getGenerationType().getType()
                    .matches("macro"))) {
                continue;
            }
            ArrayList<GeneratedInput> replacements;
            try {
                String defaultValue = contents.get(i).getFuzzedPart()
                        .getDefaultValue();
                replacements = InputGenerator.generate(fp
                        .getGenerationType(), defaultValue);
                for (int j = 0; j < replacements.size(); j++) {
                    HTTPRequestPart rp2 = new HTTPRequestPart(rp);
                    rp2.setModifiedInput(i, replacements.get(j).getContent());
                    String text = rp2.getContentText();
                    generatedPart.add(new GeneratedPart(new GeneratedInput(text,
                            replacements.get(j).getType()), rp2));
                }
            } catch (IOException ex) {
                Logger.getLogger(WebAppAttacker.class.getName()).log(
                        Level.SEVERE, null, ex);
                return new ArrayList<GeneratedPart>();
            }
        }
        return generatedPart;
    }

    /**
     * Menghasilkan request default.
     * @param requestConfig
     *      Konfigurasi request
     * @return HTTPRequest
      HTTPRequest default
     */
    private HTTPRequestContent generateDefaultRequest(HTTPRequestConfig requestConfig) {
        // Method
        HTTPRequestPart method = requestConfig.getMethodConfig();

        // URI
        HTTPRequestPart uri = requestConfig.getUriConfig();

        // HTTP Version
        HTTPRequestPart httpVersion = requestConfig.getHttpVersionConfig();

        // Headers
        ArrayList<HTTPRequestContent.RequestHeaderContent> headers =
                new ArrayList<HTTPRequestContent.RequestHeaderContent>();
        for (int i = 0; i < requestConfig.getHeadersConfig().size(); i++) {
            HTTPRequestPart key = requestConfig.getHeadersConfig().get(i)
                    .getHeaderField();
            HTTPRequestPart value = requestConfig.getHeadersConfig().get(i)
                    .getHeaderValue();
            HTTPRequestContent.RequestHeaderContent requestHeader =
                    new HTTPRequestContent.RequestHeaderContent(key, value);
            headers.add(requestHeader);
        }

        // Body
        HTTPRequestPart body = requestConfig.getBodyConfig();

        return new HTTPRequestContent(method, uri, httpVersion, headers, body);
    }

    /**
     * Mengirim HTTP request.
     * @param webClient
     *      Instance WebClient
     * @param request
     *      HTTPRequest yang dikirim
     * @return HTTPResponse
      HTTPResponse yang diperoleh
     */
    private HTTPResponse sendHttpRequest(WebClient webClient, HTTPRequest request) {
        HttpMethod httpMethod;
        switch (request.getMethod()) {
            case "GET":
                httpMethod = HttpMethod.GET;
                break;
            case "POST":
                httpMethod = HttpMethod.POST;
                break;
            case "OPTIONS":
                httpMethod = HttpMethod.OPTIONS;
                break;
            case "DELETE":
                httpMethod = HttpMethod.DELETE;
                break;
            case "HEAD":
                httpMethod = HttpMethod.HEAD;
                break;
            case "PATCH":
                httpMethod = HttpMethod.PATCH;
                break;
            case "PUT":
                httpMethod = HttpMethod.PUT;
                break;
            case "TRACE":
                httpMethod = HttpMethod.TRACE;
                break;
            default:
                return null;
        }

        URL url;
        try {
            url = new URL(request.getURI());
            WebRequest requestSettings = new WebRequest(url, httpMethod);

            ArrayList<RequestHeader> requestHeaders = request.getHeaders();
            for (int i = 0; i < requestHeaders.size(); i++) {
                String key = requestHeaders.get(i).getHeaderField();
                String value = requestHeaders.get(i).getHeaderValue();
                if ((!key.toLowerCase().equals("host")) &&
                        (!key.toLowerCase().equals("content-length"))) {
                    requestSettings.setAdditionalHeader(key, value);
                }
            }

            if (httpMethod.equals(httpMethod.POST) || httpMethod.equals(
                    httpMethod.PUT) || httpMethod.equals(httpMethod.TRACE)) {
                requestSettings.setRequestBody(request.getBody());
            }

            CollectingAlertHandler alertHandler = new CollectingAlertHandler();
            webClient.setAlertHandler(alertHandler);

            Page htmlUnitResponse = webClient.getPage(requestSettings);

            // Update headers of HTTPRequest because HtmlUnit may send different/
            // additional headers
            Map<String, String> headers = requestSettings
                    .getAdditionalHeaders();
            ArrayList<HTTPRequest.RequestHeader> updatedHeaders =
                    new ArrayList<HTTPRequest.RequestHeader>();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                updatedHeaders.add(new RequestHeader(entry.getKey(),
                        entry.getValue()));
            }
            request.setHeaders(updatedHeaders);

            int statusCode = htmlUnitResponse.getWebResponse().getStatusCode();
    //        String httpVersion = response.getWebResponse()
            String reasonPhrase = htmlUnitResponse.getWebResponse()
                    .getStatusMessage();
            String body = htmlUnitResponse.getWebResponse()
                    .getContentAsString();
            String lastURI = htmlUnitResponse.getUrl().toString();
            List<NameValuePair> htmlUnitResponseHeaders = htmlUnitResponse
                    .getWebResponse().getResponseHeaders();
            ArrayList<ResponseHeader> responseHeaders =
                    new ArrayList<ResponseHeader>();
            for (int i = 0; i < htmlUnitResponseHeaders.size(); i++) {
                ResponseHeader responseHeader = new ResponseHeader(
                htmlUnitResponseHeaders.get(i).getName(),
                        htmlUnitResponseHeaders.get(i).getValue());
                responseHeaders.add(responseHeader);
            }

            List<String> alertMsgs = new ArrayList<String>();   
            alertMsgs = alertHandler.getCollectedAlerts();

            HTTPResponse response = new HTTPResponse();
            response.setHttpVersion(null);
            response.setStatusCode(statusCode);
            response.setReasonPhrase(reasonPhrase);
            response.setBody(body);
            response.setLastUri(lastURI);
            response.setResponseHeaders(responseHeaders);
            response.setAlerts(new ArrayList<String>(alertMsgs));
            return response;
        } catch (SocketTimeoutException ex) {
            HTTPResponse response = new HTTPResponse();
            response.setIsTimeout(true);
            return response;
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebAppAttacker.class.getName()).log(
                    Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(WebAppAttacker.class.getName()).log(
                    Level.SEVERE, null, ex);
            return null;
        } catch (Exception ex) {
            Logger.getLogger(WebAppAttacker.class.getName()).log(
                    Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Main. Untuk dijalankan oleh Jenkins.
     * @param args
     *      Argumen
     */
    public static void main(String[] args) {
        System.out.println("Running attacker...");
        WebAppAttacker attacker = new WebAppAttacker(args[0], args[1]);
        attacker.run();
//        mvn exec:java -Dexec.mainClass="com.ivanandrianto.tugas.WebAppAttacker"
//         -Dexec.args="tes output.txt"
    }
}
