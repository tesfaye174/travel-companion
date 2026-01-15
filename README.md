# Travel Companion 📱✈️

Un'app Android moderna per gestire e tracciare i tuoi viaggi, con funzionalità avanzate di geolocalizzazione, statistiche e galleria fotografica.

## 🚀 Caratteristiche

- **Gestione Viaggi**: Crea e gestisci i tuoi viaggi con destinazione, date e tipo
- **Tracciamento GPS**: Traccia i tuoi spostamenti in tempo reale durante i viaggi
- **Galleria Fotografica**: Scatta e organizza foto durante i tuoi viaggi
- **Statistiche**: Visualizza statistiche dettagliate sui tuoi viaggi e spostamenti
- **Geofencing**: Notifiche automatiche quando entri o esci da aree specifiche
- **Promemoria**: Imposta promemoria per i tuoi viaggi
- **Filtri Avanzati**: Filtra i viaggi per destinazione e tipo

## 🛠️ Tecnologie Utilizzate

- **Kotlin**: Linguaggio di programmazione principale
- **Android Jetpack**:
  - ViewModel & LiveData per la gestione dello stato
  - Navigation Component per la navigazione
  - Room Database per la persistenza dei dati
  - WorkManager per i task in background
- **Google Maps API**: Per la visualizzazione delle mappe
- **CameraX**: Per la gestione della fotocamera
- **Material Design 3**: Per un'interfaccia moderna e intuitiva

## 📋 Requisiti

- Android Studio Hedgehog | 2023.1.1 o superiore
- Android SDK 26 (Android 8.0) o superiore
- Gradle 8.10
- Kotlin 1.9.0+

## 🔧 Installazione

1. Clona il repository:
```bash
git clone https://github.com/tesfaye174/travel-companion.git
```

2. Apri il progetto in Android Studio

3. Configura la Google Maps API Key:
   - Ottieni una chiave API da [Google Cloud Console](https://console.cloud.google.com/)
   - Aggiungi la chiave in `app/build.gradle` nella sezione `manifestPlaceholders`:
   ```gradle
   manifestPlaceholders = [MAPS_API_KEY: "YOUR_API_KEY_HERE"]
   ```

4. Sincronizza il progetto con Gradle

5. Esegui l'app su un emulatore o dispositivo Android

## 📱 Screenshots

*(Aggiungi screenshot dell'app qui)*

## 🏗️ Struttura del Progetto

```
travel-companion/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/travelcompanion/
│   │   │   │   ├── data/          # Layer di dati (Room, Repository)
│   │   │   │   ├── domain/         # Modelli di dominio e repository interface
│   │   │   │   ├── ui/            # UI (Fragment, ViewModel, Activity)
│   │   │   │   └── utils/         # Utility (CameraHelper, TrackingService, ecc.)
│   │   │   ├── res/               # Risorse (layout, drawable, values)
│   │   │   └── AndroidManifest.xml
│   │   └── test/                  # Test unitari
│   └── build.gradle
├── gradle/
└── build.gradle
```

## 🎨 Design

L'app utilizza Material Design 3 con una palette di colori moderna e personalizzata. I componenti UI seguono le best practices di Material Design per garantire un'esperienza utente ottimale.

## 📝 Licenza

Questo progetto è stato sviluppato come parte di un progetto accademico.

## 👤 Autore

**Tesfaye**

- GitHub: [@tesfaye174](https://github.com/tesfaye174)

## 🙏 Ringraziamenti

- Google per Android Jetpack e Material Design
- La comunità Android per il supporto e le risorse

---

⭐ Se ti piace questo progetto, considera di lasciare una stella!
