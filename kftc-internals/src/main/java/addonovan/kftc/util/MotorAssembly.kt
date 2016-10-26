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

import addonovan.kftc.ILog
import addonovan.kftc.getLog
import com.qualcomm.robotcore.util.Range

/**
 * A motor assembly is the programmatic representation of an assembly
 * of any type of motor and its components which allows for higher level
 * control of encoder movements.
 *
 * @param[motor]
 *          The motor used in this assembly.
 * @param[outputDiameter]
 *          The diameter of the wheel used in the assembly (in cm). Default is
 *          4 inches/10.16 cm.
 * @param[gearRatio]
 *          The gear ratio in this assembly (default is 1:1). This should be
 *          the number of spins of the motor divided by the spins of the wheel
 *          at the end.
 *
 * @see[MotorType]
 */
class MotorAssembly(
        val motor: MotorType,
        val outputDiameter: Double = 10.16,
        val gearRatio: Double = 1.0 ) : ILog by getLog( MotorAssembly::class, "${motor.name} | $gearRatio:1 | $outputDiameter cm" )
{

    /** The circumference of the wheel in this assembly. */
    val outputCircumference = Math.PI * outputDiameter;

    /**
     * converts the number of encoder ticks on the motor to a distance based
     * off of the circumference of the wheel from the [motor] and [gearRatio].
     *
     * @param[ticks]
     *          The number of ticks.
     *
     * @return The distance (cm) traveled if the wheel spun [ticks] ticks.
     */
    @Suppress( "unused" )
    fun toDistance( ticks: Int ): Double
    {
        // train tracks!
        // ticks |   1 rotation  |   x wheel spins  |  1 wheel circumference
        // ----- | ------------- | ---------------- | ----------------------- = distance
        // 1     |   # ticks     |   1 motor spin   |  1 wheel spin
        // rearranged for grouping similar operations
        return ( ticks * outputCircumference ).toDouble() / ( motor.EncoderTicks * gearRatio );
    }

    /**
     * Converts the distance to the approximate number of encoder ticks that
     * would be needed to be measured for the distance to have been traveled.
     *
     * @param[distance]
     *          The distance to convert to ticks (cm).
     *
     * @return The number of ticks required to travel [distance] (cm).
     */
    fun toTicks( distance: Double ): Int
    {
        // train tracks! (again!)
        // distance |  x wheel spins          |  x motor spins  |  # ticks
        // -------- | ----------------------- | --------------- | ------------ = ticks
        // 1        |  1 wheel circumference  |  1 wheel spin   |  1 rotation
        // rearranged for grouping similar operations
        return Math.round( ( distance * motor.EncoderTicks * gearRatio ).toDouble() / outputCircumference ).toInt();
    }

    /**
     * Converts the target rpm to a power value for this motor based on the gear ratio
     * and the usual no-load rpm.
     *
     * @param[targetRPM]
     *          The target RPM that the motor should try to achieve under no load.
     *
     * @return The power to run the motor at to achieve the given RPM under no load.
     */
    fun toPower( targetRPM: Double ): Double
    {
        // the required input RPM to achieve
        val requiredInput = ( targetRPM / gearRatio );

        // make this a ratio of the input / max to get the requrired power
        val power = requiredInput / motor.NoLoadRPM;

        // make sure this is a valid power rating
        if ( power > 1.0 || power < -1.0 )
        {
            // warn the user if the limit was exceeded so that there's a trace of it somehwere
            w( "$power exceeded the range [-1.0, 1.0] trying to achieve $targetRPM (clipped to fit range)!" );
            return Range.clip( power, -1.0, 1.0 );
        }

        return power;
    }

}