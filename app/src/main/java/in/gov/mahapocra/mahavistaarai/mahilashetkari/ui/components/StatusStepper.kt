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
fun StatusStepper(statusStep: Int, status: String, modifier: Modifier = Modifier) {
    val isPlainRejected = status == "rejected"
    val isAppealFlow = statusStep > 2 || status.startsWith("appeal")
    val isAppealRejected = isAppealFlow && status.contains("appeal") && status.contains("reject")

    val steps = if (isAppealFlow) {
        listOf(
            "Submitted",
            "Gram Sabha Conducted",
            "Rejected",
            "Appealed – Sent to TAO",
            if (isAppealRejected) "Appeal Rejected" else "Approved",
            "Certificate Issued"
        )
    } else {
        listOf("Submitted", "Gram Sabha Conducted", if (isPlainRejected) "Rejected" else "Approved")
    }

    // Reached-and-red steps: the branch point in the appeal flow is always
    // red (an appeal only exists because the original decision was a
    // rejection), and, for the plain flow, the final step when rejected.
    val redIndices = if (isAppealFlow) {
        setOfNotNull(2, if (isAppealRejected) 4 else null)
    } else {
        setOfNotNull(if (isPlainRejected) steps.lastIndex else null)
    }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        steps.forEachIndexed { index, label ->
            val completed = index < statusStep.coerceAtMost(steps.size)
            val isLast = index == steps.lastIndex
            val isRed = completed && index in redIndices

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
                                    isRed -> MaterialTheme.colorScheme.error
                                    completed -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outline
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (completed) {
                            Icon(
                                imageVector = if (isRed) Icons.Filled.Close else Icons.Filled.Check,
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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
