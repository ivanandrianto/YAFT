/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import ivanandrianto.yaft.attacker.FuzzedPart;
import java.util.ArrayList;

/**
 * Request Part Class.
 * @author ivanandrianto
 */
public class HTTPRequestPart {
    public static class Content {
        private String text;
        private FuzzedPart fuzzedPart;

        public Content(String text, FuzzedPart fuzzedPart) {
            this.text = text;
            this.fuzzedPart = fuzzedPart;
        }

        public Content(Content c) {
            this.text = c.text;
            if (c.getFuzzedPart() != null) {
                this.fuzzedPart = new FuzzedPart(c.getFuzzedPart());
            }
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public FuzzedPart getFuzzedPart() {
            return fuzzedPart;
        }
    }

    private String content;
    private ArrayList<Content> contents;
    private ArrayList<FuzzedPart> fuzzedParts;

    /**
     * Constructor.
     * @param content
     *      Value dari suatu bagian HTTP request
     * @param fuzzedParts
     *      Bagian yang perlu di-fuzz dari suatu bagian HTTP request
     */
    public HTTPRequestPart(String content, ArrayList<FuzzedPart>
            fuzzedParts, ArrayList<Content> contents) {
        this.content = content;
        this.fuzzedParts = fuzzedParts;
        this.contents = contents;
    }

    public HTTPRequestPart(HTTPRequestPart rp) {
        contents = new ArrayList<Content>();
        for (int i = 0; i < rp.contents.size(); i++) {
            contents.add(new Content(rp.contents.get(i)));
        }
        rp.fuzzedParts = fuzzedParts;
    }

//    /**
//     * Mendapatkan isi dari suatu bagian HTTP request.
//     * @return String
//     *      Isi dari suatu bagian HTTP request
//     */
//    public String getContent() {
//        return content;
//    }

//    /**
//     * Mendapatkan bagian yang perlu di-fuzz dari suatu bagian HTTP request.
//     * @return ArrayList<FuzzedPart>
//     *      Bagian yang perlu di-fuzz dari suatu bagian HTTP request
//     */
//    public ArrayList<FuzzedPart> getFuzzedParts() {
//        return fuzzedParts;
//    }

    /**
     * Get Text.
     * @return 
     */
    public String getContentText() {
        String s = "";
        for (int i = 0; i < contents.size(); i++) {
            Content c = contents.get(i);
            if (c.getText() != null) {
                s += c.getText();
            } else if (c.getFuzzedPart() != null) {
                s += c.getFuzzedPart().getDefaultValue();
            }
        }
        return s;
    }

    /**
     * Set modified input.
     * @param idx
     * @param replacement
     * @return 
     */
    public void setModifiedInput(int idx, String replacement) {
        int found = 0;
        for (int i = 0; i < contents.size(); i++) {
            
            Content c = contents.get(i);
//            if (c.getText() == null) {
                if (i == idx) {
                    c.setText(replacement);
                }
                found++;
//            }
        }
    }

    /**
     * Get modified input.
     * @param idx
     * @param replacement
     * @return 
     */
    public String getModifiedInput(int idx, String replacement) {
        String s = "";
        int found = 0;
        for (int i = 0; i < contents.size(); i++) {
            Content c = contents.get(i);
            if (c.getFuzzedPart() == null) {
                s += c.getText();
            } else if (c.getText() == null) {
                if (found == idx) {
                    s += replacement;
                } else {
                    s += c.getFuzzedPart().getDefaultValue();
                }
                found++;
            }
        }
        return s;
    }

    public ArrayList<Content> cloneContents() {
        ArrayList<Content>  newContents = new ArrayList<Content>();
        for (int i = 0; i < contents.size(); i++) {
            Content c = new Content(contents.get(i));
            newContents.add(c);
        }
        return newContents;
    }

    public ArrayList<Content> getContents() {
        return contents;
    }
}
