package com.hich2000.tagcapella.tags

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.hich2000.tagcapella.categories.CategoryViewModel
import com.hich2000.tagcapella.music_player.SongCard
import com.hich2000.tagcapella.music_player.SongList
import com.hich2000.tagcapella.songs.SongViewModel
import com.hich2000.tagcapella.utils.TagCapellaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    songViewModel: SongViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel(),
) {
    val showTagDialog = remember { mutableStateOf(false) }
    val clickedTag = remember { mutableStateOf<TagDTO?>(null) }
    val showSongDialog = remember { mutableStateOf(false) }
    val songList by songViewModel.songList.collectAsState()
    val tagList by tagViewModel.tags.collectAsState()

    if (showTagDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showTagDialog.value = false
                clickedTag.value = null
            },
        ) {
            TagForm(
                tag = clickedTag.value
            )
        }
    }

    if (showSongDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                showSongDialog.value = false
                clickedTag.value = null
            },
        ) {
            SongList(
                songList = songList,
                songCard = { song ->
                    val taggedSongs = clickedTag.value?.let { tagViewModel.getTaggedSongs(it) }
                    val isTagged = taggedSongs?.contains(song) ?: false

                    SongCard(
                        song = song,
                        backgroundColor = if (isTagged) {
                            Color.hsl(112f, 0.5f, 0.3f)
                        } else {
                            MaterialTheme.colorScheme.background
                        },
                        onClick = {
                            if (taggedSongs?.contains(song) ?: false) {
                                song.path.let {
                                    tagViewModel.deleteSongTag(clickedTag.value!!, song)
                                }
                            } else {
                                song.path.let {
                                    tagViewModel.addSongTag(clickedTag.value!!, song)
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TagList(
            tagList = tagList,
            tagCard = { tag ->
                val editCallback = {
                    clickedTag.value = tag
                    showTagDialog.value = true
                }
                val songCallback = {
                    clickedTag.value = tag
                    showSongDialog.value = true
                }
                val deleteCallback = {
                    tagViewModel.deleteTag(tag.id)
                }

                TagCard(
                    tag = tag,
                    editCallback = editCallback,
                    songCallback = songCallback,
                    deleteCallback = deleteCallback
                )
            }
        )
    }
}

@Composable
fun TagList(
    tagList: List<TagDTO> = emptyList(),
    tagCard: @Composable (tag: TagDTO) -> Unit,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
) {
    val columnScroll = rememberScrollState()
    val categories by categoryViewModel.categories.collectAsState()
    val scroll = rememberScrollState(0)
    var selectedCategory: Long? by remember { mutableStateOf(null) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current)
                )
                .fillMaxSize()
                .verticalScroll(columnScroll)
        ) {
            if (categories.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scroll)
                ) {
                    TagCapellaButton(
                        onClick = {
                            selectedCategory = null
                        },
                        modifier = Modifier
                            .border(2.dp, MaterialTheme.colorScheme.tertiary, RectangleShape)
                            .padding(0.dp)
                            .width(120.dp),
                        shape = RectangleShape,
                    ) {
                        Text(
                            text = "All",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                        )
                    }

                    categories.forEach { category ->
                        val buttonModifier = Modifier
                            .border(2.dp, MaterialTheme.colorScheme.tertiary, RectangleShape)
                            .padding(0.dp)
                        val finalModifier = if (category.category.length < 20) {
                            buttonModifier.width(120.dp)
                        } else {
                            buttonModifier.wrapContentWidth()
                            buttonModifier.weight(1f)
                        }

                        TagCapellaButton(
                            onClick = {
                                selectedCategory = category.id
                            },
                            modifier = finalModifier,
                            shape = RectangleShape,
                        ) {
                            Text(
                                text = category.category,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                            )
                        }
                    }
                }
            }


            tagList.forEach { tag ->
                if (selectedCategory !== null && tag.categoryId != selectedCategory) {
                    return@forEach
                }
                tagCard(tag)
            }
        }

    }
}

