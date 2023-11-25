package com.example.androidapp.screens.Items.MessageItem

import androidx.compose.foundation.lazy.LazyListState

fun isItemVisible(lazyListState: LazyListState, itemIndex: Int): Boolean {
    val layoutInfo = lazyListState.layoutInfo
    val startIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
    val endIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

    return itemIndex in startIndex..endIndex
}