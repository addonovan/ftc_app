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
@file:Suppress( "unused" )
package addonovan.kftc.util

import java.lang.System.nanoTime

/**
 * A special, high quality type of [Interval] which uses the
 * system's time in nanoseconds rather than the inaccurate
 * method of getting the time in milliseconds.
 *
 * @author addonovan
 * @since 10/14/16
 *
 * @see [Interval]
 */
class HQInterval( nanosUntilStart: Long, duration: Long ) : Interval( 0, 0 )
{

    //
    // Vals
    //

    /**
     * The start time of this interval (in nanoseconds).
     */
    override val StartTime = nanoTime() + nanosUntilStart;

    /**
     * The end time of this interval (in nanoseconds).
     */
    override val EndTime = StartTime + duration;

    //
    // Constructors
    //

    /**
     * Constructs an interval based off of the given HQInterval. The new
     * HQInterval will be active immediately after the given interval.
     *
     * @param[baseInterval]
     *          The interval which ends just before this one.
     * @param[duration]
     *          The length of this interval (in nanoseconds).
     */
    constructor( baseInterval: HQInterval, duration: Long ) : this ( baseInterval.EndTime - nanoTime(), duration );

    //
    // Active
    //

    /**
     * @return If the HQInterval is active or not.
     */
    override fun isActive() = StartTime <= nanoTime() && nanoTime() <= EndTime;

}