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
package addonovan.kftc.hardware

import addonovan.kftc.Task
import addonovan.kftc.TaskManager
import addonovan.kftc.util.MotorAssembly
import addonovan.kftc.util.MotorType
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorImpl

/**
 * A type of [DcMotor] that's been locked into running at a certain
 * amount of output RPM. This requires the motor to have encoders on it!
 *
 * The output RPM is calculated from the motor's encoder values and how quickly
 * they change, as they relate to the change in time since the last update. Because
 * this runs asynchronously as a task, it is not able to be used in a [KLinearOpMode],
 * as the task which handles the power updates would not be able to run in the background.
 *
 * The underlying DcMotor functions are not accessible in this class, only
 * the [targetRPM] value is modifiable.
 *
 * @author addonovan
 * @since 10/27/2016
 */
@Suppress( "unused" )
@HardwareExtension( DcMotor::class )
class RpmLockedMotor( dcMotor: DcMotor, name: String ) : DcMotorImpl( dcMotor.controller, dcMotor.portNumber, dcMotor.direction )
{

    //
    // RPM Locking
    //

    /**
     * The actual task that locks the rpm on this motor, as much as it can, given power requirements.
     */
    private val rpmLock = object : Task
    {

        override fun tick()
        {

        }

        // we can start immediately and we will finish whenever the robot is shut down
        override fun canStart() = true;
        override fun isFinished() = false;

    }

    //
    // Properties
    //

    /** The target RPM that this motor will always try to achieve. */
    var targetRPM: Int = 0;

    /** The backing field for [assembly] */
    private var _assembly = MotorAssembly( MotorType.TETRIX );

    /** The motor assembly on this motor. */
    val assembly: MotorAssembly
        get() = _assembly;

    //
    // Constructors
    //

    init
    {
        // register the task if we're a non-Linear OpMode
        if ( TaskManager.isLinearOpMode )
        {
            throw IllegalStateException( "RpmLockedMotor cannot be used in an KLinearOpMode! Check the documentation for why!" );
        }

        TaskManager.registerTask( rpmLock, "RPM Lock for motor: $name" );
    }

    //
    // Set Assembly
    //

    /**
     * Sets the assembly of this [RpmLockedMotor].
     *
     * @param[assembly]
     *          The new assembly on this motor.
     *
     * @return This [RpmLockedMotor] so that this may be done on declaration.
     */
    fun setAssembly( assembly: MotorAssembly ): RpmLockedMotor
    {
        _assembly = assembly;
        return this;
    }

    //
    // Block the underlying methods on this
    //

    /** Set to true only when the task is being ticked, which allows us to update the values. */
    private var updatesPermitted = false;

    override fun setPower( power: Double )
    {
        if ( !updatesPermitted) throwModifiedException( "power" );
        super.setPower( power );
    }

    override fun setMode( mode: DcMotor.RunMode )
    {
        if ( !updatesPermitted) throwModifiedException( "run mode" );
        super.setMode( mode );
    }

    override fun setZeroPowerBehavior( zeroPowerBehavior: DcMotor.ZeroPowerBehavior )
    {
        if ( !updatesPermitted ) throwModifiedException( "zero power behavior" );
        super.setZeroPowerBehavior( zeroPowerBehavior );
    }

    override fun setPowerFloat()
    {
        throw UnsupportedOperationException( "dude, this is deprecated, stop using it." );
    }

    override fun setMaxSpeed( encoderTicksPerSecond: Int )
    {
        if ( !updatesPermitted ) throwModifiedException( "max speed" );
        super.setMaxSpeed( encoderTicksPerSecond );
    }

    override fun setTargetPosition( position: Int )
    {
        if ( !updatesPermitted ) throwModifiedException( "target position" );
        super.setTargetPosition( position );
    }

    /**
     * Throws the exception that tells the user to fuck off.
     *
     * @param[name]
     *          The name of the property trying to be modified by an idiotic user.
     */
    private fun throwModifiedException( name: String )
    {
        throw UnsupportedOperationException( "Cannot modify $name on an RpmLockedMotor!" );
    }

}
