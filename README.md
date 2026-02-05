# Travel Companion

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Travel Companion è una moderna applicazione Android progettata per pianificare, tracciare e rivivere i tuoi viaggi. L'app segue i principi di Clean Architecture e MVVM, sfruttando le librerie Jetpack e le best practice di sviluppo Android.

## Sommario delle funzionalità principali

- Gestione avanzata di viaggi e itinerari
- Tracciamento GPS in tempo reale (foreground/background)
- Mappe online (Google Maps) e offline (OSMDroid)
- Foto geotaggate, note testuali e statistiche dettagliate
- Esportazione e gestione sicura dei dati

## Indice

- [Caratteristiche](#caratteristiche)
- [Esempi di codice](#esempi-di-codice)
- [Requisiti](#requisiti)
- [Installazione e avvio rapido (Windows)](#installazione-e-avvio-rapido-windows)
- [Struttura del progetto](#struttura-del-progetto)
- [Stack tecnologico](#stack-tecnologico)
- [Gestione location e geofencing](#gestione-location-e-geofencing)
- [Esecuzione test](#esecuzione-test)
- [Suggerimenti e troubleshooting](#suggerimenti-e-troubleshooting)
- [Contribuire](#contribuire)
- [Licenza e autore](#licenza-e-autore)
- [Consegna per l’esame](#consegna-per-lesame)

## Caratteristiche

- Tracciamento GPS in tempo reale tramite servizio foreground
- Integrazione con Google Maps (polilinee, marker, visualizzazione itinerari)
- Supporto offline con OSMDroid e file OSM in `app/src/main/assets`
- Scatto di foto geotaggate (CameraX)
- Note testuali e foto-note associate ai viaggi
- Statistiche visuali (MPAndroidChart)
- Geofencing avanzato (Play Services o fallback platform)
- Gestione completa di itinerari (creazione, modifica, cancellazione)
- Esportazione dati in JSON e cancellazione sicura
- Preferenze persistenti con DataStore

## Esempi di codice

### 1. Tracciamento GPS in un ViewModel

```kotlin
class TripViewModel @Inject constructor(
    private val locationProvider: LocationProvider
) : ViewModel() {
    val locationUpdates: LiveData<Location> = locationProvider.locationFlow.asLiveData()
}
```

### 2. Aggiunta di una nota geotaggata

```kotlin
val note = Note(
    text = "Vista panoramica!",
    latitude = 45.4642,
    longitude = 9.19,
    timestamp = System.currentTimeMillis()
)
noteRepository.addNote(note)
```

### 3. Esportazione dati viaggio in JSON

```kotlin
val tripData = tripRepository.getTripWithNotes(tripId)
val json = Gson().toJson(tripData)
File("/storage/emulated/0/Download/trip.json").writeText(json)
```

### 4. Configurazione di un Geofence

```kotlin
val geofence = Geofence.Builder()
    .setRequestId("punto_interesse")
    .setCircularRegion(45.4642, 9.19, 100f)
    .setExpirationDuration(Geofence.NEVER_EXPIRE)
    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
    .build()
geofenceProvider.addGeofence(geofence)
```

## Requisiti

- Windows (per i comandi di esempio)
- Android Studio (versione recente consigliata)
- JDK 11+ (o quello richiesto dal wrapper Gradle)
- Android SDK (API level come da `app/build.gradle`)
- Gradle wrapper incluso (`gradlew`, `gradlew.bat`)

## Installazione e avvio rapido (Windows)

1. Clona il repository:

```powershell
git clone https://github.com/tesfaye174/travel-companion.git ; cd travel-companion
```

2. Compila la versione debug:

```powershell
.\gradlew assembleDebug
```

3. Pulisci la build:

```powershell
.\gradlew clean
```

4. Esegui i test unitari:

```powershell
.\gradlew test
```

5. Esegui i test strumentati (richiede dispositivo/emulatore connesso):

```powershell
.\gradlew connectedAndroidTest
```

6. Analizza le dipendenze (per debug di conflitti):

```powershell
.\gradlew :app:dependencies
```

7. Output diagnostico dettagliato:

```powershell
.\gradlew build --stacktrace --info
```

> **Suggerimento:** In Android Studio, apri la cartella `travel-companion` come progetto e utilizza Run/Debug, Logcat e Debugger per uno sviluppo più agevole.

## Struttura del progetto

- `app/` — Modulo Android principale
  - `build.gradle` — Configurazione del modulo
  - `src/` — Codice sorgente (`main`, `test`)
  - `img/` — Immagini di esempio
  - `build/` — Artefatti generati (non versionati)
- `gradle/`, `gradlew`, `gradlew.bat`, `gradle-wrapper.properties` — Wrapper Gradle
- `local.properties` — Percorso SDK locale (non committare credenziali)
- `LICENSE`, `README.md`, `settings.gradle`

> **Nota:** Le cartelle generate da Gradle (es. `app/build/`) non vanno modificate manualmente.

## Stack tecnologico

- **Linguaggio:** Kotlin 1.9.x (con interoperabilità Java)
- **Min SDK:** 26
- **Target SDK:** 34 (verifica in `app/build.gradle`)
- **Architettura:** Clean Architecture + MVVM
- **Dependency Injection:** Hilt
- **Database:** Room
- **Preferenze:** DataStore
- **Concorrenza:** Coroutines + Flow
- **Mappe:** Google Maps + OSMDroid (offline)
- **Fotocamera:** CameraX
- **Background:** WorkManager
- **Grafici:** MPAndroidChart
- **Immagini:** Glide
- **Logging:** Timber

## Gestione location e geofencing

Il progetto offre due implementazioni intercambiabili per location e geofencing:

- **Play Services** (predefinito): usa `FusedLocationProviderClient` e le API di geofencing di Play Services.
- **Platform** (fallback): usa `LocationManager` e una soluzione di geofencing basata su polling.

Per cambiare modalità, modifica il flag di build `USE_PLAY_SERVICES_LOCATION` in `app/build.gradle` e ricompila il progetto.

**Limitazioni della modalità Platform:**
- Meno precisa e più energivora.
- I geofence non persistono dopo il reboot (a meno di estensioni custom).

File di riferimento (in `app/src/main/java`):
- `com.travelcompanion.location.PlayServicesLocationProvider`
- `com.travelcompanion.location.PlatformLocationProvider`
- `com.travelcompanion.location.PlayServicesGeofenceProvider`
- `com.travelcompanion.location.PlatformGeofenceProvider`
- `com.travelcompanion.utils.GeofenceBroadcastReceiver`

## Esecuzione test

- **Unit test:**

```powershell
.\gradlew test
```

- **Test strumentati (device/emulatore):**

```powershell
.\gradlew connectedAndroidTest
```

## Suggerimenti e troubleshooting

- Controlla `app/build.gradle`, `settings.gradle` e `local.properties` per errori di configurazione.
- Usa `--stacktrace --info` per dettagli sulla build Gradle.
- Logcat è la risorsa principale per crash e problemi runtime.
- Consulta la documentazione ufficiale delle librerie usate per risolvere errori specifici.

## Contribuire

Vuoi contribuire? Segui questi passi:
- Apri una issue per bug o nuove feature
- Crea un branch dedicato (`feature/`, `fix/`)
- Assicurati che le modifiche siano testate
- Apri una pull request dettagliata

## Licenza e autore

- **Licenza:** MIT — vedi il file `LICENSE` nella root del progetto.
- **Autore:** Tesfaye — [GitHub](https://github.com/tesfaye174)

## Consegna per l’esame

Questa applicazione è stata sviluppata seguendo le regole del progetto d’esame di "Laboratorio di applicazioni mobili" (Unibo).

**Per la consegna sono richiesti:**

1. **Codice dell’applicazione mobile** (nativa, non web) in formato ZIP, nominato `COGNOME1_COGNOME2.zip` (o solo COGNOME.zip per progetti individuali). Se l’archivio è troppo grande, fornire un link (Drive/OneDrive) e l’hash, ma caricare comunque su Virtuale un file con il report, il link e l’hash.
2. **Report PDF** denominato `COGNOME1_COGNOME2.pdf` che deve includere:
   - Nome, cognome, email, matricola di ogni componente.
   - Overview dell’applicazione con screenshot.
   - Dettagli implementativi (almeno il 70% del report).
   - Scelte progettuali e workflow.
   - Lunghezza consigliata: 10-15 pagine (minimo 5, massimo 15).
3. **Presentazione (slide)** da portare all’orale (10-15 slide, recap del report e screenshot). Ogni componente deve conoscere l’intera implementazione e potrà essere interrogato su qualsiasi parte.
4. **Uso di Git/versionamento** fortemente consigliato.
5. **Domande e proposte personalizzate** vanno inviate via email al docente (Dr. Lorenzo Gigli).

**Attenzione:**
- Il codice e il report vanno caricati solo su Virtuale, non via email.
- Le slide vanno portate il giorno dell’orale.
- Seguire le regole di denominazione dei file.

Per dettagli completi, consultare il regolamento ufficiale del corso.
