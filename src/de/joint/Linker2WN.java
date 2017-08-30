package de.joint;

import de.joint.linking.IterativeMappingToWordNet;
import de.joint.linking.MonosemousMappingToWordNet;
import java.io.IOException;

public class Linker2WN {
    public final static String wnlocation="/opt/WordNet-3.0/dict/";

    public static void main(String[] args) throws IOException {
        printHeader();
        if (args == null || args.length < 3) {
            printUsage();
            System.exit(0);
        }
        String dataFolder = args[0];
        String csvFilename = args[1];
        int iterations = new Integer(args[2]);
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

        System.out.println("linkerWN.sh <datafolder> <csvfilename> <numofiterations>");
        System.out.println("<datafolder> the path containing the ddt in the csv format;");
        System.out.println("<csvfilename> the filename of the ddt in the csv format;");
        System.out.println("<numofiterations> the number of iterations (integer value);");
    }
}
