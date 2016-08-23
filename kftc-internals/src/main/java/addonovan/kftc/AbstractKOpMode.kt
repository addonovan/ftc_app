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

import addonovan.kftc.config.Configurations
import addonovan.kftc.config.Profile

/**
 * !Description!
 *
 * @author addonovan
 * @since 8/22/2016
 */
abstract class AbstractKOpMode : IConfigurable, ILog
{

    //
    // IConfigurable
    //

    /**
     * The configuration profile for this opmode. Use of this
     * is generally discourages as the other `get` methods should
     * be used instead for simpler access.
     */
    override val ConfigProfile: Profile = Configurations.profileFor( javaClass.kotlin );

    //
    // ILog
    //

    /**
     * The tag used for logging. This follows the following format:
     * ```kotlin
     * "kftc.${YourOpModeClassName}.${ActiveProfileName}"
     * ```
     */
    override val LogTag: String = getLogTag( javaClass.kotlin, ConfigProfile.Name );

}
