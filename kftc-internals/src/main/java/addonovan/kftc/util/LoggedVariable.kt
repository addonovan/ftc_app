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
import kotlin.reflect.KProperty

/**
 * A logged variable will write a message to the log every time it's accessed
 * or modified, but lets you treat it as if it's the same type as without the
 * object wrapping around it.
 *
 * @author addonovan
 * @since 10/29/16
 */
interface LoggedVariable< T >
{

    /** The underlying value of this delegate. */
    var value: T;

}

//
// Delegate Things
//

/**
 * Logs the access to the [LoggedVariable] and then returns
 * the value the variable.
 *
 * @param[thisRef]
 *          Ignored
 * @param[property]
 *          The property being accessed (the name is drawn from here).
 *
 * @return The value of this [LoggedVariable].
 */
operator fun < T > LoggedVariable<T>.getValue(thisRef: Any?, property: KProperty<*>): T
{
    VariableTraceLog.d( "${property.name} accessed: value = ($value)" );
    return value;
}

/**
 * Logs the modification to the [LoggedVariable] and then updates
 * the value to the one given.
 *
 * @param[thisRef]
 *          Ignored.
 * @param[property]
 *          The property being modified (the name is drawn from here).
 * @param[value]
 *          The new value of the variable.
 */
operator fun < T > LoggedVariable<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T )
{
    VariableTraceLog.d( "${property.name} modified: value was ${this.value}, now $value" );
    this.value = value;
}

//
// Logging
//

/**
 * A singleton that's used to log accesses and modifications to
 * any logged variable.
 *
 * This is just a static logger.
 */
private object VariableTraceLog : ILog by getLog( VariableTraceLog::class );

//
// Creating a LoggedVariable< T >
//

/**
 * Creates a new [LoggedVariable] with the given initial value.
 *
 * @param[initialValue]
 *          The initial value.
 *
 * @return The created [LoggedVariable].
 */
fun < T > loggedVariable( initialValue: T ): LoggedVariable<T>
{
    return object : LoggedVariable<T>
    {
        override var value: T = initialValue;
    }
}

/**
 * Evaluates the given lambda statements creates a new [LoggedVariable]
 * with the resulting value.
 *
 * @param[initialization]
 *          The lambda statement which will result in the initial value.
 *
 * @return The created [LoggedVariable].
 */
fun < T > loggedVariable( initialization: () -> T ): LoggedVariable<T>
{
    return loggedVariable( initialization.invoke() );
}