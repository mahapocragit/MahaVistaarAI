package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.eligibility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage

@Composable
fun EligibilityScreen(lang: AppLanguage) {
    val title = if (lang == AppLanguage.EN) "Who can apply" else "कोण अर्ज करू शकते"
    val points = if (lang == AppLanguage.EN) listOf(
        "Any woman engaged in farming or allied agricultural work in Maharashtra.",
        "Applies regardless of whose name the agricultural land is registered under.",
        "Valid Aadhaar card with a mobile number linked for OTP verification.",
        "Covers crop cultivation, vegetable farming, orchards, floriculture and dairy farming."
    ) else listOf(
        "महाराष्ट्रातील शेती किंवा शेतीपूरक कामात गुंतलेली कोणतीही महिला.",
        "जमीन कोणाच्याही नावावर नोंदणीकृत असो, अर्ज करता येतो.",
        "OTP पडताळणीसाठी मोबाइल क्रमांक जोडलेले वैध आधार कार्ड आवश्यक.",
        "पीक लागवड, भाजीपाला शेती, फळबाग, फुलशेती आणि दुग्धव्यवसाय यांचा समावेश."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(18.dp))
        points.forEach { point ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(point, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
