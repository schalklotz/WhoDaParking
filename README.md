# WhoDaParking 🚗

A production-ready Android app that identifies who a car belongs to by license plate using on-device OCR and local vehicle registry data.

[![Android CI](https://github.com/schalklotz/WhoDaParking/actions/workflows/android-ci.yml/badge.svg)](https://github.com/schalklotz/WhoDaParking/actions/workflows/android-ci.yml)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Features

- **Text Search**: Manually enter license plate numbers to find vehicle owners
- **Camera Search**: Take photos of license plates with automatic OCR text extraction
- **On-Device Processing**: All OCR and matching happens locally - no network calls
- **Fuzzy Matching**: Optional Levenshtein distance matching to handle OCR errors
- **Material 3 Design**: Modern UI with dynamic theming support
- **Accessibility**: Full support for screen readers and large text
- **Production Ready**: Comprehensive tests, CI/CD, and ProGuard configuration

## Screenshots

*Add screenshots here when available*

## Architecture

### Tech Stack
- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM + Repository Pattern
- **UI**: Jetpack Compose with Material 3
- **Navigation**: Navigation Compose
- **Camera**: CameraX
- **OCR**: ML Kit Text Recognition (on-device, Latin script)
- **JSON**: Kotlinx Serialization
- **DI**: Hilt
- **Concurrency**: Kotlin Coroutines & Flow
- **Testing**: JUnit 5, Robolectric, Espresso

### Project Structure
```
com.whodaparking.app/
├── data/
│   ├── model/VehicleRecord.kt           # Data models
│   ├── VehicleDataSource.kt             # JSON asset loading
│   └── VehicleRepository.kt             # Data layer with caching
├── ui/
│   ├── theme/                           # Material 3 theming
│   ├── components/ResultCard.kt         # Reusable UI components
│   ├── TextSearchScreen.kt              # Manual input screen
│   ├── CameraSearchScreen.kt            # Camera capture screen
│   └── SearchViewModel.kt               # Shared view model
├── ocr/
│   ├── OcrAnalyzer.kt                   # ML Kit wrapper
│   └── PlateHeuristics.kt               # License plate detection logic
├── util/
│   ├── Normalization.kt                 # String normalization utilities
│   └── Levenshtein.kt                   # Fuzzy matching algorithms
├── di/
│   └── AppModule.kt                     # Hilt dependency injection
├── navigation/
│   └── WhoDaParkingNavigation.kt        # Navigation setup
├── MainActivity.kt                       # Entry point
└── WhoDaParkingApplication.kt           # Application class
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or later
- JDK 17
- Android SDK with API level 34
- Device or emulator with camera support (for photo search)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/schalklotz/WhoDaParking.git
   cd WhoDaParking
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Choose "Open an existing project"
   - Select the `WhoDaParking` folder

3. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run tests**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest  # Requires connected device/emulator
   ```

5. **Install on device**
   ```bash
   ./gradlew installDebug
   ```

### Vehicle Data Setup

The app reads vehicle data from `app/src/main/assets/vehicle_records.json`. This file contains an array of vehicle records with the following schema:

```json
[
  {
    "Derivco Staff": "John Doe",
    "Vehicle MAKE": "Toyota",
    "Vehicle COLOUR": "White",
    "Vehicle REG": "ABC123GP"
  },
  {
    "Derivco Staff": "Jane Smith", 
    "Vehicle MAKE": "Honda",
    "Vehicle COLOUR": null,
    "Vehicle REG": "DEF456GP/GHI789GP"
  }
]
```

**Field Details:**
- `Derivco Staff`: Staff member name (string)
- `Vehicle MAKE`: Vehicle manufacturer (string)
- `Vehicle COLOUR`: Vehicle color (string or null)
- `Vehicle REG`: License plate(s) - supports multiple plates separated by `/`

**To update vehicle data:**
1. Replace the content of `app/src/main/assets/vehicle_records.json`
2. Rebuild and reinstall the app
3. The app will automatically load the new data on startup

## Usage

### Text Search
1. Open the app and ensure you're on the "Search by Text" tab
2. Enter a license plate number in the text field
3. Tap "Search" to find matching vehicles
4. View results showing staff name, vehicle make, color, and registration

### Camera Search
1. Switch to the "Search by Photo" tab
2. Grant camera permission when prompted
3. Point camera at a license plate and tap the capture button
4. The app will automatically detect and extract text from the photo
5. Edit the detected text if needed, then tap "Confirm & Search"
6. View results same as text search

### Data Normalization Rules

The app normalizes license plates for consistent matching:

1. **Convert to uppercase**: `abc123gp` → `ABC123GP`
2. **Remove spaces and hyphens**: `AB C-1 23GP` → `ABC123GP`
3. **Split multiple registrations**: `ABC123GP/DEF456GP` → `[ABC123GP, DEF456GP]`
4. **Trim whitespace**: `  ABC123GP  ` → `ABC123GP`

Both input and stored registrations follow these rules for matching.

## OCR Tips

For best OCR results when using camera search:

- **Good lighting**: Ensure the license plate is well-lit
- **Stable positioning**: Hold the device steady
- **Appropriate distance**: 1-3 feet from the plate works best
- **Clean plates**: Dirty or damaged plates may not scan well
- **Minimal angle**: Try to photograph plates straight-on

## Privacy & Security

- **No network calls**: All processing happens on-device
- **No data collection**: The app doesn't collect or transmit any personal data
- **Local storage only**: Vehicle data stays in the app's assets
- **Camera permissions**: Only requested when using camera features
- **No analytics**: No usage tracking or analytics are collected

## Development

### Building for Release

```bash
# Build release APK
./gradlew assembleRelease

# Build Android App Bundle (for Play Store)
./gradlew bundleRelease
```

### Code Quality

The project includes several code quality tools:

```bash
# Run linting
./gradlew lintDebug

# Run all tests
./gradlew test

# Check test coverage
./gradlew jacocoTestReport
```

### Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes and add tests
4. Ensure all tests pass: `./gradlew test`
5. Commit your changes: `git commit -m 'Add amazing feature'`
6. Push to the branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

Please follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages.

## Troubleshooting

### Common Issues

**App crashes on startup**
- Check that `vehicle_records.json` exists in `app/src/main/assets/`
- Verify the JSON format is valid
- Check logcat for specific error messages

**OCR not detecting plates**
- Ensure good lighting conditions
- Try different angles and distances
- Clean the camera lens
- Verify camera permissions are granted

**No search results found**
- Check that the plate exists in your vehicle data
- Verify normalization hasn't changed the format
- Try enabling fuzzy matching for slight variations

**Build failures**
- Ensure you're using JDK 17
- Clear Gradle cache: `./gradlew clean`
- Invalidate Android Studio caches: File → Invalidate and Restart

### Logs

Enable debug logging to troubleshoot issues:

```bash
# View app logs
adb logcat -s WhoDaParking
```

The app uses Timber for structured logging in debug builds.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [ML Kit](https://developers.google.com/ml-kit) for on-device text recognition
- [CameraX](https://developer.android.com/camerax) for camera functionality
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI
- [Hilt](https://dagger.dev/hilt/) for dependency injection
- [Material Design 3](https://m3.material.io/) for design system

---

**Note**: This app is designed for internal use within organizations to help identify vehicle owners in parking areas. Ensure you have appropriate permissions and comply with local privacy laws when using vehicle registration data.