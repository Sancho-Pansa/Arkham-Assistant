package io.sanchopansa.arkham.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BlueRedSlider(
    minBlue: Int = 0, blueLabel: String = "Blue", maxRed: Int = 4,
    redLabel: String = "Red", initialIndex: Int = 0,
    modifier: Modifier = Modifier, enabled: Boolean = false,
    canSelect: (Int) -> Boolean = { true }, onSelect: (Int) -> Unit = {}
) {
    Card(modifier.fillMaxWidth().padding(vertical=4.dp)) {
        Column(Modifier.padding(8.dp)) {
            Text("$blueLabel / $redLabel")
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceEvenly) {
                repeat(4) { index ->
                    OutlinedButton(onClick={onSelect(index)}, enabled=enabled && canSelect(index),
                        contentPadding=PaddingValues(8.dp),
                        colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=if(index==initialIndex) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)) {
                        Text("${minBlue+index}\n${maxRed-index}")
                    }
                }
            }
        }
    }
}
