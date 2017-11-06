/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joint.wordnet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author sfaralli
 */
public class WordNetGraph extends DefaultDirectedGraph<String, DefaultEdge> implements Serializable {

    static final long serialVersionUID = 1010101210;
    private static WordNetGraph instance = null;
    public Multimap<String, String> wordpos2nodes = new HashMultimap<>();

    private WordNetGraph() {
        super(DefaultEdge.class);
    }

    public static WordNetGraph getInstance(String location) throws MalformedURLException {
        if (instance == null) {
            instance = createGraph(location);
        }
        return instance;

    }

    public static WordNetGraph createGraph(String location) throws MalformedURLException {
        WordNetGraph result = null;
        try {
            result = fromSerFile("data/wordnet.ser");
        } catch (Exception e) {
            result = null;
        }
        if (result == null) {
            result = new WordNetGraph();
            JWIWordNet wn = new JWIWordNet(location);
            System.out.println("Processing NOUN...");
            Iterator<ISynset> iterator = wn.getIterator(POS.NOUN);
            while (iterator.hasNext()) {
                ISynset is = iterator.next();
                System.out.println("NOUN:" + is.getID().toString());
                result.addVertex(is.getID().toString());
                for (IWord w : is.getWords()) {
                    result.wordpos2nodes.put(w.getLemma() + ":" + POS.NOUN, is.getID().toString());
                }
                for (ISynsetID related : is.getRelatedSynsets()) {
                    result.addVertex(related.toString());
                    result.addEdge(is.getID().toString(), related.toString());
                }
            }
            System.out.println("Processing ADVERB...");
            iterator = wn.getIterator(POS.ADVERB);
            while (iterator.hasNext()) {
                ISynset is = iterator.next();
                System.out.println("ADVERB:" + is.getID().toString());
                result.addVertex(is.getID().toString());
                for (IWord w : is.getWords()) {
                    result.wordpos2nodes.put(w.getLemma() + ":" + POS.ADVERB, is.getID().toString());
                }
                for (ISynsetID related : is.getRelatedSynsets()) {
                    result.addVertex(related.toString());
                    result.addEdge(is.getID().toString(), related.toString());
                }
            }
            System.out.println("Processing ADJECTIVE...");
            iterator = wn.getIterator(POS.ADJECTIVE);
            while (iterator.hasNext()) {
                ISynset is = iterator.next();
                System.out.println("ADJECTIVE:" + is.getID().toString());
                result.addVertex(is.getID().toString());
                for (IWord w : is.getWords()) {
                    result.wordpos2nodes.put(w.getLemma() + ":" + POS.ADJECTIVE, is.getID().toString());
                }
                for (ISynsetID related : is.getRelatedSynsets()) {
                    result.addVertex(related.toString());
                    /*ISynset relateds = wn.getSynset(related);
                     for (IWord w:relateds.getWords())
                     result.wordpos2nodes.put(w.getLemma()+":"+w.getPOS(),relateds.getID().toString());*/
                    result.addEdge(is.getID().toString(), related.toString());
                }
            }
            System.out.println("Processing VERB...");
            iterator = wn.getIterator(POS.VERB);
            while (iterator.hasNext()) {
                ISynset is = iterator.next();
                System.out.println("VERB:" + is.getID().toString());
                result.addVertex(is.getID().toString());
                for (IWord w : is.getWords()) {
                    result.wordpos2nodes.put(w.getLemma() + ":" + POS.VERB, is.getID().toString());
                }
                for (ISynsetID related : is.getRelatedSynsets()) {
                    result.addVertex(related.toString());
                    result.addEdge(is.getID().toString(), related.toString());
                }
            }
            toSerFile("wordnet.ser", result);
        }
        return result;
    }

    public static WordNetGraph fromSerFile(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
        WordNetGraph th = null;

        InputStream file = new FileInputStream(filename);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        th = (WordNetGraph) input.readObject();

        return th;

    }

    public static void toSerFile(String filename, WordNetGraph wng) {
        try (
                OutputStream file = new FileOutputStream(filename);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);) {
            output.writeObject(wng);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) throws MalformedURLException {
        WordNetGraph wng = WordNetGraph.getInstance("/opt/WordNet-3.0/dict/");
        Set<String> candidates = new HashSet<String>(wng.wordpos2nodes.get("Albert_Einstein:" + POS.NOUN));
        for (String c : candidates) {
            System.out.println("casting:" + POS.NOUN + " " + c);
        }

    }
}
