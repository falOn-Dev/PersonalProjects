package lib.choreobuilder.json.project.variables

import jdk.jshell.spi.ExecutionControl.NotImplementedException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class ExpressionSerializer: KSerializer<ChoreoVariable.Expression> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("Expression") {
            element<String>("dimension")
            element<String>("expression")
            element<Double>("val")
        }

    override fun serialize(encoder: Encoder, value: ChoreoVariable.Expression) {
        val jsonObject = buildJsonObject {
            put("dimension", JsonPrimitive(value.dimension.toString()))
            put("expression", JsonPrimitive(value.expression))
            put("val", JsonPrimitive(value.value))
        }

        encoder.encodeSerializableValue(
            JsonObject.serializer(), JsonObject(jsonObject)
        )
    }

    override fun deserialize(decoder: Decoder): ChoreoVariable.Expression {
        throw NotImplementedException("Not implemented")
    }
}