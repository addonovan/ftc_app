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

/**
 * An enum for the valid types of motors for encoder purposes.
 *
 * @param[encoderTicks]
 *          The number of encoder ticks the motor supports.
 *
 * @author addonovan
 * @since 10/14/16
 */
enum class MotorType( encoderTicks: Int, noLoadRPM: Int )
{

    //
    // Instances
    //

    /** A standard tetrix motor */
    TETRIX( 1440, 150 ),

    /** A neverest 20 motor. */
    NEVEREST_20( 560, 275 ),

    /** A neverest 40 motor. */
    NEVEREST_40( 1220, 160 ),

    /** A neverest 60 motor. */
    NEVEREST_60( 1680, 105 ),

    /** A neverest motor without the gearbox. */
    NEVEREST( 1220, 6600 );

    //
    // Vals
    //

    /**
     * The number of 'ticks' that the motor encoder can count in
     * one full cycle.
     */
    val EncoderTicks = encoderTicks;

    /**
     * The average output RPM of this device under no load.
     */
    val NoLoadRPM = noLoadRPM;

}