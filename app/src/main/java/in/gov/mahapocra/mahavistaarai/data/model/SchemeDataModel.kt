package `in`.gov.mahapocra.mahavistaarai.data.model

data class SchemeDataModel(
    val EligibilityCriteria: List<String>,
    val EligibilityCriteriaMarathi: List<String>,
    val MainComponent: List<String>,
    val MainComponentMarathi: List<String>,
    val RequiredDocumentMarathi: List<String>,
    val RequiredDocuments: List<String>,
    val SchemeCategoryName: String,
    val SchemeCategoryNameMr: String,
    val SchemeName: String,
    val SchemeNameMarathi: String,
    val SponsorshipType: String,
    val SponsorshipTypeMarathi: String
)