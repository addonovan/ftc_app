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

import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImpl

/**
 * A type of servo that can be toggled between two states
 * as well as acting like a normal servo.
 *
 * @author addonovan
 * @since 6/26/16
 */
class ToggleServo( servo: Servo ) : HardwareExtension< Servo >( servo ), Servo by servo
{

    //
    // State enum
    //

    /**
     * The state of a [ToggleServo].
     *
     * @param[StringValue]
     *          The value of the position as a string.
     */
    enum class State( val StringValue: String )
    {
        /** The servo is currently in its engaged state. */
        ENGAGED( "Engaged" ),

        /** The servo is currently in its disengaged state. */
        DISENGAGED( "Disengaged" );

    }

    //
    // Timing
    //

    /**
     * The amount of time (in milliseconds) to ensure between [toggle] calls go
     * through and toggle the position, by default this is 350.
     */
    var ToggleDelay: Long = 350;

    /** The last time the [toggle] call went through. */
    private var lastToggle: Long = 0;

    //
    // Current Positioning
    //

    /** The backing field for [CurrentPosition]. */
    private var _currentPosition = State.ENGAGED;

    /**
     * The current position of the ToggleServo.
     */
    val CurrentPosition: State
        get() = _currentPosition;

    //
    // Positions
    //

    /**
     * The engaged position of the Toggle Servo, [toggleOn] will set
     * the servo to this position. This value must be between [0.0, 1.0].
     */
    var EngagedPosition: Double = 0.0;
        get() = field;

        /**
         * @param[value]
         *          The new value, must be on [0, 1].
         */
        set( value )
        {
            if ( value < 0 ) throw IllegalArgumentException( "EngagedPosition cannot be less than 0!" );
            if ( value > 1 ) throw IllegalArgumentException( "EngagedPosition cannot be greater than 1!" );
            field = value;
        }

    /**
     * The disengaged position of the Toggle Servo, [toggleOff] will set
     * the servo to this position. This value must be between [0.0, 1.0].
     */
    var DisengagedPosition: Double = 0.0;
        get() = field;

        /**
         * @param[value]
         *          The new value, must be on [0, 1].
         */
        set( value )
        {
            if ( value < 0 ) throw IllegalArgumentException( "DisengagedPosition cannot be less than 0!" );
            if ( value > 1 ) throw IllegalArgumentException( "EngagedPosition cannot be greater than 1!" );
            field = value;
        }

    //
    // Toggling
    //

    /**
     * Toggles the servo to the opposite state than it's currently in.
     */
    fun toggle()
    {
        // ensure that at least [ToggleDelay] milliseconds have passed since
        // this method was last invoked.
        if ( System.currentTimeMillis() - lastToggle < ToggleDelay ) return;

        // toggle the position
        when ( _currentPosition )
        {
            State.ENGAGED    -> toggleOff();
            State.DISENGAGED -> toggleOn();
        }

        lastToggle = System.currentTimeMillis(); // reset the timer
    }

    /**
     * Sets the servo's position to the [EngagedPosition]
     */
    fun toggleOn()
    {
        position = EngagedPosition;
        _currentPosition = State.ENGAGED;
    }

    /**
     * Sets the servo's position to the [DisengagedPosition].
     */
    fun toggleOff()
    {
        position = DisengagedPosition;
        _currentPosition = State.DISENGAGED;
    }

}
