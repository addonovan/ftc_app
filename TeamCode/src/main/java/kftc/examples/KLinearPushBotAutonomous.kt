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

import addonovan.kftc.util.Interval
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled

/**
 * This will demonstrate how to use a KLinearOpMode with the PushBot
 * Autonomous' mode.
 *
 * This will show how using the Task system and Linear OpMode system
 * will differ when it comes to autonomous. While this seems to be
 * much easier than using the task system, the task system can be
 * much easier to control for more complex autonomous programs with
 * much more complicated start and end conditions.
 *
 * @author addonovan
 * @since 10/27/2016
 */
@Autonomous( name= "KLinearPushBotAutonomous" )
@Disabled
@Suppress( "unused" )
class KLinearPushBotAutonomous : KLinearOpMode()
{

    /**
     * companion object to give hardware access to this class after initialization.
     */
    companion object : KPushBotHardware();

    //
    // Overrides
    //

    override fun runOpMode()
    {
        // run the motors for 2.5 seconds
        val interval = Interval( 2500 );
        while ( interval.isActive() )
        {
            movementMotors.power = 1.0;
        }
        movementMotors.brake();

        // make the claws clasp whatever's in front of it
        while ( leftClaw.position != 0.5 && rightClaw.position != 0.5 )
        {
            leftClaw.position = 0.5;
            rightClaw.position = 0.5;
        }
    }

}
