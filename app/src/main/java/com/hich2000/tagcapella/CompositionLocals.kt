package com.hich2000.tagcapella

import androidx.compose.runtime.compositionLocalOf
import com.hich2000.tagcapella.tags.TagViewModel

val LocalTagViewModel = compositionLocalOf<TagViewModel> { error("TagViewMdoel not provided") }