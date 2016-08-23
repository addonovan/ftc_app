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
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import kotlin.reflect.KClass

/**
 * An interface for stating that you would like to access the configuration
 * profiles for the OpMode. If an OpMode does not have this interface, it
 * will not be set up in the configurator.
 *
 * @author addonovan
 * @since 8/21/16
 */
interface IConfigurable
{

    /** The underlying profile that this configurable allows access to. */
    val ConfigProfile: Profile;

    /**
     * Gets the boolean value from the active profile.
     *
     * @param[name]
     *          The name of the boolean.
     * @param[default]
     *          The default value of the boolean, if it isn't found.
     *          This can be excluded for a default value of `false`.
     *
     * @return The boolean value associated with the name, or the default
     *         if no value was found.
     */
    fun get( name: String, default: Boolean ) = ConfigProfile[ name, default ];

    /**
     * Gets the long value from the active profile.
     *
     * @param[name]
     *          The name of the long.
     * @param[default]
     *          The default value of the long, if it isn't found.
     *          This can be excluded for a default value of `false`.
     *
     * @return The long value associated with the name, or the default
     *         if no value was found.
     */
    fun get( name: String, default: Long ) = ConfigProfile[ name, default ];

    /**
     * Gets the double value from the active profile.
     *
     * @param[name]
     *          The name of the double.
     * @param[default]
     *          The default value of the double, if it isn't found.
     *          This can be excluded for a default value of `false`.
     *
     * @return The double value associated with the name, or the default
     *         if no value was found.
     */
    fun get( name: String, default: Double ) = ConfigProfile[ name, default ];

    /**
     * Gets the string value from the active profile.
     *
     * @param[name]
     *          The name of the string.
     * @param[default]
     *          The default value of the string, if it isn't found.
     *          This can be excluded for a default value of `false`.
     *
     * @return The string value associated with the name, or the default
     *         if no value was found.
     */
    fun get( name: String, default: String ) = ConfigProfile[ name, default ];

}

/**
 * The instance of the configurable that is actually used whenever
 * the IConfigurable interface is delegated.
 *
 * @param[ConfigProfile]
 *          The profile to use to back the getters.
 */
private class Configurable( override val ConfigProfile: Profile ) : IConfigurable;

/**
 * Gets the Configurable for the given class.
 *
 * The returned configurable is backed by the active profile for the given
 * OpMode.
 *
 * @param[kClass]
 *          The OpMode class.
 *
 * @return The Configurable for delegation.
 */
fun getConfig( kClass: KClass< out AbstractKOpMode > ): IConfigurable = Configurable( Configurations.profileFor( kClass ) );