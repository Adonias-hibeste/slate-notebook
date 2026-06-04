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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slate.data.model.MockData
import com.example.slate.data.model.Note
import com.example.slate.theme.*
import kotlinx.coroutines.delay
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
            note?.tags?.let { addAll(it) } ?: addAll(listOf("notes", "new"))
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(0) } // 0: AI Actions, 1: AI Chat

    // AI Processing states
    var isProcessing by remember { mutableStateOf(false) }
    var aiResult by remember { mutableStateOf<String?>(null) }
    
    // AI Chat states
    var userMessage by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("AI", "Hi, I'm Slate Copilot. Ask me anything about your current note!")
        )
    }
    var isAiTyping by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SlateTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Save action */ onBackClick() }) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = SlateSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = SlateSecondary,
                contentColor = SlateBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Note Title
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Note Title", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SlateTextTertiary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = SlateTextPrimary,
                    unfocusedTextColor = SlateTextPrimary
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Inline tags editor
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(currentTags) { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateSurfaceVariant)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable { currentTags.remove(tag) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "#$tag", color = SlateSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Close, contentDescription = "Delete Tag", tint = SlateTextSecondary, modifier = Modifier.size(10.dp))
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateSurface)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                val newTags = listOf("architecture", "todo", "meeting", "code", "draft")
                                val randomTag = newTags.random()
                                if (randomTag !in currentTags) {
                                    currentTags.add(randomTag)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Add Tag", tint = SlatePrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Tag", color = SlateTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Note editor main content
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Start typing...", fontSize = 16.sp, color = SlateTextTertiary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = SlateTextPrimary,
                    unfocusedTextColor = SlateTextPrimary
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, lineHeight = 24.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Dynamic rich-text formatting toolbar overlay above keyboard
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Simulate format */ }) {
                    Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = SlateTextSecondary)
                }
                IconButton(onClick = { /* Simulate list */ }) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List", tint = SlateTextSecondary)
                }
                IconButton(onClick = {
                    content += "\n- "
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item", tint = SlateTextSecondary)
                }
                IconButton(onClick = {
                    // Quick dictation simulation inside editor
                    content += " [Dictated: Sync with Adonias completed.]"
                }) {
                    Icon(Icons.Default.Mic, contentDescription = "Quick Voice dictation", tint = SlateSecondary)
                }
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Panel", tint = SlatePrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Inline AI Result Card
            if (aiResult != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    border = borderStroke(SlateBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SlatePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Copilot Suggestion", color = SlatePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(aiResult!!, color = SlateTextPrimary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { aiResult = null }) {
                                Text("Dismiss", color = SlateTextSecondary)
                            }
                            Button(
                                onClick = { 
                                    content += "\n\n" + aiResult
                                    aiResult = null 
                                    note?.let {
                                        MockData.notes.find { n -> n.id == it.id }?.let { original ->
                                            // Mock copy update
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary)
                            ) {
                                Text("Insert")
                            }
                        }
                    }
                }
            }
        }
        
        // AI Bottom Sheet Drawer
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = SlateSurface
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    // Drawer Header Tabs
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = SlateSurface,
                        contentColor = SlateTextPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                                color = SlatePrimary
                            )
                        }
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

                    Spacer(modifier = Modifier.height(16.dp))

                    if (activeTab == 0) {
                        // AI ACTIONS TAB
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            if (isProcessing) {
                                CircularProgressIndicator(color = SlatePrimary, modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp))
                                Text("AI Assistant processing notes...", color = SlatePrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                AIOptionRow(icon = Icons.Default.AutoAwesome, title = "Summarize Note") {
                                    scope.launch {
                                        isProcessing = true
                                        delay(1200)
                                        isProcessing = false
                                        showBottomSheet = false
                                        aiResult = "Summary: Key items revolve around product architectures, sync plans, and milestones. Actions involve optimization drafts."
                                    }
                                }
                                AIOptionRow(icon = Icons.Default.FormatListBulleted, title = "Extract Action Items") {
                                     scope.launch {
                                        isProcessing = true
                                        delay(1200)
                                        isProcessing = false
                                        showBottomSheet = false
                                        aiResult = "- Draft final system configurations\n- Setup cloud databases\n- Schedule Adonias review check"
                                    }
                                }
                                AIOptionRow(icon = Icons.Default.Translate, title = "Translate to Spanish") {
                                    scope.launch {
                                        isProcessing = true
                                        delay(1200)
                                        isProcessing = false
                                        showBottomSheet = false
                                        aiResult = "Resumen: El equipo discutió la migración de la base de datos y la capacidad para el próximo trimestre."
                                    }
                                }
                            }
                        }
                    } else {
                        // AI COPILOT CHAT TAB
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Chat messages list
                            val chatScrollState = rememberScrollState()
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .verticalScroll(chatScrollState)
                                    .padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                                        topStart = 12.dp,
                                                        topEnd = 12.dp,
                                                        bottomStart = if (isAi) 0.dp else 12.dp,
                                                        bottomEnd = if (isAi) 12.dp else 0.dp
                                                    )
                                                )
                                                .background(if (isAi) SlateSurfaceVariant else SlatePrimary)
                                                .padding(10.dp)
                                                .widthIn(max = 240.dp)
                                        ) {
                                            Text(
                                                text = msg.text,
                                                color = if (isAi) SlateTextPrimary else SlateBackground,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                                
                                if (isAiTyping) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                        Text("AI is typing...", color = SlateSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                            }

                            // Message input row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextField(
                                    value = userMessage,
                                    onValueChange = { userMessage = it },
                                    placeholder = { Text("Ask about: '$title'", fontSize = 13.sp, color = SlateTextTertiary) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = SlateSurfaceVariant,
                                        unfocusedContainerColor = SlateSurfaceVariant,
                                        focusedIndicatorColor = SlateSecondary,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = SlateTextPrimary,
                                        unfocusedTextColor = SlateTextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
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
                                                delay(1200)
                                                isAiTyping = false
                                                val answer = when {
                                                    query.contains("action", ignoreCase = true) || query.contains("todo", ignoreCase = true) -> 
                                                        "Based on '$title', action items are: 1. Optimize cluster indexing 2. Hold sync with Adonias."
                                                    query.contains("summary", ignoreCase = true) || query.contains("about", ignoreCase = true) ->
                                                        "This note discusses database architectures and team priorities for engineering sync pipelines."
                                                    else -> 
                                                        "That's interesting. We should index this note with tags so that we can link it in the Mind Map dashboard."
                                                }
                                                chatMessages.add(ChatMessage("AI", answer))
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(SlatePrimary)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send message", tint = SlateBackground, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun borderStroke(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

@Composable
fun AIOptionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateSurfaceVariant)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SlatePrimary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = SlateTextPrimary)
    }
    Spacer(modifier = Modifier.height(12.dp))
}

