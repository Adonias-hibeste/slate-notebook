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
import com.example.slate.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindMapScreen(onNoteClick: (String) -> Unit) {
    val nodeStates = remember {
        mutableStateMapOf<String, Offset>().apply {
            MockData.notes.forEach { note ->
                this[note.id] = Offset(note.x, note.y)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SlateBackground)) {
        // Draw connection paths dynamically on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val notes = MockData.notes
            val drawnConnections = mutableSetOf<String>()

            for (noteA in notes) {
                val posA = nodeStates[noteA.id] ?: Offset(noteA.x, noteA.y)
                
                for (noteB in notes) {
                    if (noteA.id == noteB.id) continue

                    val shareTags = noteA.tags.any { noteB.tags.contains(it) }
                    if (shareTags) {
                        val connId1 = "${noteA.id}-${noteB.id}"
                        val connId2 = "${noteB.id}-${noteA.id}"
                        
                        if (connId1 !in drawnConnections && connId2 !in drawnConnections) {
                            drawnConnections.add(connId1)

                            val posB = nodeStates[noteB.id] ?: Offset(noteB.x, noteB.y)
                            
                            val centerA = Offset(posA.x + 220f, posA.y + 110f)
                            val centerB = Offset(posB.x + 220f, posB.y + 110f)

                            val controlPointX = (centerA.x + centerB.x) / 2
                            val controlPointY = ((centerA.y + centerB.y) / 2) - 100f
                            
                            val path = Path().apply {
                                moveTo(centerA.x, centerA.y)
                                quadraticTo(controlPointX, controlPointY, centerB.x, centerB.y)
                            }

                            // Glow effect
                            drawPath(
                                path = path,
                                brush = Brush.linearGradient(listOf(SlateSecondary.copy(alpha = 0.3f), SlatePrimary.copy(alpha = 0.3f))),
                                style = Stroke(width = 16f)
                            )
                            drawPath(
                                path = path,
                                brush = Brush.linearGradient(listOf(SlateSecondary, SlatePrimary)),
                                style = Stroke(width = 4f)
                            )
                        }
                    }
                }
            }
        }

        MockData.notes.forEach { note ->
            val currentPos = nodeStates[note.id] ?: Offset(note.x, note.y)

            Box(
                modifier = Modifier
                    .offset { IntOffset(currentPos.x.roundToInt(), currentPos.y.roundToInt()) }
                    .size(width = 160.dp, height = 80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SlateGlass)
                    .border(
                        width = 1.dp,
                        color = if (note.isAIProcessed) SlatePrimary else SlateGlassBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .pointerInput(note.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val oldPos = nodeStates[note.id] ?: Offset(note.x, note.y)
                            val newX = (oldPos.x + dragAmount.x).coerceIn(20f, 800f)
                            val newY = (oldPos.y + dragAmount.y).coerceIn(20f, 1500f)
                            nodeStates[note.id] = Offset(newX, newY)
                        }
                    }
                    .clickable { onNoteClick(note.id) }
                    .padding(12.dp),
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
                            Icon(Icons.Default.AutoAwesome, null, tint = SlatePrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = note.category.uppercase(),
                            color = when (note.category) {
                                "Work" -> SlateSecondary
                                "Personal" -> SlateAccentPink
                                else -> SlateAccentEmerald
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = note.title,
                        color = SlateTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Elegant Title header
        Column(modifier = Modifier.padding(32.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("AI Neural Graph", style = Typography.displayMedium, color = SlateTextPrimary)
            Text("Visual connection of your notes", color = SlateTextSecondary, fontSize = 16.sp)
        }

        // Info guide toast overlay
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(SlateGlass)
                .border(1.dp, SlateGlassBorder, RoundedCornerShape(32.dp))
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, null, tint = SlatePrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Drag nodes to map, tap to open.",
                color = SlateTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
