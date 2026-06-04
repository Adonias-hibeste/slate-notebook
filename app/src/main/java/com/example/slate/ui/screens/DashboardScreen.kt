package com.example.slate.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slate.data.model.MockData
import com.example.slate.data.model.Note
import com.example.slate.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(onNoteClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var fabExpanded by remember { mutableStateOf(false) }

    val categories = listOf("All", "Work", "Personal", "Ideas")

    // Filter notes based on search query and category
    val filteredNotes = MockData.notes.filter { note ->
        val matchesCategory = selectedCategory == "All" || note.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) || 
                            note.previewText.contains(searchQuery, ignoreCase = true) ||
                            note.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Glassmorphic Welcome Panel
            WelcomePanel()

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            DashboardSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Folder Cards (Carousel)
            Text(
                text = "Folders",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = SlateTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            FolderCarousel(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Notes Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Notes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SlateTextPrimary
                )
                Text(
                    text = "${filteredNotes.size} found",
                    color = SlateTextSecondary,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Notes Staggered Grid
            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No notes found matching current filters.",
                        color = SlateTextTertiary,
                        fontSize = 14.sp
                    )
                }
            } else {
                NotesStaggeredGrid(
                    notes = filteredNotes,
                    onNoteClick = onNoteClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Expanded Floating Action Menu Button Overlay
        FloatingActionHub(
            expanded = fabExpanded,
            onExpandChange = { fabExpanded = it },
            onNoteClick = onNoteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun WelcomePanel() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome Back,",
                        color = SlateTextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Adonias",
                        color = SlateTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SlatePrimary, SlateSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SlateBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = SlateBorder)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickStatItem(label = "Notes", count = "8", color = SlatePrimary)
                QuickStatItem(label = "Voice Memos", count = "4", color = SlateSecondary)
                QuickStatItem(label = "AI Links", count = "12", color = SlateAccentPink)
            }
        }
    }
}

@Composable
fun QuickStatItem(label: String, count: String, color: Color) {
    Column {
        Text(text = count, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = SlateTextTertiary, fontSize = 11.sp)
    }
}

@Composable
fun DashboardSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search title, tags, content...", color = SlateTextTertiary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SlateSecondary) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search", tint = SlateTextSecondary)
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SlateSurface,
            unfocusedContainerColor = SlateSurface,
            focusedIndicatorColor = SlateSecondary,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = SlateTextPrimary,
            unfocusedTextColor = SlateTextPrimary
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
    )
}

@Composable
fun FolderCarousel(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val notesCount = if (category == "All") {
                MockData.notes.size
            } else {
                MockData.notes.count { it.category.equals(category, ignoreCase = true) }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SlateSurfaceVariant else SlateSurface
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .width(110.dp)
                    .height(90.dp)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) SlatePrimary else SlateBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onCategorySelect(category) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) SlatePrimary.copy(alpha = 0.2f) else SlateBorder
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (category) {
                                "Work" -> Icons.Default.Work
                                "Personal" -> Icons.Default.Person
                                "Ideas" -> Icons.Default.Lightbulb
                                else -> Icons.Default.FolderOpen
                            },
                            contentDescription = null,
                            tint = if (isSelected) SlatePrimary else SlateTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = category,
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "$notesCount notes",
                            color = SlateTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesStaggeredGrid(
    notes: List<Note>,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp,
        modifier = modifier.fillMaxSize()
    ) {
        items(notes) { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNoteClick(note.id) }
                    .border(1.dp, SlateBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    // Category & AI Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (note.category) {
                                        "Work" -> SlatePrimary.copy(alpha = 0.15f)
                                        "Personal" -> SlateAccentPink.copy(alpha = 0.15f)
                                        else -> SlateAccentEmerald.copy(alpha = 0.15f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = note.category.uppercase(),
                                color = when (note.category) {
                                    "Work" -> SlatePrimary
                                    "Personal" -> SlateAccentPink
                                    else -> SlateAccentEmerald
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        if (note.isAIProcessed) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Enhanced",
                                tint = SlateSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    Text(
                        text = note.title,
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Snippet preview
                    Text(
                        text = note.previewText,
                        color = SlateTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hashtags flow
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        maxItemsInEachRow = 2
                    ) {
                        note.tags.forEach { tag ->
                            Text(
                                text = "#$tag",
                                color = SlateSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Timestamp
                    Text(
                        text = note.timestamp,
                        color = SlateTextTertiary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingActionHub(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Expandable FAB Options
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Option: Voice dictation (simulate navigation)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Voice Note",
                        color = SlateTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    FloatingActionButton(
                        onClick = {
                            onExpandChange(false)
                            // We trigger navigation implicitly by simulating note creation
                            onNoteClick("new")
                        },
                        containerColor = SlateSecondary,
                        contentColor = SlateBackground,
                        modifier = Modifier.size(45.dp),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Dictate", modifier = Modifier.size(20.dp))
                    }
                }

                // Option: Text note
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Text Note",
                        color = SlateTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    FloatingActionButton(
                        onClick = {
                            onExpandChange(false)
                            onNoteClick("new")
                        },
                        containerColor = SlatePrimary,
                        contentColor = Color.White,
                        modifier = Modifier.size(45.dp),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "New Text Note", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // Primary trigger FAB button
        FloatingActionButton(
            onClick = { onExpandChange(!expanded) },
            containerColor = if (expanded) SlateAccentPink else SlatePrimary,
            contentColor = if (expanded) SlateBackground else Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Trigger expand options menu"
            )
        }
    }
}
