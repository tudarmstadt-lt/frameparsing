package de.joint.linking;

import de.joint.thesaurus.Thesaurus;

import edu.mit.jwi.item.POS;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.jlt.util.Files;
import it.uniroma1.lcl.jlt.util.Language;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 *
 * @author sfaralli
 */
public class MonosemousMappingToBabelNet {
    

    public static void map(String datafolder, String csvfilename) throws IOException {
        
       
        String thesauri_map = datafolder + csvfilename + ".iterative_0_babelnet.map.tsv";
        String thesauri = datafolder + csvfilename;
        
        
        System.out.println("Loading Thesaurus...");
        Thesaurus thes = Thesaurus.fromSerFile(thesauri + ".ser");
        System.out.println("...ok.");

        BabelNet bn = BabelNet.getInstance();
        System.out.println("...ok.");
        BufferedWriter bw = Files.getBufferedWriter(thesauri_map, true);
        bw.write("JOBIMID\tBN_ID\tBN_SENSE\tHYPERNYMS\tAVGDEGREE\tCANDIDATEDEGREE\tINDUCEDSUBWNGRAPHDEGREE\n");
        System.out.println("Analyzing monosemous...");
        
        for (String word : thes.word2ids.keySet()) {
            String[] rw = word.split("#");
            if (rw == null) {
                continue;
            }
            if (rw.length < 2) {
                continue;
            }
            if (thes.word2ids.get(word).size() == 1) {
                for (String wid : thes.word2ids.get(word)) {

                    String[] wrw = wid.split("#");
                    String lemma = wrw[0].replaceAll(" ", "_");

                    POS pos = getPosFromTag(wrw[1]);

                    List<BabelSense> candidates = bn.getSenses(Language.EN, lemma.replaceAll("_", " "), pos);

                    if (candidates.size() == 1) {

                        BabelSense candidate = candidates.iterator().next();

                        bw.write(wid + "\t" + candidate.getSensekey() + "\t"
                                + candidate.getSynset().getId() + "\t"
                                + "" + "\t"
                                + 0.5 + "\n"
                        );

                        System.out.println(wid + "\t" + candidate.getSensekey() + "\t" + candidate.getSynset().getId() + "\t" + "" + "\t" + 0.5);
                    } else {
                        System.out.println(wid + " DISCARDED");
                    }

                }
            }

        }
        bw.close();
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
