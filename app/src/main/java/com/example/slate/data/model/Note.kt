package com.example.slate.data.model

data class Note(
    val id: String,
    val title: String,
    val previewText: String,
    val timestamp: String,
    val category: String,
    val isAIProcessed: Boolean = false,
    val tags: List<String> = emptyList(),
    val x: Float = 0f,
    val y: Float = 0f,
    val audioDuration: String? = null,
    val transcript: String? = null
)

object MockData {
    val notes = listOf(
        Note(
            id = "1",
            title = "Engineering Sync Q3",
            previewText = "Discussed the migration to the new microservices architecture and team capacity for Q4 planning.",
            timestamp = "10:30 AM",
            category = "Work",
            isAIProcessed = false,
            tags = listOf("architecture", "cloud", "work"),
            x = 220f,
            y = 250f
        ),
        Note(
            id = "2",
            title = "Product Roadmap 2027",
            previewText = "Focus areas: AI-driven features, enhanced accessibility, and deeper integrations.",
            timestamp = "Yesterday",
            category = "Work",
            isAIProcessed = true,
            tags = listOf("strategy", "ai", "work"),
            x = 520f,
            y = 180f
        ),
        Note(
            id = "3",
            title = "Groceries & Meal Prep",
            previewText = "Avocados, Chicken breast, Brown rice, Spinach, Almond milk, Coffee beans.",
            timestamp = "Yesterday",
            category = "Personal",
            isAIProcessed = false,
            tags = listOf("health", "personal"),
            x = 180f,
            y = 680f
        ),
        Note(
            id = "4",
            title = "Blog Post Ideas",
            previewText = "1. The Future of Kotlin 2. Jetpack Compose Best Practices 3. AI in Mobile Development.",
            timestamp = "Mon",
            category = "Ideas",
            isAIProcessed = true,
            tags = listOf("ideas", "creative", "kotlin"),
            x = 750f,
            y = 300f
        ),
        Note(
            id = "5",
            title = "Client Call: Zenith Corp",
            previewText = "They are looking for a complete overhaul of their legacy system with a focus on real-time sync.",
            timestamp = "Last Week",
            category = "Work",
            isAIProcessed = false,
            tags = listOf("client", "consulting", "work"),
            x = 350f,
            y = 450f
        ),
        Note(
            id = "6",
            title = "Gym Routine",
            previewText = "Push, Pull, Legs split. Focus on progressive overload.",
            timestamp = "Last Week",
            category = "Personal",
            isAIProcessed = false,
            tags = listOf("health", "fitness", "personal"),
            x = 420f,
            y = 780f
        ),
        Note(
            id = "7",
            title = "Q4 Marketing Strategy",
            previewText = "Leverage TikTok and Instagram Reels for organic growth. Run targeted ads for the new product line.",
            timestamp = "Oct 12",
            category = "Work",
            isAIProcessed = true,
            tags = listOf("marketing", "growth", "work"),
            x = 600f,
            y = 500f
        ),
        Note(
            id = "8",
            title = "Books to Read",
            previewText = "1. Atomic Habits 2. Deep Work 3. The Pragmatic Programmer.",
            timestamp = "Oct 10",
            category = "Ideas",
            isAIProcessed = false,
            tags = listOf("ideas", "reading", "personal"),
            x = 780f,
            y = 650f
        )
    )
}

