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

import addonovan.kftc.UtilityContainer
import addonovan.kftc.isOfType
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * A class intended to be extended in order to form hardware definition
 * files that contain all the hardware fetching goodness of a regular
 * OpMode, but separated entirely.
 *
 * @author addonovan
 * @since 8/27/16
 */
abstract class HardwareDefinition
{

    /** The hardware map used to form the hardware definition. */
    protected val HardwareMap: HardwareMap
            get() = UtilityContainer.HardwareMap;

    /**
     * Simple function to get all types of hardware by parameterization.
     *
     * This method will return the correct device from the hardware device mappings
     * in the HardwareMap by using the parameterized type and the name given.
     * This can also create hardware extension classes on the go as well, so
     * long as they are valid (and appear as loaded in LogCat).
     *
     * This is a convenience method for
     * ```kotlin
     * HardwareMap.getDeviceByType( T::class.java, name ) as T;
     * ```
     * Which is much uglier than simply allowing type inference take care
     * of all that `T` stuff.
     *
     * @param[T]
     *          The type of hardware device to get.
     * @param[name]
     *          The name of the hardware device to return.
     *
     * @return The hardware device with the given type, [T], and the name, [name].
     *
     * @throws IllegalArgumentException
     *          If [T] had no DeviceMapping associated with it (i.e. no valid
     *          Hardware Extension and no way to get it out of [HardwareMap].
     * @throws NullPointerException
     *          If there was no entry for [name] with the type [T].
     */
    inline fun < reified T : HardwareDevice > get( name: String ): T = HardwareMap.getDeviceByType( T::class.java, name ) as T;

}
