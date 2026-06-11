package com.example.slate.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

    val filteredNotes = MockData.notes.filter { note ->
        val matchesCategory = selectedCategory == "All" || note.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) || 
                            note.previewText.contains(searchQuery, ignoreCase = true) ||
                            note.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    Box(modifier = Modifier.fillMaxSize().background(SlateBackground)) {
        // Subtle background glow meshes
        Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = 50.dp, y = (-50).dp).size(250.dp).blur(80.dp).background(SlateGlowAmber, CircleShape))
        Box(modifier = Modifier.align(Alignment.CenterStart).offset(x = (-80).dp, y = 200.dp).size(300.dp).blur(100.dp).background(SlateGlowCyan, CircleShape))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Premium Hero Header
            HeroHeader()

            Spacer(modifier = Modifier.height(24.dp))

            // Glassmorphism Search Bar
            DashboardSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

            Spacer(modifier = Modifier.height(28.dp))

            // Sleek Toggles
            Text(
                text = "Folders",
                style = Typography.titleLarge,
                color = SlateTextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            FolderCarousel(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Notes Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Recent",
                    style = Typography.titleLarge,
                    color = SlateTextPrimary
                )
                Text(
                    text = "${filteredNotes.size} found",
                    color = SlateTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notes found.", color = SlateTextTertiary, fontSize = 16.sp)
                }
            } else {
                NotesStaggeredGrid(
                    notes = filteredNotes,
                    onNoteClick = onNoteClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Floating Action Menu Overlay
        FloatingActionHub(
            expanded = fabExpanded,
            onExpandChange = { fabExpanded = it },
            onNoteClick = onNoteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 24.dp)
        )
    }
}

@Composable
fun HeroHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good evening,",
                color = SlateTextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Adonias",
                style = Typography.displayLarge,
                color = SlateTextPrimary
            )
        }
        
        // Premium Profile Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(SlatePrimary, SlateAccentPink)))
                .border(2.dp, SlateGlassBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun DashboardSearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search your mind...", color = SlateTextTertiary, fontSize = 16.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SlateTextSecondary) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = SlateTextSecondary)
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SlateGlass,
            unfocusedContainerColor = SlateGlass,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = SlateTextPrimary,
            unfocusedTextColor = SlateTextPrimary
        ),
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, SlateGlassBorder, RoundedCornerShape(24.dp))
    )
}

@Composable
fun FolderCarousel(categories: List<String>, selectedCategory: String, onCategorySelect: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val animatedColor by animateColorAsState(targetValue = if (isSelected) SlatePrimary else SlateSurfaceVariant, label = "")
            val animatedTextColor by animateColorAsState(targetValue = if (isSelected) Color.Black else SlateTextPrimary, label = "")
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(animatedColor)
                    .clickable { onCategorySelect(category) }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = category,
                    color = animatedTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun NotesStaggeredGrid(notes: List<Note>, onNoteClick: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(notes) { note ->
            NoteCard(note = note, onClick = { onNoteClick(note.id) })
        }
    }
}

@Composable
fun NoteCard(note: Note, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .border(1.dp, SlateGlassBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateGlass),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (note.category) {
                                "Work" -> SlateSecondary.copy(alpha = 0.2f)
                                "Personal" -> SlateAccentPink.copy(alpha = 0.2f)
                                else -> SlateAccentEmerald.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = note.category.uppercase(),
                        color = when (note.category) {
                            "Work" -> SlateSecondary
                            "Personal" -> SlateAccentPink
                            else -> SlateAccentEmerald
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
                
                if (note.isAIProcessed) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Enhanced",
                        tint = SlatePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = note.title,
                color = SlateTextPrimary,
                style = Typography.titleLarge.copy(fontSize = 18.sp, lineHeight = 22.sp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.previewText,
                color = SlateTextSecondary,
                fontSize = 13.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = note.timestamp,
                color = SlateTextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FloatingActionHub(expanded: Boolean, onExpandChange: (Boolean) -> Unit, onNoteClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(300)) + slideInVertically(spring(dampingRatio = 0.6f, stiffness = 400f), initialOffsetY = { it / 2 }),
            exit = fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Voice Note", color = SlateTextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.background(SlateSurface, RoundedCornerShape(8.dp)).padding(8.dp))
                    FloatingActionButton(onClick = { onExpandChange(false); onNoteClick("new") }, containerColor = SlateSecondary, shape = CircleShape) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Text Note", color = SlateTextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.background(SlateSurface, RoundedCornerShape(8.dp)).padding(8.dp))
                    FloatingActionButton(onClick = { onExpandChange(false); onNoteClick("new") }, containerColor = SlatePrimary, shape = CircleShape) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black)
                    }
                }
            }
        }

        val rotation by animateFloatAsState(if (expanded) 45f else 0f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f), label = "")
        val scale by animateFloatAsState(if (expanded) 0.9f else 1f, label = "")
        
        FloatingActionButton(
            onClick = { onExpandChange(!expanded) },
            containerColor = if (expanded) SlateSurface else SlatePrimary,
            contentColor = if (expanded) SlateTextPrimary else Color.Black,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.size(64.dp).scale(scale).border(1.dp, SlateGlassBorder, RoundedCornerShape(20.dp))
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(28.dp).graphicsLayer(rotationZ = rotation))
        }
    }
}
