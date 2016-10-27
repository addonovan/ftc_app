/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Austin Donovan (addonovan)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package kftc.examples

import addonovan.kftc.hardware.HardwareDefinition
import addonovan.kftc.hardware.Motor
import addonovan.kftc.util.MotorGroup
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.util.ElapsedTime

/**
 * Port of the PushBot's hardware definitions.
 *
 *  @author addonovan
 * @since 8/27/16
 */
abstract class KPushBotHardware : HardwareDefinition()
{

    // Companion object for constants
    // these could go along with the other vals, if you don't care
    // about them being consts or not. It works either way.
    companion object
    {
        const val MID_SERVO = 0.5;
        const val ARM_UP_POWER = 0.45;
        const val ARM_DOWN_POWER = -0.45;
    }

    val leftMotor  = get< Motor >( "left motor" );
    val rightMotor = get< Motor >( "right motor" );

    /** Used to move the motors at the same power. */
    val movementMotors = MotorGroup( leftMotor, rightMotor );

    val armMotor = get< Motor >( "arm motor" );

    val leftClaw = get< Servo >( "left claw" );
    val rightClaw = get< Servo >( "right claw" );

    private val period = ElapsedTime();

    init
    {
        leftMotor.direction  = DcMotorSimple.Direction.FORWARD;  // REVERSE if using AndyMark
        rightMotor.direction = DcMotorSimple.Direction.REVERSE; // FORWARD if using AndyMark

        // set all motors to zero power
        leftMotor.power  = 0.0;
        rightMotor.power = 0.0;
        armMotor.power   = 0.0;

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftMotor.mode  = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        rightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        armMotor.mode   = DcMotor.RunMode.RUN_WITHOUT_ENCODER;

        // initialize ALL installed servos
        leftClaw.position = MID_SERVO;
        rightClaw.position = MID_SERVO;
    }

    @Suppress( "unused" )
    fun waitForTick( periodMs: Long )
    {
        val remaining = periodMs - period.milliseconds().toLong();

        // sleep for the remaining portion of the regular cycle period.
        if ( remaining > 0 ) Thread.sleep( remaining );

        // reset the cycle clock for the next pass.
        period.reset();
    }

}
