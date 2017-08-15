
package de.joint.frames.parsing.data;
import java.net.*;
import java.io.*;
import java.util.*;
import net.sf.json.JSONArray;

/**
 *
 * @author sfaralli
 */
public class BabelFy 
{
    
     private final static String userkey="yourkeyhere";
     private final static String USER_AGENT = "Mozilla/5.0";
     
    public static String babelfy(String text) throws UnsupportedEncodingException, Exception
    {
        //https://babelfy.io/v1/disambiguate?text=BabelNet%20is%20both%20a%20multilingual%20encyclopedic%20dictionary%20and%20a%20semantic%20network&lang=EN&key=KEY
     return sendGet("https://babelfy.io/v1/disambiguate?text="+URLEncoder.encode(text, "UTF-8")+"&lang=EN&key="+userkey);
    }
    // HTTP GET request
    private static String sendGet(String url) throws Exception 
        {

		//String url = "http://www.google.com/search?q=mkyong";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		return response.toString();

	}
    public static List<BFyAnnotation> parse(String json)
    { 
        
        List<BFyAnnotation> result=new ArrayList<>();
    JSONArray arr = JSONArray.fromObject(json);
     List<String> list = new ArrayList<String>();
     for(int i = 0; i < arr.size(); i++)
        {
            
            String tokenFragment_s=arr.getJSONObject(i).getJSONObject("tokenFragment").getString("start");
            String tokenFragment_e=arr.getJSONObject(i).getJSONObject("tokenFragment").getString("end");
            String charSegment_s=arr.getJSONObject(i).getJSONObject("charFragment").getString("start");
            String charSegment_e=arr.getJSONObject(i).getJSONObject("charFragment").getString("end");
            String babelSynsetID=arr.getJSONObject(i).getString("babelSynsetID");
            String DBpediaURL=arr.getJSONObject(i).getString("DBpediaURL");
            String BabelNetURL=arr.getJSONObject(i).getString("BabelNetURL");
            String score=arr.getJSONObject(i).getString("score");
            String coherenceScore=arr.getJSONObject(i).getString("coherenceScore");
            String globalScore=arr.getJSONObject(i).getString("globalScore");
            String source=arr.getJSONObject(i).getString("source");
            
            result.add(
            new BFyAnnotation(new Integer(tokenFragment_s),
                                new Integer(tokenFragment_e),
                                new Integer(charSegment_s),
                                new Integer(charSegment_e),
                                    babelSynsetID,
                                DBpediaURL,
                                BabelNetURL,
                                new Double(score),
                                new Double(coherenceScore),
                                new Double(globalScore),
                                source
                        )
            
            );
        }
    
      
        
        
        return result;
    }
    public static void main(String[] args)
    {
        
    String json="[{\"tokenFragment\":{\"start\":0,\"end\":1},\"charFragment\":{\"start\":0,\"end\":8},\"babelSynsetID\":\"bn:00836821n\",\"DBpediaURL\":\"http://dbpedia.org/resource/January_8\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00836821n\",\"score\":1.0,\"coherenceScore\":0.16666666666666666,\"globalScore\":0.011042944785276074,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":1,\"end\":1},\"charFragment\":{\"start\":2,\"end\":8},\"babelSynsetID\":\"bn:00047960n\",\"DBpediaURL\":\"http://dbpedia.org/resource/January\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00047960n\",\"score\":0.9795918367346939,\"coherenceScore\":0.3333333333333333,\"globalScore\":0.029447852760736196,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":1,\"end\":2},\"charFragment\":{\"start\":2,\"end\":13},\"babelSynsetID\":\"bn:02822563n\",\"DBpediaURL\":\"http://dbpedia.org/resource/1998\",\"BabelNetURL\":\"http://babelnet.org/rdf/s02822563n\",\"score\":1.0,\"coherenceScore\":0.7777777777777778,\"globalScore\":0.4208588957055215,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":3,\"end\":3},\"charFragment\":{\"start\":15,\"end\":21},\"babelSynsetID\":\"bn:16752786n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Persian_people\",\"BabelNetURL\":\"http://babelnet.org/rdf/s16752786n\",\"score\":1.0,\"coherenceScore\":0.2222222222222222,\"globalScore\":0.0098159509202454,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":3,\"end\":4},\"charFragment\":{\"start\":15,\"end\":31},\"babelSynsetID\":\"bn:01650357n\",\"DBpediaURL\":\"http://dbpedia.org/resource/President_of_Iran\",\"BabelNetURL\":\"http://babelnet.org/rdf/s01650357n\",\"score\":1.0,\"coherenceScore\":0.3333333333333333,\"globalScore\":0.04049079754601227,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":4,\"end\":4},\"charFragment\":{\"start\":23,\"end\":31},\"babelSynsetID\":\"bn:00064233n\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00064233n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":4,\"end\":6},\"charFragment\":{\"start\":23,\"end\":48},\"babelSynsetID\":\"bn:01203868n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Mohammad_Khatami\",\"BabelNetURL\":\"http://babelnet.org/rdf/s01203868n\",\"score\":1.0,\"coherenceScore\":0.3333333333333333,\"globalScore\":0.029447852760736196,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":5,\"end\":6},\"charFragment\":{\"start\":33,\"end\":48},\"babelSynsetID\":\"bn:01203868n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Mohammad_Khatami\",\"BabelNetURL\":\"http://babelnet.org/rdf/s01203868n\",\"score\":1.0,\"coherenceScore\":0.3333333333333333,\"globalScore\":0.029447852760736196,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":7,\"end\":7},\"charFragment\":{\"start\":50,\"end\":58},\"babelSynsetID\":\"bn:00092439v\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00092439v\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":9,\"end\":9},\"charFragment\":{\"start\":64,\"end\":77},\"babelSynsetID\":\"bn:00001422n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Business_administration\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00001422n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":11,\"end\":11},\"charFragment\":{\"start\":82,\"end\":87},\"babelSynsetID\":\"bn:00026550n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Desire\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00026550n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":13,\"end\":13},\"charFragment\":{\"start\":92,\"end\":100},\"babelSynsetID\":\"bn:00085279v\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00085279v\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":16,\"end\":16},\"charFragment\":{\"start\":111,\"end\":123},\"babelSynsetID\":\"bn:00047146n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Political_international\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00047146n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":16,\"end\":19},\"charFragment\":{\"start\":111,\"end\":144},\"babelSynsetID\":\"bn:00045680n\",\"DBpediaURL\":\"http://dbpedia.org/resource/International_Atomic_Energy_Agency\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00045680n\",\"score\":1.0,\"coherenceScore\":0.16666666666666666,\"globalScore\":0.007361963190184049,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":17,\"end\":17},\"charFragment\":{\"start\":125,\"end\":130},\"babelSynsetID\":\"bn:00344830n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Linearizability\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00344830n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":17,\"end\":18},\"charFragment\":{\"start\":125,\"end\":137},\"babelSynsetID\":\"bn:00006818n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Atomic_energy\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00006818n\",\"score\":1.0,\"coherenceScore\":0.05555555555555555,\"globalScore\":6.134969325153375E-4,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":18,\"end\":18},\"charFragment\":{\"start\":132,\"end\":137},\"babelSynsetID\":\"bn:00030820n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Thermodynamic_free_energy\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00030820n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":19,\"end\":19},\"charFragment\":{\"start\":139,\"end\":144},\"babelSynsetID\":\"bn:00001961n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Government_agency\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00001961n\",\"score\":0.8461538461538461,\"coherenceScore\":0.16666666666666666,\"globalScore\":0.020245398773006136,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":21,\"end\":21},\"charFragment\":{\"start\":148,\"end\":151},\"babelSynsetID\":\"bn:00045680n\",\"DBpediaURL\":\"http://dbpedia.org/resource/International_Atomic_Energy_Agency\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00045680n\",\"score\":1.0,\"coherenceScore\":0.16666666666666666,\"globalScore\":0.007361963190184049,\"source\":\"BABELFY\"}]";
    parse(json);
    }
}
