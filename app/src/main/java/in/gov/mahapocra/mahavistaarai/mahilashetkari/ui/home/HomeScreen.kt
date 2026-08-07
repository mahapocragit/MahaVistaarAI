package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.data.repository.MahilaShetkariRepository
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.chatbot.ChatBotDialog
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.navigation.Screen
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.AppLanguage
import `in`.gov.mahapocra.mahavistaarai.mahilashetkari.util.Strings

@Composable
fun HomeScreen(repository: MahilaShetkariRepository, lang: AppLanguage, onNavigate: (String) -> Unit) {
    val strings = Strings.home(lang)
    val chatStrings = Strings.chatBot(lang)
    var chatOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero — gradient panel carrying the act badge, heading and primary CTAs.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = strings.badge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = strings.heading,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = strings.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )

                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MsPrimaryButtonOnHero(
                        text = strings.applyNow,
                        icon = Icons.Filled.Description,
                        onClick = { onNavigate(Screen.Apply.route) }
                    )
                    MsOutlinedButtonOnHero(
                        text = strings.checkEligibility,
                        icon = Icons.AutoMirrored.Filled.FactCheck,
                        onClick = { onNavigate(Screen.Eligibility.route) }
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            // Quick-action cards
            ActionCard(
                icon = Icons.Filled.TrackChanges,
                title = strings.trackApplication,
                onClick = { onNavigate(Screen.Track.route) }
            )
            Spacer(Modifier.height(12.dp))
            ActionCard(
                icon = Icons.Filled.DownloadForOffline,
                title = strings.downloadCertificate,
                onClick = { onNavigate(Screen.Track.route) }
            )

            Spacer(Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = strings.footer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }

        FloatingActionButton(
            onClick = { chatOpen = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Filled.SmartToy, contentDescription = chatStrings.fabDescription)
        }
    }

    if (chatOpen) {
        ChatBotDialog(
            repository = repository,
            lang = lang,
            onDismiss = { chatOpen = false },
            onNavigate = onNavigate
        )
    }
}

@Composable
private fun ActionCard(icon: ImageVector, title: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MsPrimaryButtonOnHero(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.height(50.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun MsOutlinedButtonOnHero(text: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
        modifier = Modifier.height(50.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
