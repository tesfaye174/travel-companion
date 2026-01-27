# Travel Companion

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern Android application for tracking, organizing, and reliving your travel memories. Built with Clean Architecture, MVVM pattern, and Jetpack libraries.

## Screenshots

| Home | Trips | Map | Statistics |
| ---- | ----- | --- | ---------- |
| ![Home](docs/screenshots/home.png) | ![Trips](docs/screenshots/trips.png) | ![Map](docs/screenshots/map.png) | ![Stats](docs/screenshots/stats.png) |

## Features

### Core Features

- **GPS Tracking** - Real-time location tracking with foreground service
- **Google Maps Integration** - View routes with polylines and markers
- **Map Integration (OSM)** - View routes with polylines and markers using offline OSM and OSMDroid
- **Photo Capture** - Take geotagged photos during trips using CameraX
- **Notes** - Add text notes to document your journey
- **Statistics** - Visualize travel data with charts (MPAndroidChart)
- **Geofencing** - Get notified when entering/exiting saved locations

### Trip Management

- Create, edit, and delete trips
- Search and filter trips by type
- Swipe-to-delete with undo
- Export data to JSON
- Delete all data option

### User Experience

│   ├── map/               # Map UI (OSMDroid / offline OSM)

- Persistent settings with DataStore
- Modern Material Design 3 UI
- Accessibility support

## Architecture

This project follows **Clean Architecture** principles with **MVVM** pattern:

```text
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Fragments  │  │  ViewModels │  │  Adapters/Bindings  │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Use Cases  │  │   Models    │  │  Repository (I/F)   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Repository  │  │    DAOs     │  │      Entities       │  │
│  │   (Impl)    │  │   (Room)    │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Project Structure

```text
app/src/main/java/com/travelcompanion/
├── data/
│   ├── db/
│   │   ├── dao/           # Room DAOs (6 DAOs)
│   │   ├── entities/      # Room Entities (6 tables)
│   │   ├── converters/    # Type converters
│   │   └── AppDatabase.kt
│   ├── preferences/       # DataStore preferences
│   └── repository/        # Repository implementations
├── di/                    # Hilt dependency injection
├── domain/
│   ├── model/             # Domain models
│   ├── repository/        # Repository interfaces
│   └── usecase/           # Use cases (business logic)
├── ui/
│   ├── home/              # Home screen
│   ├── trips/             # Trips list
│   ├── tripdetails/       # Trip details
│   ├── newtrip/           # Create trip
│   ├── tracking/          # GPS tracking (Activity + Service)
│   ├── map/               # Google Maps
│   ├── statistics/        # Charts and stats
│   └── settings/          # App settings
└── utils/                 # Utility classes
```

## Tech Stack

| Category | Libraries |
| -------- | --------- |
| **Language** | Kotlin 1.9.21 |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 34 (Android 14) |
| **Architecture** | Clean Architecture + MVVM |
| **DI** | Hilt 2.48 |
| **Database** | Room 2.6.1 |
| **Preferences** | DataStore 1.0.0 |
| **Async** | Coroutines + Flow |
| **Navigation** | Navigation Component 2.7.6 |
| **Maps** | OSMDroid (offline OSM files) |
| **Location** | Fused Location Provider 21.1.0 |
| **Camera** | CameraX 1.3.1 |
| **Background** | WorkManager 2.9.0 |
| **Charts** | MPAndroidChart 3.1.0 |
| **Images** | Glide 4.15.1 |
| **Logging** | Timber 5.0.1 |

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/tesfaye174/travel-companion.git
   cd travel-companion
   ```

2. **Provide offline OSM data**

   Place your OSM XML file in `app/src/main/assets/map.osm`. The app's map UI (OSMDroid) will load `assets/map.osm` at runtime. Replace this file with your provided OSM export.

3. **Build the project**

   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on device/emulator**

   ```bash
   ./gradlew installDebug
   ```

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Database Schema

## Platform vs Play Services (location & geofencing)

This project supports two interchangeable implementations for location and geofencing:

- **Play Services (default)**: uses Google Play Services `FusedLocationProviderClient` and the Play Services Geofencing API. This provides more accurate location fixes and a robust geofencing service.
- **Platform (fallback)**: uses Android `LocationManager` for location and a polling-based geofence detector implemented by the app. This mode is intended for environments without Google Play Services, but has limitations (see below).

How to switch:

- The app exposes a build-time flag in `app/build.gradle` called `USE_PLAY_SERVICES_LOCATION` (default: `true`). To use platform providers, set it to `false` and rebuild.

Limitations of Platform mode:

- Geofencing is implemented via periodic location updates and distance checks (battery- and accuracy-sensitive).
- Platform geofencing does not persist across device reboots and may miss fast enter/exit transitions.
- For production-level geofencing (reliable background delivery, device restarts), prefer the Play Services implementation.

Files of interest:

- `app/src/main/java/com/travelcompanion/location/PlayServicesLocationProvider.kt`
- `app/src/main/java/com/travelcompanion/location/PlatformLocationProvider.kt`
- `app/src/main/java/com/travelcompanion/location/PlayServicesGeofenceProvider.kt`
- `app/src/main/java/com/travelcompanion/location/PlatformGeofenceProvider.kt`
- `app/src/main/java/com/travelcompanion/utils/GeofenceBroadcastReceiver.kt`

If you want, I can extend the platform provider to persist geofences across reboots and add battery optimizations.

The app uses Room database with 6 tables:

| Table | Description |
| ----- | ----------- |
| `trips` | Main trip information |
| `journeys` | GPS tracked segments |
| `photo_notes` | Photos with optional notes |
| `notes` | Text notes |
| `geofence_areas` | Saved geofence locations |
| `geofence_events` | Geofence enter/exit events |

## Permissions

| Permission | Usage |
| ---------- | ----- |
| `ACCESS_FINE_LOCATION` | GPS tracking |
| `ACCESS_BACKGROUND_LOCATION` | Geofencing |
| `CAMERA` | Photo capture |
| `POST_NOTIFICATIONS` | Tracking & geofence alerts |
| `FOREGROUND_SERVICE_LOCATION` | Background tracking |

## Testing

The project includes:

- **Unit Tests**: ViewModel and Repository tests with Mockito
- **Instrumented Tests**: Room database tests
- **Test Utilities**: Custom dispatchers for coroutine testing

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

### Tesfaye

- GitHub: [@tesfaye174](https://github.com/tesfaye174)

## Acknowledgments

- [OSMDroid](https://github.com/osmdroid/osmdroid)
- [Material Design](https://material.io)
- [Android Jetpack](https://developer.android.com/jetpack)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
