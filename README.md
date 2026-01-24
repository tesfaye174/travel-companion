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

- Dark/Light theme support (Material You)
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
| **Maps** | Google Maps SDK 18.2.0 |
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
- Google Maps API Key

### Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/tesfaye174/travel-companion.git
   cd travel-companion
   ```

2. **Configure Google Maps API Key**

   Create or edit `gradle.properties` in the project root:

   ```properties
   MAPS_API_KEY=your_google_maps_api_key_here
   ```

   > ⚠️ Never commit API keys to version control!

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

- [Google Maps Platform](https://developers.google.com/maps)
- [Material Design](https://material.io)
- [Android Jetpack](https://developer.android.com/jetpack)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
