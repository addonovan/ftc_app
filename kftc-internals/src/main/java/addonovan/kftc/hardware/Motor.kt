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
@file:Suppress("unused")
package addonovan.kftc.hardware

import addonovan.kftc.*
import com.qualcomm.robotcore.hardware.*

/**
 * An abstraction on top of the [DcMotor] which makes it much easier
 * to move via encoders and distances. The [assembly] value allows
 * for the motor assembly to be specified so that movement by encoders
 * becomes a trivial task.
 *
 * A big thing to note is that motor values are no longer represented
 * on the scale [-1.0, 1.0], but now are on the interval [-100.0, 100.0],
 * because they are much easier to read that way.
 *
 * @param[dcMotor]
 *          The motor this is based off of.
 * @param[name]
 *          The name of this device in the hardware map.
 *
 * @author addonovan
 * @since 6/27/16
 */
@HardwareExtension( DcMotor::class )
class Motor( dcMotor: DcMotor, name: String ) : DcMotorImpl( dcMotor.controller, dcMotor.portNumber, dcMotor.direction )
{

    //
    // Motor identification
    //

    /** The name of this motor in the hardware map */
    val Name = "Motor: $name";

    //
    // Motor Assembly
    //

    /**
     * The motor assembly that this motor is a part of. By default,
     * this represents an assembly with a tetrix motor, with a 4 inch
     * (10.16 cm) wheel, and a 1:1 gear ratio.
     */
    private var assembly: MotorAssembly = MotorAssembly( MotorType.TETRIX );

    /**
     * @param[assembly]
     *          The new motor assembly.
     * @return This [Motor] so that this method can be invoked on initialization.
     */
    fun setAssembly( assembly: MotorAssembly ): Motor
    {
        this.assembly = assembly;
        return this;
    }

    /**
     * @return The current motor assembly.
     */
    fun getAssembly() = assembly;

    //
    // Encoders
    //

    /**
     * Resets the motor encoders.
     */
    fun resetEncoders()
    {
        // this is guaranteed since they finally made the SDK synchronous
        setMode( DcMotor.RunMode.STOP_AND_RESET_ENCODER );
    }

    /**
     * Creates and registers a task with the [TaskManager] that wil
     * make the motor move at the given power until the given distance
     * has been covered.
     *
     * If the OpMode is linear, then whenever this method returns, the
     * task will have been completed.
     *
     * This is not guaranteed to be accurate, as it is entirely dependent
     * upon motor encoders!
     *
     * @param[distance]
     *          The distance to cover (in cm).
     * @param[power]
     *          The power to move the motor at.
     * @return The created task. (Use [Task.isFinished] to determine
     *         when the task has been finished).
     */
    fun moveDistance( distance: Double, power: Double ): Task
    {
        val ticks = assembly.toTicks( distance ); // pre-calculate the number of ticks
        resetEncoders(); // register the task for resetting the encoders

        // create the task
        val task = object : Task
        {

            override fun tick()
            {
                setPower( power ); // continually set the power
            }

            // we're only finished once we're in the right place
            override fun isFinished(): Boolean
            {
                return currentPosition == ticks;
            }

            // we reached the goal
            override fun onFinish()
            {
                brake();
            }

        };

        TaskManager.registerTask( task, "$Name running for $ticks encoder ticks" );
        return task;
    }

    //
    // Basic Movement
    //

    /**
     * Sets the power of the motor to be 0%.
     */
    fun brake()
    {
        power = 0.0;
    }

    /**
     * @param[power]
     *          The power to set the motor at [-1,1].
     */
    override fun setPower( power: Double )
    {
        super.setPower( power );
    }

    /**
     * @return The power, on a scale of [-1,1].
     */
    override fun getPower() = super.getPower();

}