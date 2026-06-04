package com.example.slate.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slate.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen() {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Analytics Dashboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
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
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Notes",
                    value = "8",
                    icon = Icons.Default.Notes,
                    accentColor = SlatePrimary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "AI Tokens",
                    value = "24,520",
                    icon = Icons.Default.AutoAwesome,
                    accentColor = SlateSecondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Voice Notes",
                    value = "3.2 hrs",
                    icon = Icons.Default.KeyboardVoice,
                    accentColor = SlateAccentPink
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Words",
                    value = "12,450",
                    icon = Icons.Default.Title,
                    accentColor = SlateAccentEmerald
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Donut Chart - Note Distribution
            Text(
                "Category Distribution",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DonutChart(
                        modifier = Modifier.size(140.dp),
                        proportions = listOf(0.5f, 0.25f, 0.25f),
                        colors = listOf(SlatePrimary, SlateSecondary, SlateAccentPink)
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ChartLegendItem("Work (50%)", SlatePrimary)
                        ChartLegendItem("Personal (25%)", SlateSecondary)
                        ChartLegendItem("Ideas (25%)", SlateAccentPink)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bar Chart - Weekly Activity
            Text(
                "Weekly Productivity",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    BarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        dataPoints = listOf(3, 5, 2, 7, 4, 6, 8),
                        labels = listOf("M", "T", "W", "T", "F", "S", "S"),
                        color = SlateSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = SlateTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                color = SlateTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ChartLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = SlateTextSecondary, fontSize = 14.sp)
    }
}

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    proportions: List<Float>,
    colors: List<Color>
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 35f
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        var startAngle = -90f

        proportions.forEachIndexed { index, prop ->
            val sweepAngle = prop * 360f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    dataPoints: List<Int>,
    labels: List<String>,
    color: Color
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val maxVal = dataPoints.maxOrNull() ?: 1
        val barCount = dataPoints.size
        val spacing = 30f
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = (width - totalSpacing) / barCount

        val textPaint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.GRAY
            this.textSize = 30f
            this.textAlign = android.graphics.Paint.Align.CENTER
        }

        dataPoints.forEachIndexed { index, value ->
            val barHeight = (value.toFloat() / maxVal.toFloat()) * (height - 60f)
            val left = index * (barWidth + spacing)
            val top = height - 50f - barHeight
            val right = left + barWidth
            val bottom = height - 50f

            // Draw shadow/glow behind bars
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(color.copy(alpha = 0.4f), Color.Transparent)
                ),
                topLeft = Offset(left, top - 10f),
                size = Size(barWidth, barHeight + 10f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )

            // Draw main bar
            drawRoundRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )

            // Draw label
            drawContext.canvas.nativeCanvas.drawText(
                labels[index],
                left + barWidth / 2,
                height - 10f,
                textPaint
            )
        }
    }
}
