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
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister
import dalvik.system.DexFile
import org.firstinspires.ftc.robotcore.internal.AppUtil
import java.lang.reflect.Modifier
import java.util.*

/**
 * A redo of the OpMode registration process. This is based off
 * of annotations and grabs other things as well.
 *
 * @author addonovan
 * @since 11/14/16
 */
object KOpModeRegister : OpModeRegister, ILog by getLog( KOpModeRegister::class )
{

    //
    // Vals
    //

    private val opModes: List< Class< out OpMode > > by lazy {

        val value = ArrayList< Class< out OpMode > >();

        val dexFile = DexFile( AppUtil.getInstance().activity.packageCodePath );

        for ( name in dexFile.entries() )
        {

            try
            {
                val clazz = Class.forName( name );

                if ( !Modifier.isPublic( clazz.modifiers ) ) continue;
                if ( Modifier.isAbstract( clazz.modifiers ) ) continue;
                if ( Modifier.isInterface( clazz.modifiers ) ) continue;

                // make sure it's an OpMode
                if ( !clazz.isAssignableFrom( OpMode::class.java ) ) continue;

                @Suppress( "unchecked_cast" )
                val opModeClass = clazz as Class< out OpMode >;

                // make sure it's registerable
                if ( opModeClass.isNotRegisterable() ) continue;

                // we don't want anything that isn't a KOpMode or KLinearOpMode
                if ( !opModeClass.isAssignableFrom( KOpMode::class.java ) && !opModeClass.isAssignableFrom( KLinearOpMode::class.java ) ) continue;

                value.add( opModeClass );
                i( "Loaded OpMode class: $name" );
            }
            catch ( e: Exception ) {}

        }

        value;
    };


    //
    // OpModeRegister
    //

    override fun register( manager: OpModeManager )
    {
        hookRobotIcon();

        // register all the OpModes
        opModes.forEach { clazz ->
            manager.register( clazz.getOpModeMeta(), clazz );
            Configurations.registeredOpModes += clazz;
        };
    }

}
