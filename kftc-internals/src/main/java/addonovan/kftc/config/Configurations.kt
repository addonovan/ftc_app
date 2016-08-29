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

import addonovan.kftc.*
import android.os.Environment
import android.util.JsonWriter
import android.widget.Toast
import org.json.JSONObject
import java.io.*
import java.util.*

/**
 * !Description!
 *
 * Serializes to
 * ```json
 * root: [
 *   {
 *     name: "name",
 *     activeProfile: "activeProfileName",
 *     profiles: [
 *       {
 *         name: "name",
 *         config: [
 *           [ "name", type, value ],
 *           ...
 *         ]
 *       },
 *       ...
 *     ]
 *   },
 *   ...
 * ]
 * ```
 *
 * @author addonovan
 * @since 8/17/16
 */
object Configurations : Jsonable, ILog by getLog( Configurations::class )
{

    //
    // Values
    //

    /** The location of the config file on the disk. */
    private val ConfigFile = File( Environment.getExternalStorageDirectory(), "/FIRST/profiles.ftcext" );

    /** A map of the OpModeConfigs for each OpMode. */
    private val OpModeConfigs = HashMap< String, OpModeConfig >();

    /** A map of all the registered OpModes' names and their classes. */
    val RegisteredOpModes = OpModeMap();

    //
    // Shortcuts
    //

    /**
     * Gets the active profile for the given OpMode.
     *
     * @param[clazz]
     *          The class of the [KAbstractOpMode] to get the active profile for.
     *
     * @return The active profile for the given OpMode.
     */
    fun profileFor( clazz: Class< out KAbstractOpMode > ): Profile
    {
        val className = clazz.canonicalName;
        d( "Fetching active profile for $className" );

        // get the name from the registered OpModes
        val name = RegisteredOpModes[ clazz ];
        v( "$className registered as '$name'" );

        // get the active profile
        return opModeConfigFor( name ).ActiveProfile;
    }

    /**
     * Gets the OpModeConfig object for the given name. A blank one is
     * created if one does not already exist.
     *
     * @param[name]
     *          The name of the string.
     *
     * @return The [OpModeConfig] for the given name.
     */
    fun opModeConfigFor( name: String ): OpModeConfig
    {
        // if one exists, return it
        if ( name in OpModeConfigs )
        {
            v( "Pre-existing profile found!" );
            return OpModeConfigs[ name ]!!;
        }

        // otherwise, make a new one
        w( "Generating blank configuration for: $name" );
        val blankConfig = OpModeConfig.fromRaw( name );
        OpModeConfigs[ name ] = blankConfig; // add it to the map for future use
        return blankConfig;
    }

    //
    // Serialization
    //

    /**
     * Creates [ConfigFile] if it needs to be.
     *
     * @return `true` if the file was created, `false` otherwise.
     */
    private fun createFileIfNeeded(): Boolean
    {
        if ( !ConfigFile.exists() )
        {
            e( "Config file doesn't exist!" );

            if ( !ConfigFile.parentFile.exists() )
            {
                i( "Making directory to file" );
                ConfigFile.parentFile.mkdirs();
            }

            ConfigFile.createNewFile();
            return true;
        }

        return false;
    }

    /**
     * Loads the configurations from the configuration file.
     */
    fun load()
    {
        if ( createFileIfNeeded() )
        {
            OpModeConfigs.clear();
            return;
        }

        i( "Loading profiles from config file" );
        try
        {
            fromJson( JSONObject( ConfigFile.readText() ) );
        }
        catch ( e: Exception )
        {
            e( "Error loading JSON!", e );
            showToast( "Error loading configuration!", time= Toast.LENGTH_LONG );
            i( "Deleting config file for future sanity" );
            ConfigFile.delete();
        }
    }

    /**
     * Saves the configurations to the configuration file.
     */
    fun save()
    {
        createFileIfNeeded();

        i( "Saving profiles to config file" );
        try
        {
            val writer = JsonWriter( OutputStreamWriter( FileOutputStream( ConfigFile ) ) );

            // set up the writer
            writer.setIndent( "    " );
            writer.beginObject();

            // save the data
            toJson( writer );

            // close the writer
            writer.endObject();
            writer.close();
        }
        catch ( e: Exception )
        {
            e( "Error saving JSON!", e );
            showToast( "Error saving configuration!", time= Toast.LENGTH_LONG );
        }
    }

    override fun toJson( writer: JsonWriter )
    {
        writer.name( "root" ).beginArray();
        OpModeConfigs.forEach { it.value.toJson( writer ); };
        writer.endArray();
    }

    /**
     * Reads the json object and populates [OpModeConfigs] to
     * resemble it.
     *
     * @param[json]
     *          The json to parse.
     */
    fun fromJson( json: JSONObject )
    {
        // clear any previous configs
        OpModeConfigs.clear();

        // load everything from the list
        val root = json.getJSONArray( "root" );
        for ( i in 0..root.length() - 1 )
        {
            val omConfig = OpModeConfig.fromJson( root.getJSONObject( i ) );
            OpModeConfigs[ omConfig.Name ] = omConfig;
        }
    }

    //
    // Nested Classes
    //

    /**
     * A custom collection that stores the names and classes of registered OpModes
     * so that they can be accessed by either entry.
     */
    class OpModeMap
    {

        /** The names of all the registered OpModes. */
        private val names = ArrayList< String >();

        /** The classes of all the registered OpModes. */
        private val classes = ArrayList< Class< out KAbstractOpMode > >();

        /** All the names of the registered OpModes. */
        val Names: List< String >
            get() = Collections.unmodifiableList( names );

        /**
         * Adds a class to the class map.
         *
         * @param[clazz]
         *          The OpMode class to add to the map.
         */
        operator fun plusAssign( clazz: Class< out KAbstractOpMode > )
        {
            val name = clazz.getAnnotatedName();
            i( "Registering ${clazz.simpleName} as $name" );

            // if there's a name conflict, that's a big problem!
            if ( name in this )
            {
                e( "!!Name conflict!!" );
                throw IllegalArgumentException(
                        "Two OpMode may not have the same name! Conflict: $name." +
                        "Shared by ${get( name ).canonicalName} and ${clazz.canonicalName}"
                );
            }

            // if the class has already been registered, that's also a big problem!
            if ( clazz in this )
            {
                e( "!!Class conflict!!" );
                throw IllegalArgumentException(
                        "One OpMode class may not be registered twice!" +
                        "Class \"${clazz.canonicalName}\" registered twice! As \"${get( clazz )}\" and \"$name\"!"
                );
            }

            // add it to both lists, same position
            names.add( 0, name );
            classes.add( 0, clazz );

            v( "Registration complete" );
        }

        //
        // HashMap operations
        //

        /**
         * Clears all OpModes from the map.
         */
        fun clear()
        {
            names.clear();
            classes.clear();
        }

        operator fun get( clazz: Class< out KAbstractOpMode > ) = names[ classes.indexOf( clazz ) ];
        operator fun contains( clazz: Class< out KAbstractOpMode > ) = clazz in classes;

        operator fun get( name: String ) = classes[ names.indexOf( name ) ];
        operator fun contains( name: String ) = name in names;

    }

}
