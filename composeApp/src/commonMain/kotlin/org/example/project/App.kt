package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.*


@Preview
@Composable
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var pokemonName by remember { mutableStateOf<String?>(null) }

        val coroutineScope = rememberCoroutineScope()

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                showContent = !showContent
                if (showContent) {
                    coroutineScope.launch {
                        //pokemonName = Greeting().greet()
                        pokemonName = fetchPokemonName()
                    }
                }
            }) {
                Text("Click me!")
            }

            AnimatedVisibility(showContent) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(pokemonName ?: "Chargement...")
                }
            }
        }
    }
}

@Serializable
data class PokemonResponse(
    val name: Map<String, String>
)

suspend fun fetchPokemonName(): String {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) // Configure JSON ici
        }
    }

    return try {
        // Utilisez `body<PokemonResponse>()` pour désérialiser la réponse
        val response: PokemonResponse = client.get("https://tyradex.vercel.app/api/v1/pokemon/149").body()
        response.name["fr"] ?: "Nom inconnu"
    } catch (e: Exception) {
        "Erreur : ${e.message}"
    } finally {
        client.close()
    }
}
