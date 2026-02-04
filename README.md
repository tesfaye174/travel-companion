# Travel Companion

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Una moderna applicazione Android per pianificare, tracciare e rivivere i tuoi viaggi. Implementa principi di Clean Architecture e MVVM, utilizzando le librerie Jetpack.

Breve sommario:
- Gestione viaggi e itinerari
- Tracciamento GPS (foreground/background)
- Mappe (Google Maps e OSM/OSMDroid per modalità offline)
- Foto geotaggate, note e statistiche

## Indice

- [Caratteristiche](#caratteristiche)
- [Requisiti](#requisiti)
- [Installazione e avvio rapido (Windows)](#installazione-e-avvio-rapido-windows)
- [Struttura del progetto](#struttura-del-progetto)
- [Stack tecnologico](#stack-tecnologico)
- [Dettagli su location/geofencing](#dettagli-su-locationgeofencing)
- [Esecuzione test](#esecuzione-test)
- [Contribuire](#contribuire)
- [Licenza e autore](#licenza-e-autore)

## Caratteristiche

- Tracciamento GPS in tempo reale con servizio foreground
- Integrazione con Google Maps (polilinee, marker)
- Supporto offline con OSMDroid e file OSM in `app/src/main/assets`
- Scatto di foto geotaggate (CameraX)
- Note testuali e foto-note per i viaggi
- Statistiche visuali (MPAndroidChart)
- Geofencing (Play Services o implementazione platform fallback)
- Creazione/modifica/cancellazione itinerari
- Esportazione dati in JSON e cancellazione completa dati
- Preferenze persistenti con DataStore

## Requisiti

- Windows (per comandi mostrati qui)
- Android Studio (consigliata versione recente compatibile con il wrapper Gradle)
- JDK 11+ o quello richiesto dal wrapper Gradle
- Android SDK (API level presente in `app/build.gradle`)
- Gradle wrapper incluso (`gradlew`, `gradlew.bat`)

## Installazione e avvio rapido (Windows)

1. Clona il repository (se non l'hai già fatto):

```powershell
git clone https://github.com/tesfaye174/travel-companion.git ; cd travel-companion
```

2. Compilare (build debug):

```powershell
.\gradlew assembleDebug
```

3. Pulire la build:

```powershell
.\gradlew clean
```

4. Eseguire test unitari:

```powershell
.\gradlew test
```

5. Test strumentati (richiede dispositivo o emulatore connesso):

```powershell
.\gradlew connectedAndroidTest
```

6. Analisi dipendenze (utile per debug conflitti):

```powershell
.\gradlew :app:dependencies
```

7. Per output diagnostico più dettagliato (stacktrace + info):

```powershell
.\gradlew build --stacktrace --info
```

In Android Studio: aprire la cartella `travel-companion` come progetto e usare Run/Debug, Logcat e il Debugger.

## Struttura del progetto

Principali cartelle e file:

- `app/` — modulo Android principale
  - `build.gradle` — configurazione del modulo
  - `src/` — codice sorgente (`main`, `test`)
  - `img/` — immagini di esempio incluse nel repo
  - `build/` — artefatti generati (non versionati)
- `gradle/`, `gradlew`, `gradlew.bat`, `gradle-wrapper.properties` — wrapper Gradle
- `local.properties` — percorso SDK locale (non committare credenziali)
- `LICENSE`, `README.md`, `settings.gradle`

Note: le cartelle generate da Gradle (es. `app/build/`) non dovrebbero essere modificate manualmente.

## Stack tecnologico

- Linguaggio: Kotlin 1.9.x (con interoperabilità Java dove necessario)
- Min SDK: 26
- Target SDK: 34 (verificare in `app/build.gradle`)
- Architettura: Clean Architecture + MVVM
- DI: Hilt
- DB: Room
- Preferenze: DataStore
- Concorrenza: Coroutines + Flow
- Mappe: Google Maps + OSMDroid (offline)
- Fotocamera: CameraX
- Background: WorkManager
- Grafici: MPAndroidChart
- Immagini: Glide
- Logging: Timber

## Dettagli su location/geofencing

Questo progetto fornisce due implementazioni intercambiabili per location e geofencing:

- `Play Services` (predefinito): usa `FusedLocationProviderClient` e le API di geofencing di Play Services.
- `Platform` (fallback): usa `LocationManager` e una soluzione di geofencing basata su polling.

Come cambiare modalità: modificare il flag di build `USE_PLAY_SERVICES_LOCATION` in `app/build.gradle` e ricostruire il progetto.

Limitazioni della modalità Platform:
- Meno precisa e più sensibile alla batteria.
- Geofence non persistono automaticamente dopo reboot (a meno che non si estenda il provider per salvarli e ri-registrarli).

File utili di riferimento (path relativi a `app/src/main/java`):
- `com.travelcompanion.location.PlayServicesLocationProvider` (o percorso corrispondente)
- `com.travelcompanion.location.PlatformLocationProvider`
- `com.travelcompanion.location.PlayServicesGeofenceProvider`
- `com.travelcompanion.location.PlatformGeofenceProvider`
- `com.travelcompanion.utils.GeofenceBroadcastReceiver`

## Esecuzione test

- Unit tests:

```powershell
.\gradlew test
```

- Instrumented tests (device/emulatore richiesto):

```powershell
.\gradlew connectedAndroidTest
```

## Suggerimenti per debug

- Controllare `app/build.gradle`, `settings.gradle` e `local.properties` per errori di configurazione.
- Usare `--stacktrace --info` per ottenere dettagli dalla build Gradle.
- Logcat è la prima risorsa per crash e problemi runtime.

## Contribuire

Se vuoi contribuire:
- Apri un issue per bug/feature
- Crea un branch per ogni modifica (`feature/`, `fix/`)
- Apri una pull request con descrizione e test quando possibile

## Licenza e autore

- Licenza: MIT — vedere il file `LICENSE` nella root del progetto.
- Autore: Tesfaye — GitHub: https://github.com/tesfaye174

---

Se vuoi, posso:
- Tradurre le schermate (`docs/screenshots`) e aggiungere anteprime aggiornate.
- Aggiungere una sezione di troubleshooting più dettagliata (esempi di errori comuni e come risolverli).
- Implementare uno script PowerShell di setup per Windows per eseguire build/test automaticamente.

