# kftc

Kftc is a library meant to improve the standard FTC api and make it more suitable for programming in Kotlin (hence, the k).

## Hardware Access

Hardware access, even when following the convention of separate files, is not exactly pleasing in the standard api. First off, you are meant to have a separate hardware definition file (which is still recommended in kftc), but it would have no access to the hardware map, so all the fields would be set to null, until a specific method is called.

In kftc, this process is much cleaner. By extending the `HardwareDefinition` class, you can have direct access to the hardware map, and an additional method which will make the hardware-fetching statements even shorter and more powerful. The `get< T : HardwareDevice >( name: String ): T` method will automatically return the same type that it is parameterized with. The best part about this, is that Kotlin's amazing type inference will make that unnecessary most of the time anyways!

The class that extends `HardwareDefinition` should also be kept `abstract`, as you will need to extend it later in your OpMode to get nicer access to it.

```kotlin
abstract class KPushBotHardware : HardwareDefinition
{
    val leftMotor:  DcMotor = get( "left motor" );
    val rightMotor: DcMotor = get( "right motor" );
    val armMotor:   DcMotor = get( "arm motor" );
    
    val leftClaw:  Servo = get( "left claw" );
    val rightClaw: Servo = get( "right claw" );
}

class KPushBot : KOpMode()
{
    /*
     * Now all properties in the KPushBotHardware class can be accessed directly,
     * as if they were members of KPushBot.
     */
    companion object : KPushBotHardware();
    
    // other stuff
}
```

## Configuration

Having hard-coded values in a program will make it more difficult if anything ever needs to be changed at a later time, especially if the value isn't written in just one place (i.e. no constant). The same is true with OpModes. When programming anything for the first time, it's difficult to know if one speed is "too fast" or "too slow" or what, exactly, the best servo position is, or the best threshold for sensor values, or anything of this sort. Finding the perfect value is usually a trial-and-error type situation. This is exactly what the configuration system API aims to alleviate. The most annoying part of any of this is all the time it takes to recompile, redownload, reinstall, and restart the app to change just *one* *little* value!

With the configuration system, an OpMode can have multiple confiugration "profiles", which are essentially maps just maps, that only support `Boolean`, `Long`, `Double`, and `String` values. Every KOpMode or KLinearOpMode always has access to its active profile.

#### Profiles

Profiles are simply the word for a configuration setup. For instance, if an OpMode has a String value with the name "foo", one profile could map "foo" to be "bar", while another one could map it to "baz". This is useful to keep the number of OpMode classes down. Instead of having an OpMode for the red alliance and one for the blue alliance, you could have one OpMode, with a configurable boolean that is then configured by a "blue" and "red" profile to signify which alliance you're running.

The profile editor is accessible directly through the app. After the app loads and finds all the OpModes, the Robot Icon in the top left corner will start blinking red, meaning that you can then tap it and open the profile editor. You will have to select the OpMode you wish to configure, then you can create, edit, and delete profiles for that specific OpMode. Easy as that!

Any number of variables are able to be configured; however, names cannot be shared between types (i.e. you cannot have a long named "foo" and also a string named "foo").

#### Configuration Example
```kotlin
@TeleOp( name= "Pushbot: TeleOp Tank", group= "Pushbot" )
class KPushBot : KOpMode()
{
    // returns the value for the property, defaults to 0.02 if it hasn't been configured
    val CLAW_SPEED: Double = get( "claw speed", 0.02 );
}
```

## Examples

Some examples of how to use the library are included in the TeamCode module, in the package "kftc.examples" (see [here](TeamCode/src/main/java/kftc/examples)). These are a mixture of ports from the Qualcomm samples and some new ones.

### Installation

Simply fork the repository and start extending `KOpMode` and `KLinearOpMode` instead of their non-k-prefixed counterparts. Everything else is already handled for you automatically.
