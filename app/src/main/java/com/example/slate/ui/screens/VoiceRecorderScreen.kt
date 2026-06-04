package com.example.slate.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slate.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRecorderScreen(onNoteClick: (String) -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var transcriptionText by remember { mutableStateOf("") }
    var savedNoteId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val transcriptionScript = listOf(
        "Connecting to Voice Engine...",
        "The new database cluster is performing within expected benchmarks...",
        "but we need to optimize the client-side indexing rules to achieve low latency.",
        "Let's schedule a review meeting with Adonias for tomorrow's sync.",
        "Saved voice memo as Note #9: 'Voice Note Database Optimization'."
    )

    // Pulsing animation for mic button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Animate wave height offsets
    var waveTick by remember { mutableStateOf(0) }
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(100)
                waveTick++
            }
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            transcriptionText = ""
            savedNoteId = null
            for (sentence in transcriptionScript) {
                delay(2200)
                transcriptionText += (if (transcriptionText.isEmpty()) "" else "\n") + sentence
            }
            isRecording = false
            savedNoteId = "5" // Links to Client Sync note or similar
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice Memo AI", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Waveform visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SlateSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isRecording) {
                    AudioWaveform(tick = waveTick, color = SlateSecondary)
                } else {
                    Text(
                        text = "Tap the microphone to start recording dictation",
                        color = SlateTextTertiary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Real-time transcription box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SlatePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live AI Transcription",
                            color = SlatePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (transcriptionText.isEmpty()) "Transcript will stream here..." else transcriptionText,
                        color = if (transcriptionText.isEmpty()) SlateTextTertiary else SlateTextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (savedNoteId != null) {
                        Button(
                            onClick = { onNoteClick(savedNoteId!!) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateAccentEmerald)
                        ) {
                            Text("Open Saved Note", color = SlateBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Microphone action button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .border(
                            width = 4.dp * pulseScale,
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if (isRecording) SlateSecondary else SlatePrimary,
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(if (isRecording) SlateSecondary else SlateSurface)
                        .clickable { isRecording = !isRecording },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Record microphone button",
                        tint = if (isRecording) SlateBackground else SlateSecondary,
                        modifier = Modifier.size(42.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isRecording) "Recording... Tap to Pause" else "Tap Mic to Dictate",
                    color = if (isRecording) SlateSecondary else SlateTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun AudioWaveform(tick: Int, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val columnWidth = 15f
        val gap = 10f
        val totalCols = (width / (columnWidth + gap)).toInt()

        for (i in 0 until totalCols) {
            // Compute a simulated height based on sine waves and tick
            val angle = i.toFloat() * 0.3f + tick * 0.4f
            val baseHeight = height * 0.4f
            val simulatedAmplitude = baseHeight * (sin(angle) * 0.6f + 0.4f)
            
            // Random jitter to make it look realistic
            val jitter = if (i % 2 == 0) 1.2f else 0.7f
            val barHeight = (simulatedAmplitude * jitter).coerceIn(10f, height * 0.9f)
            
            val x = i * (columnWidth + gap)
            val y = centerY - barHeight / 2

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(columnWidth, barHeight),
                cornerRadius = CornerRadius(5f, 5f)
            )
        }
    }
}
