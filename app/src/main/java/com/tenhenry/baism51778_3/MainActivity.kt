package com.tenhenry.baism51778_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RickAndMortyScreen()
                }
            }
        }
    }
}

@Composable
fun RickAndMortyScreen() {
    val scope = rememberCoroutineScope()
    var characters by remember { mutableStateOf<List<Character>>(emptyList()) }
    var selected by remember { mutableStateOf<Character?>(null) }
    var idText by remember { mutableStateOf("1") }
    var status by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Rick and Morty API", style = MaterialTheme.typography.headlineSmall)

        Button(
            onClick = {
                scope.launch {
                    status = "Ładowanie..."
                    try {
                        val response = ApiClient.api.getCharacters()
                        characters = response.results
                        selected = null
                        status = "Pobrano ${characters.size} z ${response.info.count} postaci (strona 1)"
                    } catch (e: Exception) {
                        status = "Błąd: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pobierz wszystkie postacie")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = idText,
                onValueChange = { idText = it.filter(Char::isDigit) },
                label = { Text("ID postaci") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                val id = idText.toIntOrNull()
                if (id != null) {
                    scope.launch {
                        status = "Ładowanie..."
                        try {
                            selected = ApiClient.api.getCharacter(id)
                            status = ""
                        } catch (e: Exception) {
                            status = "Błąd: ${e.message}"
                        }
                    }
                }
            }) {
                Text("Pobierz po ID")
            }
        }

        if (status.isNotEmpty()) Text(status)

        selected?.let { CharacterDetails(it) }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(characters, key = { it.id }) { c ->
                Text(
                    text = "#${c.id}  ${c.name} (${c.species})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = c }
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun CharacterDetails(c: Character) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(c.name, style = MaterialTheme.typography.titleMedium)
            Text("ID: ${c.id}")
            Text("Status: ${c.status}")
            Text("Gatunek: ${c.species}")
            Text("Płeć: ${c.gender}")
            Text("Pochodzenie: ${c.origin.name}")
            Text("Lokalizacja: ${c.location.name}")
        }
    }
}