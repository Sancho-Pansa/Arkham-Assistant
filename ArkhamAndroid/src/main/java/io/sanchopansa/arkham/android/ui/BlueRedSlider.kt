package io.sanchopansa.arkham.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BlueRedSlider(
    minBlue: Int = 0, blueLabel: String = "Blue", maxRed: Int = 4,
    redLabel: String = "Red", initialIndex: Int = 0,
    modifier: Modifier = Modifier, enabled: Boolean = false,
    canSelect: (Int) -> Boolean = { true }, onSelect: (Int) -> Unit = {}
) {
    Card(modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Column(Modifier.padding(8.dp)) {
            Text("$blueLabel / $redLabel")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                repeat(4) { index ->
                    OutlinedButton(
                        onClick = { onSelect(index) }, enabled = enabled && canSelect(index),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (index == initialIndex) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("${minBlue + index}\n${maxRed - index}")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun previewBlueRedSlider() {
    BlueRedSlider(
        minBlue = 0,
        blueLabel = "Скорость",
        maxRed = 4,
        redLabel = "Скрытность",
        enabled = true
    ) {
    }
}