package frc.robot

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest
import edu.wpi.first.units.Units
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import edu.wpi.first.wpilibj2.command.button.Trigger
import frc.robot.Constants.OperatorConstants
import frc.robot.commands.Autos
import frc.robot.generated.TunerConstants
import frc.robot.subsystems.swerve.Drivetrain
import frc.robot.subsystems.swerve.SwerveLogger

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

    private val maxSpeed = Units.MetersPerSecond.of(TunerConstants.kSpeedAt12VoltsMps)
    private const val maxAngular = 1.5 * Math.PI

    private val logger: SwerveLogger = SwerveLogger(maxSpeed)

    val drive: SwerveRequest.FieldCentric = SwerveRequest.FieldCentric()
        .withDeadband(TunerConstants.kSpeedAt12VoltsMps * 0.1)
        .withRotationalDeadband(maxAngular * 0.1)
        .withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage)
        .withSteerRequestType(SwerveModule.SteerRequestType.MotionMagicExpo)

    init {
        Drivetrain
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
        Drivetrain.defaultCommand = Drivetrain.applyRequest {
            drive.withVelocityX(-driverController.leftY * maxSpeed.`in`(Units.MetersPerSecond))
                .withVelocityY(driverController.leftX * maxSpeed.`in`(Units.MetersPerSecond))
                .withRotationalRate(driverController.rightX * maxAngular)
        }.ignoringDisable(true)

        driverController.leftBumper().onTrue(Drivetrain.runOnce { Drivetrain.seedFieldRelative() })

        Drivetrain.registerTelemetry(logger::telemetrize)
    }
}
