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
import addonovan.kftc.Task
import addonovan.kftc.TaskManager

/**
 *
 *
 * @author addonovan
 * @since 10/14/2016
 */
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

        override fun onStart()
        {

        }

        override fun tick()
        {

        }

        override fun isFinished(): Boolean
        {
            return false;
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

    }

}
