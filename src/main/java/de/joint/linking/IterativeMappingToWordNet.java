/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joint.linking;


import de.joint.thesaurus.Thesaurus;
import de.joint.util.Counter;
import de.joint.wordnet.JWIWordNet;
import de.joint.wordnet.WordNetGraph;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.SynsetID;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;


/**
 *
 * @author sfaralli
 */
public class IterativeMappingToWordNet {

    public static void map(String datafoler, String csvfilename, int iterations, String wnlocation) throws IOException 
    {
        String thesauri = datafoler + csvfilename;
        System.out.println("Loading Thesaurus...");
        Thesaurus thes = Thesaurus.fromSerFile(thesauri + ".ser");
        System.out.println("...ok.");

        System.out.println("Loading WordNetGraph...");
        WordNetGraph wng = WordNetGraph.getInstance(wnlocation);
        JWIWordNet wn = new JWIWordNet(wnlocation);
        System.out.println("...ok.");
        for (int step=1;step<9;step++)
        {
        
        String thesauri_map = datafoler + csvfilename + ".iterative_" + step + "_" + "0.0_wordnet.map.tsv";
           
        Map<String, String> mapping = new HashMap<String, String>();
        System.out.println("Loading Previous Map...");
        for (int i = 0; i < step; i++) 
         {
              BufferedReader br;
            if (i==0)
              br= Files.newBufferedReader(Paths.get(datafoler + csvfilename + ".iterative_" + i + "_wordnet.map.tsv"));
            else
                   br= Files.newBufferedReader(Paths.get(datafoler + csvfilename + ".iterative_" + i + "_0.0_wordnet.map.tsv"));
           
          
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                String[] lines = line.split("\t");
                double d = new Double(lines[4]);
                
                if (d >= 0.0) 
                {
                    mapping.put(lines[0].replace(".0",""), lines[1]);
                }
            }
            br.close();
        }

        System.out.println("...ok.");
        BufferedWriter bw = Files.newBufferedWriter(Paths.get(thesauri_map));
        bw.write("JOBIMID\tWN_ID\tWN_SENSE\tHYPERNYMS\tAVGDEGREE\tCANDIDATEDEGREE\tINDUCEDSUBWNGRAPHDEGREE\n");
        System.out.println("Step:" + step + " Analyzing thesaurus again ...");

        for (String word : thes.word2ids.keySet()) {
            String[] rw = word.split("#");
            if (rw == null) {
                continue;
            }
            if (rw.length < 2) {
                continue;
            }
            for (String wid : thes.word2ids.get(word)) {
                if (mapping.keySet().contains(wid)) {

                    System.out.println(wid + "\talready mapped");
                    continue;
                }

                String[] wrw = wid.split("#");
                String lemma = wrw[0].replaceAll(" ", "_");
                String sn = wrw[2];
                POS pos = getPosFromID(wrw[1]);

              Set<String> rbow=new HashSet<String>();
              if (thes.ids2related.containsKey(wid))
              for (String r:thes.ids2related.get(wid))
              {
                  if (r!=null)
                 {
                    if (mapping.keySet().contains(r)) 
                    {
                         ISynset synset= wn.getSynset(SynsetID.parseSynsetID(mapping.get(r)));  
                         rbow.addAll(getBoW(synset));
                    }
                    else
                    {
                     String[] rs=r.split("#");
                     if (rs.length>0)
                           rbow.add(rs[0].toLowerCase().replaceAll("_", " "));
                    }
                  }
              }
            for (String r:thes.ids2hypernym.get(wid))
              {
                  if (r!=null)
                  {
                     if (mapping.keySet().contains(r)) 
                    {
                         ISynset synset = wn.getSynset(SynsetID.parseSynsetID(mapping.get(r)));
                         rbow.addAll(getBoW(synset));
                    }
                    else
                    {
                     String[] rs=r.split("#");
                     if (rs.length>0)
                           rbow.add(rs[0].toLowerCase().replaceAll("_", " "));
                    }
                  }
              }
                /* compute F */


                Map<String, Double> rank = new HashMap<>();
                Set<String> candidates = new HashSet<String>(wng.wordpos2nodes.get(lemma + ":" + pos));
                for (String candidate : candidates) 
                {
                    ISynset synset= wn.getSynset(SynsetID.parseSynsetID(candidate));
                    Set<String> bbow = new HashSet<String>();
                    
                    bbow.addAll(getBoW(synset));
                          for (DefaultEdge de:wng.outgoingEdgesOf(candidate))
                       {
                            ISynset desynset= wn.getSynset(SynsetID.parseSynsetID(wng.getEdgeTarget(de)));
                            bbow.addAll(getBoW(desynset));
                       
                       }
                       for (DefaultEdge de:wng.incomingEdgesOf(candidate))
                       {
                            ISynset desynset= wn.getSynset(SynsetID.parseSynsetID(wng.getEdgeSource(de)));
                            bbow.addAll(getBoW(desynset));
                       
                       }
                    int totalwords=Math.max(rbow.size(), bbow.size());
                    bbow.retainAll(rbow);
                    double avgdegree =0.0;
                    if (totalwords>0.0)
                        avgdegree = (double) bbow.size()/ (double) totalwords;
                    rank.merge(candidate, avgdegree, Double::sum);
                }

                double top = rank.values().stream().mapToDouble(e -> e).max().orElse(0d);
                int num = 0;
                for (final Map.Entry<String, Double> candidate: Counter.sortedIterator(rank)) {
                    if (candidate.getValue() == top) {
                        num++;
                    }
                }
                if (top > 0 && num == 1) {
                    for (final Map.Entry<String, Double> candidate: Counter.sortedIterator(rank)) {
                        if (candidate.getValue() != top) {
                            continue;
                        }
                        ISynsetID sid = SynsetID.parseSynsetID(candidate.getKey());
                        ISynset sense = wn.getSynset(sid);
                        List<ISynset> hs = wn.getHypernyms(sense);
                        StringBuilder sb = new StringBuilder();
                        for (ISynset is : hs) {
                            sb.append(",").append(is.getID().toString()).append("[").append(is.toString()).append("]");
                        }
                        String ssb = sb.toString();
                        if (hs.size() > 0) {
                            ssb = ssb.substring(1);
                        }

                        bw.write(wid + "\t" + candidate.getKey() + "\t"
                                + sense.toString() + "\t"
                                + ssb + "\t"
                                + top + "\n");

                        System.out.println(wid + "\t" + candidate.getKey() + "\t" + sense.toString() + "\t" + ssb + "\t" + top );
                    }
                }
            }

        }
        bw.close();
        }
    }

    public static POS getPosFromID(String pos) 
    {
        POS epos = POS.NOUN;
        if (pos.equals("ADJ")) 
            epos = POS.ADJECTIVE;
        if (pos.equals("ADV")) 
            epos = POS.ADVERB;
        if (pos.equals("VERB")) 
            epos = POS.VERB;
        if (pos.equals("PROPN"))
            epos = POS.NOUN;
        if (pos.equals("NOUN"))
            epos = POS.NOUN;
        return epos;
    }
    
    public static POS getPosFromTag(String pos) {
      POS epos = POS.NOUN;
        if (pos.equals("ADJ")) 
            epos = POS.ADJECTIVE;
        if (pos.equals("ADV")) 
            epos = POS.ADVERB;
        if (pos.equals("VERB")) 
            epos = POS.VERB;
        if (pos.equals("PROPN"))
            epos = POS.NOUN;
        if (pos.equals("NOUN"))
            epos = POS.NOUN;
        return epos;
    }

    private static Set<String> getBoW(ISynset synset) 
    {
            Set<String> bbow=new HashSet<String>();
            if (synset==null) return bbow;
            String gloss=synset.getGloss();
            if (gloss==null) return  bbow;
            gloss=gloss.replace("(", " ").replace(")"," ").toLowerCase();
            bbow.addAll(Arrays.asList(gloss.split(" ")));
            List<IWord> lbss=synset.getWords();
            for (IWord bss:lbss)
            {
               bbow.add(bss.getLemma().replaceAll("_", " ").replaceAll("\\([^\\(]*\\)", "").toLowerCase());
            }
            return bbow;
    }
}
