package lib.choreobuilder.json.project.version

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class SemanticVersionSerializer: KSerializer<SemanticVersion> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("SemanticVersion", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: SemanticVersion) {
        val versionString = "v${value.major}.${value.minor}.${value.patch}"
        encoder.encodeString(versionString)
    }

    override fun deserialize(decoder: Decoder): SemanticVersion {
        val versionString = decoder.decodeString()

        val versionParts = versionString.removePrefix("v").split(".")
        return SemanticVersion(
            versionParts[0].toInt(),
            versionParts[1].toInt(),
            versionParts[2].toInt()
        )
    }
}