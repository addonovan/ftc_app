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

import addonovan.kftc.Task
import addonovan.kftc.TaskManager
import addonovan.kftc.util.Interval
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.hardware.DcMotor

/**
 *
 *
 * @author addonovan
 * @since 10/14/2016
 */
@Autonomous( name= "KPushBotAutonomous" )
@Disabled
@Suppress( "unused" )
class KPushBotAutonomous : KOpMode()
{

    /**
     * companion object to give hardware access to this class after initialization.
     */
    companion object : KPushBotHardware();

    //
    // Tasks
    //

    /*
     * A task is a simple snippet of code that will begin to run after it's
     * registered with the TaskManager and its canStart() method returns true.
     * It will then continuously run (regardless of canStart()'s value) until
     * the isFinished() method returns true, upon which the task's onFinish()
     * is called then deregistered.
     *
     * These are useful to have re-usable linear sections of code that can be
     * run inside of a regular KOpMode and not a KLinearOpMode.
     */

    /** This will move forward for 3 seconds */
    private val moveForward = object : Task
    {

        private lateinit var interval: Interval;

        override fun onStart()
        {
            interval = Interval( 2500 );
        }

        override fun tick()
        {
            movementMotors.power = 1.0;
        }

        override fun isFinished(): Boolean
        {
            return !interval.isActive();
        }

        override fun onFinish()
        {
            movementMotors.brake();

            // after this task finished, we can move on to the next one if the tasks
            // are just need to run linearly
            TaskManager.registerTask( grabClaw, "grab object" );
        }
    }

    /** This will close the claw to grab something */
    private val grabClaw = object : Task
    {

        override fun tick()
        {
            leftClaw.position = 0.5;
            rightClaw.position = 0.5;
        }

        override fun isFinished(): Boolean
        {
            return ( leftClaw.position == 0.5 && rightClaw.position == 0.5 );
        }

    }

    //
    // KOpMode Hooks
    //

    override fun start()
    {
        TaskManager.registerTask( moveForward, "move forward" );
    }

    override fun loop()
    {
        // anything else you would need (e.g. telemetry/logging) to do
        // in the loop, aside from tasks.
    }

}
