package lib

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import org.littletonrobotics.junction.Logger
import java.util.function.BiConsumer

/**
 * A class to log command status.
 * @param scheduler The command scheduler to log commands from.
 * @param table The table to log to.
 * @param mode The mode to log in. see [LoggingMode]
 * @constructor Creates a new CommandLogger with the given scheduler, table, and mode.
 */
class CommandLogger(
    private val scheduler: CommandScheduler,
    private var table: String = "scheduler/",
    private val mode: LoggingMode = LoggingMode.IS_ACTIVE,
) {
    /** The consumer to log command status. */
    val logCommandFunction: BiConsumer<Command, Boolean>


    init {
        if (!table.endsWith("/")) table += "/"

        when (mode) {
            LoggingMode.IS_ACTIVE -> {
                logCommandFunction =
                    BiConsumer { command: Command, active: Boolean ->
                        val id: String = command.name + command.hashCode()
                        Logger.recordOutput(table + "is-active" + id, active)
                    }
            }
            LoggingMode.ACTIVE_COUNT -> {
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

        scheduler.onCommandInitialize { command -> logCommandFunction.accept(command, true) }
        scheduler.onCommandFinish { command -> logCommandFunction.accept(command, false) }
        scheduler.onCommandInterrupt { command -> logCommandFunction.accept(command, false) }
    }

    /**
     * Enum to specify the mode of logging.
     */
    enum class LoggingMode {
        /** Log if commands are active. */
        IS_ACTIVE,
        /** Log how many instances of each command are active. */
        ACTIVE_COUNT,
        /** Log how many times each command has been run. */
        TIMES_RAN,
        /** Log if commands are active and how many instances of each command are active. */
        IS_ACTIVE_AND_TIMES_RAN,
        /** Log if commands are active and how many times each command has been run. */
        IS_ACTIVE_AND_ACTIVE_COUNT,
    }
}
