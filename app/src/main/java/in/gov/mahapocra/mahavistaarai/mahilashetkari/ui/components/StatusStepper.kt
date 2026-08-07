package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** status_step from the API is 0/1/2 (Submitted / Under Review / Approved-or-Rejected). */
@Composable
fun StatusStepper(statusStep: Int, isRejected: Boolean, modifier: Modifier = Modifier) {
    val steps = listOf("Submitted", "Under Review", if (isRejected) "Rejected" else "Approved")
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        steps.forEachIndexed { index, label ->
            val completed = index <= statusStep
            val isLast = index == steps.lastIndex
            val isFinalRejected = isRejected && index == 2

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            thickness = 2.dp
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isFinalRejected -> MaterialTheme.colorScheme.error
                                    completed -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outline
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (completed) {
                            Icon(
                                imageVector = if (isFinalRejected) Icons.Filled.Close else Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    if (!isLast) {
                        Spacer(Modifier.weight(1f))
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (completed) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
