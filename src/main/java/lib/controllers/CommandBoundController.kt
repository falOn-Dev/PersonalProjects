package lib.controllers

import edu.wpi.first.wpilibj2.command.button.Trigger
import lib.controllers.BoundController.Companion.defaultAxes
import lib.controllers.BoundController.Companion.defaultButtons

class CommandBoundController(private val port: Int) {
    val underlyingHID = BoundController(port)

    fun loadBindings(
        buttonBindings: Bindings = defaultButtons,
        axisBindings: Bindings = defaultAxes,
    ) {
        underlyingHID.loadBindings(buttonBindings, axisBindings)
    }

    fun getButton(button: String): Trigger {
        return Trigger { underlyingHID.getButton(button) }
    }

    fun getButton(button: BoundController.StandardButtons): Trigger {
        return getButton(button.toString())
    }

    fun getAxis(axis: String): Double {
        return underlyingHID.getAxis(axis)
    }

    fun getAxis(axis: BoundController.StandardAxes): Double {
        return getAxis(axis.toString())
    }
}