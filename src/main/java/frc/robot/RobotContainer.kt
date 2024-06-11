package frc.robot

import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import edu.wpi.first.wpilibj2.command.button.Trigger
import frc.robot.Constants.OperatorConstants
import frc.robot.commands.Autos
import frc.robot.commands.ExampleCommand
import frc.robot.subsystems.ExampleSubsystem
import lib.telemetry.Alert

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the [Robot]
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 *
 * In Kotlin, it is recommended that all your Subsystems are Kotlin objects. As such, there
 * can only ever be a single instance. This eliminates the need to create reference variables
 * to the various subsystems in this container to pass into to commands. The commands can just
 * directly reference the (single instance of the) object.
 */
object RobotContainer {
    // Replace with CommandPS4Controller or CommandJoystick if needed
    private val driverController = CommandXboxController(OperatorConstants.DRIVER_CONTROLLER_PORT)
    private val testWarning: Alert = Alert("Test Warning", Alert.Level.WARNING)
    private val testWarning2: Alert = Alert("Test Warning 2", Alert.Level.WARNING)
    private val testError: Alert = Alert("Test Error", Alert.Level.ERROR)
    private val testError2: Alert = Alert("Test Error 2", Alert.Level.ERROR)
    private val testInfo: Alert = Alert("Test Info", Alert.Level.INFO)
    private val testInfo2: Alert = Alert("Test Info 2", Alert.Level.INFO)

    init {
        configureBindings()
        // Reference the Autos object so that it is initialized, placing the chooser on the dashboard
        Autos
    }

    /**
     * Use this method to define your `trigger->command` mappings. Triggers can be created via the
     * [Trigger] constructor that takes a [BooleanSupplier][java.util.function.BooleanSupplier]
     * with an arbitrary predicate, or via the named factories in [GenericHID][edu.wpi.first.wpilibj2.command.button.CommandGenericHID]
     * subclasses such for [Xbox][CommandXboxController]/[PS4][edu.wpi.first.wpilibj2.command.button.CommandPS4Controller]
     * controllers or [Flight joysticks][edu.wpi.first.wpilibj2.command.button.CommandJoystick].
     */
    private fun configureBindings() {
        driverController.a().onTrue(Commands.runOnce(testInfo::toggleActive))
        driverController.b().onTrue(Commands.runOnce(testInfo2::toggleActive))
        driverController.x().onTrue(Commands.runOnce(testWarning::toggleActive))
        driverController.y().onTrue(Commands.runOnce(testWarning2::toggleActive))
        driverController.start().onTrue(Commands.runOnce(testError::toggleActive))
        driverController.back().onTrue(Commands.runOnce(testError2::toggleActive))
    }
}
