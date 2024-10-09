package frc.robot

import edu.wpi.first.units.Units
import kotlinx.serialization.json.Json
import lib.choreobuilder.json.project.variables.ChoreoVariable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals


internal class ChoreoVariableTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testLengthVariable() {
        val testLengthValue = ChoreoVariable.Expression.fromMeasure(Units.Feet.of(10.0))
        val lengthJson = json.encodeToString(ChoreoVariable.Expression.serializer(), testLengthValue)

        assertEquals("{\"dimension\":\"Length\",\"expression\":\"3.048 m\"}", lengthJson)
    }

    @Test
    fun testAngleVariable(){
        val testAngleValue = ChoreoVariable.Expression.fromMeasure(Units.Degrees.of(90.0))
        val angleJson = json.encodeToString(ChoreoVariable.Expression.serializer(), testAngleValue)

        assertEquals("{\"dimension\":\"Angle\",\"expression\":\"1.5707963267948966 rad\"}", angleJson)
    }
}