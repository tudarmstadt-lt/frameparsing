#Linked Disambiguated Distributional Semantic Networks<br>
Source code related to the <a href="http://web.informatik.uni-mannheim.de/joint/">JOIN-T</a> project.
 
#Creation of Disambiguated Distributional Semantic-based Sense Inventories (PCZs)<br>

Our approach consists of three main phases (Figure 1):<br>
1) Learning a JoBimText model: initially, we automatically create a sense inventory from a large text collection using the pipeline of the JoBimText project;<br>
2) Disambiguation of related words: we fully disambiguate all lexical information associated with a proto-concept (i.e. similar terms and hypernyms) based on the partial disambiguation from step 1). The result is a proto-conceptualization (PCZ). In contrast to a term-based distributional thesaurus (DT), a PCZ consists of sense-disambiguated entries, i.e. all terms have a sense identifier (Table 1);<br>
3) Linking to a lexical resource: we align the PCZ with an existing lexical resource (LR). That is, we create a mapping between the two sense inventories and then combine them into a new extended sense inventory, our hybrid aligned resource. Finally, to obtain a truly unified resource, we link the "orphan" PCZ senses for which no corresponding sense could be found by inferring their type in the LR.<br>


![ScreenShot](http://web.informatik.uni-mannheim.de/joint/img/jointworkflow.png)<br>
Figure 1: Overview of our pipeline for the construcion of a hybrid aligned resource.

