package org.example.dtos

data class ChatMessage(
    val role: String,
    val content: String
)
data class OpenAIChatRequest(
    val model: String,
    val messages: List<ChatMessage>
)
data class Choice(
    val message: ChatMessage
)
data class OpenAIChatResponse(
    val choices: List<Choice>
)