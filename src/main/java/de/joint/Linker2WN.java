package de.joint;

import de.joint.linking.IterativeMappingToWordNet;
import de.joint.linking.MonosemousMappingToWordNet;
import java.io.IOException;

public class Linker2WN {
    

    public static void main(String[] args) throws IOException {
        printHeader();
        if (args == null || args.length < 3) {
            printUsage();
            System.exit(0);
        }
        String dataFolder = args[0];
        String csvFilename = args[1];
        int iterations = new Integer(args[2]);
        String wnlocation=args[3]+"dict/";
        System.out.println("Mapping the monosemous senses");
        MonosemousMappingToWordNet.map(dataFolder, csvFilename,wnlocation);
        System.out.println("Mapping the polysemous senses");
        for (int iteration = 1; iteration < iterations; iteration++) {
            IterativeMappingToWordNet.map(dataFolder, csvFilename, iteration,wnlocation);
        }

    }

    public static void printHeader() {

        System.out.println("DDTs Linker2WN v 1.0");
    }

    public static void printUsage() {

        System.out.println("linker2WN.sh <datafolder> <csvfilename> <numofiterations> <wordnetfolder>");
        System.out.println("<datafolder> the path containing the ddt in the csv format;");
        System.out.println("<csvfilename> the filename of the ddt in the csv format;");
        System.out.println("<numofiterations> the number of iterations (integer value);");
        System.out.println("<wordnetfolder> the path to WordNet installation);");
    }
}
