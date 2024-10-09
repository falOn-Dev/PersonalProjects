package lib.choreobuilder.json.project.variables

import edu.wpi.first.units.Angle
import edu.wpi.first.units.Distance
import edu.wpi.first.units.Measure
import edu.wpi.first.units.Unit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ChoreoVariable {
    @Serializable
    data class Expression(
        val name: String,
        val dimension: VariableDimensions,
        val expression: String,
        @SerialName("val") val value: Double = 0.0
    ) : ChoreoVariable() {
        companion object {
            fun <U : Unit<U>> fromMeasure(name: String, measure: Measure<U>): Expression {
                return when (measure.unit()) {
                    is Distance -> Expression(name, VariableDimensions.Length, "${measure.baseUnitMagnitude()} m")
                    is Angle -> Expression(name, VariableDimensions.Angle, "${measure.baseUnitMagnitude()} rad")
                    else -> throw IllegalArgumentException("Unsupported unit type")
                }
            }
        }
    }
}