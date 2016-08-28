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
import com.qualcomm.robotcore.eventloop.opmode.*
import com.qualcomm.robotcore.util.Util
import dalvik.system.DexFile
import java.lang.reflect.Modifier
import java.util.*
import kotlin.reflect.KClass

/**
 * The entry point for the kftc library.
 *
 * @author addonovan
 * @since 8/21/16
 */
class AddOpModeRegister : OpModeRegister, ILog by getLog( AddOpModeRegister::class )
{

    //
    // OpModeRegister
    //

    override fun register( manager: OpModeManager )
    {
        initSystems(); // initialize all of the systems that need to be

        i( "Registering OpModes" );
        for ( clazz in ClassFinder.OpModeClasses )
        {
            val flavor =
                    if ( clazz.isAutonomous() )
                    {
                        OpModeMeta.Flavor.AUTONOMOUS;
                    }
                    else if ( clazz.isTeleOp() )
                    {
                        OpModeMeta.Flavor.TELEOP;
                    }
                    else
                    {
                        throw IllegalStateException( "OpMode has neither an Autonomous nor TeleOp Annotation!" );
                    }

            val name = clazz.getAnnotatedName();
            val group = clazz.getAnnotatedGroup();

            // register it with Ftc
            manager.register( OpModeMeta( name, flavor, group ), wrap( clazz ) );

            // register it with the configurator
            Configurations.RegisteredOpModes += clazz;
        }
        d( "OpModes registered" );
    }

    //
    // Actions
    //

    /**
     * Initializes the other systems in this framework.
     */
    private fun initSystems()
    {
        i( "Initializing kftc systems" );

        d( "Init Configurations..." );
        Configurations.RegisteredOpModes.clear(); // just in case some things have already been registered
        Configurations.load();

        d( "Hooking Robot icon..." );
        hookRobotIcon();

        d( "kftc systems initialized" );
    }

    //
    // Wrapping Nonsense
    //

    /**
     * Wraps the given class in the correct wrapper based on its supertype.
     *
     * @param[clazz]
     *          The class to wrap in a OpMode wrapper.
     *
     * @throws IllegalArgumentException
     *          If, somehow, the class isn't a subclass of either KOpMode or KLinearOpMode.
     *
     * @return The OpMode wrapper for registration.
     */
    @Suppress( "unchecked_cast" ) // checked via reflections
    private fun wrap( clazz: Class< out KAbstractOpMode> ): OpMode
    {
        if ( KOpMode::class.java.isAssignableFrom( clazz ) ) return KOpModeWrapper( clazz as Class< out KOpMode > );
        if ( KLinearOpMode::class.java.isAssignableFrom( clazz ) ) return KLinearOpModeWrapper( clazz as Class< out KLinearOpMode > );

        throw IllegalArgumentException( "How did this even happen? It was already checked before this?" );
    }

    /**
     * Updates all the utilities with the given OpMode's.
     *
     * @param[opMode]
     *          The OpMode to get utilities from.
     */
    private fun updateUtilities( opMode: OpMode )
    {
        UtilityContainer.HardwareMap = opMode.hardwareMap;
        UtilityContainer.Gamepad1 = opMode.gamepad1;
        UtilityContainer.Gamepad2 = opMode.gamepad2;
        UtilityContainer.Telemetry = opMode.telemetry;
    }

    /**
     * A wrapper that converts commands to a Qualcomm OpMode to a KOpMode.
     *
     * @param[clazz]
     *          The KOpMode's class to wrap around.
     */
    private inner class KOpModeWrapper( private val clazz: Class< out KOpMode > ) : OpMode()
    {
        /**
         * The instance we're wrapping around.
         */
        private lateinit var instance: KOpMode;

        override fun init()
        {
            unhookRobotIcon(); // Unhooks the listener because we're running now
            updateUtilities( this );
            instance = clazz.newInstance();
            instance.init();
        }

        override fun start()
        {
            updateUtilities( this );
            instance.start();
        }

        override fun loop()
        {
            updateUtilities( this );
            instance.loop();
        }

        override fun stop()
        {
            updateUtilities( this );
            instance.stop();
        }

    }

    /**
     * A wrapper that converts commands to a Qualcomm LinearOpMode to a KLinearOpMode.
     *
     * @param[clazz]
     *          The KLinearOpMode's class to wrap around.
     */
    private inner class KLinearOpModeWrapper( private val clazz: Class< out KLinearOpMode > ) : LinearOpMode()
    {
        /**
         * The actual instance of the KLinearOpMode that we're wrapping around.
         */
        private lateinit var instance: KLinearOpMode;

        override fun runOpMode()
        {
            unhookRobotIcon(); // Unhooks the listener because we're running now
            updateUtilities( this );
            instance = clazz.newInstance();
            instance.runOpMode();
        }

    }

}