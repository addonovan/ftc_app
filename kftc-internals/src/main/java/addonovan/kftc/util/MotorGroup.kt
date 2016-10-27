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
package addonovan.kftc.util

import addonovan.kftc.Task
import addonovan.kftc.hardware.Motor

/**
 * A [MotorGroup] represents multiple motors whose powers should all be
 * modified at one time. This is essentially ease-of-access for setting the
 * power of multiple motors at a time.
 *
 * @author addonovan
 * @since 10/27/2016
 */
class MotorGroup( vararg _motors: Motor )
{

    //
    // Fields
    //

    /** The motors that this motor group will control. */
    private val motors = listOf( *_motors );

    //
    // Motor Properties
    //

    /**
     * Sets the power of all of the motors in this motor group. This cannot be read from., only written to.
     */
    var power: Double
        get() = throw IllegalAccessException( "Can't access the power of a MotorGroup! This value is write-only!" );
        set( value )
        {
            motors.forEach { m -> m.power = value };
        }

    //
    // Actions
    //

    /**
     * Resets all the motor encoders on the motors in this group.
     */
    fun resetEncoders()
    {
        motors.forEach( Motor::resetEncoders );
    }

    /**
     * Sets all the power of the motors in this motor group to zero.
     */
    fun brake()
    {
        power = 0.0;
    }

    //
    // Tasks
    //

    fun moveDistance( distance: Double, power: Double )
    {
        motors.forEach { m -> m.moveDistance( distance, power ) }
    }

}