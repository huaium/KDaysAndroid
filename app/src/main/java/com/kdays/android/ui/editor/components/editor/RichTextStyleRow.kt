package com.kdays.android.ui.editor.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.FormatAlignRight
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.FormatAlignCenter
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kdays.android.ext.RichTextStateExts.addEmotion
import com.kdays.android.ext.RichTextStateExts.addHide
import com.kdays.android.ext.RichTextStateExts.addSell
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.logic.Repository
import com.kdays.android.logic.model.editor.emotion.Category
import com.kdays.android.ui.editor.components.dialog.EmotionAlertDialog
import com.kdays.android.ui.editor.components.dialog.HideAlertDialog
import com.kdays.android.ui.editor.components.dialog.LinkAlertDialog
import com.kdays.android.ui.editor.components.dialog.SellAlertDialog
import com.kdays.android.ui.theme.KDaysTheme
import com.kdays.android.utils.NetworkUtils.handleNetwork
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun RichTextStyleRow(
    modifier: Modifier = Modifier,
    state: RichTextState,
    onImageClick: () -> Unit
) {
    var showEmotionDialog by rememberSaveable { mutableStateOf(false) }
    var showLinkDialog by rememberSaveable { mutableStateOf(false) }
    var showSellDialog by rememberSaveable { mutableStateOf(false) }
    var showHideDialog by rememberSaveable { mutableStateOf(false) }

    val emotionData = remember { mutableListOf<Category>() }

    LaunchedEffect(Unit) {
        try {
            val result = withContext(Dispatchers.IO) {
                Repository.getEmotion()
            }

            val data = result.getOrNull()
            if (data != null) {
                val baseDir = data.baseDir
                data.categories.forEach { category ->
                    val name = category.name
                    val transformedMap = category.items.mapValues { (_, path) ->
                        return@mapValues baseDir + path
                    }
                    emotionData.add(Category(name, transformedMap))
                }
            } else {
                handleNetwork {
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
        } catch (e: Exception) {
            handleNetwork {
                e.printStackTrace()
            }
        }
    }

    Box(modifier = modifier) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item {
                RichTextStyleButton(
                    onClick = {
                        state.addParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Left,
                            )
                        )
                    },
                    isSelected = state.currentParagraphStyle.textAlign == TextAlign.Left,
                    icon = Icons.AutoMirrored.Outlined.FormatAlignLeft
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.addParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Center
                            )
                        )
                    },
                    isSelected = state.currentParagraphStyle.textAlign == TextAlign.Center,
                    icon = Icons.Outlined.FormatAlignCenter
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.addParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Right
                            )
                        )
                    },
                    isSelected = state.currentParagraphStyle.textAlign == TextAlign.Right,
                    icon = Icons.AutoMirrored.Outlined.FormatAlignRight
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        if (emotionData.isNotEmpty()) {
                            showEmotionDialog = true
                        } else {
                            "未能获取表情列表".showToast()
                        }
                    },
                    icon = Icons.Outlined.EmojiEmotions
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.toggleSpanStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                    icon = Icons.Outlined.FormatBold
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.toggleSpanStyle(
                            SpanStyle(
                                fontStyle = FontStyle.Italic
                            )
                        )
                    },
                    isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                    icon = Icons.Outlined.FormatItalic
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.toggleSpanStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    },
                    isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
                    icon = Icons.Outlined.FormatUnderlined
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.toggleSpanStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    },
                    isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
                    icon = Icons.Outlined.FormatStrikethrough
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.toggleSpanStyle(
                            SpanStyle(
                                color = Color.Red
                            )
                        )
                    },
                    isSelected = state.currentSpanStyle.color == Color.Red,
                    icon = Icons.Filled.Circle,
                    tint = Color.Red
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.toggleSpanStyle(
                            SpanStyle(
                                background = Color.Yellow
                            )
                        )
                    },
                    isSelected = state.currentSpanStyle.background == Color.Yellow,
                    icon = Icons.Outlined.Circle,
                    tint = Color.Yellow
                )
            }

            item {
                Box(
                    Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color(0xFF393B3D))
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.toggleUnorderedList()
                    },
                    isSelected = state.isUnorderedList,
                    icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                )
            }

            item {
                RichTextStyleButton(
                    onClick = {
                        state.toggleOrderedList()
                    },
                    isSelected = state.isOrderedList,
                    icon = Icons.Outlined.FormatListNumbered,
                )
            }

            item {
                Box(
                    Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color(0xFF393B3D))
                )
            }

            item {
                RichTextStyleButton(
                    onClick = { showLinkDialog = true },
                    icon = Icons.Outlined.Link
                )
            }

            item {
                RichTextStyleButton(
                    onClick = onImageClick,
                    icon = Icons.Outlined.Image
                )
            }

            item {
                RichTextStyleButton(
                    onClick = { showSellDialog = true },
                    icon = Icons.Outlined.Paid
                )
            }

            item {
                RichTextStyleButton(
                    onClick = { showHideDialog = true },
                    icon = Icons.Outlined.VisibilityOff
                )
            }
        }

        if (showEmotionDialog) {
            EmotionAlertDialog(
                emotionData = emotionData,
                onClick = { id ->
                    state.addEmotion(id)
                },
                onDismissRequest = {
                    showEmotionDialog = false
                }
            )
        }

        if (showLinkDialog) {
            LinkAlertDialog(
                onConfirmation = { link, text ->
                    state.addLink(text, link)
                },
                onDismissRequest = {
                    showLinkDialog = false
                }
            )
        }

        if (showSellDialog) {
            SellAlertDialog(
                onConfirmation = { point, text, id ->
                    state.addSell(point, text, id)
                },
                onDismissRequest = {
                    showSellDialog = false
                }
            )
        }

        if (showHideDialog) {
            HideAlertDialog(
                onConfirmation = { point, text, id ->
                    state.addHide(point, text, id)
                },
                onDismissRequest = {
                    showHideDialog = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRichTextStyleRow() {
    KDaysTheme {
        RichTextStyleRow(
            state = rememberRichTextState(),
            onImageClick = {}
        )
    }
}