package de.joint.wordnet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class JWIWordNet {

    private final IDictionary dict;

    public JWIWordNet(String path) throws MalformedURLException {

        final Dictionary dic
                = new Dictionary(new URL("file", null, path));
        this.dict = dic;
        this.dict.open();
    }

    public Iterator<ISynset> getIterator(POS pos) {
        return dict.getSynsetIterator(pos);
    }

    public ISynset getSynset(ISynsetID id) {
        return dict.getSynset(id);
    }

    public List<ISynset> getHypernyms(final ISynset synset) {
        final List<ISynset> hypers = new ArrayList<>();

        try {
            for (ISynsetID hyper : synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE)) {
                hypers.add(this.getSynset(hyper));
            }

            for (ISynsetID hyper : synset.getRelatedSynsets(Pointer.HYPERNYM)) {
                hypers.add(this.getSynset(hyper));
            }
        } catch (Exception e) {
        }

        return hypers;

    }

}
