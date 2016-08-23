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

import addonovan.kftc.AbstractKOpMode
import addonovan.kftc.ILog
import addonovan.kftc.getLog
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

    //
    // Shortcuts
    //

    /**
     * Shortcut for getting the active profile for the given OpMode.
     *
     * Cover for
     * ```kotlin
     * OpModeConfigs[ opMode.RegisteredName ]!!.ActiveProfile
     * ```
     *
     * @param[kClass]
     *          The class of the OpMode to get the active profile for.
     *
     * @return The active profile for the given opmode.
     */
    fun profileFor( kClass: KClass< out AbstractKOpMode> ): Profile
    {
        d( "Fetching active profile for ${kClass.simpleName}" );
        kClass.annotations.forEach {
            if ( it is TeleOp ) return getActiveProfile( it.name );
            if ( it is Autonomous ) return getActiveProfile( it.name );
        }

        throw IllegalArgumentException( "Class '${kClass.qualifiedName}' is an OpMode but doesn't have a @Teleop or @Autonomous annotation!" );
    }

    /**
     * Gets the active profile for the given name.
     *
     * Gets the active profile for the opmode with the given registered name,
     * if one doesn't exist, then one is made and inserted into the map for
     * serialization and later use.
     *
     * @param[name]
     *          The name of the OpMode.
     *
     * @return The active profile for the OpMode.
     */
    private fun getActiveProfile( name: String ): Profile
    {
        v( "Finding active profile for $name" );
        if ( OpModeConfigs[ name ] != null )
        {
            v( "Pre-existing profile found!" );
            return OpModeConfigs[ name ]!!.ActiveProfile;
        }

        w( "Generating blank configuration for: $name" );
        val omc = OpModeConfig.fromRaw( name );
        OpModeConfigs[ name ] = omc; // add it to the map for future use
        return omc.ActiveProfile;
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

}
