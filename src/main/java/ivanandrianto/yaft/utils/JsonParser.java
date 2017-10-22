/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * JSON Parser class.
 * @author ivanandrianto
 */
public class JsonParser {
    private static String content;

    /**
     * Constructor.
     * @param content
     *      Dokumen JSON
     */
    public JsonParser(String content) {
        this.content = content;
    }

    /**
     * Mem-parse sebuah file JSON untuk mendapatkan isi dari elemen tertentu.
     * @param part
     *      Elemen dari sebuah JSON yang ingin diambil
     * @return String
     *      Isi dari elemen
     * @throws ParseException
     *      Apabila parsing gagal
     */
    public String parse(String part) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(content);
        String parsedContent = null;
        String[] splitPart = part.split("\\.");
        for (int i = 0; i < splitPart.length; i++) {
            String str = splitPart[i];
            if (str.matches(".*[\\[]\\d+[\\]]$")) {
                int idx = str.lastIndexOf("[");
                String name = str.substring(0, idx);

                Pattern p = Pattern.compile("\\[(\\d+?)\\]$");
                Matcher m = p.matcher(str);
                m.find();
                int arrIdx = Integer.parseInt(m.group(1));

                JSONArray jsonArray = (JSONArray) jsonObject.get(name);
                if (i == splitPart.length - 1) {
                    parsedContent = (String) jsonArray.get(arrIdx);
                } else {
                    jsonObject = (JSONObject) jsonArray.get(arrIdx);
                }
            } else {
                if (i == splitPart.length - 1) {
                    parsedContent = (String) jsonObject.get(str);
                } else {
                    jsonObject = (JSONObject) jsonObject.get(str);
                }
            }
        }
        return parsedContent;
    }

}
