package com.hambab.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hambab.app.ui.theme.HbAmber
import com.hambab.app.ui.theme.HbButtonShape
import com.hambab.app.ui.theme.HbCreamCard
import com.hambab.app.ui.theme.HbFgSoft

@Composable
fun EmptyState(
    title: String,
    subtitle: String? = null,
    ctaLabel: String? = null,
    onCta: (() -> Unit)? = null,
) {
    HbCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 24.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = HbFgSoft),
                    textAlign = TextAlign.Center,
                )
            }
            if (ctaLabel != null && onCta != null) {
                Button(
                    onClick = onCta,
                    shape = HbButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HbAmber,
                        contentColor = HbCreamCard,
                    ),
                    modifier = Modifier.padding(top = 4.dp),
                ) { Text(ctaLabel) }
            }
        }
    }
}
