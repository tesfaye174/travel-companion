# Travel Companion

[![Piattaforma](https://img.shields.io/badge/Piattaforma-Android-green.svg)](https://developer.android.com)
[![Linguaggio](https://img.shields.io/badge/Linguaggio-Kotlin-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Licenza](https://img.shields.io/badge/Licenza-MIT-yellow.svg)](LICENSE)

Travel Companion è un'applicazione Android, scritta in Kotlin, che permette
all'utente di tracciare i propri viaggi, organizzarli e rivivere i ricordi
attraverso foto e annotazioni. L'app è stata sviluppata seguendo i principi
della Clean Architecture, con il pattern MVVM nello strato di presentazione e
le librerie di Android Jetpack.

## Schermate

| Home | Viaggi | Mappa | Statistiche |
| ---- | ----- | --- | ---------- |
| ![Home](docs/screenshots/home.png) | ![Viaggi](docs/screenshots/trips.png) | ![Mappa](docs/screenshots/map.png) | ![Stat](docs/screenshots/stats.png) |

## Funzionalità

### Funzionalità principali

- **Tracciamento GPS** in tempo reale tramite foreground service.
- **Visualizzazione su mappa** dei percorsi con polilinee e marker (OpenStreetMap via OSMDroid, supporto offline).
- **Foto geolocalizzate** scattate con CameraX e associate al viaggio.
- **Note testuali** per documentare il viaggio.
- **Statistiche** sui viaggi con grafici (MPAndroidChart).
- **Geofencing**: notifiche di ingresso/uscita dalle aree salvate.

### Gestione viaggi

- Creazione, modifica ed eliminazione dei viaggi.
- Ricerca e filtraggio per tipo.
- Eliminazione con swipe e possibilità di annullare.
- Esportazione dei dati in formato JSON.
- Cancellazione totale dei dati.

### Esperienza utente

- Preferenze persistenti con DataStore.
- Interfaccia Material Design 3 (tema chiaro e scuro).
- Supporto all'accessibilità.

## Architettura

Il progetto segue i principi della **Clean Architecture** con pattern **MVVM**
nella presentazione.

```text
┌─────────────────────────────────────────────────────────────┐
│                  PRESENTAZIONE (UI)                          │
│  Fragment · ViewModel · Adapter · ViewBinding                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DOMINIO                                │
│  Casi d'uso · Modelli · Interfacce repository                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         DATI                                 │
│  Repository (impl) · DAO Room · DataStore · LocationProvider │
└─────────────────────────────────────────────────────────────┘
```

### Struttura del progetto

```text
app/src/main/java/com/travelcompanion/
├── data/
│   ├── db/
│   │   ├── dao/           # DAO Room (6 file)
│   │   ├── entities/      # Entità Room (6 tabelle)
│   │   ├── converters/    # Type converter
│   │   └── AppDatabase.kt
│   ├── preferences/       # Preferenze DataStore
│   └── repository/        # Implementazione repository
├── di/                    # Moduli Hilt
├── domain/
│   ├── model/             # Modelli di dominio
│   ├── repository/        # Interfacce repository
│   └── usecase/           # Casi d'uso
├── ui/
│   ├── home/              # Home
│   ├── trips/             # Elenco viaggi
│   ├── tripdetails/       # Dettagli viaggio
│   ├── newtrip/           # Nuovo viaggio
│   ├── tracking/          # Tracciamento GPS (Activity + Service)
│   ├── map/               # Mappa OSM
│   ├── statistics/        # Statistiche e grafici
│   └── settings/          # Impostazioni
└── utils/                 # Classi di utilità
```

## Tecnologie utilizzate

| Categoria | Libreria / versione |
| -------- | --------- |
| **Linguaggio** | Kotlin 1.9.22 |
| **API minima** | 26 (Android 8.0) |
| **API target** | 34 (Android 14) |
| **Architettura** | Clean Architecture + MVVM |
| **DI** | Hilt 2.50 |
| **Database** | Room 2.6.1 |
| **Preferenze** | DataStore 1.0.0 |
| **Asincronia** | Coroutines 1.7.3 + Flow |
| **Navigazione** | Navigation Component 2.7.7 |
| **Mappe** | OSMDroid 6.1.18 (file OSM offline) |
| **Posizione** | Fused Location Provider 21.1.0 |
| **Fotocamera** | CameraX 1.3.1 |
| **Background** | WorkManager 2.9.0 |
| **Grafici** | MPAndroidChart 3.1.0 |
| **Immagini** | Glide 4.16.0 |
| **Logging** | Timber 5.0.1 |
| **Build** | AGP 8.3.0, Gradle 8.11.1, KSP 1.9.22-1.0.17 |

## Come iniziare

### Prerequisiti

- Android Studio Hedgehog (2023.1.1) o successivo
- JDK 17
- Android SDK 34

### Configurazione iniziale

1. **Clonare il repository**

   ```bash
   git clone https://github.com/tesfaye174/travel-companion.git
   cd travel-companion
   ```

2. **Configurare `local.properties`**

   ```bash
   cp local.properties.example local.properties
   ```

   Modificare il file impostando `sdk.dir` (path dell'SDK Android) e,
   opzionalmente, le chiavi per la firma della build di release.

3. **(Facoltativo) Aggiornare il file OSM**

   Per il supporto offline alla mappa, il file `app/src/main/assets/map.osm`
   viene caricato da OSMDroid all'avvio. Può essere sostituito con un export
   personalizzato di OpenStreetMap.

4. **Compilare il progetto**

   ```bash
   ./gradlew assembleDebug
   ```

5. **Installare su dispositivo o emulatore**

   ```bash
   ./gradlew installDebug
   ```

### Esecuzione dei test

```bash
# Test unitari
./gradlew test

# Test strumentati (richiede dispositivo o emulatore)
./gradlew connectedAndroidTest
```

## Schema del database

L'app utilizza Room con sei tabelle:

| Tabella | Descrizione |
| ----- | ----------- |
| `trips` | Informazioni principali del viaggio |
| `journeys` | Segmenti GPS registrati |
| `photo_notes` | Foto con nota opzionale |
| `notes` | Note testuali |
| `geofence_areas` | Aree di interesse salvate |
| `geofence_events` | Eventi di ingresso/uscita dalle geofence |

Lo schema corrente è alla versione 4. I file `app/schemas/*/3.json` e
`app/schemas/*/4.json` sono versionati per tracciare le migrazioni.

## Modalità Play Services vs Platform

Il progetto supporta due implementazioni intercambiabili per posizione e
geofencing:

- **Play Services (default)**: utilizza `FusedLocationProviderClient` e la
  Geofencing API. Garantisce maggiore accuratezza e affidabilità in
  background.
- **Platform (fallback)**: utilizza `LocationManager` di Android e un
  rilevatore di geofence basato su polling. Pensata per dispositivi senza
  Google Play Services.

Per cambiare modalità, modificare il flag `USE_PLAY_SERVICES_LOCATION` in
`app/build.gradle` e ricompilare. Limitazioni della modalità Platform:

- il geofencing è realizzato tramite aggiornamenti periodici della posizione
  e controllo della distanza, quindi è sensibile alla batteria;
- non è garantita la persistenza delle geofence dopo un riavvio del
  dispositivo;
- transizioni rapide di ingresso/uscita possono andare perse.

File rilevanti:

- `app/src/main/java/com/travelcompanion/location/PlayServicesLocationProvider.kt`
- `app/src/main/java/com/travelcompanion/location/PlatformLocationProvider.kt`
- `app/src/main/java/com/travelcompanion/location/PlayServicesGeofenceProvider.kt`
- `app/src/main/java/com/travelcompanion/location/PlatformGeofenceProvider.kt`
- `app/src/main/java/com/travelcompanion/utils/GeofenceBroadcastReceiver.kt`

## Permessi richiesti

| Permesso | Motivo |
| ---------- | ----- |
| `ACCESS_FINE_LOCATION` | Tracciamento GPS |
| `ACCESS_BACKGROUND_LOCATION` | Geofencing in background |
| `CAMERA` | Scatto delle foto |
| `POST_NOTIFICATIONS` | Notifiche di tracciamento e geofence |
| `FOREGROUND_SERVICE_LOCATION` | Tracciamento in foreground service |

## Test

Il progetto include tre suite di test unitari:

- `TripValidationUtilsTest` — validazione date e titolo del viaggio.
- `AnalyzePredictionUseCaseTest` — caso d'uso di analisi predittiva.
- `HomeViewModelTest` — stato della Home e flussi `StateFlow`.

Le dipendenze di test comprendono JUnit 4, MockK, Turbine, Truth e
`coroutines-test`. Per i test strumentati sono dichiarate (anche se non
ancora utilizzate diffusamente) AndroidX Test, Espresso e Room Testing.

## Licenza

Il progetto è rilasciato sotto licenza MIT. Si veda il file [LICENSE](LICENSE)
per i dettagli.

## Autore

**Tesfaye** — [@tesfaye174](https://github.com/tesfaye174)

## Ringraziamenti

- [OSMDroid](https://github.com/osmdroid/osmdroid)
- [Material Design](https://material.io)
- [Android Jetpack](https://developer.android.com/jetpack)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
