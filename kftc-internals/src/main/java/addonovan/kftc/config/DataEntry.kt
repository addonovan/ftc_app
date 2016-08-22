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
package addonovan.kftc.config

import android.util.JsonWriter
import android.util.MalformedJsonException
import com.addonovan.ftcext.config.Jsonable
import org.json.JSONArray

/**
 * An entry in the configuration file.
 *
 * This is serialized to a single array, that follows this pattern:
 * `[ "name", type_designation, value ]`
 *
 * @author addonovan
 * @since 8/17/16
 */
class DataEntry< out T : Any > private constructor( val Name: String, val Value: T ) : Jsonable
{

    /**
     * Used for instance creation.
     */
    companion object
    {
        /** The key used to denote an entry as a boolean. */
        private const val KEY_BOOLEAN = "b";
        /** The key used to denote an entry as a long. */
        private const val KEY_LONG    = "l";
        /** The key used to denote an entry as a double. */
        private const val KEY_DOUBLE  = "d";
        /** The key used to denote an entry as a string. */
        private const val KEY_STRING  = "s";

        /**
         * Reads a data entry from the given JSONArray.
         *
         * @param[json]
         *          The json array that matches the format [ "name", "type", value ]
         *
         * @throws MalformedJsonException
         *          If the array is malformed or has an unknown type as its key.
         */
        fun fromJson( json: JSONArray): DataEntry<*>
        {
            if ( json.length() != 3 ) throw MalformedJsonException( "Incorrect number of indices in array length. Expected: 3, Found: ${json.length()}" );

            // [ $name, $type, $value ]
            val name = json.getString( 0 );
            val type = json.getString( 1 );

            return when ( type )
            {
                KEY_BOOLEAN -> DataEntry( name, json.getBoolean( 2 ) );
                KEY_LONG    -> DataEntry( name, json.getLong( 2 ) );
                KEY_DOUBLE  -> DataEntry( name, json.getDouble( 2 ) );
                KEY_STRING  -> DataEntry( name, json.getString( 2 ) );
                else        -> throw MalformedJsonException( "Unknown key type encountered parsing ConfigEntry: \'$type\'" );
            }
        }

        /**
         * Creates a new DataEntry object from the given values.
         *
         * @param[name]
         *          The name of the entry.
         * @param[value]
         *          The value of the entry.
         * @param[T]
         *          The type of the value of the entry.
         *
         * @throws IllegalArgumentException
         *          If [T] is not [Boolean], [Long], [Double], or [String].
         *
         * @return The new DataEntry.
         */
        fun < T : Any > fromRaw( name: String, value: T ): DataEntry<T>
        {
            // make sure it's an applicable type
            if ( value !is Boolean && value !is Long && value !is Double && value !is String )
            {
                throw IllegalArgumentException( "Value must be of only types: boolean, long, double, or String! (Got: ${value.javaClass.name}" );
            }

            return DataEntry( name, value );
        }
    }

    override fun toJson( writer: JsonWriter)
    {
        writer.beginArray();

        writer.value( Name );

        when ( Value )
        {
            is Boolean -> { writer.value( KEY_BOOLEAN ); writer.value( Value ); }
            is Long    -> { writer.value( KEY_LONG ); writer.value( Value ); }
            is Double  -> { writer.value( KEY_DOUBLE ); writer.value( Value ); }
            is String  -> { writer.value( KEY_STRING ); writer.value( Value ); }
            else       -> { writer.value( "wtf" ); writer.value( Value.toString() ) } // how could this be created and have a type isn't supported???
        }

        writer.endArray();
    }

}