package `in`.gov.mahapocra.farmerapppks.ui.chatbot

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonObject;

class QueryRequest(
    @field:SerializedName("query") var query: String,
    variables: JsonObject
) {
    @SerializedName("variables")
    var variables: JsonObject = variables
}