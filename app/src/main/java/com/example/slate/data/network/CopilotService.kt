package com.example.slate.data.network

import com.example.slate.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CopilotService {

    // Ideally, the API key is provided securely, but for a showcase, we read from BuildConfig
    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    // We use gemini-1.5-flash for fast text summarization and copilot interactions
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun summarizeNote(content: String): String {
        if (apiKey.isBlank()) return "Simulated Summary: Please configure your Gemini API key in local.properties or build.gradle.kts to enable live AI."
        if (content.isBlank()) return "The note is currently empty."
        
        return withContext(Dispatchers.IO) {
            try {
                val prompt = "Summarize the following note content into a concise 1-2 sentence executive summary:\n\n$content"
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Could not generate summary."
            } catch (e: Exception) {
                "Error generating summary: ${e.localizedMessage}"
            }
        }
    }

    suspend fun extractActionItems(content: String): String {
        if (apiKey.isBlank()) return "- Simulated Action 1\n- Simulated Action 2\n(Configure GEMINI_API_KEY)"
        if (content.isBlank()) return "No content to extract actions from."
        
        return withContext(Dispatchers.IO) {
            try {
                val prompt = "Extract key action items, tasks, or todos from the following note content. Return them as a bulleted list:\n\n$content"
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Could not extract action items."
            } catch (e: Exception) {
                "Error: ${e.localizedMessage}"
            }
        }
    }

    suspend fun translateToSpanish(content: String): String {
        if (apiKey.isBlank()) return "Traducción simulada (Configure GEMINI_API_KEY)"
        if (content.isBlank()) return "Nada que traducir."
        
        return withContext(Dispatchers.IO) {
            try {
                val prompt = "Translate the following note content to professional Spanish:\n\n$content"
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Could not translate content."
            } catch (e: Exception) {
                "Error: ${e.localizedMessage}"
            }
        }
    }

    suspend fun copilotChat(noteTitle: String, noteContent: String, userQuery: String): String {
        if (apiKey.isBlank()) return "Simulated Answer: I see you are asking about '$userQuery'. Please configure GEMINI_API_KEY for live answers."
        
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    You are Slate Copilot, a helpful AI assistant integrated into a note-taking app.
                    The current note being viewed is titled "$noteTitle".
                    Here is the content of the note:
                    ---
                    $noteContent
                    ---
                    
                    The user is asking: "$userQuery"
                    
                    Please provide a helpful, concise answer based on the note content if applicable.
                """.trimIndent()
                val response = generativeModel.generateContent(prompt)
                response.text ?: "I'm sorry, I couldn't process that query."
            } catch (e: Exception) {
                "Error: ${e.localizedMessage}"
            }
        }
    }
}
