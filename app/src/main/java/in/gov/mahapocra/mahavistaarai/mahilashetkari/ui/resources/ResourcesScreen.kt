package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage

private data class ResourceItem(val title: String)

@Composable
fun ResourcesScreen(lang: AppLanguage) {
    val title = if (lang == AppLanguage.EN) "Resources" else "संसाधने"
    val items = if (lang == AppLanguage.EN) listOf(
        ResourceItem("Maharashtra Women Farmer Empowerment Act, 2026 — full text"),
        ResourceItem("Application process guide (step-by-step)"),
        ResourceItem("List of eligible agricultural work categories"),
        ResourceItem("Taluka Agriculture Office contact directory"),
        ResourceItem("Frequently asked questions")
    ) else listOf(
        ResourceItem("महाराष्ट्र महिला शेतकरी सक्षमीकरण अधिनियम, २०२६ — संपूर्ण मजकूर"),
        ResourceItem("अर्ज प्रक्रिया मार्गदर्शक (टप्प्याटप्प्याने)"),
        ResourceItem("पात्र शेती कामाच्या प्रकारांची यादी"),
        ResourceItem("तालुका कृषी कार्यालय संपर्क सूची"),
        ResourceItem("वारंवार विचारले जाणारे प्रश्न")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(18.dp))
        items.forEach { item ->
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
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(item.title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
