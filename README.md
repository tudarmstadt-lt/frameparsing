Please note we are currently working hard to the contents of this repository.<br>

#<b>Linked Disambiguated Distributional Semantic Networks</b><br>
Source code related to the <a href="http://web.informatik.uni-mannheim.de/joint/">JOIN-T</a> project.<br>


#<b>Creation of Disambiguated Distributional Semantic-based Sense Inventories (PCZs)</b><br>
We provide in this repository all the source code needed for the induction of distributional semantic-based sense inventories form corpora. 


Our approach consists of three main phases (Figure 1):<br>
1) Learning a JoBimText model: initially, we automatically create a sense inventory from a large text collection using the pipeline of the JoBimText project;<br>
2) Disambiguation of related words: we fully disambiguate all lexical information associated with a proto-concept (i.e. similar terms and hypernyms) based on the partial disambiguation from step 1). The result is a proto-conceptualization (PCZ). In contrast to a term-based distributional thesaurus (DT), a PCZ consists of sense-disambiguated entries, i.e. all terms have a sense identifier (Table 1);<br>
3) Linking to a lexical resource: we align the PCZ with an existing lexical resource (LR). That is, we create a mapping between the two sense inventories and then combine them into a new extended sense inventory, our hybrid aligned resource. Finally, to obtain a truly unified resource, we link the "orphan" PCZ senses for which no corresponding sense could be found by inferring their type in the LR.<br>


![ScreenShot](http://web.informatik.uni-mannheim.de/joint/img/jointworkflow.png)<br>
Figure 1: Overview of our pipeline for the construcion of a hybrid aligned resource.



#<b>Learning a JoBimText model</b><br/>
Section under construction.

#<b>Disambiguation of related words</b><br/>
Section under construction.

#<b>Linking to a lexical resource</b><br/>
We provide the source code for the linking with <a href="http://www.babelnet.org/">BabeblNet</a> and <a href="https://wordnet.princeton.edu/">WordNet</a>.<br>
<ul>
<item> Prerequirements<br/>
In order to correctly execute the linking procedure please follow this three steps:<br> 
<ol>
<item>1) <b>Thesauri serializtions</b><br/> 
<i>serializeThesaurus.sh FOLDER GZIPPEDTHESAURUS</i><br/> 
</item>
<item>2) <b>BabeblNet</b> following the instructions from  http://babelnet.org/  download the Java API and the lastest index distribution. 
Copy both the API jar and the "config" folder into the project folder "dist/lib/";</item>
<item>3) </b>WordNet</b> download the lastest WordNet distribution from https://wordnet.princeton.edu/ and install the resource in some specific folder <i>WORDNETFOLDER</i> (e.g. "/opt/WordNet-3.1/") </item>
</ol>
</item> Usage:
<ul>
<item>Link to BabelNet:<br/> 
 execute <i>linker2BN.sh FOLDER FILE ITERATIONS</i></item>
<item>Link to WordNet:<br/> 
execute <i>linker2WN.sh FOLDER FILE ITERATIONS WORDNETFOLDER</i></item>
</ul>
</ul>