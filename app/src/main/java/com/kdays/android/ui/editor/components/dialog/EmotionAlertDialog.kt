package com.kdays.android.ui.editor.components.dialog

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.kdays.android.logic.model.editor.emotion.Category
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmotionAlertDialog(
    emotionData: List<Category>,
    onClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { emotionData.size }, initialPage = 0)
    val scope = rememberCoroutineScope()

    AlertDialog(
        icon = {
            Icon(Icons.Outlined.EmojiEmotions, contentDescription = "添加表情")
        },
        title = {
            Text(text = "添加表情")
        },
        text = {
            Column {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        )
                    }
                ) {
                    emotionData.onEachIndexed { index, (name, _) ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            text = {
                                Text(text = name)
                            },
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        index,
                                        0f,
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                }
                            })
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxHeight(),
                    beyondBoundsPageCount = 2,
                    verticalAlignment = Alignment.Top
                ) { page ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        items(
                            items = emotionData[page].items.toList(),
                            key = { (id, _) -> id }
                        ) { (id, url) ->
                            AsyncImage(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clickable {
                                        onClick(id)
                                        onDismissRequest()
                                    },
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(url)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = null,
                                alignment = Alignment.Center,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("返回")
            }
        },
    )
}