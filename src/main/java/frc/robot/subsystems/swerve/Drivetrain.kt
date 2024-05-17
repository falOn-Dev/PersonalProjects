package frc.robot.subsystems.swerve

import com.ctre.phoenix6.SignalLogger
import com.ctre.phoenix6.Utils
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest
import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.commands.PathPlannerAuto
import com.pathplanner.lib.util.HolonomicPathFollowerConfig
import com.pathplanner.lib.util.PIDConstants
import com.pathplanner.lib.util.ReplanningConfig
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.units.Measure
import edu.wpi.first.units.Units
import edu.wpi.first.units.Voltage
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.RobotController
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import frc.robot.generated.TunerConstants
import frc.robot.generated.TunerConstants.moduleConstants
import java.util.function.Supplier

object Drivetrain : SwerveDrivetrain(
    TunerConstants.DrivetrainConstants,
    *moduleConstants
), Subsystem {
    private var simNotifier: Notifier? = null
    private var lastSimTime = 0.0

    private val BlueAlliancePerspecitveRotation: Rotation2d = Rotation2d.fromDegrees(0.0)
    private val RedAlliancePerspectiveRotation: Rotation2d = Rotation2d.fromDegrees(180.0)
    private var hasAppliedOperatorPerspective: Boolean = false

    private val translationCharacterization: SwerveRequest.SysIdSwerveTranslation =
        SwerveRequest.SysIdSwerveTranslation()
    private val rotationCharacterization: SwerveRequest.SysIdSwerveRotation = SwerveRequest.SysIdSwerveRotation()
    private val steerCharacterization: SwerveRequest.SysIdSwerveSteerGains = SwerveRequest.SysIdSwerveSteerGains()

    val translationSysId: SysIdRoutine = SysIdRoutine(
        SysIdRoutine.Config(
            null,
            Units.Volts.of(4.0),
            null,
            { state -> SignalLogger.writeString("state", state.toString()) },
        ),
        SysIdRoutine.Mechanism(
            { volts: Measure<Voltage> -> setControl(translationCharacterization.withVolts(volts)) },
            null,
            this,
        ),
    )

    val rotationSysId: SysIdRoutine = SysIdRoutine(
        SysIdRoutine.Config(
            null,
            Units.Volts.of(4.0),
            null,
            { state -> SignalLogger.writeString("state", state.toString()) },
        ),
        SysIdRoutine.Mechanism(
            { volts: Measure<Voltage> -> setControl(rotationCharacterization.withVolts(volts)) },
            null,
            this,
        ),
    )

    val steerSysId: SysIdRoutine = SysIdRoutine(
        SysIdRoutine.Config(
            null,
            Units.Volts.of(7.0),
            null,
            { state -> SignalLogger.writeString("state", state.toString()) },
        ),
        SysIdRoutine.Mechanism(
            { volts: Measure<Voltage> -> setControl(steerCharacterization.withVolts(volts)) },
            null,
            this,
        ),
    )

    private val routineToApply: SysIdRoutine = translationSysId // Change this to the routine you want to run

    private val autoRequest: SwerveRequest.ApplyChassisSpeeds = SwerveRequest.ApplyChassisSpeeds()

    val currentChassisSpeeds: ChassisSpeeds
        get() = m_kinematics.toChassisSpeeds(*state.ModuleStates)

    init {
        configurePathPlanner()
        if (RobotBase.isSimulation()) {
            startSim()
        }
    }

    private fun configurePathPlanner() {
        var drivebaseRadius = 0.0
        for (loc in m_moduleLocations) {
            drivebaseRadius = drivebaseRadius.coerceAtLeast(loc.norm)
        }

        AutoBuilder.configureHolonomic(
            { this.state.Pose },
            this::seedFieldRelative,
            this::currentChassisSpeeds,
            { speeds -> this.setControl(autoRequest.withSpeeds(speeds)) },
            HolonomicPathFollowerConfig(
                PIDConstants(10.0, 0.0, 0.0),
                PIDConstants(10.0, 0.0, 0.0),
                TunerConstants.kSpeedAt12VoltsMps,
                drivebaseRadius,
                ReplanningConfig(true, true),
            ),
            { DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red },
            this,
        )
    }

    fun applyRequest(requestSupplier: Supplier<SwerveRequest>): Command {
        return this.run {
            this.setControl(requestSupplier.get())
        }
    }

    fun getAutoPath(pathname: String): Command {
        return PathPlannerAuto(pathname)
    }

    fun sysIdQuasistatic(direction: SysIdRoutine.Direction): Command {
        return routineToApply.quasistatic(direction)
    }

    fun sysIdDynamic(direction: SysIdRoutine.Direction): Command {
        return routineToApply.dynamic(direction)
    }

    private fun startSim(){
        lastSimTime = Utils.getCurrentTimeSeconds()

        simNotifier = Notifier {
            val currentTime = Utils.getCurrentTimeSeconds()
            val deltaTime = currentTime - lastSimTime
            lastSimTime = currentTime

            updateSimState(deltaTime, RobotController.getBatteryVoltage())
        }
        simNotifier!!.startPeriodic(kSimLoopPeriod)
    }

    override fun periodic() {
        if(!hasAppliedOperatorPerspective || DriverStation.isDisabled()){
            DriverStation.getAlliance().ifPresent { color ->
                if(color == DriverStation.Alliance.Red){
                    this.setOperatorPerspectiveForward(RedAlliancePerspectiveRotation)
                } else {
                    this.setOperatorPerspectiveForward(BlueAlliancePerspecitveRotation)
                }
            }
        }
    }

    private const val kSimLoopPeriod = 0.005 // 5ms

}
