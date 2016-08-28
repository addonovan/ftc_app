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

import addonovan.kftc.KOpMode
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range

/**
 * A port of the PushBot example to the kftc framework.
 *
 * This is based off of `PushbotTeleopTank_Iterative`.
 *
 * @author addonovan
 * @since 8/27/16
 */
@TeleOp( name= "Pushbot: Teleop Tank", group= "Pushbot" )
//@Disabled
@Suppress( "unused" )
class KPushBot : KOpMode()
{

    /**
     * This companion object allows the hardware to be completely
     * initialized in another file, yet gives us full access to it
     * so we can access it like all the methods and fields were
     * initialized in this class. Neat, huh?
     */
    companion object : KPushBotHardware();

    /** Servo mid position */
    var clawOffset = 0.0;

    /** sets rate to move servo */
    // question: Why are the Qualcomm comments so terse and uninformative??
    val CLAW_SPEED = 0.02;

    //
    // Overrides
    //

    override fun init()
    {
        Telemetry.addData( "Say", "Hello Driver" );
        Telemetry.update();
    }

    override fun loop()
    {
        // Run wheels in tank mode (note: the joystick goes negative when pushed forwards, so negate it)
        val left = -Gamepad1.left_stick_y.toDouble();
        val right = -Gamepad1.right_stick_y.toDouble();
        leftMotor.power = left;
        rightMotor.power = right;

        // Use gamepad left & right bumpers to open and close the claw
        if ( Gamepad1.right_bumper ) clawOffset += CLAW_SPEED;
        else if ( Gamepad1.left_bumper ) clawOffset -= CLAW_SPEED;

        // move both servos to new position. Assume servos are mirror image of each other.
        clawOffset = Range.clip( clawOffset, -0.5, 0.5 );
        leftClaw.position = KPushBotHardware.MID_SERVO + clawOffset;
        rightClaw.position = KPushBotHardware.MID_SERVO - clawOffset;

        // use gamepad buttons to move the arm up (Y) and down (A)
        if ( Gamepad1.y ) armMotor.power = KPushBotHardware.ARM_UP_POWER;
        else if ( Gamepad1.a ) armMotor.power = KPushBotHardware.ARM_DOWN_POWER;
        else armMotor.power = 0.0;

        // send telemetry message to signify robot running;
        Telemetry.addData( "claw", "Offset = %.2f", clawOffset );
        Telemetry.addData( "left", "%.2f", left );
        Telemetry.addData( "right", "%.2f", right );
        Telemetry.update();
    }

}