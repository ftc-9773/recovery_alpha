package org.firstinspires.ftc.teamcode.infrastructure;

/**
 * Created by robocracy on 11/11/17.
 */

public class InstrumentDoubleArray extends Instrumentation {
    private int num = 0;
    private String header;
    private double[] lastEntry = null;
    private double tolerence = 1e-6;
    private long identicalCount = -1;
    private boolean printedTitle = false;

    public InstrumentDoubleArray(String fileName, int num, String entryDescriptionCSV, double tolerence) {
        super(fileName);
        if (! ENABLED) return;
        assert(num>0 && num<64);
        this.num = num;
        this.lastEntry = new double[num];
        this.tolerence = tolerence;
        writeHeader("identicalCount, " + entryDescriptionCSV);
    }

    // write the new vector of double values if any are different from the last ones
    public void push(double[] newEntry) {
        if (! ENABLED || this.enabled) return;
        int i;
        boolean identical = true;
        if (this.identicalCount == -1) {
            // first time, force identical to false
            identical = false;
            this.identicalCount = 0;
        } else {
            // next time, have valid last entry, check for difference
            for (i = 0; i < this.num; i++) {
                double diff = Math.abs(newEntry[i] - this.lastEntry[i]);
                if (diff > this.tolerence) {
                    identical = false;
                    break;
                }
            }
            if (identical) {
                this.identicalCount++;
                return;
            }
        }
        writeElement(String.format ("%d", this.identicalCount));
        for (i=0; i<this.num; i++) {
            writeElement(String.format ("%f", newEntry[i]));
            this.lastEntry[i] = newEntry[i];
        }
        writeElementDone();
        this.identicalCount = 0;
    }
}
