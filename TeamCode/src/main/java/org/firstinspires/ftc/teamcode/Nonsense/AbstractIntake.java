package org.firstinspires.ftc.teamcode.Nonsense;
/**
 * Defines the functionality it is necessary for an intake to have.
 *
 * @author Cadence
 * @version 1.0
 * */
public abstract class AbstractIntake {
    public enum intakeStates {INTAKE, TRANSFER, STORE};

    /**
     * Set the state of the intake to one of intakeStates
     * Transfer : move intake into a position to transfer the cubes to the lift mechanism
     * Intake : move intake into a position to pick up cubes
     * Store : move intake into a position needed for the start of the game.
     * */
    public abstract void transferState();
    public abstract void intakeState();
    public abstract void storeState();

    public abstract void intakeOn();
    public abstract void intakeOff();
    //public abstract void setPower(double pow);
}
