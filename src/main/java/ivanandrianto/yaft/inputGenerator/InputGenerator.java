/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.inputGenerator;

import ivanandrianto.yaft.configurator.FuzzVectorManager;
import ivanandrianto.yaft.attacker.FuzzedPart.Generation;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Input Generator Class.
 * @author ivanandrianto
 */
public final class InputGenerator {

    private static final String[] INPUT_GENERATIONS_FOR_VULNERABILITIES = {
        "SQL", "activeSQL", "passiveSQL", "LDAP", "pathTraversal", "XSS", "XML",
        "XPath", "SSI"};
//    private static final String[] INPUT_GENERATIONS_BY_MUTATION = {
////        "prefix\\s*\\(\\s*'.+'\\s*\\)$",                  //0
////        "suffix\\s*\\(\\s*'.+'\\s*\\)$",                  //1
////        "replace\\s*\\(\\s*'.+'\\s*,\\s*'.+'\\s*\\)$",  //2
////        "subString\\s*\\(\\s*\\d+\\s*,\\s*\\d+\\s*\\)$",        //3
////        "reverseSubString\\s*\\(\\s*\\d+\\s*,\\s*\\d+\\s*\\)$", //4
////        "lowerCase",                                        //5
////        "upperCase"                                         //6
//    };
    private static final String[] GENERATE_BY_BRUTE_FORCE = {
        "alphabet", "numeric", "alphanumeric", "base64", "base32"
    };
    private static final String GENERATE_SIMILAR_STRINGS = "similarStrings";
    private static final String GENERATE_NUMBERS = "similarStrings";
    private static final String[] MODIFICATION_OPTIONS = {
        "replace\\s*\\(\\s*'.+'\\s*,\\s*'.+'\\s*\\)$",  //0
        "lowerCase",                                    //1
        "upperCase",                                    //2
        "encode\\s*\\(\\s*'.+'\\s*\\)$"   //3
    };

    /**
     * Prevent instantiation.
     */
    private InputGenerator() {

    }

    /**
     * Method ini berfungsi untuk menghasilkan sekumpulan input.
     * @param generation
     *      Tipe dan opsi pembangkitan
     * @param defaultValue
     *      Value default
     * @return ArrayList<GeneratedInput>
     *      Input yang dibangkitkan
     * @throws IOException
     *      Apabila terjadi kesalahan ketika membaca file
     */
    public static ArrayList<GeneratedInput> generate(Generation generation,
            String defaultValue)
            throws IOException {
        ArrayList<GeneratedInput> generatedInput =
                new ArrayList<GeneratedInput>();
        JSONObject generationOptions = generation.getOptions();
        String delim = "|";
        String regex = "(?<!\\\\)" + Pattern.quote(delim);
        String[] generationTypes = generation.getType().split(regex);
        for (int i = 0; i < generationTypes.length; i++) {
            String generationType = generationTypes[i].trim();
            if (matchesIgnoreCase(INPUT_GENERATIONS_FOR_VULNERABILITIES,
                    generationType)) {
                generatedInput.addAll(generateVulnerabilitySpecificInputs(
                        generationType));
            } else if (matchesIgnoreCase(
                    GENERATE_BY_BRUTE_FORCE, generationType)) {
                generatedInput.addAll(generateBruteForceStrings(
                        generationType, generationOptions));
            } else if (generationType.matches("(?i)"
                    + GENERATE_SIMILAR_STRINGS)) {
              generatedInput.addAll(generateSimilarStrings(defaultValue));
            } else if (generationType.matches("(?i)"
                    + GENERATE_NUMBERS)) {
                generatedInput.addAll(generateNumbers(generationOptions));
            } else {
                generatedInput.addAll(generateVulnerabilitySpecificInputs(
                        generationType));
            }
        }

        System.out.println("GENERATED INPUTS: " + generatedInput.size());
        for (int i = 0; i < generatedInput.size(); i++) {
//            System.out.println("Generated input: " + generatedInput.get(i)
//                    .getContent());
        }

        //Apply modifications
        if (generationOptions.get("modifications") != null) {
//            if ((defaultValue != null) && (defaultValue.length() > 0)) {
//                generatedInput.add(0, new GeneratedInput(defaultValue,
//                        "DEFAULT"));
//            }
            generatedInput = applyModificationOptions(generatedInput,
                generationOptions);
        }

        return generatedInput;
    }

