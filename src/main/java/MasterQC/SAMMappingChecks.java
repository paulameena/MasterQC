package MasterQC;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import htsjdk.samtools.*;


public class SAMMappingChecks {

    private SamReader reader;
    private SamReaderFactory readerFactory;
    private HashMap<Integer, Integer> viewed_reads; //key = read_ids; value = chromosome number
    private SAMFileHeader sam_header;
    private int duplicate_reads;
    private int paired_reads;
    private int total_reads;
    private ArrayList<Integer> insertsizes;
    private float median_insertsize;
    private int total_unique_hq_reads;
    private float fraction_unique_hq_reads;
    private int unmapped_reads;
    private float fraction_dups;
    private float fraction_unmapped_reads;


    public SAMMappingChecks(File file) {
        //SamReaderFactory readerFactory2 = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT);
        //this.readerFactory = readerFactory;
        //System.out.println(file.exists());
        //System.out.println(file.getPath());
        this.reader = SamReaderFactory.makeDefault().open(file);
        this.viewed_reads = new HashMap();
        this.sam_header = reader.getFileHeader();
        this.total_reads = 0;
        this.median_insertsize = 0;
        this.insertsizes = new ArrayList<Integer>();
        this.total_unique_hq_reads = 0;
        //this.fraction_unique_hq_reads = 0;
        this.paired_reads = 0;
        this.sam_header = this.reader.getFileHeader();
        this.unmapped_reads = 0;
        System.out.println(this.sam_header);
    }

    private float determine_median() {
        Collections.sort(this.insertsizes);
        if(this.insertsizes.size() % 2 == 0) {
            return this.insertsizes.get(this.insertsizes.size() / 2);
        } else {
            return (this.insertsizes.get(this.insertsizes.size()/2) + (this.insertsizes.get(this.insertsizes.size()/2 -1)))/2;
        }
    }
    public void calculate_mapping_checks() {
        for (SAMRecord read : this.reader) {
            //System.out.println(read.getAlignmentStart());
            //System.out.println(read.getCigar());
            //System.out.println();
            this.total_reads += 1;
            if (read.getDuplicateReadFlag()) {
                this.duplicate_reads += 1;
                //System.out.println("entered this loop 1");
                //TODO: confirm vs original +2 in python
            }
            if (read.getReadPairedFlag()) {
                this.paired_reads += 1;
            }
            if (read.getReadUnmappedFlag() || read.getMappingQuality() == 255) {
                //System.out.println("entered this loop 2");
                this.unmapped_reads += 1;
            }
            //TODO: confirm cutoff for high quality?
            if (!read.isSecondaryOrSupplementary() && read.getMappingQuality() >= 30) {
                this.total_unique_hq_reads +=1;
                //System.out.println("entered this loop 3");
            }
            //this.insertsizes.add(read.getInferredInsertSize());
            /*TODO: confirm below is accurate;
            when i tried to use the built in function the median was 0 and
            the insert sizes listed were sometimes negative and overall had a very wide range*/
            this.insertsizes.add(read.getAlignmentEnd()-read.getAlignmentStart());

        }
        this.median_insertsize=determine_median();
        System.out.println(insertsizes);
//        System.out.println();
        this.fraction_dups = this.duplicate_reads/this.total_reads;
        //System.out.println(unmapped_reads);
        this.fraction_unmapped_reads = (float)(this.unmapped_reads / this.total_reads);
        //System.out.println(total_unique_hq_reads);
        this.fraction_unique_hq_reads = (float) this.total_unique_hq_reads/this.total_reads;


    }

    /*Set up initial class variables based on input sam/bam file; currently assuming sam because I can visually check
    * printed/system output but will have to generalize to bam and cram files eventually
    * TODO: generalize to bam/cram inputs as well and check for file type upon initial input*/
    public void initialize_defaults(File file) {
        SamReaderFactory readerFactory2 = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT);
        this.readerFactory = readerFactory2;
        this.reader = this.readerFactory.open(file);
        this.viewed_reads = new HashMap();
        this.sam_header = reader.getFileHeader();
        this.total_reads = 0;
        this.median_insertsize = 0;
        this.insertsizes = new ArrayList<Integer>();
        this.total_unique_hq_reads = 0;
        //this.fraction_unique_hq_reads = 0;
        this.paired_reads = 0;
        this.sam_header = this.reader.getFileHeader();
        this.unmapped_reads = 0;
        System.out.println(this.sam_header);

    }

//    public void main(String[] args){
//        if (args.length < 1) {
//            System.out.println("Usage: " + PrintVariantsExample.class.getCanonicalName() + " inFile [outFile]");
//            System.exit(1);
//        }
//        File inputFile = new File(args[0]);
//        initialize_defaults(inputFile);
//        calculate_mapping_checks();
//
//
//    }


    public int getTotal_reads() {
        return total_reads;
    }

    public float getMedian_insertsize() {
        return median_insertsize;
    }

    public float getFraction_unique_hq_reads() {
        return fraction_unique_hq_reads;
    }

    public float getFraction_dups() {
        return fraction_dups;
    }

    public float getFraction_unmapped_reads() {
        return fraction_unmapped_reads;
    }

    public int getTotal_Paired_reads() {
        return paired_reads;
    }
}

