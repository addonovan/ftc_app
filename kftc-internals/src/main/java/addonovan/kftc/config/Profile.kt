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

import addonovan.kftc.ILog
import addonovan.kftc.getLog
import android.util.JsonWriter
import android.util.MalformedJsonException
import org.json.JSONObject
import java.util.*

/**
 * A custom profile for an OpMode. This can be configured in a certain way
 * and will be saved to a file to be read later.
 *
 * This is serialized to the json which follows this pattern:
 * ```json
 * {
 *   name: "name",
 *   config: [
 *     [ "name", type, value ],
 *     ...
 *   ]
 * }
 * ```
 *
 * @param[opModeConfig]
 *          The OpModeConfig that this Profile is a member of.
 * @param[Name]
 *          The name of this profile.
 *
 * @author addonovan
 * @since 8/17/16
 */
/* Each profile gets its own logger so that it's easier to tell which one had a problem. */
class Profile(
        private val opModeConfig: OpModeConfig,
        val Name: String ) : Jsonable, ILog by getLog( Profile::class, "$Name(${opModeConfig.Name}" )
{

    companion object
    {

        /** The name for the default profile. */
        const val DEFAULT_NAME = "default";

        /**
         * Creates a new Profile from a json object.
         */
        fun fromJson( opMode: OpModeConfig, json: JSONObject ): Profile
        {
            // check the length of the object
            if ( json.length() != 2 ) throw MalformedJsonException( "Incorrect number of elements in profile!" );

            // check for the tags
            if ( !json.has( "name" ) ) throw MalformedJsonException( "Missing name tag" );
            if ( !json.has( "config" ) ) throw MalformedJsonException( "Missing config tag" );


            // create the profile
            val profile = Profile( opMode, json.getString( "name" ) );

            // populate its map
            val config = json.getJSONArray( "config" );
            for( i in 0..config.length() - 1 )
            {
                try
                {
                    val entry = DataEntry.fromJson( config.getJSONArray( i ) );
                    profile.config[ entry.Name ] = entry; // add the entry to the map
                }
                catch ( ife: IllegalArgumentException )
                {
                    // log the error
                    profile.e( "Encountered error while trying to parse from json!" );
                    profile.e( "Json: ${config.getJSONArray( i ).toString( 0 ).replace( "\\s".toRegex(), "" )}" );
                    profile.e( "Exception:", ife );

                    // continue to try to parse the rest of the file
                }
            }

            return profile;
        }

        /**
         * Creates a new Profile from the raw data.
         */
        fun fromRaw( opMode: OpModeConfig, name: String ): Profile
        {
            return Profile( opMode, name );
        }

    }

    //
    // Values
    //

    /** The list of all the data in this profile. */
    internal val config = HashMap< String, DataEntry<*>>();

    //
    // Actions
    //

    /**
     * Removes the Profile from the configuration list.
     *
     * Cover method for
     * ```kotlin
     * opModeConfig.deleteProfile( Name )
     * ```
     *
     * @return `true` if the profile was removed from the configuration list,
     *         `false` if it already was.
     */
    fun delete() = opModeConfig.deleteProfile( Name );

    /**
     * Sets the active profile for the OpModeConfig to this.
     *
     * Cover method for
     * ```kotlin
     * opModeConfig.setActiveProfile( Name )
     * ```
     *
     * @return `true` if the active profile has been switched to this,
     *         `false` if it was set to the default.
     */
    fun activate() = opModeConfig.setActiveProfile( Name );

    //
    // Getters
    //

    /**
     * The underlying method for getting values.
     *
     * If there is no [DataEntry] associated with [name] in [config], a new one
     * will be created from [name] and [default] then inserted. If there was
     * already a value associated with the name, it will be fetched and then will
     * be casted to [T] and returned. If the value cannot be casted to [T] an error
     * will be logged and a [ClassCastException] thrown.
     *
     * @param[name]
     *          The name of the key.
     * @param[default]
     *          The default value of the key if it doesn't already exist.
     * @param[T]
     *          The type of the default value.
     *
     * @throws ClassCastException
     *          If the pre-existing value associated with [name] in [config] cannot
     *          be casted to [T].
     *
     * @return The value in [config] associated with [name], if it exists; otherwise, default.
     */
    @Suppress( "unchecked_cast" )
    private fun < T : Any > get( name: String, default: T ): T
    {
        // create the entry if it doesn't exist
        if ( !config.containsKey( name ) || config[ name ] == null )
        {
            config[ name ] = DataEntry.fromRaw( name, default );
            return default; // no point in going through the expensive operations below
        }

        val value = config[ name ]!!.Value;

        try
        {
            return value as T;
        }
        catch ( cce: ClassCastException )
        {
            e( "$name already has an entry of type ${value.javaClass.name} and cannot be casted to ${default.javaClass.name}!" );
            throw cce;
        }
    }

    /**
     * Gets the boolean value with the given name. If it doesn't exist, the entry
     * is added and the default returned.
     *
     * @param[name]
     *          The name of the value.
     * @param[default]
     *          The default value if there was none.
     *
     * @throws ClassCastException
     *          If the pre-existing value associated with [name] was not a boolean.
     *
     * @return The boolean value with the given name from this profile.
     */
    operator fun get( name: String, default: Boolean ) = get< Boolean >( name, default );

    /**
     * Gets the long value with the given name. If it doesn't exist, the entry
     * is added and the default returned.
     *
     * @param[name]
     *          The name of the value.
     * @param[default]
     *          The default value if there was none.
     *
     * @throws ClassCastException
     *          If the pre-existing value associated with [name] was not a long.
     *
     * @return The long value with the given name from this profile.
     */
    operator fun get( name: String, default: Long ) = get< Long >( name, default );

    /**
     * Gets the double value with the given name. If it doesn't exist, the entry
     * is added and the default returned.
     *
     * @param[name]
     *          The name of the value.
     * @param[default]
     *          The default value if there was none.
     *
     * @throws ClassCastException
     *          If the pre-existing value associated with [name] was not a double.
     *
     * @return The double value with the given name from this profile.
     */
    operator fun get( name: String, default: Double ) = get< Double >( name, default );

    /**
     * Gets the string value with the given name. If it doesn't exist, the entry
     * is added and the default returned.
     *
     * @param[name]
     *          The name of the value.
     * @param[default]
     *          The default value if there was none.
     *
     * @throws ClassCastException
     *          If the pre-existing value associated with [name] was not a string.
     *
     * @return The string value with the given name from this profile.
     */
    operator fun get( name: String, default: String ) = get< String >( name, default );

    //
    // Overrides
    //

    override fun toJson( writer: JsonWriter )
    {
        writer.beginObject();

        writer.name( "name" ).value( Name );

        // write the config list
        writer.name( "config" ).beginArray();
        config.forEach { it.value.toJson( writer ) };
        writer.endArray();

        writer.endObject();
    }

}
