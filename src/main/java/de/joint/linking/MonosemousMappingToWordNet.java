package de.joint.linking;


import de.joint.wordnet.JWIWordNet;
import de.joint.wordnet.WordNetGraph;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.SynsetID;
import it.uniroma1.lcl.jlt.util.Files;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;


/**
 *  H(S,M)=  BoW Overlap MEASURE
 * @author sfaralli
 */
public class MonosemousMappingToWordNet {

   

    public static void map(String datafolder, String csvfilename,String wnlocation) throws IOException 
    {
        String thesauri_map = datafolder + csvfilename + ".iterative_0_wordnet.map.tsv";
        String thesauri = datafolder + csvfilename;
        
        
        System.out.println("Loading Thesaurus...");
        de.joint.thesaurus.Thesaurus thes = de.joint.thesaurus.Thesaurus.fromSerFile(thesauri + ".ser");
        System.out.println("...ok.");

        System.out.println("Loading WordNetGraph...");
        WordNetGraph wng = WordNetGraph.getInstance(wnlocation);
        JWIWordNet wn = new JWIWordNet(wnlocation);
        System.out.println("...ok.");
        BufferedWriter bw = Files.getBufferedWriter(thesauri_map);
        bw.write("JOBIMID\tWN_ID\tWN_SENSE\tHYPERNYMS\tAVGDEGREE\tCANDIDATEDEGREE\tINDUCEDSUBWNGRAPHDEGREE\n");
        System.out.println("Analyzing monosemous...");
        for (String word : thes.word2ids.keySet()) 
        {
            String[] rw = word.split("#");
            if (rw == null) {
                continue;
            }
            if (rw.length < 2) {
                continue;
            }
            if (thes.word2ids.get(word).size() == 1) 
            {
                for (String wid : thes.word2ids.get(word)) 
                {
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
                     String[] rs=r.split("#");
                     if (rs.length>0)
                           rbow.add(rs[0].toLowerCase().replaceAll("_", " "));
                  }
                }
               for (String r:thes.ids2hypernym.get(wid))
              {
                  if (r!=null)
                  {
                     String[] rs=r.split("#");
                     if (rs.length>0)
                           rbow.add(rs[0].toLowerCase().replaceAll("_", " "));
                  }
              }
                    
                    
               Set<String> candidates = new HashSet<String>(wng.wordpos2nodes.get(lemma + ":" + pos));
               if (candidates.size() == 1) 
                    {
                       String candidate=candidates.iterator().next();
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

                
                
                        ISynsetID sid = SynsetID.parseSynsetID(candidate);
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

                        bw.write(wid + "\t" + candidate + "\t"
                                + sense.toString() + "\t"
                                + ssb + "\t"
                                + avgdegree + "\n"
                                );

                        System.out.println(wid + "\t" + candidate + "\t" + sense.toString() + "\t" + ssb + "\t" + avgdegree );
                    }

                }
            }

        }
        bw.close();
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
    private static Set<String> getBoW(ISynset synset) 
    {
            Set<String> bbow=new HashSet<String>();
            String gloss=synset.getGloss().replace("(", " ").replace(")"," ").toLowerCase();
            bbow.addAll(Arrays.asList(gloss.split(" ")));
            List<IWord> lbss=synset.getWords();
            for (IWord bss:lbss)
            {
               bbow.add(bss.getLemma().replaceAll("_", " ").replaceAll("\\([^\\(]*\\)", "").toLowerCase());
            }
            return bbow;
    }
}
