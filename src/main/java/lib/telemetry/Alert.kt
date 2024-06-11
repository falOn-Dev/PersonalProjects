package lib.telemetry

import edu.wpi.first.util.sendable.Sendable
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import java.util.function.Predicate


class Alert(
    group: String,
    message: String,
    type: Level,
) {

    private val type: Level
    private var message: String
    private var activeStartTime: Double = 0.0
    private var active: Boolean = false

    init {
        if (!groups.containsKey(group)) {
            groups[group] = SendableAlerts()
            SmartDashboard.putData(group, groups[group])
        }

        this.type = type
        this.message = message
        groups[group]?.alerts?.add(this) // Add this alert to the group
    }

    constructor(message: String, type: Level) : this("Alerts", message, type) {}

    fun set(active: Boolean) {
        if (active && !this.active) {
            activeStartTime = Timer.getFPGATimestamp()
            when (type) {
                Level.ERROR -> DriverStation.reportError(message, false)
                Level.WARNING -> DriverStation.reportWarning(message, false)
                Level.INFO -> println(message)
            }
        }
        this.active = active
    }

    /** Updates current alert text.  */
    fun setText(text: String) {
        if (active && text != this.message) {
            when (type) {
                Level.ERROR -> DriverStation.reportError(text, false)
                Level.WARNING -> DriverStation.reportWarning(text, false)
                Level.INFO -> println(text)
            }
        }
        this.message = text
    }

    fun toggleActive() {
        set(!active)
    }

    class SendableAlerts : Sendable {
        val alerts: MutableList<Alert> = ArrayList()

        fun getStrings(type: Level): Array<String> {
            val activeFilter: Predicate<Alert> = Predicate<Alert> { x: Alert -> x.type == type && x.active }
            val timeSorter =
                Comparator { a1: Alert, a2: Alert -> (a2.activeStartTime - a1.activeStartTime).toInt() }
            return alerts.stream()
                .filter(activeFilter)
                .sorted(timeSorter)
                .map<Any> { a: Alert -> a.message }
                .toArray<String> { length -> arrayOfNulls(length) }
        }

        override fun initSendable(builder: SendableBuilder) {
            builder.setSmartDashboardType("Alerts")
            builder.addStringArrayProperty("errors", { getStrings(Level.ERROR) }, null)
            builder.addStringArrayProperty("warnings", { getStrings(Level.WARNING) }, null)
            builder.addStringArrayProperty("infos", { getStrings(Level.INFO) }, null)
        }
    }

    enum class Level {
        INFO, WARNING, ERROR
    }

    companion object {
        private val groups = HashMap<String, SendableAlerts>()
    }
}
