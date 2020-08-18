package MasterQC;
import htsjdk.variant.example.PrintVariantsExample;
import htsjdk.*;
import javafx.scene.shape.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunQCModule {
    public static void main(String[] args){
        final long start = System.currentTimeMillis();
        //System.out.println(start);
        if (args.length < 1) {
            System.out.println("Uage: " + PrintVariantsExample.class.getCanonicalName() + " inFile [outFile]");
            System.exit(1);
        }
//        System.out.println("Please enter the complete path to the input file");
        File inputFile = new File(args[0].trim());
        if (!inputFile.exists()) {
            System.out.println("File does not exist");
        }
//        File inputFile2 = new File(args[1]);
//        try {
//            Runtime.getRuntime().exec("samtools view -h " + args[0] + " > temp.sam");
//            File targetsam = new File("temp.sam");
//        } catch (Exception e) {
//            //do nothing for now
//        }
        try {
            String s;
            SAMMappingChecks mapqc = new SAMMappingChecks(inputFile);
            System.out.println("trying 1");
            Process p = Runtime.getRuntime().exec("bamtools stats -in " + inputFile + " -insert");
            System.out.println("trying 2");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            System.out.println(p);
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            mapqc.calculate_mapping_checks();    System.out.println("Fraction of duplicate reads: " + Float.toString(mapqc.getFraction_dups()));
            System.out.println("Fraction of uniquely-mapped, high quality reads: " + Float.toString(mapqc.getFraction_unique_hq_reads()));
            System.out.println("Fraction of unmapped reads: " + Float.toString(mapqc.getFraction_unmapped_reads()));
            System.out.println("Total reads: " + mapqc.getTotal_reads());
            System.out.println("Median insert size: " + mapqc.getMedian_insertsize());
            System.out.println("Total number paired reads: " + mapqc.getTotal_Paired_reads());
               } catch(IOException e) {
            System.out.println(e.getMessage());
            SAMMappingChecks mapqc =null;
}       //mapqc.initialize_defaults(inputFile);

        final long end = System.currentTimeMillis();
        //System.out.println(end);
        float time_taken = (float)(end-start)/1000;
        if (time_taken <60) {
            System.out.println("Time taken: " + (float)(end-start)/1000 + "s");
        }
        else {
            System.out.println("Time taken: " + time_taken/60 + "min " + time_taken%60 + "s");
        }
    }
}
