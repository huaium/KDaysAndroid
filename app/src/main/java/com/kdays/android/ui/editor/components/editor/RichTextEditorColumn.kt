package com.kdays.android.ui.editor.components.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kdays.android.ui.theme.KDaysTheme
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState

@Composable
fun RichTextEditorColumn(
    modifier: Modifier = Modifier,
    state: RichTextState,
    onClickSend: () -> Unit,
    onImageClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RichTextEditorBox(
            modifier = Modifier
                .weight(1f),
            state = state,
            onClickSend = onClickSend
        )

        RichTextStyleRow(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            onImageClick = onImageClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRichTextEditorColumn() {
    KDaysTheme {
        RichTextEditorColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            state = rememberRichTextState(),
            onClickSend = {},
            onImageClick = {},
        )
    }
}