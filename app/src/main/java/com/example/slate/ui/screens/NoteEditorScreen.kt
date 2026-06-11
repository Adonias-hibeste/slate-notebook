package com.example.slate.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slate.data.model.MockData
import com.example.slate.theme.*
import com.example.slate.data.network.CopilotService
import kotlinx.coroutines.launch

data class ChatMessage(val sender: String, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(noteId: String, onBackClick: () -> Unit) {
    val note = MockData.notes.find { it.id == noteId }
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.previewText ?: "") }
    
    val currentTags = remember { 
        mutableStateListOf<String>().apply {
            note?.tags?.let { addAll(it) } ?: addAll(listOf("notes", "draft"))
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val copilotService = remember { CopilotService() }
    
    var showBottomSheet by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(0) }

    var isProcessing by remember { mutableStateOf(false) }
    var aiResult by remember { mutableStateOf<String?>(null) }
    
    var userMessage by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("AI", "Hi Adonias, I'm your Copilot. How can I help refine this note?")
        )
    }
    var isAiTyping by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(SlateBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Transparent Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SlateGlass)
                        .border(1.dp, SlateGlassBorder, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SlateTextPrimary)
                }
                IconButton(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SlatePrimary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Big Typography Title
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Untitled", style = Typography.displayMedium, color = SlateTextTertiary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = SlateTextPrimary,
                    unfocusedTextColor = SlateTextPrimary
                ),
                textStyle = Typography.displayMedium,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sleek Pills for Tags
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(currentTags) { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateSurfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable { currentTags.remove(tag) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "#$tag", color = SlateTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = SlateTextTertiary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Transparent)
                            .border(1.dp, SlateGlassBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                val newTags = listOf("architecture", "meeting", "code")
                                val randomTag = newTags.random()
                                if (randomTag !in currentTags) currentTags.add(randomTag)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = SlatePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Tag", color = SlateTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Result Card (Glass)
            if (aiResult != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateGlass),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateGlassBorder)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SlatePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Copilot Suggestion", color = SlatePrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(aiResult!!, color = SlateTextPrimary, fontSize = 15.sp, lineHeight = 22.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { aiResult = null }) {
                                Text("Dismiss", color = SlateTextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { 
                                    content += "\n\n" + aiResult
                                    aiResult = null 
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Insert", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            
            // Content Editor
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Start typing your thoughts...", style = Typography.bodyLarge, color = SlateTextTertiary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = SlateTextPrimary,
                    unfocusedTextColor = SlateTextPrimary
                ),
                textStyle = Typography.bodyLarge.copy(lineHeight = 28.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 400.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Floating Dynamic Island Toolbar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(SlateGlass)
                .border(1.dp, SlateGlassBorder, RoundedCornerShape(32.dp))
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = SlateTextSecondary)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = "List", tint = SlateTextSecondary)
                }
                IconButton(onClick = { content += " [Dictated] " }) {
                    Icon(Icons.Default.Mic, contentDescription = "Dictate", tint = SlateTextPrimary)
                }
                IconButton(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SlatePrimary)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color.Black)
                }
            }
        }
        
        // AI Bottom Sheet (Sleek Dark Mode)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = SlateSurface,
                dragHandle = { BottomSheetDefaults.DragHandle(color = SlateTextSecondary) }
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = SlateSurface,
                        contentColor = SlateTextPrimary,
                        indicator = { tabPositions ->
                            if (activeTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                                    color = SlatePrimary
                                )
                            }
                        },
                        divider = { HorizontalDivider(color = SlateBorder) }
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = { Text("AI Tools", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("Copilot Chat", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (activeTab == 0) {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            if (isProcessing) {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = SlatePrimary)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Copilot is analyzing...", color = SlateTextSecondary)
                                    }
                                }
                            } else {
                                AIOptionRow(Icons.Default.AutoAwesome, "Summarize Note") {
                                    scope.launch {
                                        isProcessing = true
                                        val result = copilotService.summarizeNote(content)
                                        isProcessing = false
                                        showBottomSheet = false
                                        aiResult = result
                                    }
                                }
                                AIOptionRow(Icons.AutoMirrored.Filled.FormatListBulleted, "Extract Action Items") {
                                     scope.launch {
                                        isProcessing = true
                                        val result = copilotService.extractActionItems(content)
                                        isProcessing = false
                                        showBottomSheet = false
                                        aiResult = result
                                    }
                                }
                                AIOptionRow(Icons.Default.Translate, "Translate to Spanish") {
                                    scope.launch {
                                        isProcessing = true
                                        val result = copilotService.translateToSpanish(content)
                                        isProcessing = false
                                        showBottomSheet = false
                                        aiResult = result
                                    }
                                }
                            }
                        }
                    } else {
                        // AI Chat
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                chatMessages.forEach { msg ->
                                    val isAi = msg.sender == "AI"
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 20.dp,
                                                        topEnd = 20.dp,
                                                        bottomStart = if (isAi) 4.dp else 20.dp,
                                                        bottomEnd = if (isAi) 20.dp else 4.dp
                                                    )
                                                )
                                                .background(if (isAi) SlateSurfaceVariant else SlatePrimary)
                                                .padding(16.dp)
                                                .widthIn(max = 280.dp)
                                        ) {
                                            Text(
                                                text = msg.text,
                                                color = if (isAi) SlateTextPrimary else Color.Black,
                                                fontSize = 15.sp,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    }
                                }
                                if (isAiTyping) {
                                    Text("Copilot is typing...", color = SlateTextTertiary, fontSize = 13.sp, modifier = Modifier.padding(start = 16.dp))
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TextField(
                                    value = userMessage,
                                    onValueChange = { userMessage = it },
                                    placeholder = { Text("Ask Copilot...", color = SlateTextTertiary) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = SlateSurfaceVariant,
                                        unfocusedContainerColor = SlateSurfaceVariant,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = SlateTextPrimary,
                                        unfocusedTextColor = SlateTextPrimary
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        if (userMessage.isNotBlank()) {
                                            val query = userMessage
                                            chatMessages.add(ChatMessage("User", query))
                                            userMessage = ""
                                            isAiTyping = true
                                            scope.launch {
                                                val answer = copilotService.copilotChat(title, content, query)
                                                isAiTyping = false
                                                chatMessages.add(ChatMessage("AI", answer))
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(SlatePrimary)
                                ) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = "Send", tint = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AIOptionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SlateSurfaceVariant)
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SlatePrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(20.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateTextPrimary)
    }
    Spacer(modifier = Modifier.height(16.dp))
}