@Composable
fun ExpandableFab(
    buttons: List<@Composable () -> Unit>,
    expanded: Boolean = false,
    onclick: (() -> Unit)
) {
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn(tween(200)) + scaleIn(tween(200)) togetherWith
                        fadeOut(tween(200)) + scaleOut(tween(200))
            },
            label = "FAB Expansion",
        ) { expanded ->
            if (expanded) {
                Column(
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.tertiary)
                        .width(200.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    buttons.forEach { button ->
                        button()
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = onclick,
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .padding(16.dp)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary),
                    shape = RectangleShape
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun TagCard(
    tag: TagDTO,
    editCallback: (() -> Unit)? = null,
    songCallback: (() -> Unit)? = null,
    deleteCallback: (() -> Unit)? = null,
    onClick: (tag: TagDTO) -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    tagViewModel: TagViewModel = hiltViewModel()
) {
    Card(
        modifier = Modifier
            .border(2.dp, MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary)
            .height(50.dp),
        onClick = { onClick(tag) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Label,
                contentDescription = "Label",
                modifier = Modifier.weight(0.15f),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                tag.tag,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (deleteCallback != null) {
                IconButton(
                    onClick = deleteCallback,
                    modifier = Modifier.weight(0.2f)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (editCallback != null) {
                IconButton(
                    onClick = editCallback,
                    modifier = Modifier.weight(0.2f)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (songCallback != null) {
                IconButton(
                    onClick = songCallback,
                    modifier = Modifier.weight(0.2f)
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = "Tag songs",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    "(${tagViewModel.getTaggedSongs(tag).count()})",
                    modifier = Modifier.weight(0.3f),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagForm(
    tag: TagDTO? = null,
    tagViewModel: TagViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var textState by remember { mutableStateOf(if (tag is TagDTO) tag.tag else "") }
    var dropdownState by remember { mutableStateOf(if (tag is TagDTO) tag.categoryId else null) }
    val categories by categoryViewModel.categories.collectAsState()
    var dropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RectangleShape
                )
        ) {

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                val dropdownWidth = this.maxWidth

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryEditable,
                                enabled = true
                            ),
                        label = { Text("category") },
                        onValueChange = {},
                        readOnly = true,
                        value = if (dropdownState == null) {
                            "(no category)"
                        } else {
                            categories.find { category -> category.id == dropdownState }?.category
                                ?: "(no category)"
                        },
                    )

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        shape = RectangleShape,
                        modifier = Modifier
                            .width(dropdownWidth)
                            .border(
                                2.dp, MaterialTheme.colorScheme.tertiary, shape = RectangleShape
                            )
                            .padding(0.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "(no category)",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            onClick = {
                                dropdownState = null
                                dropdownExpanded = false
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(0.dp)
                        )

                        categories.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = it.category,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                onClick = {
                                    dropdownState = it.id
                                    dropdownExpanded = false
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(0.dp)
                            )
                        }
                    }
                }
            }

            TextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text("Tag") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            )

            Box(
                modifier = Modifier.padding(0.dp)
            ) {
                if (tag === null) {
                    TagCapellaButton(
                        onClick = {
                            tagViewModel.insertTag(textState, dropdownState)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(36.dp)
                            .border(2.dp, MaterialTheme.colorScheme.tertiary)
                            .padding(0.dp)
                    ) {
                        Text("add")
                    }
                } else {
                    TagCapellaButton(
                        onClick = {
                            tagViewModel.updateTag(
                                id = tag.id,
                                tag = textState,
                                category = dropdownState
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(36.dp)
                            .border(2.dp, MaterialTheme.colorScheme.tertiary)
                            .padding(0.dp)
                    ) {
                        Text("update")
                    }
                }
            }
        }
    }
}


@Composable
fun TagDialog(
    onButtonPress: () -> Unit = {},
    tagCardComposable: @Composable (tag: TagDTO) -> Unit,
    tagViewModel: TagViewModel = hiltViewModel(),
) {
    val tags by tagViewModel.tags.collectAsState()

    Dialog(
        onDismissRequest = {
            tagViewModel.closeDialog()
        },
    ) {
        Card(
            shape = CutCornerShape(0.dp),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxSize()
                .border(2.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TagList(
                    tagList = tags,
                    tagCard = tagCardComposable,
                )

                TagCapellaButton(
                    onClick = {
                        onButtonPress()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(36.dp)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}