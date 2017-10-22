/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.requestCollector;

import ivanandrianto.yaft.attacker.HTTPRequest;
import ivanandrianto.yaft.utils.FileUtil;
import de.sstoehr.harreader.HarReader;
import de.sstoehr.harreader.HarReaderException;
import de.sstoehr.harreader.model.HarHeader;
import de.sstoehr.harreader.model.HarPostDataParam;
import de.sstoehr.harreader.model.HarRequest;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.openqa.selenium.NoSuchWindowException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * HTTPRequest Collector Class.
 * @author ivanandrianto
 */
public class HTTPRequestCollector {

    private int count;
    private String currentTime;
    private String session;
    private String homeUrl;
    private static String collectedRequestsFolder;
    private boolean stop;
    private final String CHROME_DRIVER = "chromedriver.exe";

    /**
     * Constructor.
     * @param project
     *      Nama session
     */
    public HTTPRequestCollector(String project) {
        count = 0;
        this.session = project;
        collectedRequestsFolder = "projects/" + project + "/collectedRequests/";
        stop = false;
    }

    /**
     * Menghapus satu atau beberapa file request yang telah dikumpulkan.
     * @param fileNames
     *      Daftar file yang ingin dihapus
     * @return boolean
     *      Berhasil atau tidak
     */
    public boolean delete(ArrayList<String> fileNames) {
        boolean ret = true;
        for (int i = 0; i < fileNames.size(); i++) {
            String filePath = collectedRequestsFolder + fileNames.get(i);
            if (!FileUtil.delete(filePath)) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Menjalankan browser untuk mengumpulkan request.
     * @param browser
     *      Browser yang digunakan
     * @param url
     *      URL yang dibuka ketika browser dijalakan
     * @param urlFilter
     *      Filter untuk hanya menyimpan URL tertentu
     * @throws Exception
     *      Apabila terjadi kesalahan
     */
    public void run(String browser, String url, String urlFilter)
            throws Exception {

        String h = url;
        if ((h == null) || (url.isEmpty())) {
            h = "http://localhost";
        }
        this.homeUrl = h;

//        System.setProperty("selenide.browser", "com.ivanandrianto.tugas"
//                + ".requestCollector.BmpFirefox");
        Har har = null;
        BrowserMobProxyServer bmp = null;
        bmp = new BrowserMobProxyServer();
        bmp.setHarCaptureTypes(CaptureType.getRequestCaptureTypes());
        bmp.start(0);
//        bmp.setHarCaptureTypes(CaptureType.REQUEST_HEADERS);
        bmp.newHar("example.com");
        boolean start = true;

        while (!stop) {
            try {
                if (start) {
                    start = false;
                    WebDriver driver;
                    if (browser.equals("Firefox")) {
//                        System.setProperty("webdriver.gecko.driver",
//                                pathToDriver);
                        DesiredCapabilities capabilities = DesiredCapabilities
                                .firefox();
                        capabilities.setCapability(CapabilityType.PROXY,
                                ClientUtil.createSeleniumProxy(
                                        bmp));
                        driver = new FirefoxDriver(capabilities);
                    } else if (browser.equals("Chrome")) {
//                        System.setProperty("webdriver.chrome.driver",
//                                "C:\\Users\\ivanandrianto\\Downloads\\"
//                                + "chromedriver_win32 (1)\\chromedriver.exe");
                        System.setProperty("webdriver.chrome.driver",
                                CHROME_DRIVER);
                        DesiredCapabilities capabilities = DesiredCapabilities
                                .chrome();
                        capabilities.setCapability(CapabilityType.PROXY,
                                ClientUtil.createSeleniumProxy(
                                        bmp));
                        driver = new ChromeDriver(capabilities);
                    } else {
                        break;
                    }
                    driver.navigate().to(this.homeUrl);
                }
                while (!stop) {
                    if (stop == true) {
                        break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("Request collector stoopped");
            } catch (NoSuchWindowException e) {
                stop = true;
                currentTime = String.valueOf(System.currentTimeMillis());
                createCollectedRequestsDirectory();
                String filePath =  collectedRequestsFolder + "/"
                        + "collectedRequests.har";
                har = bmp.getHar();
                har.writeTo(new File(filePath));
                bmp.stop();
                readHar(filePath, urlFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentTime = String.valueOf(System.currentTimeMillis());
        createCollectedRequestsDirectory();
        String filePath =  collectedRequestsFolder + "/"
                + "collectedRequests.har";
        har = bmp.getHar();
        har.writeTo(new File(filePath));
        bmp.stop();
        readHar(filePath, urlFilter);
    }

    /**
     * Menghentikan proses pengumpulan HTTP request.
     */
    public void stop() {
        System.out.println("Request Collector stopped");
        stop = true;
    }

    /**
     * Membaca file .har yang dihasilkan.
     * @param harFilePath
     *      Path menuju file .har
     * @param urlFilter
     *      Filter untuk hanya menyimpan URL tertentu
     * @throws HarReaderException
     *      Apabila gagal membaca file .har
     */
    private void readHar(String harFilePath, String urlFilter)
            throws HarReaderException {
        HarReader harReader = new HarReader();
        de.sstoehr.harreader.model.Har har = harReader.readFromFile(
                new File(harFilePath));

        for (int i = 0; i < har.getLog().getEntries().size(); i++) {
            HarRequest harRequest = har.getLog().getEntries().get(i)
                    .getRequest();
            String method = harRequest.getMethod().toString();
            String uri = harRequest.getUrl();
            if ((urlFilter == null) || (urlFilter.isEmpty())
                    || ((!urlFilter.isEmpty()) && (uri.matches(urlFilter)))) {
                String httpVersion = harRequest.getHttpVersion().toString();

                List<HarHeader> harHeaders = harRequest.getHeaders();
                ArrayList<HTTPRequest.RequestHeader> headers =
                        new ArrayList<HTTPRequest.RequestHeader>();
                for (int j = 0; j < harHeaders.size(); j++) {
                    HTTPRequest.RequestHeader requestHeader = new HTTPRequest
                            .RequestHeader(harHeaders.get(j).getName(),
                            harHeaders.get(j).getValue());
                    headers.add(requestHeader);
                }

                String body = null;
                if (method.equals("POST")) {
                    body = harRequest.getPostData().getText();

                    if (body == null) {
                        List<HarPostDataParam> params =
                            harRequest.getPostData().getParams();
                        if (params != null) {
                            body = "";
                            for (int j = 0; j < params.size(); j++) {
                                HarPostDataParam param = params.get(j);
                                try {
                                    String charset = "UTF-8";
                                    String name = URLEncoder.encode(
                                            param.getName(), charset);
                                    String value = URLEncoder.encode(
                                            param.getValue(), charset);
                                    if (j != 0) {
                                    body += "&";
                                }
                                body += name + "=" + value;
                                } catch (UnsupportedEncodingException ex) {
                                    Logger.getLogger(HTTPRequestCollector.class
                                            .getName()).log(Level.SEVERE,
                                            null, ex);
                                }
                                
                            }
                        }
                    }
                }

                HTTPRequest request = new HTTPRequest(method, uri, httpVersion, headers,
                        body);

                saveRequest(request);
            }
        }
    }

    /**
     * Menyimpan suatu request ke dalam suatu file.
     * @param request
     *      Objek HTTPRequest
     */
    private void saveRequest(HTTPRequest request) {
        count++;

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("yaft");
            doc.appendChild(rootElement);

            Element requestElement = doc.createElement("request");
            rootElement.appendChild(requestElement);

            // requestline
            Element requestLine = doc.createElement("requestline");
            requestElement.appendChild(requestLine);

            Element method = doc.createElement("method");
            method.appendChild(doc.createTextNode(request.getMethod()));
            requestLine.appendChild(method);

            Element uri = doc.createElement("uri");
            uri.appendChild(doc.createTextNode(request.getURI()));
            requestLine.appendChild(uri);

            Element httpVersion = doc.createElement("httpVersion");
            httpVersion.appendChild(doc.createTextNode(request
                    .getHttpVersion()));
            requestLine.appendChild(httpVersion);

            // headers
            Element headers = doc.createElement("headers");
            requestElement.appendChild(headers);

            ArrayList<HTTPRequest.RequestHeader> requestHeaders = request
                    .getHeaders();
            for (int i = 0; i < requestHeaders.size(); i++) {
                Element header = doc.createElement("header");
                headers.appendChild(header);
                Element key = doc.createElement("key");
                Element value = doc.createElement("value");
                header.appendChild(key);
                header.appendChild(value);

                String keyStr = requestHeaders.get(i).getHeaderField();
                String valueStr = requestHeaders.get(i).getHeaderValue();
                key.appendChild(doc.createTextNode(keyStr));
                value.appendChild(doc.createTextNode(valueStr));
            }

            // message-body
            String body = request.getBody();
            if ((body != null) && (body.length() > 0)) {
                Element messageBody = doc.createElement("body");
                messageBody.appendChild(doc.createTextNode(body));
                requestElement.appendChild(messageBody);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}"
                    + "indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(
                    collectedRequestsFolder + currentTime + "_" + count
                    + ".xml"));

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    /**
     * Membentuk direktori untuk menampung request yang dikumpulkan apabila
     * direktori belum terbentuk.
     */
    private static void createCollectedRequestsDirectory() {
        File newDirectory = new File(collectedRequestsFolder);
        if (!newDirectory.exists()) {
            newDirectory.mkdir();
        }
    }

   /**
     * Membaca isi dari sebuah file yang berisi suatu request yang telah
     * dikumpulkan.
     * @param fileName
     *      Nama dari file yang berisi suatu request yang telah dikumpulkan
     * @return String
     *      Isi dari file
     * @throws IOException
     *      Apabila terjadi kesalahan ketika membaca file
     */
    public String readCollectedRequest(String fileName)
            throws IOException {
        String filePath = collectedRequestsFolder + fileName;
        return FileUtil.read(filePath);
    }

}
