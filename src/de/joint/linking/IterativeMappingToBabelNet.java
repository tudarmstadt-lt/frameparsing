package de.joint.linking;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.joint.thesaurus.Thesaurus;

import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.POS;
import it.uniroma1.lcl.babelnet.BabelGloss;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelPointer;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.jlt.util.DoubleCounter;
import it.uniroma1.lcl.jlt.util.Files;
import it.uniroma1.lcl.jlt.util.Language;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  H(S,M)= BoW Overlap MEASURE
 * @author sfaralli
 */
public class IterativeMappingToBabelNet {

    public static void map(String datafoler, String csvfilename, int iterations) throws IOException {
        String thesauri = datafoler + csvfilename;
        System.out.println("Loading Thesaurus...");
        Thesaurus thes = Thesaurus.fromSerFile(thesauri + ".ser");
        System.out.println("...ok.");

        BabelNet bn = BabelNet.getInstance();
        System.out.println("...ok.");
        for (int step = 1; step < iterations; step++) {

            String thesauri_map = datafoler + csvfilename + ".iterative_" + step + "_" + "0.0_babelnet.map.tsv";
            Map<String, String> mapping = new HashMap<>();
            System.out.println("Loading Previous Map...");
            for (int i = 0; i < step; i++) {
                BufferedReader br;
                if (i == 0) {
                    br = Files.getBufferedReader(datafoler + csvfilename + ".iterative_" + i + "_babelnet.map.tsv");
                } else {
                    br = Files.getBufferedReader(datafoler + csvfilename + ".iterative_" + i + "_0.0_babelnet.map.tsv");
                }

                br.ready();
                br.readLine();

                while (br.ready()) {
                    String line = br.readLine();
                    String[] lines = line.split("\t");
                    if (lines[4].startsWith("AVG")) {
                        continue;
                    }
                    double d = new Double(lines[4]);

                    if (d >= 0.0) {
                        mapping.put(lines[0].replace(".0", ""), lines[2]);
                    }
                }
                br.close();
            }

            System.out.println("...ok.");
            BufferedWriter bw = Files.getBufferedWriter(thesauri_map);
            bw.write("JOBIMID\tBN_ID\tBN_SENSE\tHYPERNYMS\tAVGDEGREE\tCANDIDATEDEGREE\tINDUCEDSUBWNGRAPHDEGREE\n");
            System.out.println("Step:" + step + " Analyzing thesaurus again ...");

            List<String> array = new ArrayList<>(thes.word2ids.keySet());
            Collections.shuffle(array);
            for (String word : array) {
                String[] rw = word.split("#");
                if (rw == null) {
                    continue;
                }
                if (rw.length < 2) {
                    continue;
                }

                if (!rw[1].toLowerCase().startsWith("n")) {
                    continue;
                }
                POS pos = getPosFromTag(rw[1]);
                boolean found = false;
                boolean foundm = false;
                for (String wid : thes.word2ids.get(word)) {
                    if (mapping.keySet().contains(wid)) {
                        foundm = true;

                    }
                    if (!mapping.keySet().contains(wid)) {
                        found = true;
                        //   break;
                    }
                }
                if (!found) {
                    continue;
                }
                if (foundm) {
                    continue;
                }
                List<BabelSynset> candidates = bn.getSynsets(Language.EN, rw[0].replaceAll("_", " "), pos);
                if (candidates.isEmpty()) {
                    continue;
                }
                for (String wid : thes.word2ids.get(word)) {

                    String[] wrw = wid.split("#");

                    if (!wrw[1].toLowerCase().startsWith("n")) {
                        continue;
                    }
                    System.out.print(wid);
                    if (mapping.keySet().contains(wid)) {

                        System.out.println("\talready mapped");
                        continue;
                    }

                    Set<String> rbow = new HashSet<>();
                    if (thes.ids2related.containsKey(wid)) {
                        for (String r : thes.ids2related.get(wid)) {
                            if (r != null) {
                                if (mapping.keySet().contains(r)) {

                                    BabelSynset synset = bn.getSynsetFromId(mapping.get(r));
                                    rbow.addAll(getBoW(synset, true));
                                } else {
                                    String[] rs = r.split("#");
                                    if (rs.length > 0) {
                                        rbow.add(rs[0].toLowerCase().replaceAll("_", " "));
                                    }
                                }
                            }
                        }
                    }
                    for (String r : thes.ids2hypernym.get(wid)) {
                        if (r != null) {
                            if (mapping.keySet().contains(r)) {
                                BabelSynset synset = bn.getSynsetFromId(mapping.get(r));
                                rbow.addAll(getBoW(synset, true));
                            } else {
                                String[] rs = r.split("#");
                                if (rs.length > 0) {
                                    rbow.add(rs[0].toLowerCase().replaceAll("_", " "));
                                }
                            }
                        }
                    }
                    /* compute H */

                    DoubleCounter<String> rank = new DoubleCounter<>();

                    Multimap<String, BabelSynset> syn2hyp = HashMultimap.create();
                    for (BabelSynset candidate : candidates) {
                        BabelSynset synset = candidate;
                        Set<String> bbow = new HashSet<>();

                        bbow.addAll(getBoW(synset, true));
                        Map<IPointer, List<BabelSynset>> rm = synset.getRelatedMap();

                        for (IPointer ip : rm.keySet()) {
                            int semrel = 0;

                            for (BabelSynset bs : rm.get(ip)) {
                                if (ip.equals(BabelPointer.HYPERNYM) || ip.equals(BabelPointer.HYPERNYM_INSTANCE)) {
                                    syn2hyp.put(candidate.getId(), bs);
                                }
                                semrel++;
                                if (semrel > 4) {
                                    break;
                                }
                                bbow.addAll(getBoW(bs, false));

                            }
                        }

                        int totalwords = Math.max(rbow.size(), bbow.size());
                        bbow.retainAll(rbow);
                        double avgdegree = 0.0;
                        if (totalwords > 0.0) {
                            avgdegree = (double) bbow.size() / (double) totalwords;
                        }
                        rank.count(synset.getId(), avgdegree);
                    }

                    double top = rank.getTopValue();
                    int num = 0;
                    for (String candidate : rank.getSortedElements()) {
                        if (rank.get(candidate) == top) {
                            num++;
                        }
                    }
                    boolean mapped = false;
                    if (top > 0 && num == 1) {
                        for (String candidate : rank.getSortedElements()) {
                            if (rank.get(candidate) != top) {
                                continue;
                            }

                            BabelSynset sense = bn.getSynsetFromId(candidate);

                            StringBuilder sb = new StringBuilder();
                            for (BabelSynset is : syn2hyp.get(sense.getId())) {
                                sb.append(",").append(is.getId()).append("[").append(is.toString()).append("]");
                            }
                            String ssb = sb.toString();
                            if (syn2hyp.get(sense.getId()).size() > 0) {
                                ssb = ssb.substring(1);
                            }

                            bw.write(wid + "\t" + candidate + "\t"
                                    + sense.getId() + "\t"
                                    + ssb + "\t"
                                    + top + "\n");
                            bw.flush();
                            mapped = true;
                            System.out.println("\t" + candidate + "\t" + sense.getId() + "\t" + ssb + "\t" + top);
                        }
                    }
                    if (!mapped) {
                        System.out.println("\t NOT MAPPED");
                    }

                }

            }
            bw.close();
        }
    }

