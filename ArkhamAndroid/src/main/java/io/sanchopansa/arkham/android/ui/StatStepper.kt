package io.sanchopansa.arkham.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun StatStepper(
    modifier: Modifier = Modifier,
    header: String = "",
    value: Int = 0,
    max: Int = 0,
    prependText: String = "",
    appendText: String = "",
    prependIcon: ImageVector? = null,
    appendIcon: ImageVector? = null,
    minusAction: () -> Unit = { },
    plusAction: () -> Unit = { }
) {
    if (header != "") {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(header)
        }
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = minusAction,
            enabled = true
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                contentDescription = "Minus Sign"
            )
        }

        prependIcon?.let { Icon(imageVector = it, contentDescription = "Prepend Icon") }

        Text(
            text = "$prependText $value / $max $appendText",
            modifier = modifier
        )

        appendIcon?.let { Icon(imageVector = it, contentDescription = "Append Icon") }

        IconButton(
            onClick = plusAction,
            enabled = true,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowRight,
                contentDescription = "Plus Sign"
            )
        }
    }
}

@Preview
@Composable
fun StatStepperPreview() {
    StatStepper(
        header = "Заголовок",
        max = 6
    )
}