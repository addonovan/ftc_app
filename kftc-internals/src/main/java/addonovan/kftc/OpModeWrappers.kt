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
package addonovan.kftc

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode

/**
 * A wrapper that converts commands to a Qualcomm OpMode to a KOpMode.
 *
 * @param[clazz]
 *          The KOpMode's class to wrap around.
 *
 * @author addonovan
 * @since 8/21/16
 */
class KOpModeWrapper( private val clazz: Class< out KOpMode > ) : OpMode()
{
    /**
     * The instance we're wrapping around.
     */
    private lateinit var instance: KOpMode;

    override fun init()
    {
        AddOpModeRegister.CurrentOpMode = this; // set the current opmode to us, so the KOpMode can call methods on us

        unhookRobotIcon(); // Unhooks the listener because we're running now
        updateUtilities( this );
        instance = clazz.newInstance();
        instance.init();
    }

    override fun init_loop()
    {
        updateUtilities( this );
        instance.init_loop();
    }

    override fun start()
    {
        updateUtilities( this );
        instance.start();
    }

    override fun loop()
    {
        updateUtilities( this );
        TaskManager.tick(); // tick the task manager for the users
        instance.loop();
        telemetry.update(); // automatically update telemetry for the users
    }

    override fun stop()
    {
        updateUtilities( this );
        instance.stop();
    }

}

/**
 * A wrapper that converts commands to a Qualcomm LinearOpMode to a KLinearOpMode.
 *
 * @param[clazz]
 *          The KLinearOpMode's class to wrap around.
 *
 * @author addonovan
 * @since 8/21/16
 */
class KLinearOpModeWrapper( private val clazz: Class< out KLinearOpMode > ) : LinearOpMode()
{
    /**
     * The actual instance of the KLinearOpMode that we're wrapping around.
     */
    private lateinit var instance: KLinearOpMode;

    override fun runOpMode()
    {
        AddOpModeRegister.CurrentLinearOpMode = this; // update this reference so the KLinearOpMode can call us

        unhookRobotIcon(); // Unhooks the listener because we're running now
        updateUtilities( this );
        instance = clazz.newInstance();
        instance.runOpMode();
    }

}

//
// Actions
//

/**
 * Updates all the utilities with the given OpMode's.
 *
 * @param[opMode]
 *          The OpMode to get utilities from.
 */
private fun updateUtilities( opMode: OpMode )
{
    UtilityContainer.HardwareMap = opMode.hardwareMap;
    UtilityContainer.Gamepad1 = opMode.gamepad1;
    UtilityContainer.Gamepad2 = opMode.gamepad2;
    UtilityContainer.Telemetry = opMode.telemetry;
}