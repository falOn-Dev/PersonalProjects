package frc.robot.subsystems.swerve

import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain.SwerveDriveState
import edu.wpi.first.units.Distance
import edu.wpi.first.units.Measure
import edu.wpi.first.units.Units
import edu.wpi.first.units.Velocity
import org.littletonrobotics.junction.Logger

class SwerveLogger(val maxSpeed: Measure<Velocity<Distance>> = Units.FeetPerSecond.of(12.0)) {

    fun telemetrize(state: SwerveDriveState) {
        Logger.recordOutput("Speeds", state.speeds)
        Logger.recordOutput("Desired States", *state.ModuleTargets)
        Logger.recordOutput("Actual States", *state.ModuleStates)
        Logger.recordOutput("Pose", state.Pose)
        Logger.recordOutput("Gyro Angle", state.Pose.rotation)
        Logger.recordOutput("Failed DAQs", state.FailedDaqs)
    }
}
