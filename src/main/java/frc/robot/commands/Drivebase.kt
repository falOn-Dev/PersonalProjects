package frc.robot.commands

import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.util.HolonomicPathFollowerConfig
import com.pathplanner.lib.util.PIDConstants
import com.pathplanner.lib.util.ReplanningConfig
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.filter.SlewRateLimiter
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Filesystem
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.SubsystemBase
import swervelib.SwerveController
import swervelib.SwerveDrive
import swervelib.parser.SwerveDriveConfiguration
import swervelib.parser.SwerveParser
import java.io.File
import java.util.function.DoubleSupplier
import kotlin.jvm.optionals.getOrDefault
import kotlin.time.times
import edu.wpi.first.math.util.Units as Conversions

object Drivebase : SubsystemBase() {

    private val configDirectory: File = File(Filesystem.getDeployDirectory(), "/swerve_configurations/base")
    private val drivebase: SwerveDrive
    val maxSpeed: Double = Conversions.feetToMeters(15.0)

    private val driveFeedforward: SimpleMotorFeedforward = SimpleMotorFeedforward(0.0, 0.0, 0.0)

    private val forwardsSlewRateLimiter: SlewRateLimiter = SlewRateLimiter(2.0)
    private val strafeSlewRateLimiter: SlewRateLimiter = SlewRateLimiter(2.0)
    private val rotationSlewRateLimiter: SlewRateLimiter = SlewRateLimiter(2.0)

    private var shouldFieldOriented: Boolean = true

    private val slowmodeFunc: (Double) -> Double = {
        supplier: Double ->
        (-0.8 * supplier) + 1.0
    }

    init {
        // Load the base configuration from the filesystem
        // This is pulled from Bounty's code, update if a different bot is used
        drivebase = SwerveParser(configDirectory).createSwerveDrive(driveFeedforward, maxSpeed)
    }

    val kinematics: SwerveDriveKinematics
        get() = drivebase.kinematics

    val pose: Pose2d
        get() = drivebase.pose

    val heading: Rotation2d
        get() = pose.rotation

    val fieldVelocity: ChassisSpeeds
        get() = drivebase.fieldVelocity

    val robotVelocity: ChassisSpeeds
        get() = drivebase.robotVelocity

    val controller: SwerveController
        get() = drivebase.swerveController

    val config: SwerveDriveConfiguration
        get() = drivebase.swerveDriveConfiguration

    fun configurePathPlanner() {
        AutoBuilder.configureHolonomic(
            this::pose,
            this::resetOdometry,
            this::robotVelocity,
            this::setChassisSpeeds,
            HolonomicPathFollowerConfig(
                PIDConstants(1.0, 0.0, 1.0),
                PIDConstants(1.0, 0.0, 1.0),
                4.0,
                config.driveBaseRadiusMeters,
                ReplanningConfig(
                    true,
                    true
                )
            ),
            {
                if(DriverStation.getAlliance().isPresent) {
                    DriverStation.getAlliance().getOrDefault(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red
                } else {
                    false
                }
            },
            this
        )
    }

    fun driveCommand(
        forwardsSupplier: DoubleSupplier,
        strafeSupplier: DoubleSupplier,
        rotationSupplier: DoubleSupplier,
        slowModeSupplier: DoubleSupplier
    ): Command {
        return this.run {
            val speedScale = slowmodeFunc(slowModeSupplier.asDouble)

            val forwards = forwardsSlewRateLimiter.calculate(forwardsSupplier.asDouble)
            val strafe = strafeSlewRateLimiter.calculate(strafeSupplier.asDouble)
            val rotation = rotationSlewRateLimiter.calculate(rotationSupplier.asDouble)

            drivebase.drive(
                Translation2d(
                    forwards * maxSpeed * speedScale,
                    strafe * maxSpeed * speedScale
                ),
                rotation * controller.config.maxAngularVelocity * speedScale,
                shouldFieldOriented,
                false
            )
        }
    }

    fun setChassisSpeeds(speeds: ChassisSpeeds){
        drivebase.setChassisSpeeds(speeds)
    }

    fun resetOdometry(pose: Pose2d) {
        drivebase.resetOdometry(pose)
    }

    fun zeroHeading() {
        drivebase.zeroGyro()
    }

    fun setMotorBrakeMode(brake: Boolean) {
        drivebase.setMotorIdleMode(brake)
    }

    fun toggleFieldOriented() {
        shouldFieldOriented = !shouldFieldOriented
    }

    fun configureVisionStdDevs(
        forwardStdDev: Double,
        strafeStdDev: Double,
        rotationStdDev: Double
    ) {
        drivebase.swerveDrivePoseEstimator.setVisionMeasurementStdDevs(
            VecBuilder.fill(forwardStdDev, strafeStdDev, rotationStdDev)
        )
    }

    fun addVisionMeasurement(
        pose: Pose2d,
        timestamp: Double,
    ) {
        drivebase.addVisionMeasurement(pose, timestamp)
    }
}
