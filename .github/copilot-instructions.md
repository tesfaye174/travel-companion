# Copilot Instructions for Travel Companion

## Overview

Travel Companion is a modern Android application for tracking, organizing, and reliving travel memories. It is built using **Clean Architecture** and the **MVVM** pattern, leveraging Jetpack libraries and Kotlin. The app supports both Google Maps and offline OSM maps, geofencing, GPS tracking, and multimedia notes.

## Architecture

The project follows a layered architecture:

- **Presentation Layer**: Fragments, ViewModels, and UI bindings.
- **Domain Layer**: Use cases, domain models, and repository interfaces.
- **Data Layer**: Repository implementations, Room DAOs, and entities.

Refer to the [README.md](../README.md) for a detailed architecture diagram and project structure.

## Key Files and Directories

- `app/src/main/java/com/travelcompanion/data/`: Data layer, including Room DAOs and entities.
- `app/src/main/java/com/travelcompanion/domain/`: Domain layer with use cases and repository interfaces.
- `app/src/main/java/com/travelcompanion/ui/`: Presentation layer with screens and components.
- `app/src/main/java/com/travelcompanion/location/`: Location and geofencing providers.
- `app/src/main/assets/map.osm`: Offline OSM map data.

## Developer Workflows

### Build and Run

1. Clone the repository:
   ```bash
   git clone https://github.com/tesfaye174/travel-companion.git
   cd travel-companion
   ```
2. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run on a device/emulator:
   ```bash
   ./gradlew installDebug
   ```

### Testing

- Run unit tests:
  ```bash
  ./gradlew test
  ```
- Run instrumented tests (requires device/emulator):
  ```bash
  ./gradlew connectedAndroidTest
  ```

### Debugging

- Use Android Studio's debugger for breakpoints and inspecting app state.
- Key classes for debugging location and geofencing:
  - `PlayServicesLocationProvider.kt`
  - `PlatformLocationProvider.kt`
  - `GeofenceBroadcastReceiver.kt`

## Project-Specific Conventions

- **Location Providers**: The app supports both Google Play Services and platform-based location/geofencing. Toggle the `USE_PLAY_SERVICES_LOCATION` flag in `app/build.gradle` to switch implementations.
- **Offline Maps**: Place your OSM XML file in `app/src/main/assets/map.osm`.
- **Dependency Injection**: Hilt is used for DI. Modules are defined in the `di/` directory.
- **Testing Utilities**: Custom coroutine dispatchers are used for ViewModel and repository tests.

## External Dependencies

- **OSMDroid**: Offline map rendering.
- **MPAndroidChart**: Charting library for statistics.
- **CameraX**: Photo capture.
- **Timber**: Logging.

## Examples

### Adding a New Feature

1. Define a use case in `domain/usecase/`.
2. Add repository methods in `domain/repository/` and implement them in `data/repository/`.
3. Create a ViewModel in `ui/` and bind it to a Fragment.

### Modifying Geofencing Behavior

1. Update `PlayServicesGeofenceProvider.kt` or `PlatformGeofenceProvider.kt`.
2. Test changes using the `GeofenceBroadcastReceiver.kt`.
3. Run instrumented tests to verify behavior.

## Notes

- Follow the Clean Architecture principles when adding new features.
- Ensure all new code is covered by unit or instrumented tests.
- Use the `README.md` for additional setup and usage details.