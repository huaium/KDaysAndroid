package com.kdays.android.ui.editor.components.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kdays.android.R
import com.kdays.android.ext.ComponentExts.AnimatedFloatingActionButton
import com.kdays.android.ui.theme.KDaysTheme
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RichTextEditorBox(
    modifier: Modifier = Modifier,
    state: RichTextState,
    onClickSend: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        RichTextEditor(
            state = state,
            modifier = Modifier
                .fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            colors = RichTextEditorDefaults.richTextEditorColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )

        AnimatedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            padding = 16.dp,
            imageVector = Icons.AutoMirrored.Filled.Send,
            msg = stringResource(id = R.string.pub),
            onClick = onClickSend
        )
    }
}

@Preview
@Composable
fun PreviewRichTextEditorBox() {
    KDaysTheme {
        RichTextEditorBox(
            state = rememberRichTextState(),
            onClickSend = {}
        )
    }
}