package com.kdays.android.ui.editor.components.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kdays.android.ui.theme.KDaysTheme

@Composable
fun SellAlertDialog(
    onConfirmation: (String, String, String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var pointValue by rememberSaveable { mutableStateOf("") }
    var textValue by rememberSaveable { mutableStateOf("") }

    var isDropdown by rememberSaveable { mutableStateOf(false) }
    val menuItems =
        rememberSaveable {
            mutableMapOf(
                "1" to "坑币(0~20)",
                "2" to "棒棒糖(0~5)",
                "4" to "萝莉(0~1)"
            )
        }
    var selectedId by rememberSaveable { mutableStateOf("1") }

    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        icon = {
            Icon(Icons.Outlined.Paid, contentDescription = "添加出售内容")
        },
        title = {
            Text(text = "添加出售内容")
        },
        text = {
            Column {
                Row {
                    TextField(
                        modifier = Modifier.weight(1F),
                        value = pointValue,
                        onValueChange = {
                            pointValue = it
                        },
                        label = {
                            Text("积分")
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusRequester.requestFocus()
                            }
                        ),
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically),
                    ) {
                        Button(
                            onClick = { isDropdown = true }
                        ) {
                            menuItems[selectedId]?.let { Text(text = it) }
                        }

                        DropdownMenu(
                            expanded = isDropdown,
                            onDismissRequest = { isDropdown = false }
                        ) {
                            menuItems.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(text = name) },
                                    onClick = {
                                        isDropdown = false
                                        selectedId = id
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = textValue,
                    onValueChange = {
                        textValue = it
                    },
                    label = {
                        Text("文本")
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(pointValue, textValue, selectedId)
                    onDismissRequest()
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("取消")
            }
        }
    )
}

@Preview
@Composable
fun PreviewSellAlertDialog() {
    KDaysTheme {
        SellAlertDialog(
            onConfirmation = { _, _, _ -> },
            onDismissRequest = {}
        )
    }
}