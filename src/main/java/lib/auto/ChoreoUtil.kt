package lib.auto

import com.choreo.lib.Choreo
import com.choreo.lib.ChoreoTrajectory
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.InstantCommand
import frc.robot.subsystems.swerve.Drivetrain

fun getPath(traj: ChoreoTrajectory, isRed: Boolean, drivebase: Drivetrain, parallel: Command = InstantCommand()): Command {
    return Choreo.choreoSwerveCommand(
        traj,
        {drivebase.state.Pose},
        PIDController(0.7, 0.0, 0.0),
        PIDController(0.7, 0.0, 0.0),
        PIDController(0.05, 0.0, 0.01),
        drivebase::setChassisSpeeds,
        { isRed },
        drivebase,
    ).alongWith(parallel)
}
