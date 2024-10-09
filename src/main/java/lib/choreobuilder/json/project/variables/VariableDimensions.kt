package lib.choreobuilder.json.project.variables

import kotlinx.serialization.Serializable

@Serializable
enum class VariableDimensions {
    Number,
    Length,
    LinVel,
    LinAcc,
    Angle,
    AngVel,
    AngAcc,
    Time,
    Mass,
    Torque,
    MoI
}