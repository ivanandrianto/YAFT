/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.inputGenerator;

/**
 * Generated Input Class.
 * @author ivanandrianto
 */
public class GeneratedInput {
    private String content;
    private String type;
    private String modifications;

    /**
     * Constructor.
     * @param content
     *      Mendapatkan isi dari input yang dibangkitkan.
     * @param type
     *      Tipe pembangkitan yang diguanakan
     */
    public GeneratedInput(String content, String type) {
        this.content = content;
        this.type = type;
        this.modifications = "";
    }

    /**
     * Menetapkan isi dari input yang dibangkitkan.
     * @param content
     *      Isi dari input yang dibangkitkan
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Mendapatkan isi dari input yang dibangkitkan.
     * @return String
     *      Isi dari input yang dibangkitkan
     */
    public String getContent() {
        return content;
    }

    /**
     * Mendapatkan tipe pembangkitan input yang diguanakan.
     * @return String
     *      Tipe pembangkitan yang diguanakan
     */
    public String getType() {
        return type;
    }

    /**
     * Menambahkan modifikasi yang diterapkan.
     * @param modification
     *      Modifikasi yang diterapkan
     */
    public void addModifications(String modification) {
        modifications += "\n" + modification;
    }

    /**
     * Mendapatkan modifikasi yang telah diterapkan.
     * @return String
     *      Modifikasi yang telah diterapkan
     */
    public String getModifiations() {
        return modifications;
    }
}
