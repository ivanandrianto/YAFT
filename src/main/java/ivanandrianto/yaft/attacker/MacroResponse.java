/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import ivanandrianto.yaft.attacker.HTTPRequestConfig.MacroConfig;
import ivanandrianto.yaft.utils.JsonParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Macro HTTPResponse Class.
 * @author ivanandrianto
 */
public class MacroResponse {
    private String id;
    private String response;
    private String partContent;

    /**
     * Constructor.
     * @param response
     *      HTTPResponse dari macro
     * @param macroConfig
     *      Konfigurasi macro
     * @throws ParseException
     *      Apabila terjadi kesalahan ketika memparsing response dari macro.
     */
    public MacroResponse(HTTPResponse response, MacroConfig macroConfig)
            throws ParseException {
            this.id = macroConfig.getId();

            if ((response != null) && (response.getBody() != null)) {
                String part = macroConfig.getPart();
            if (part != null) {
                String[] splitPart = part.split(":", 2);
                if (splitPart[0].toLowerCase().equals("json")) {
                    JsonParser jsonParser = new JsonParser(response.getBody());
                    partContent = jsonParser.parse(splitPart[1]);
                } else if (splitPart[0].toLowerCase().equals("html")) {
                    String body = response.getBody();
                    Document doc = Jsoup.parse(body);
                    partContent = doc.select(splitPart[1]).text();
                }
            }
        }
    }

    /**
     * Mendapatkan ID dari macro.
     * @return String
     *       ID dari macro
     */
    public String getId() {
        return id;
    }

    /**
     * Mendapatkan response dari macro.
     * @return String
            HTTPResponse dari macro
     */
    public String getResponse() {
        return response;
    }

    /**
     * Mendapatkan isi dari hasil response macro yang ingin diperoleh.
     * @return
     *      Isi dari hasil response macro yang ingin diperoleh
     */
    public String getPartContent() {
        return partContent;
    }
}
