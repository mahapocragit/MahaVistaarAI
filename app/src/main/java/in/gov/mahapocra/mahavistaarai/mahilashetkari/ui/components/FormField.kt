package `in`.gov.mahapocra.mahavistaarai.mahilashetkari.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun MsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    supportingText: String? = null,
    voiceInputEnabled: Boolean = false
) {
    val startVoiceInput = if (voiceInputEnabled && enabled) {
        rememberVoiceInputLauncher(onResult = onValueChange)
    } else null

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            enabled = enabled,
            singleLine = singleLine,
            isError = error != null,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = startVoiceInput?.let { launch ->
                {
                    IconButton(onClick = launch) {
                        Icon(Icons.Filled.Mic, contentDescription = "Speak to fill $label")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        val message = error ?: supportingText
        if (message != null) {
            Text(
                text = message,
                color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
