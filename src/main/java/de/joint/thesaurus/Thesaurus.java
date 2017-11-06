/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joint.thesaurus;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author sfaralli
 */
public class Thesaurus implements Serializable {

    static final long serialVersionUID = 1212121212;
    public Multimap<String, String> word2ids = new HashMultimap<>();
    public Map<String, String> id2word = new HashMap<>();
    public Multimap<String, String> ids2related = new HashMultimap<>();
    public Multimap<String, String> ids2hypernym = new HashMultimap<>();

    public void toSerFile(String filename) {
        try (
                OutputStream file = new FileOutputStream(filename);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);) {
            output.writeObject(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void clean() {

        for (String s : word2ids.keySet()) {
            ArrayList<String> ids = new ArrayList<String>(word2ids.get(s));
            for (int i = 0; i < ids.size(); i++) {
                String t = ids.get(i).replace(".0", "");
                ids.set(i, t);
            }
            word2ids.replaceValues(s, ids);

        }
        Map<String, String> id2newid = new HashMap<String, String>();
        for (String s : id2word.keySet()) {
            String k = s.replace(".0", "");
            id2newid.put(k, id2word.get(s));
        }
        id2word = id2newid;
        Multimap<String, String> nids2related = new HashMultimap<>();
        for (String s : ids2related.keySet()) {
            String k = s.replace(".0", "");
            for (String ssk : ids2related.get(s)) {
                nids2related.put(k, ssk.replace(".0", ""));
            }
        }
        ids2related = nids2related;

        Multimap<String, String> nids2hypernym = new HashMultimap<>();
        for (String s : ids2hypernym.keySet()) {
            String k = s.replace(".0", "");
            for (String ssk : ids2hypernym.get(s)) {
                nids2hypernym.put(k, ssk.replace(".0", ""));
            }
        }
        ids2hypernym = nids2hypernym;

    }

    public static Thesaurus fromFile2(String filename) throws IOException {
        return fromFile2(filename, false);
    }

    public static Thesaurus fromFile2(String filename, boolean allnames) throws FileNotFoundException, IOException {
        Thesaurus th = new Thesaurus();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        br.readLine();
        int row = 0;
        while (br.ready()) {

            row++;
            if (row % 10000 == 0) {
                System.out.println(row);
            }
            String line = br.readLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] lines = line.split("\t");
            if (lines.length < 2) {
                continue;
            }
            String word = lines[0].trim();
            if (allnames) {
                word = word + "#NN";
            }
            String wordid = word + "#" + lines[1];

            String[] wess = word.split("#");
            if (wess.length < 2) {
                continue;
            }
            String poss = wess[1];
            if (poss.equals("DET")) {
                continue;
            }
            th.word2ids.put(word, wordid);
            th.id2word.put(wordid, word);

            if (lines.length >= 4) {
                String[] hypernyms = lines[3].split(",");
                for (String s : hypernyms) {
                    String[] wd = s.split(":");
                    if (allnames) {
                        th.ids2hypernym.put(wordid, wd[0].trim().replace("#?#", "#NN#"));
                    } else {
                        th.ids2hypernym.put(wordid, wd[0].trim());
                    }

                }
            }
            if (lines.length >= 3) {
                String[] related = lines[2].split(",");
                for (String s : related) {

                    String[] wd = s.split(":");
                    if (allnames) {
                        th.ids2related.put(wordid, wd[0].trim().replace("#?#", "#NN#"));
                    } else {
                        th.ids2related.put(wordid, wd[0].trim());
                    }
                }
            }
        }
        br.close();
        return th;

    }

    public static String getLemma_1(String s) {
        String result = "";
        String[] parts = s.split(" ");
        for (String wp : parts) {
            result = result + "_" + wp.split("#")[0].trim();
        }
        return result.substring(1);
    }

    public static String getLemma_new(String s) {
        String result = "";
        String[] parts = s.split(" ");
        for (String wp : parts) {
            result = result + "_" + wp.split("\\|")[0].trim();
        }
        return result.substring(1);
    }

    public static String getPos_new(String s) {
        String result = "";
        String[] parts = s.split(" ");
        for (String wp : parts) {
            result = result + "_" + wp.split("\\|")[1].split("#")[0].trim();
        }
        return result.substring(1);
    }

    public static String getId_new(String s) {
        String result = "";
        String[] parts = s.split(" ");
        for (String wp : parts) {
            String wp1 = wp.split("\\|")[1];
            String[] wps1 = wp1.split("#");
            if (wps1.length > 1) {
                result = result + "_" + wps1[1].split(":")[0].trim();
            }
        }
        if (!result.isEmpty()) {
            return result.substring(1);
        }
        return "";
    }

    public static String getConfidence_new(String s) {
        String result = "";
        String[] parts = s.split(" ");
        for (String wp : parts) {
            try {
                result = result + "_" + wp.split("\\|")[1].split("#")[1].split(":")[1].trim();
            } catch (Exception e) {
            }

        }
        return result.substring(1);
    }

    public static String getId_1(String s) {
        String result = "";
        String[] parts = s.split(" ");
        String[] subparts = parts[parts.length - 1].split("#");
        String[] lastsubarts = subparts[subparts.length - 1].split(":");
        result = lastsubarts[0];
        return result.trim();
    }

    public static Thesaurus fromFile3(String filename, boolean allnames) throws FileNotFoundException, IOException {
        Thesaurus th = new Thesaurus();
        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
        int row = 0;
        br.readLine();
        while (br.ready()) {

            row++;
            if (row % 10000 == 0) {
                System.gc();
                System.out.println(row);
            }
            String line = br.readLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] lines = line.split("\t");
            if (lines.length < 2) {
                continue;
            }
            String word = getLemma_new(lines[0].trim());
            String pos = getPos_new(lines[0].trim());
            if (allnames) {
                word = word + "#NN";
            } else {
                word = word + "#" + pos;
            }
            String wordid = word + "#" + lines[1];
            th.word2ids.put(word, wordid);
            th.id2word.put(wordid, word);
            if (lines.length >= 4) {
                String[] hypernyms = lines[3].split(",");
                for (String s : hypernyms) {
                    String wd = getLemma_new(s) + "#" + getPos_new(s) + "#" + getId_new(s);
                    if (allnames) {
                        th.ids2hypernym.put(wordid, wd.trim().replace("#?#", "#NN#"));
                    } else {
                        th.ids2hypernym.put(wordid, wd.trim());
                        //  System.out.println(wordid+"->"+wd[0].trim());
                    }

                }
            }
            if (lines.length >= 3) {
                String[] related = lines[2].split(",");
                for (String s : related) {

                    String wd = getLemma_new(s) + "#" + getPos_new(s) + "#" + getId_new(s);
                    if (allnames) {
                        th.ids2related.put(wordid, wd.trim().replace("#?#", "#NN#"));
                    } else {
                        th.ids2related.put(wordid, wd.trim());
                    }
                }
            }
        }
        br.close();
        return th;

    }

    public static Thesaurus fromSerFile(String filename) {
        Thesaurus th = null;
        try (
                InputStream file = new FileInputStream(filename);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream(buffer);) {

            th = (Thesaurus) input.readObject();
            th.clean();
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        return th;

    }

    public static void main(String[] args) throws IOException {
        String folder = args[0];
        String thesauri = args[1];
        Thesaurus t = Thesaurus.fromFile2(folder + thesauri);
        System.gc();
        t.toSerFile(thesauri + ".ser");
    }
}