    private static Set<String> getBoW(BabelSynset synset, boolean getgloss) throws IOException {
        Set<String> bbow = new HashSet<>();

        for (BabelSense bss : synset.getSenses(Language.EN)) {
            bbow.add(bss.getLemma().replaceAll("_", " ").replaceAll("\\([^\\(]*\\)", "").toLowerCase());
        }

        if (getgloss) {
            List<BabelGloss> bgs = synset.getGlosses(Language.EN);
            if (bgs != null) {
                for (BabelGloss gloss : bgs) {
                    if (gloss == null) {
                        continue;
                    }
                    bbow.addAll(Arrays.asList(gloss.getGloss().replace("(", " ").replace(")", " ").toLowerCase().split(" ")));
                }
            }
        }

        return bbow;
    }

    public static POS getPosFromTag(String pos) {
        POS epos = POS.NOUN;
        if (pos.equals("ADJ")) {
            epos = POS.ADJECTIVE;
        }
        if (pos.equals("ADV")) {
            epos = POS.ADVERB;
        }
        if (pos.equals("VERB")) {
            epos = POS.VERB;
        }
        if (pos.equals("PROPN")) {
            epos = POS.NOUN;
        }
        if (pos.equals("NOUN")) {
            epos = POS.NOUN;
        }
        return epos;
    }

}
