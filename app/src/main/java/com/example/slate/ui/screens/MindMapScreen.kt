package com.example.slate.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slate.data.model.MockData
import com.example.slate.data.model.Note
import com.example.slate.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindMapScreen(onNoteClick: (String) -> Unit) {
    // Hold coordinate state locally to support real-time dragging
    val nodeStates = remember {
        mutableStateMapOf<String, Offset>().apply {
            MockData.notes.forEach { note ->
                this[note.id] = Offset(note.x, note.y)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Notes Graph", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Draw connection paths dynamically on Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val notes = MockData.notes
                val drawnConnections = mutableSetOf<String>()

                for (noteA in notes) {
                    val posA = nodeStates[noteA.id] ?: Offset(noteA.x, noteA.y)
                    
                    for (noteB in notes) {
                        if (noteA.id == noteB.id) continue

                        // Check if notes share tags to build connections
                        val shareTags = noteA.tags.any { noteB.tags.contains(it) }
                        if (shareTags) {
                            val connId1 = "${noteA.id}-${noteB.id}"
                            val connId2 = "${noteB.id}-${noteA.id}"
                            
                            if (connId1 !in drawnConnections && connId2 !in drawnConnections) {
                                drawnConnections.add(connId1)

                                val posB = nodeStates[noteB.id] ?: Offset(noteB.x, noteB.y)
                                
                                // Node centers (approximating node width/height as 160dp x 80dp)
                                val centerA = Offset(posA.x + 220f, posA.y + 110f)
                                val centerB = Offset(posB.x + 220f, posB.y + 110f)

                                // Create a smooth curved path
                                val controlPointX = (centerA.x + centerB.x) / 2
                                val controlPointY = ((centerA.y + centerB.y) / 2) - 100f
                                
                                val path = Path().apply {
                                    moveTo(centerA.x, centerA.y)
                                    quadraticTo(controlPointX, controlPointY, centerB.x, centerB.y)
                                }

                                drawPath(
                                    path = path,
                                    brush = Brush.linearGradient(
                                        colors = listOf(SlateSecondary.copy(alpha = 0.6f), SlatePrimary.copy(alpha = 0.6f))
                                    ),
                                    style = Stroke(width = 4f)
                                )
                            }
                        }
                    }
                }
            }

            // Draw Note node components
            MockData.notes.forEach { note ->
                val currentPos = nodeStates[note.id] ?: Offset(note.x, note.y)

                Box(
                    modifier = Modifier
                        .offset { IntOffset(currentPos.x.roundToInt(), currentPos.y.roundToInt()) }
                        .size(width = 160.dp, height = 80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateSurface)
                        .border(
                            width = 1.dp,
                            color = if (note.isAIProcessed) SlateSecondary else SlateBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .pointerInput(note.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val oldPos = nodeStates[note.id] ?: Offset(note.x, note.y)
                                // Restrict node coordinates to within reasonable screen space bounds
                                val newX = (oldPos.x + dragAmount.x).coerceIn(20f, 800f)
                                val newY = (oldPos.y + dragAmount.y).coerceIn(20f, 1500f)
                                nodeStates[note.id] = Offset(newX, newY)
                            }
                        }
                        .clickable { onNoteClick(note.id) }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (note.isAIProcessed) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SlateSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = note.category,
                                color = if (note.category == "Work") SlatePrimary else SlateAccentPink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = note.title,
                            color = SlateTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Info guide toast overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SlateSurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = SlateSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Drag nodes to map, tap to open note.",
                    color = SlateTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
