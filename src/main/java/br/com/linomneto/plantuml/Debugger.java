package br.com.linomneto.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class Debugger {

    private String source;
    private Boolean inForking = false;

    public Debugger(String action) {
        this.source = "@startuml";
        this.source += "\nstart";
        this.boldAction(action);
    }

    private Debugger() {
        this.source = "";
    }

    public Debugger newFork() {
        return new Debugger();
    }

    public void fork(Debugger subDebugger) {
        if (this.inForking)
            this.source += "\nfork again";
        else
            this.source += "\nfork";

        this.source += subDebugger.source;
        this.inForking = true;
    }

    public void mergeForks() {
        this.source += "\nend merge";
        this.inForking = false;
    }

    public void action(String label) {
        this.source += "\n:" + sanitize(label) + ";";
    }

    public void boldAction(String label) {
        this.source += "\n:" + sanitize(label) + ";";
    }

    public void action(String label, String note) {
        this.action(label);
        this.source += "\nnote right";
        this.source += "\n" + sanitize(note);
        this.source += "\nend note";
    }

    public void condition(String label, Boolean result) {
        if (result)
            this.source += "\nif (" + sanitize(label) + ") then (true)\n";
        else
            this.source += "\nif (" + sanitize(label) + ") then (true)\nelse (false)";
    }

    public void endCondition(Boolean result) {
        if (result)
            this.source += "\nelse (false)";
        this.source += "\nendif";
    }

    public String end(String label) {
        this.boldAction(label);
        this.source += "\nstop";
        this.source += "\n@enduml";

        return this.getLink();
    }

    private static final String BASE64_MAPPING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";
    private static final String PLANT_UML_SERVER = "https://www.plantuml.com/plantuml/svg/~1";

    public String getLink() {
        String encodedCode = encodePlantUML(this.source);
        String imageUrl = PLANT_UML_SERVER + encodedCode;
        return imageUrl;
    }

    private static String encodePlantUML(String plantUMLCode) {
        try {
            byte[] compressedData = compress(plantUMLCode.getBytes("UTF-8"));
            String encodedData = encodeAscii(compressedData);
            return encodedData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, new Deflater());
        deflaterOutputStream.write(data);
        deflaterOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private static String encodeAscii(byte[] data) {
        StringBuilder encodedBuilder = new StringBuilder();
        int paddingCount = (3 - (data.length % 3)) % 3;

        int i = 0;
        int len = data.length;
        while (i < len) {
            int val = (data[i++] & 0xFF) << 16;
            if (i < len) {
                val |= (data[i++] & 0xFF) << 8;
                if (i < len) {
                    val |= (data[i++] & 0xFF);
                }
            }

            encodedBuilder.append(BASE64_MAPPING.charAt((val >> 18) & 0x3F));
            encodedBuilder.append(BASE64_MAPPING.charAt((val >> 12) & 0x3F));
            encodedBuilder.append(BASE64_MAPPING.charAt((val >> 6) & 0x3F));
            encodedBuilder.append(BASE64_MAPPING.charAt(val & 0x3F));
        }

        if (paddingCount > 0) {
            encodedBuilder.setLength(encodedBuilder.length() - paddingCount);
        }

        return encodedBuilder.toString();
    }

    private String sanitize(String str) {
        return str.trim();
    }

}
