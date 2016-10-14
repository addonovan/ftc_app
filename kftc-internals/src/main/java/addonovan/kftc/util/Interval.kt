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

/**
 * An Interval is an object that represents a time interval.
 * The interval is said to be "true" when the current time is
 * within the given interval.
 *
 * For more accurate timing, see [HQInterval].
 *
 * @param[startTime]
 *          The time (in milliseconds, based on [System.currentTimeMillis])
 *          that this Interval will start being active at.
 * @param[duration]
 *          The time (in milliseconds) that this Interval will be active for.
 *
 * @author addonovan
 * @since 10/14/2016
 *
 * @see [HQInterval]
 */
open class Interval( startTime: Long, duration: Long )
{

    //
    // Vals
    //

    /** The time that this interval begins to be active at (milliseconds). */
    open val StartTime = startTime;

    /** The time that this interval stops being active at (milliseconds). */
    open val EndTime = startTime + duration;

    //
    // Constructors
    //

    /**
     * Constructs an interval which occurs directly after the given interval.
     *
     * @param[baseInterval]
     *          The interval which happens immediately before this one.
     * @param[duration]
     *          The time (in milliseconds) that this Interval will be active for.
     */
    constructor( baseInterval: Interval, duration: Long ) : this( baseInterval.EndTime, duration );

    /**
     * Constructs an interval which starts at the millisecond that the constructor
     * is called and spans the given duration.
     *
     * @param[duration]
     *          The duration (in milliseconds) for which this Interval will be active.
     */
    constructor( duration: Long ) : this( System.currentTimeMillis(), duration );

    //
    // Active
    //

    /**
     * @return If this interval is active or not.
     */
    open fun isActive(): Boolean
    {
        val time = System.currentTimeMillis();

        // first do the regular check
        if ( StartTime <= time && time <= EndTime ) return true;

        // if that fails, then check to see if we're within 5 ms of the boundaries
        // because the System.currentTimeMillis() is inaccurate, so just give the
        // Interval the benefit of the doubt.

        // calculate the differences in the time with the boundaries
        val startDiff = Math.abs( time - StartTime );
        val endDiff = Math.abs( time - EndTime );

        return startDiff <= 5 || endDiff <= 5;
    }

}