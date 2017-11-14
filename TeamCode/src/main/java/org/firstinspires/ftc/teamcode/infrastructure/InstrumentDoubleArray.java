package org.firstinspires.ftc.teamcode.infrastructure;

/**
 * Created by robocracy on 11/11/17.
 *
 * Create a new statistic by creating an object, passing the name, the number of double,
 * a comma separated header string, and the tolerence for which you want to consider
 * 2 consecutive numbers (in time) to be considered as the same.
 *
 * For the moment, we limit the number of doubles to less than 64.
 *
 * To push a stat, call push with a vector of new values.
 *
 * Stats can be enabled (default on) and disabled by calls to enable() and disabled()
 *
 * close() should be called before exiting.
 *
 * For example:
 *
 * // init
 * InstrumentDoubleArray myMotorStat =
 *   new InstrumentDoubleArray("myMotorStat", 2, "motor x, motor y", 10e-6)
 *
 * // push stat (ideally, the creating of the array is done only once, and is part of another object)
 * double[] newStat = new double[2];
 * newStat[0] = 0; newStat[1] = 1
 * myMotorStat.push(newStat);
 *
 * // disable for a while
 * myMotorStat.disable();
 *
 * // re-enable some time later
 * myMotorStat.enable();
 *
 * // at the end
 * myMotorStat.close()
 */

public class InstrumentDoubleArray extends Instrumentation {
    private int num = 0;
    private String header;
    private double[] lastEntry = null;
    private double tolerence = 1e-6;
    private long identicalCount = -1;
    private boolean printedTitle = false;

    /////////////////////////////////////////////////////////////////
    // inherited user interface

    // check Instrumentation's user interface (disable, enable, close)

    /////////////////////////////////////////////////////////////////
    // main user interface

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
