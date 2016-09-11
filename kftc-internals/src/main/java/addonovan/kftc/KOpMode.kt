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
 * The kotlin equivalent of the Qualcomm OpMode.
 *
 * @author addonovan
 * @since 8/22/2016
 */
abstract class KOpMode : KAbstractOpMode()
{

    /**
     * This is called immediately after the init button has been pressed.
     */
    open fun init() {}

    /**
     * This is called repeatedly after the init button has been pressed,
     * but not yet started.
     */
    open fun init_loop() {}

    /**
     * This is called immediately after the start button has been pressed.
     */
    open fun start() {}

    /**
     * This is called repeatedly after the start button has been pressed,
     * but not yet stopped.
     */
    abstract fun loop();

    /**
     * This is called immediately after the stop button has been pressed.
     */
    open fun stop() {}

}