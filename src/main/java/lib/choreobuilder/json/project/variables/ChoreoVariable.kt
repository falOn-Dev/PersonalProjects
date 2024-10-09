package lib.choreobuilder.json.project.variables

import edu.wpi.first.units.Angle
import edu.wpi.first.units.Distance
import edu.wpi.first.units.Mass
import edu.wpi.first.units.Measure
import edu.wpi.first.units.Time
import edu.wpi.first.units.Unit
import edu.wpi.first.units.Velocity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class ChoreoVariable {
    @Serializable(with = ExpressionSerializer::class)
    data class Expression(
        val name: String,
        val dimension: VariableDimensions,
        val expression: String,
        @SerialName("val") val value: Double = 0.0
    ) : ChoreoVariable() {
        companion object {
            /**
             * Create an expression variable from a measure.
             *
             * Does not work with compound units like [Velocity].
             */
            fun <U : Unit<U>> fromMeasure(name: String, measure: Measure<U>): Expression {
                return when (measure.unit()) {
                    is Distance -> Expression(name, VariableDimensions.Length, "${measure.baseUnitMagnitude()} m")
                    is Angle -> Expression(name, VariableDimensions.Angle, "${measure.baseUnitMagnitude()} rad")
                    is Time -> Expression(name, VariableDimensions.Time, "${measure.baseUnitMagnitude()} s")
                    is Mass -> Expression(name, VariableDimensions.Mass, "${measure.baseUnitMagnitude()} kg")
                    else -> throw IllegalArgumentException("Unsupported unit type")
                }
            }

            inline fun <reified U : Unit<U>> fromMeasure(name: String, measure: Measure<Velocity<U>>): Expression {
                return when(U::class){
                    Distance::class -> Expression(name, VariableDimensions.Length, "${measure.baseUnitMagnitude()}(m / s)")
                    Angle::class -> Expression(name, VariableDimensions.Angle, "${measure.baseUnitMagnitude()}(rad / s)")
                    else -> throw IllegalArgumentException("Unsupported unit type")
                }
            }

            inline fun <reified U : Unit<U>> fromMeasure(name: String, measure: Measure<Velocity<Velocity<U>>>): Expression {
                return when(U::class){
                    Distance::class -> Expression(name, VariableDimensions.Length, "${measure.baseUnitMagnitude()}(m / s^2)")
                    Angle::class -> Expression(name, VariableDimensions.Angle, "${measure.baseUnitMagnitude()}(rad / s ^ 2)")
                    else -> throw IllegalArgumentException("Unsupported unit type")
                }
            }
        }
    }
}