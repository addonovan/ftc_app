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

/**
 * !Description!
 *
 * @author addonovan
 * @since 8/22/2016
 */
abstract class KLinearOpMode : AbstractKOpMode()
{

    /**
     * Sleeps for the given period of time.
     *
     * @param[milliseconds]
     *          The time, in milliseconds, to sleep for.
     *
     * @return `true` if an exception was thrown, `false` otherwise.
     */
    fun sleep( milliseconds: Long ): Boolean
    {
        try
        {
            Thread.sleep( milliseconds );
            return false;
        }
        catch ( e: InterruptedException )
        {
            i( "Encountered Interruppted exception while sleeping!", e );
            return true;
        }
    }

    abstract fun runOpMode();

}