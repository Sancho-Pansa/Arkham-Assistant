package io.sanchopansa.arkham.android.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BlueRedSlider(
    minBlue: Int = 0, blueLabel: String = "Blue", maxRed: Int = 4,
    redLabel: String = "Red", initialIndex: Int = 0,
    modifier: Modifier = Modifier, enabled: Boolean = false,
    canSelect: (Int) -> Boolean = { true }, onSelect: (Int) -> Unit = {}
) {
    Card(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text("$blueLabel / $redLabel")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                repeat(4) { index ->
                    val unreachable = enabled && !canSelect(index)
                    OutlinedButton(
                        onClick = { onSelect(index) }, enabled = enabled && !unreachable,
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (index == initialIndex) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                            disabledContainerColor = if (unreachable) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            } else {
                                Color.Transparent
                            }
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (index == initialIndex) Color.Black else Color.Gray
                        )
                    ) {
                        Column {
                            Text(
                                text = "${minBlue + index}",
                                color = Color(0xFF1565C0),
                                fontWeight = if (index == initialIndex) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "${maxRed - index}",
                                color = Color(0xFFC62828),
                                fontWeight = if (index == initialIndex) FontWeight.Bold else FontWeight.Normal
                            )
                        }
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
