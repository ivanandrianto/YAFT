/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import org.json.simple.JSONObject;

/**
 * Fuzzed Part Class.
 * @author ivanandrianto
 */
public class FuzzedPart {

    /**
     * Generation Class.
     */
    public static class Generation {
        private String type;
        private JSONObject options;

        /**
         * Constructor.
         * @param type
         *      Tipe pembangkitan input
         * @param options
         *      Opsi untuk pembangkitan input
         */
        public Generation(String type, JSONObject options) {
            this.type = type;
            this.options = new JSONObject(options);
        }

        public Generation(Generation g) {
            this.type = g.type;
            this.options = g.options;
        }

        /**
         * Mendapatkan tipe pembangkitan input.
         * @return String
         *      Tipe pembangkitan input
         */
        public String getType() {
            return type;
        }

        /**
         * Mendapatkan opsi untuk pembangkitan input.
         * @return JSONObject
         *      Opsi untuk pembangkitan input
         */
        public JSONObject getOptions() {
            return options;
        }
    }

    private String defaultValue;
    private Generation generation;
    private String cname;
    private int cid;

    /**
     * Constructor.
     * @param defaultValue
     *      Value default
     * @param generation
     *      Tipe pembangkitan input yang akan diterapkan
     */
    public FuzzedPart(String defaultValue, Generation generation) {
        this.defaultValue = defaultValue;
        this.generation = generation;
    }

    /**
     * Copy Constructor.
     * @param defaultValue
     *      Value default
     * @param generation
     *      Tipe pembangkitan input yang akan diterapkan
     */
    public FuzzedPart(FuzzedPart fp) {
        this.defaultValue = fp.defaultValue;
        this.generation = new Generation(fp.getGenerationType());
        this.cname = cname;
        this.cid = cid;
    }

    /**
     * Mendapatkan default value.
     * @return String
     *      Value default
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Mendapatkan pembangkitan yang akan diterapkan.
     * @return Generation
     *      Tipe pembangkitan input yang akan diterapkan
     */
    public Generation getGenerationType() {
        return generation;
    }

    /**
     * Menetapkan nama kombinasi apabila bagian tersebut ingin dikombinasikan
     * dengan bagian lain.
     * @param cname
     *      Nama kombinasi
     */
    public void setCombinationName(String cname) {
        this.cname = cname;
    }

    /**
     * Mendapatkan nama kombinasi apabila bagian tersebut ingin dikombinasikan
     * dengan bagian lain.
     * @return String
     *      Nama kombinasi
     */
    public String getCombinationName() {
        return cname;
    }

    /**
     * Menetapkan ID unik untuk suatu bagian yang ingin dikombinasikan dengan
     * bagian lain.
     * @param cid
     *      ID kombinasi
     */
    public void setCid(int cid) {
        this.cid = cid;
    }

    /**
     * Mendapatkan ID unik dari suatu bagian yang ingin dikombinasikan dengan
     * bagian lain.
     * @return int
     *      ID kombinasi
     */
    public int getCid() {
        return cid;
    }
}
