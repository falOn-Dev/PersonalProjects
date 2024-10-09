package lib.choreobuilder.json.project

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import lib.choreobuilder.json.project.variables.ChoreoVariable
import lib.choreobuilder.json.project.version.SemanticVersion


@Serializable
data class ChoreoProject(
    val name: String,
    val version: SemanticVersion,
    @SerialName("type")
    val projectType: ChoreoProjectType,
    val variables: Map<String, List<ChoreoVariable>>
)