    /**
     * Memeriksa apakah suatu pembangkitan input termasuk ke dalam kategori
     * tertentu.
     * @param arr
     *      Array yang berisi nama pembangkitan input yang didukung
     * @param generationType
     *      Tipe pembangkitan input yang ditetapkan pada RCF
     * @return boolean
     *      Benar apabila generationType sesuai dengan salah satu elemen di arr
     */
    private static boolean matchesIgnoreCase(String[] arr, String
            generationType) {
        boolean found = false;
        for (int i = 0; i < arr.length; i++) {
            if (generationType.matches("(?i)" + arr[i])) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Memeriksa apakah suatu apakah suatu String sesuai dengan salah satu
     * pembangkitan input.
     * @param generationType
     *      Tipe pembangkitan input.
     * @return boolean
     *      Matches or not
     */
    public static boolean matchAnyInputGeneration(String generationType) {
        if ((matchesIgnoreCase(INPUT_GENERATIONS_FOR_VULNERABILITIES,
                generationType))
                || (matchesIgnoreCase(
                        GENERATE_BY_BRUTE_FORCE, generationType))
//                || (matchesIgnoreCase(
//                        INPUT_GENERATIONS_BY_MUTATION, generationType))
                || (generationType.matches("(?i)" + GENERATE_SIMILAR_STRINGS))
                || (generationType.matches("(?i)" + GENERATE_NUMBERS))
                || (generationType.matches("macro"))) {
            return true;
        }
        return false;
    }

    /**
     * Membangkitkan input yang spesifik untuk suatu jenis celah keamanan.
     * @param generationType
     *      Tipe pembangkitan input yang ditetapkan pada RCF
     * @return ArrayList<GeneratedInput>
     *      Sekumpulan input dari fuzz vector
     */
    private static ArrayList<GeneratedInput>
        generateVulnerabilitySpecificInputs(String generationType) {
        ArrayList<GeneratedInput> generatedInput =
                new ArrayList<GeneratedInput>();

        switch (generationType.toLowerCase()) {
            case "sql":
                ArrayList<GeneratedInput> activeSQL = readFuzzVector(
                        "activeSQL", "Active SQL Injection");
                ArrayList<GeneratedInput> passiveSQL = readFuzzVector(
                        "passiveSQL", "Passive SQL Injection");
                activeSQL.addAll(passiveSQL);
                return activeSQL;
            case "activesql":
                return readFuzzVector("activeSQL", "Active SQL Injection");
            case "passivesql":
                return readFuzzVector("passiveSQL", "Passive SQL Injection");
            case "xss":
                return readFuzzVector("XSS", "XSS");
            case "ldap":
                return readFuzzVector("LDAP", "LDAP Injection");
            case "pathtraversal":
                return readFuzzVector("pathTraversal", "Path Traversal");
            case "xml":
                return readFuzzVector("XML", "XML Injection");
            case "xpath":
                return readFuzzVector("XPath", "XPath Injection");
            case "ssi":
                return readFuzzVector("SSI", "SSI Injection");
            default:
                return readFuzzVector(generationType, generationType);
        }
    }

//    /**
//     * Membangkitkan input dengan mutasi.
//     * @param generationType
//     *      Tipe pembangkitan input yang ditetapkan pada RCF
//     * @param defaultValue
//     *      Value default sebelum dilakukan mutasi
//     * @return ArrayList<GeneratedInput>
//     *      Input yang dihasilkan dari mutasi
//     */
//    private static ArrayList<GeneratedInput> generateByMutation(String
//            generationType, String defaultValue) {
//        ArrayList<GeneratedInput> generatedInput =
//                new ArrayList<GeneratedInput>();
//
//        if (generationType.matches("(?i)"
//                + INPUT_GENERATIONS_BY_MUTATION[0])) {
//            // Prefix
//            Pattern p = Pattern.compile("(?i)prefix\\s*\\(\\s*'(.+?)'"
//                    + "\\s*\\)$");
//            Matcher m = p.matcher(generationType);
//            m.find();
//            String prefix = m.group(1);
//            String newContent = prefix + defaultValue;
//            generatedInput.add(new GeneratedInput(newContent,
//                    generationType));
//        } else if (generationType.matches("(?i)"
//                + INPUT_GENERATIONS_BY_MUTATION[1])) {
//            // Suffix
//            Pattern p = Pattern.compile("(?i)suffix\\s*\\(\\s*'(.+?)'"
//                    + "\\s*\\)$");
//            Matcher m = p.matcher(generationType);
//            m.find();
//            String prefix = m.group(1);
//            String newValue = prefix + defaultValue;
//            generatedInput.add(new GeneratedInput(newValue, generationType));
//        } else if (generationType.matches("(?i)"s
//                + INPUT_GENERATIONS_BY_MUTATION[2])) {
//            // Replace
//            Pattern p = Pattern.compile("(?i)replace\\s*\\(\\s*'(.+?)'\\s*,"
//                    + "\\s*'(.+?)'\\s*\\)$");
//            System.out.println(generationType);
//            Matcher m = p.matcher(generationType);
//            String[] values = new String[2];
//            m.find();
//
//            if (m.groupCount() == 2) {
//                values[0] = m.group(1);
//                values[1] = m.group(2);
//                String newValue = defaultValue.replaceAll(values[0],
//                        values[1]);
//                generatedInput.add(new GeneratedInput(newValue,
//                        generationType));
//            }
//        } else if (generationType.matches("(?i)"
//                + INPUT_GENERATIONS_BY_MUTATION[3])) {
//            // Substring
//            System.out.println("---ssubstring");
//            Pattern p = Pattern.compile("(?i)subString\\s*\\(\\s*(\\d+?)\\s*"
//                    + ",\\s*(\\d+?)\\s*\\)$");
//            Matcher m = p.matcher(generationType);
//            int[] values = new int[2];
//            m.find();
//            if (m.groupCount() == 2) {
//                values[0] = Integer.parseInt(m.group(1));
//                values[1] = Integer.parseInt(m.group(2));
//                String newValue = defaultValue.substring(values[0],
//                        values[1]);
//                generatedInput.add(new GeneratedInput(newValue,
//                        generationType));
//            }
//
//        } else if (generationType.matches("(?i)"
//                + INPUT_GENERATIONS_BY_MUTATION[4])) {
//            // Reverse Substring
//            Pattern p = Pattern.compile("(?i)reverseSubString\\s*\\(\\s*"
//                    + "(\\d+?)\\s*,\\s*(\\d+?)\\s*\\)$");
//            Matcher m = p.matcher(generationType);
//            int[] values = new int[2];
//            m.find();
//            if (m.groupCount() == 2) {
//                values[0] = Integer.parseInt(m.group(1));
//                values[1] = Integer.parseInt(m.group(2));
//                String newValue = new StringBuilder(defaultValue.substring(
//                    values[0], values[1])).reverse().toString();
//                generatedInput.add(new GeneratedInput(newValue,
//                        generationType));
//            }
//        } else if (generationType.matches("(?i)"
//                + INPUT_GENERATIONS_BY_MUTATION[5])) {
//            // Lower Case
//            String newValue = defaultValue.toLowerCase();
//            generatedInput.add(new GeneratedInput(newValue, generationType));
//        } else if (generationType.matches("(?i)"
//                + INPUT_GENERATIONS_BY_MUTATION[6])) {
//            // Upper Case
//            String newValue = defaultValue.toUpperCase();
//            generatedInput.add(new GeneratedInput(newValue, generationType));
//        }
//
//        return generatedInput;
//    }

    /**
     * Membaca isi sebuah file fuzz vector.
     * @param filePath
     *      Path menuju file yang menyimpan fuzz vector
     * @param type
     *      Tipe pembangkitan input
     * @return ArrayList<GeneratedInput>
     *      Sekumpulan input dari fuzz vector
     */
    private static ArrayList<GeneratedInput> readFuzzVector(String fileName,
            String type) {
        ArrayList<GeneratedInput> generatedInputs =
                new ArrayList<GeneratedInput>();
        ArrayList<String> content = FuzzVectorManager
                .getContentAsList(fileName);

        for (int  i = 0; i < content.size(); i++) {
            generatedInputs.add(new GeneratedInput(content.get(i), type));
        }

        return generatedInputs;
    }

    private static ArrayList<GeneratedInput> generateNumbers(JSONObject
            generationOptions) {
        int min = 0; //default
        if (generationOptions.get("min") != null) {
            String minStr = generationOptions.get("min").toString();
            try {
                min = Integer.parseInt(minStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid minLength value");
            }
        }

        int max = 1000; //default
        if (generationOptions.get("max") != null) {
            String maxStr = generationOptions.get("max").toString();
            try {
                max = Integer.parseInt(maxStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid minLength value");
            }
        }

        ArrayList<GeneratedInput> generatedInputs =
                new ArrayList<GeneratedInput>();
        for (int i = min; i <= max; i++) {
            generatedInputs.add(new GeneratedInput(String.valueOf(i),
                    "number"));
        }
        return generatedInputs;
    }

    /**
     * Menghasilkan sekumpulan input dengan mengincrement karakter.
     * @param generationType
     *      Tipe pembangkitan input.
     * @param generationOptions
     *      Opsi untuk pembangkitan (panjang minimum, panjang maksimum, jumlah
     *      pembangkitan
     * @return ArrayList<GeneratedInput>
     *      Sekumpulan random input
     */
    private static ArrayList<GeneratedInput> generateBruteForceStrings(String
            generationType, JSONObject generationOptions) {
        int minLength = 1; //default
        if (generationOptions.get("minLength") != null) {
            String minLengthStr = generationOptions.get("minLength").toString();

            try {
                minLength = Integer.parseInt(minLengthStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid minLength value");
            }
        }

        int maxLength = 100; //default
        if (generationOptions.get("maxLength") != null) {
            String maxLengthStr = generationOptions.get("maxLength").toString();
            try {
                maxLength = Integer.parseInt(maxLengthStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid maxLength value");
            }
        }

        int total = 10; //default
        if (generationOptions.get("total") != null) {
            String totalStr = generationOptions.get("total").toString();
            try {
                total = Integer.parseInt(totalStr);
            } catch (NumberFormatException e) {}
        }

        boolean generateAll = false;
        if (generationOptions.get("generateAll") != null) {
            String generateAllStr = generationOptions.get("generateAll")
                    .toString();
            try {
                generateAll = Boolean.parseBoolean(generateAllStr);
            } catch (NumberFormatException e) {}
        }

        ArrayList<GeneratedInput> generatedInputs =
                new ArrayList<GeneratedInput>();
        String chars;
        switch (generationType.toLowerCase()) {
            case "alphabet":
                chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
                if (generateAll) {
                    generatedInputs = generateAllStrings(chars, "alphabet",
                            minLength, maxLength);
                } else {
                    generatedInputs = generateSomeStrings(chars, "alphabet",
                            total, minLength, maxLength);
                }
                break;
            case "numeric":
                chars = "0123456789";
                if (generateAll) {
                    generatedInputs = generateAllStrings(chars, "numeric",
                            minLength, maxLength);
                } else {
                    generatedInputs = generateSomeStrings(chars, "numeric",
                            total, minLength, maxLength);
                }
                break;
            case "alphanumeric":
                chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqr"
                        + "stuvwxyz";
                if (generateAll) {
                    generatedInputs = generateAllStrings(chars, "alphanumeric",
                            minLength, maxLength);
                } else {
                    generatedInputs = generateSomeStrings(chars, "alphanumeric",
                        total, minLength, maxLength);
                }
                break;
            case "base64":
                chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01"
                        + "23456789+/";
                if (generateAll) {
                    generatedInputs = generateAllStrings(chars, "base64",
                            minLength, maxLength);
                } else {
                    generatedInputs = generateSomeStrings(chars, "base64",
                            total, minLength, maxLength);
                }
                break;
            case "base32":
                chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
                if (generateAll) {
                    generatedInputs = generateAllStrings(chars, "base32",
                            minLength, maxLength);
                } else {
                    generatedInputs = generateSomeStrings(chars, "base32",
                            total, minLength, maxLength);
                }
                break;
            default:
                break;
        }
        return generatedInputs;
    }

    /**
     * Menghasilkan sekumpulan input dengan karakter,
     * jumlah, panjang minimum, dan panjang maksimum tertentu.
     * @param chars
     *      Daftar char yang digunakan untuk membentuk string.
     * @param type
     *      Tipe pembangkitan input.
     * @param total
     *      Jumlah yang ingin dibangkitkan.
     * @param minLength
     *      Panjang minimum.
     * @param maxLength
     *      Panjang maksimum.
     * @return ArrayList<GeneratedInput>
     *      Input yang dibangkitkan
     */
    private static ArrayList<GeneratedInput> generateSomeStrings(String chars,
            String type, int total, int minLength, int maxLength) {
        ArrayList<GeneratedInput> generatedInputs =
                new ArrayList<GeneratedInput>();
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < total; i++) {
            int randomLen = ThreadLocalRandom.current().nextInt(minLength,
                    maxLength + 1);
            StringBuilder sb = new StringBuilder(randomLen);
            SecureRandom rnd2 = new SecureRandom();
            for (int j = 0; j < randomLen; j++) {
                sb.append(chars.charAt(rnd2.nextInt(chars.length())));
            }
            generatedInputs.add(new GeneratedInput(sb.toString(), type));
        }

        return generatedInputs;
    }

    /**
     * Menghasilkan seluruh kemungkinan input dengan karakter,
     * panjang minimum, dan panjang maksimum tertentu.
     * @param chars
     *      Daftar char yang digunakan untuk membentuk string.
     * @param type
     *      Tipe pembangkitan input.
     * @param minLength
     *      Panjang minimum.
     * @param maxLength
     *      Panjang maksimum.
     * @return ArrayList<GeneratedInput>
     *      Input yang dibangkitkan
     */
    private static ArrayList<GeneratedInput> generateAllStrings(String chars,
            String type, int minLength, int maxLength) {
        ArrayList<GeneratedInput> generatedInputs =
                new ArrayList<GeneratedInput>();

        for (int i = minLength; i <= maxLength; i++) {
            ArrayList<String> combinations = new ArrayList<String>();
            generateAllCombinations(combinations, chars, "", i);
            for (int j = 0; j < combinations.size(); j++) {
                generatedInputs.add(new GeneratedInput(combinations.get(j),
                        type));
            }
        }

        return generatedInputs;
    }

    /**
     * Mengehasilkan kombinasi input.
     * @param combinations
     *      Hasil kombinasi input yang dibangkitkan.
     * @param chars
     *      Karakter set
     * @param str
     *      Str untuk menampung input yang sedang dibangkitkan
     * @param n
     *      panjang tersisa
     */
    private static void generateAllCombinations(ArrayList<String> combinations,
            String chars, String str, int n) {
        if (n == 1) {
            for (int i = 0; i < chars.length(); i++) {
                String temp = str;
                temp += String.valueOf(chars.charAt(i));
                combinations.add(temp);
            }
        } else {
            for (int i = 0; i < chars.length(); i++) {
                String temp = str;
                temp += String.valueOf(chars.charAt(i));
                generateAllCombinations(combinations, chars, temp, n - 1);
            }
        }
    }

    /**
     * Membangkitkan input dengan character fobbler.
     * @param defaultValue
     *      Value default
     * @return ArrayList<GeneratedInput>
     *      Input yang dibangkitkan
     */
    private static ArrayList<GeneratedInput> generateSimilarStrings(String
            defaultValue) {
        ArrayList<GeneratedInput> generatedInputs =
                new ArrayList<GeneratedInput>();
        for (int i = 0; i < defaultValue.length(); i++) {
            StringBuilder sb = new StringBuilder(defaultValue);
            char c = sb.charAt(i);
            c++;
            sb.setCharAt(i, c);
            generatedInputs.add(new GeneratedInput(sb.toString(),
                    "Character fobbler"));
        }
        return generatedInputs;
    }

    /**
     * Menerapkan modifikasi pada input.
     * @param generatedInput
     *      Input yang dibangkitkan
     * @param generationOptions
     *      Opsi generasi input
     * @return ArrayList<GeneratedInput>
     *      Input yang telah dimodifikasi.
     */
    private static ArrayList<GeneratedInput> applyModificationOptions(
            ArrayList<GeneratedInput> inputs, JSONObject generationOptions) {
        String modificationsStr = generationOptions.get("modifications")
                .toString();
        String delim = "|";
        String regex = "(?<!\\\\)" + Pattern.quote(delim);
        String[] modifications = modificationsStr.split(regex);
        for (int i = 0; i < modifications.length; i++) {
            String modification = modifications[i];
            if (!matchesIgnoreCase(
                        MODIFICATION_OPTIONS, modification)) {
                continue;
            }

            if (modification.matches("(?i)" + MODIFICATION_OPTIONS[0])) {
                inputs = replaceInputs(modification, inputs);
            } else if (modification.matches("(?i)" + MODIFICATION_OPTIONS[1])) {
                inputs = convertToLowerCase(modification, inputs);
            } else if (modification.matches("(?i)" + MODIFICATION_OPTIONS[2])) {
                inputs = convertToUpperCase(modification, inputs);
            } else if (modification.matches("(?i)" + MODIFICATION_OPTIONS[3])) {
                inputs = encodeInputs(modification, inputs);
            }
        }
        return inputs;
    }

    /**
     * Menerapkan modifikasi input dengan mengganti string tertentu dengan
     * string lainnnya.
     * @param modification
     *      Modifikasi yang ingin diterapkan
     * @param inputs
     *      Input yang dibangkitkan
     * @return ArrayList<GeneratedInput>
     *      Input yang telah dimodifikasi
     */
    private static ArrayList<GeneratedInput> replaceInputs(String modification,
            ArrayList<GeneratedInput> inputs) {
        // Replace
        Pattern p = Pattern.compile("(?i)replace\\s*\\(\\s*'(.+?)'\\s*,\\s*"
                + "'(.+?)'\\s*\\)$");
        Matcher m = p.matcher(modification);
        String[] values = new String[2];
        m.find();

        if (m.groupCount() == 2) {
            values[0] = m.group(1);
            values[1] = m.group(2);
            for (int i = 0; i < inputs.size(); i++) {
                GeneratedInput gi = inputs.get(i);
                if ((gi == null) || (gi.getContent() == null)) {
                    continue;
                }
                String newValue = gi.getContent()
                        .replaceAll(values[0], values[1]);
                gi.setContent(newValue);
                gi.addModifications(modification);
            }
        }
        return inputs;
    }

    /**
     * Menerapkan modifikasi input dengan mengubah kapitalisasi menjadi
     * lower case.
     * @param modification
     *      Modifikasi yang ingin diterapkan
     * @param inputs
     *      Input yang dibangkitkan
     * @return ArrayList<GeneratedInput>
     *      Input yang telah dimodifikasi
     */
    private static ArrayList<GeneratedInput> convertToLowerCase(String
            modification, ArrayList<GeneratedInput> inputs) {
        for (int i = 0; i < inputs.size(); i++) {
            GeneratedInput gi = inputs.get(i);
            if ((gi == null) || (gi.getContent() == null)) {
                continue;
            }
            String newValue = gi.getContent().toLowerCase();
            gi.setContent(newValue);
            gi.addModifications(modification);
        }
        return inputs;
    }

    /**
     * Menerapkan modifikasi input dengan mengubah kapitalisasi menjadi
     * upper case.
     * @param modification
     *      Modifikasi yang ingin diterapkan
     * @param inputs
     *      Input yang dibangkitkan
     * @return ArrayList<GeneratedInput>
     *      Input yang telah dimodifikasi
     */
    private static ArrayList<GeneratedInput> convertToUpperCase(String
            modification, ArrayList<GeneratedInput> inputs) {
        for (int i = 0; i < inputs.size(); i++) {
            GeneratedInput gi = inputs.get(i);
            if ((gi == null) || (gi.getContent() == null)) {
                continue;
            }
            String newValue = gi.getContent().toUpperCase();
            gi.setContent(newValue);
            gi.addModifications(modification);
        }
        return inputs;
    }

    /**
     * Menerapkan modifikasi input dengan melakukan encoding.
     * @param modification
     *      Modifikasi yang ingin diterapkan
     * @param inputs
     *      Input yang dibangkitkan
     * @return ArrayList<GeneratedInput>
     *      Input yang telah dimodifikasi
     */
    private static ArrayList<GeneratedInput> encodeInputs(String
            modification, ArrayList<GeneratedInput> inputs) {
        
        Pattern p = Pattern.compile("(?i)encode\\s*\\(\\s*'(.+?)'\\s*\\)$");
        Matcher m = p.matcher(modification);
        m.find();

        if (m.groupCount() == 1) {
            String charset = m.group(1);
            if (Charset.isSupported(charset)) {
                for (int i = 0; i < inputs.size(); i++) {
                    
                    try {
                        GeneratedInput gi = inputs.get(i);
                        if ((gi == null) || (gi.getContent() == null)) {
                            continue;
                        }
                        String newValue = URLEncoder.encode(gi.getContent(), charset);
                        gi.setContent(newValue);
                        gi.addModifications(modification);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(InputGenerator.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                }
            }
        }

        return inputs;
    }

}
