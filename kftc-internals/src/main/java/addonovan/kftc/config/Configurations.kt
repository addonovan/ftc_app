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
import com.qualcomm.robotcore.eventloop.opmode.*
import org.json.JSONObject
import java.io.*
import java.util.*
import kotlin.reflect.KClass

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
    private val RegisteredOpModes = OpModeMap();

    //
    // OpMode Registration
    //

    /**
     * Registers the OpMode with the configuration system.
     *
     * @param[clazz]
     *          The OpMode class to register.
     */
    fun registerOpMode( clazz: Class< out KAbstractOpMode > )
    {
        RegisteredOpModes += clazz;
        i( "Registered OpMode class ${clazz.simpleName} as ${RegisteredOpModes[ clazz ]}" );
    }

    /**
     * @param[name]
     *          The name of the opmode to fetch the class for.
     *
     * @return The class of the opmode registered with the given name.
     */
    fun getOpModeByName( name: String ) = RegisteredOpModes[ name ];

    /**
     * De-registers all OpModes
     */
    fun deregisterOpModes()
    {
        RegisteredOpModes.clear();
    }

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
        val name = RegisteredOpModes[ clazz ] ?: throw IllegalArgumentException( "Class $className was not registered!" );

        v( "$className registered as '$name'" );

        if ( OpModeConfigs[ name ] != null )
        {
            v( "Pre-existing profile found!" );
            return OpModeConfigs[ name ]!!.ActiveProfile;
        }

        w( "Generating blank configuration for: $name" );
        val blankConfig = OpModeConfig.fromRaw( name );
        OpModeConfigs[ name ] = blankConfig; // add it to the map for future use
        return blankConfig.ActiveProfile;
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
        fromJson( JSONObject( ConfigFile.readText() ) );
    }

    /**
     * Saves the configurations to the configuration file.
     */
    fun save()
    {
        createFileIfNeeded();

        i( "Saving profiles to config file" );
        val writer = JsonWriter( OutputStreamWriter( FileOutputStream( ConfigFile ) ) );
        toJson( writer );
        writer.close();
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
     * A custom doubly-linked hashmap that contains links between both the OpMode class
     * and its name, as well as the other way around.
     */
    private class OpModeMap : HashMap< Class< out KAbstractOpMode >, String >()
    {

        /** Map used for class lookup by name. */
        private val reverseMap = HashMap< String, Class< out KAbstractOpMode > >();

        /**
         * Adds a class to the class map.
         *
         * @param[clazz]
         *          The OpMode class to add to the map.
         */
        operator fun plusAssign( clazz: Class< out KAbstractOpMode > )
        {
            val name = clazz.getAnnotatedName();

            // if there's a name conflict, that's a big problem!
            if ( name in this )
            {
                throw IllegalArgumentException(
                        "Two OpMode may not have the same name! Conflict: $name." +
                        "Shared by ${get( name )!!.canonicalName} and ${clazz.canonicalName}"
                );
            }

            // add it to both maps
            this[ clazz ] = name;
            reverseMap[ name ] = clazz;
        }

        //
        // Reverse Map operators
        //

        operator fun get( name: String ) = reverseMap[ name ];
        operator fun contains( name: String ) = name in reverseMap;

    }

}
