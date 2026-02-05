package com.travelcompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.ui.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place

@Composable
fun HomeScreen(viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        floatingActionButton = {
            if (state.trips.none { it.isTracking }) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Text("+")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp)) {
            Text("Ready for your next adventure?", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            state.prediction?.let { pred ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Prediction AI")
                        Text(pred.message)
                    }
                }
            }

            state.trips.filter { it.isTracking }.forEach { trip ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Attivo: ${trip.title}")
                        Button(onClick = { viewModel.stopTrip() }) { Text("STOP") }
                    }
                }
            }

            state.trips.filter { !it.isTracking }.forEach { trip ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(text = trip.title, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddTripDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, dest, type -> viewModel.startTrip(name, dest, type); showAddDialog = false }
        )
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, label = { Text("Home") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Place, contentDescription = "Map") }, label = { Text("Map") })
    }
}

@Composable
fun AddTripDialog(onDismiss: () -> Unit, onConfirm: (String, String, TripType) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dest by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TripType.LOCAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo Viaggio") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = dest, onValueChange = { dest = it }, label = { Text("Destinazione") })
                Spacer(modifier = Modifier.height(8.dp))
                // TODO: Implementare un selettore di tipo viaggio (es. DropdownMenu) per permettere all'utente di scegliere il TripType.
                // Questo migliora l'usabilità e la flessibilità della schermata Home, facilitando la selezione e il filtraggio dei viaggi.
                // Suggerimento: utilizzare un componente Material DropdownMenu e collegarlo al ViewModel per aggiornare la lista dei viaggi mostrati.
            }
        },
        confirmButton = { Button(onClick = { onConfirm(name, dest, type) }) { Text("Inizia") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    )
}
