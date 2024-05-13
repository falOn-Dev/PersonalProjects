package lib.controllers

import edu.wpi.first.wpilibj.GenericHID

typealias Bindings = Map<String, Int>

class BoundController(port: Int) : GenericHID(port) {
    var buttonBindings: Bindings = defaultButtons
    var axisBindings: Bindings = defaultAxes

    fun loadBindings(
        buttonBindings: Bindings = defaultButtons,
        axisBindings: Bindings = defaultAxes,
    ) {
        this.buttonBindings = buttonBindings
        this.axisBindings = axisBindings
    }

    fun getButton(button: String): Boolean {
        if (buttonBindings.containsKey(button)) {
            return getRawButton(buttonBindings[button]!!)
        } else {
            return false
        }
    }

    fun getButton(button: StandardButtons): Boolean {
        return getButton(button.toString())
    }

    fun getAxis(axis: String): Double {
        if (axisBindings.containsKey(axis)) {
            return getRawAxis(axisBindings[axis]!!)
        } else {
            return 0.0
        }
    }

    fun getAxis(axis: StandardAxes): Double {
        return getAxis(axis.toString())
    }

    enum class StandardButtons {
        A, B, X, Y, LEFT_BUMPER, RIGHT_BUMPER, BACK, START, LEFT_STICK, RIGHT_STICK;

        override fun toString(): String {
            return when (this) {
                A -> "A"
                B -> "B"
                X -> "X"
                Y -> "Y"
                LEFT_BUMPER -> "LB"
                RIGHT_BUMPER -> "RB"
                BACK -> "BACK"
                START -> "START"
                LEFT_STICK -> "LEFT_STICK"
                RIGHT_STICK -> "RIGHT_STICK"
            }
        }
    }

    enum class StandardAxes {
        LEFT_X, LEFT_Y, LEFT_TRIGGER, RIGHT_TRIGGER, RIGHT_X, RIGHT_Y;

        override fun toString(): String {
            return when (this) {
                LEFT_X -> "LX"
                LEFT_Y -> "LY"
                LEFT_TRIGGER -> "LT"
                RIGHT_TRIGGER -> "RT"
                RIGHT_X -> "RX"
                RIGHT_Y -> "RY"
            }
        }
    }

    companion object {
        val defaultButtons: Bindings = mapOf(
            "A" to 1,
            "B" to 2,
            "X" to 3,
            "Y" to 4,
            "LB" to 5,
            "RB" to 6,
            "BACK" to 7,
            "START" to 8,
            "LEFT_STICK" to 9,
            "RIGHT_STICK" to 10,
        )

        val defaultAxes: Bindings = mapOf(
            "LX" to 0,
            "LY" to 1,
            "LT" to 2,
            "RT" to 3,
            "RX" to 4,
            "RY" to 5,
        )
    }
}
