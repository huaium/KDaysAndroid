package com.kdays.android.ext

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

object ComponentExts {
    @Composable
    fun AnimatedFloatingActionButton(
        modifier: Modifier = Modifier,
        padding: Dp,
        imageVector: ImageVector,
        msg: String,
        onClick: () -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        FloatingActionButton(
            onClick = {
                if (!expanded) {
                    expanded = true
                } else {
                    onClick()
                }
            },
            modifier = modifier.padding(padding)
        ) {
            Row(
                modifier = Modifier.padding(start = padding, end = padding)
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = msg,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                AnimatedVisibility(
                    visible = expanded,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(
                        modifier = Modifier.padding(start = padding),
                        text = msg
                    )
                }
            }
        }
    }
}