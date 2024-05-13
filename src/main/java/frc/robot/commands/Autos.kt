package frc.robot.commands

import com.pathplanner.lib.auto.AutoBuilder
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.PrintCommand
import frc.robot.subsystems.ExampleSubsystem
import java.util.function.Supplier

object Autos {
    private val autoModeChooser = SendableChooser<Supplier<Command>>().let {
        it.setDefaultOption("Do Nothing") { PrintCommand("Doing Nothing") }
        it
    }

    val selectedAuto: Command
        get() = autoModeChooser.selected.get()

    fun setupChooser() {
        if(DriverStation.isFMSAttached()) {
            AutoBuilder.getAllAutoNames().stream().filter { name -> name.endsWith("_cmp") }.forEach {
                    name -> autoModeChooser.addOption(name) { Drivebase.getAutonomousCommand(name, true) }
            }
        } else {
            AutoBuilder.getAllAutoNames().forEach {
                    name -> autoModeChooser.addOption(name) { Drivebase.getAutonomousCommand(name, false) }
            }
        }
    }
}
