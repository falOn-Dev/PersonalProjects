package lib.auto

import com.choreo.lib.Choreo
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import frc.robot.subsystems.swerve.Drivetrain
import java.util.*
import java.util.function.Supplier

/**
 * Class for defining a choreo autonomous routine with an event map
 *
 * @param pathGroup Name of the path group that is used for this routine
 * @param swerve Swerve subsystem to send control requests to
 * @param shouldReset Boolean to define if the auto routine should reset odometry to the initial position of the first path
 * @param sequentialEventMap Map of path index -> command to run after path finishes, starts at zero
 * @param timeout Timeout in seconds of the routine, defaults to 16, just above the auto period
 */
class ChoreoAuto(
    val pathGroup: String,
    val swerve: Drivetrain,
    val shouldReset: Boolean = true,
    val sequentialEventMap: Map<Int, Supplier<Command>>,
    val parallelEventMap: Map<Int, Supplier<Command>>,
    val timeout: Double = 16.0,
    val startCommand: Supplier<Command> = Supplier { InstantCommand() },
) {
    val pathList = Choreo.getTrajectoryGroup(pathGroup)

    /**
     * Factory for creating the actual command group from the event map and path group
     *
     * @param isRed Whether the paths and initial pose need to be flipped or not
     */
    fun createCommand(isRed: Boolean): Command {
        val auto: SequentialCommandGroup = SequentialCommandGroup()
        auto.addCommands(startCommand.get())

        if (shouldReset) {
            if (isRed) {
                auto.addCommands(InstantCommand({ swerve.resetOdometry(pathList.first().flippedInitialPose) }).withName("Reset Pose"))
            } else {
                auto.addCommands(InstantCommand({ swerve.resetOdometry(pathList.first().initialPose) }).withName("Reset Pose"))
            }
        }
        pathList.forEachIndexed() { index, path ->
            val parallel: Command = kotlin.collections.Map<Int, Supplier<Command>>::getOrDefault
                .invoke(parallelEventMap, index, Supplier { InstantCommand() }).get()

            auto.addCommands(
                getPath(path, isRed, swerve, parallel),
                kotlin.collections.Map<Int, Supplier<Command>>::getOrDefault
                    .invoke(sequentialEventMap, index, Supplier { InstantCommand() }).get(),
            )
        }

        return Commands.waitSeconds(timeout).deadlineWith(auto)
    }
}
