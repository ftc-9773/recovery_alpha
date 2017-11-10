package org.firstinspires.ftc.teamcode.infrastructure;

import android.text.BoringLayout;

/**
 * Created by robocracy on 10/9/17.
 */

public class ButtonStatus {
    private boolean currVal;
    private boolean lastVal;

    public ButtonStatus() {
        this.currVal = this.lastVal = false;
    }

    // each iteration, you can push a new value here, give the gamepad.x
    public void recordNewValue(boolean newButtonValue) {
        this.lastVal = this.currVal;
        this.currVal = newButtonValue;
    }

    public boolean isJustOn() { return (this.currVal && ! this.lastVal); }

    public boolean isJustOff() { return (! this.currVal && this.lastVal); }

    public boolean isOn() { return this.currVal; }

    public boolean isOff() { return ! this.currVal; }
}
