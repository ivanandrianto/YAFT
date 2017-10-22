    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import ivanandrianto.yaft.inputGenerator.GeneratedInput;
import ivanandrianto.yaft.attacker.HTTPRequestPart.Content;
import java.util.ArrayList;

/**
 *
 * @author ivanandrianto
 */
public class GeneratedPart {
    private GeneratedInput generatedInput;
    private HTTPRequestPart contents;

    /**
     * Constructor.
     * @param generatedInput
     *      Input yang dibangkitkan
     * @param contents 
     *      Isi dari suatu bagian HTTP request
     */
    public GeneratedPart(GeneratedInput generatedInput, HTTPRequestPart contents) {
        this.generatedInput = generatedInput;
        this.contents = contents;
    }

    /**
     * Memperoleh input yang dibangkitkan.
     * @return GeneratedInput
     *      Input yang dibangkitkan
     */
    public GeneratedInput getGeneratedInput() {
        return generatedInput;
    }

    /**
     * Memperoleh isi dari suatu bagian HTTP request.
     * @return HTTPRequestPart
     *      Isi dari suatu bagian HTTP request
     */
    public HTTPRequestPart getContents() {
        return contents;
    }
}
