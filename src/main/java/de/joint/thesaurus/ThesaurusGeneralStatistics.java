/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joint.thesaurus;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sfaralli
 */
public class ThesaurusGeneralStatistics
{
    
    
    double averageRelated=0;
    double averageFoundRelated=0;
    double averageHypernym=0;
    double totalRelated=0;
    double totalHypernym=0;
    double totalword=0;
    double totalwordsense=0;
    double totalPolysemy=0;
    double totalFoundRelated=0;
    double averagePolysemy=0;
    double max_polysemy=0;
    double over300polesemy=0;
    double over200polesemy=0;
    double over100polesemy=0;
    double over50polesemy=0;
    double over40polesemy=0;
    double over30polesemy=0;
    double over20polesemy=0;
    double over10polesemy=0;
    double over5polesemy=0;
    double over1polesemy=0;
    double monosemous=0;
    double differentHypernym=0;
    double differentRelated=0;
    
    double lexicalHypernymCoverage=0;
    
    public void analyze(Thesaurus t)
    {
        Set<String> hypernyms= new HashSet<String>();
        Set<String> relateds = new HashSet<String>();
        Set<String> wordsenses = new HashSet<String>();
        for (String word:t.word2ids.keySet())
        {
            totalword+=1.0;
            HashSet<String> wordsensest=new HashSet<String>(t.word2ids.get(word));
            wordsenses.addAll(wordsensest);
            totalPolysemy+=wordsensest.size();
            if (wordsensest.size()>max_polysemy) max_polysemy=wordsensest.size();
    
    if (wordsensest.size()>300) over300polesemy++;
    if (wordsensest.size()>200) over200polesemy++;
    if (wordsensest.size()>100) over100polesemy++;
    if (wordsensest.size()>50)   over50polesemy++;
    if (wordsensest.size()>40) over40polesemy++;
    if (wordsensest.size()>30) over30polesemy++;
    if (wordsensest.size()>20) over20polesemy++;
    if (wordsensest.size()>10)  over10polesemy++;
    if (wordsensest.size()>5) over5polesemy++;
    if (wordsensest.size()>1)  over1polesemy++;    
    if (wordsensest.size()==1) monosemous++;    
    
            for (String ws:wordsensest)
            {
                HashSet<String> hypernymst=new HashSet<String>(t.ids2hypernym.get(ws));
                totalHypernym+=hypernymst.size();
                hypernyms.addAll(hypernymst);
                HashSet<String> relatedt=new HashSet<String>(t.ids2related.get(ws));
                totalRelated+=relatedt.size();
                relateds.addAll(relatedt);
                /*Set<String> found=new HashSet<String>();
                for (String rs:relatedt)
                    if (t.ids2related.containsKey(rs)) found.add(rs);
                totalFoundRelated+=found.size();*/
            }
            
        }
        Set<String> lexicalwords = new HashSet<String>(t.word2ids.keySet());
        lexicalwords.retainAll(hypernyms);
        totalwordsense=(double)wordsenses.size();
      //  lexicalHypernymCoverage=(double)lexicalwords.size()/(double)hypernyms.size();
        averageHypernym=(double)totalHypernym/(double)totalwordsense;
        averageRelated=(double)totalRelated/(double)totalwordsense;
        averagePolysemy=(double)totalPolysemy/(double)totalword;
       // averageFoundRelated=(double)totalFoundRelated/(double)totalwordsense;
        differentHypernym=(double)hypernyms.size();
        differentRelated= (double)relateds.size();
    }
    public void printStatistics()
    {
        System.out.println("#word: "+totalword);
        System.out.println("#wordsense: "+totalwordsense);
        System.out.println("#averagePolysemy per word: "+averagePolysemy);
        System.out.println("#max_polysemy:"+max_polysemy); 
        System.out.println("#monosemous:"+monosemous); 
        System.out.println("#polysemous:"+(totalword-monosemous)); 
        
        System.out.println("-------------------------------------------");
        System.out.println("differentRelated: "+differentRelated);
        System.out.println("averageRelated per ws: "+averageRelated);
        System.out.println("-------------------------------------------");
        System.out.println("differentHypernym: "+differentHypernym);
        System.out.println("averageHypernym per ws: "+averageHypernym);
        System.out.println("lexicalHypernymCoverage: "+lexicalHypernymCoverage);
    }
    public static void main(String[] args)
    {
       String data = "/home/sfaralli/Scrivania/progetti/JOINT-T/jobimtext/thesauri/";
       String dd ="ddt-wiki-n30-1400k-v3-closure";
    //public static final String dd ="ddt-wiki-n200-380k-v3-closure";
   // public static final String dd="ddt-news-n50-485k-closure";
    //public static final String dd="ddt-news-n200-345k-closure";
    
       String thesauri = data + dd+".csv.gz";  
        
      Thesaurus t=Thesaurus.fromSerFile(thesauri+".ser");
      ThesaurusGeneralStatistics ugs = new ThesaurusGeneralStatistics();
      ugs.analyze(t);
      ugs.printStatistics();
    }
  }
