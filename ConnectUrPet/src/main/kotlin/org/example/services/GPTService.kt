package org.example.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.dtos.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

interface GPTService{
    fun generateText(recommendationInput: RecommendationInput): PetCareInfo?
}

@Service
class AbstractGPTService(private val restTemplate: RestTemplate, private val objectMapper: ObjectMapper):GPTService {

    @Value("\${openai.api.key}")
    lateinit var apiKey: String

    @Value("\${openai.api.url}")
    lateinit var apiUrl: String

    override fun generateText(recommendationInput: RecommendationInput): PetCareInfo? {

        val prompt = "Dame sugerencias de cuidados de un animal de: $recommendationInput \n"+
                "devuelve como un json los siguientes datos:\n" +
                "nutrition{foodType, portionSize, supplements}, exercise{daily,activities}, grooming{brushing,bathing,earCleaning,nailTrimming}, healthCare{vaccination}. \nHazlo en un string"

        val request = OpenAIChatRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(ChatMessage(role = "user", content = prompt))
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiKey")
        }

        val entity = HttpEntity(request, headers)

        val response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String::class.java)

        val openAIResponse = objectMapper.readValue(response.body, OpenAIChatResponse::class.java)
        val jsonString = openAIResponse.choices.firstOrNull()?.message?.content?.trim() ?: return null
        return objectMapper.readValue(jsonString, PetCareInfo::class.java)
    }
}

