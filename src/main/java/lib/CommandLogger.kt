package lib

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import org.littletonrobotics.junction.Logger
import java.util.function.BiConsumer

class CommandLogger(
    private val scheduler: CommandScheduler,
    private var table: String = "scheduler/",
    private val mode: LoggingMode = LoggingMode.IS_ACTIVE,
) {
    val logCommandFunction: BiConsumer<Command, Boolean>

    init {
        // Ensure table name ends with "/"
        if (!table.endsWith("/")) table += "/"

        // Define the logging function based on the selected mode
        when (mode) {
            LoggingMode.IS_ACTIVE -> {
                // Define a BiConsumer that logs whether each command is active
                // It creates a unique id for each command and logs the active status of the command
                logCommandFunction =
                    BiConsumer { command: Command, active: Boolean ->
                        val id: String = command.name + command.hashCode()
                        Logger.recordOutput(table + "is-active" + id, active)
                    }
            }
            LoggingMode.ACTIVE_COUNT -> {
                // Define a BiConsumer that logs the count of active instances for each command
                // It maintains a count of active instances for each command and logs the count
                val activeCount: MutableMap<String, Int> = mutableMapOf()
                logCommandFunction =
                    BiConsumer { command: Command, active: Boolean ->
                        val name = command.name
                        val newCount = activeCount.getOrDefault(name, 0) + if (active) 1 else -1
                        activeCount[name] = newCount
                        Logger.recordOutput(table + "active-count/" + name, newCount)
                    }
            }
            LoggingMode.TIMES_RAN -> {
                // Define a BiConsumer that logs the number of times each command has been run
                // It maintains a count of how many times each command has been run and logs the count
                val timesRan: MutableMap<String, Int> = mutableMapOf()
                logCommandFunction =
                    BiConsumer { command: Command, active: Boolean ->
                        val name = command.name
                        val newCount = timesRan.getOrDefault(name, 0) + if (active) 1 else 0
                        timesRan[name] = newCount
                        Logger.recordOutput(table + "times-ran/" + name, newCount)
                    }
            }
            LoggingMode.IS_ACTIVE_AND_ACTIVE_COUNT -> {
                // Define a BiConsumer that logs both whether each command is active and the count of active instances
                // It creates a unique id for each command, logs the active status of the command, and maintains a count of active instances for each command
                val activeCount: MutableMap<String, Int> = mutableMapOf()
                logCommandFunction =
                    BiConsumer { command: Command, active: Boolean ->
                        val name = command.name
                        val id = name + command.hashCode()
                        Logger.recordOutput(table + "is-active" + id, active)
                        val newCount = activeCount.getOrDefault(name, 0) + if (active) 1 else -1
                        activeCount[name] = newCount
                        Logger.recordOutput(table + "active-count/" + name, newCount)
                    }
            }
            LoggingMode.IS_ACTIVE_AND_TIMES_RAN -> {
                // Define a BiConsumer that logs both whether each command is active and the number of times it has been run
                // It creates a unique id for each command, logs the active status of the command, and maintains a count of how many times each command has been run
                val timesRan: MutableMap<String, Int> = mutableMapOf()
                logCommandFunction =
                    BiConsumer { command: Command, active: Boolean ->
                        val name = command.name
                        val id = name + command.hashCode()
                        Logger.recordOutput(table + "is-active" + id, active)
                        val newCount = timesRan.getOrDefault(name, 0) + if (active) 1 else 0
                        timesRan[name] = newCount
                        Logger.recordOutput(table + "times-ran/" + name, newCount)
                    }
            }
        }

        // Register the logging function to be called when commands are initialized, finished, or interrupted
        // When a command is initialized, the logging function is called with the command and true as arguments
        // When a command is finished or interrupted, the logging function is called with the command and false as arguments
        scheduler.onCommandInitialize { command -> logCommandFunction.accept(command, true) }
        scheduler.onCommandFinish { command -> logCommandFunction.accept(command, false) }
        scheduler.onCommandInterrupt { command -> logCommandFunction.accept(command, false) }
    }

    enum class LoggingMode {
        IS_ACTIVE,
        ACTIVE_COUNT,
        TIMES_RAN,
        IS_ACTIVE_AND_TIMES_RAN,
        IS_ACTIVE_AND_ACTIVE_COUNT,
    }
}