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
    val variables: Map<String, Map<String, ChoreoVariable>>
) {
    companion object {
        fun create(
            name: String = "ChoreoProject",
            version: SemanticVersion = SemanticVersion(2025, 0, 0),
            projectType: ChoreoProjectType = ChoreoProjectType.Swerve,
            expressionVariables: List<ChoreoVariable.Expression>
        ): ChoreoProject {
            return ChoreoProject(
                "Test",
                SemanticVersion(1, 0, 0),
                ChoreoProjectType.Swerve,
                mapOf(
                    "expressions" to expressionVariables.associateBy { it.name }
                )
            )
        }
    }
}