You are an Android/Kotlin senior engineer. Build a complete, production-ready Android app called WhoDaParking that identifies who a car belongs to by license plate.

Summary / Goals
Input a registration plate by:

Typing it in a text field, or

Taking a photo of the plate and extracting text via on-device OCR.

Compare the plate (normalized) against registry data and display the matching Derivco Staff name (and basic vehicle info).

Ship a runnable project with CI, tests, and clear build/run instructions.

Initialize Git and push to https://github.com/schalklotz/WhoDaParking.

Data Source
Use a local JSON file at runtime (ship in assets/) named vehicle_records.json.

JSON schema (array of records):

"Derivco Staff" (string)

"Vehicle MAKE" (string)

"Vehicle COLOUR" (string or null)

"Vehicle REG" (string)

Load on app start into memory (use a repository with lazy caching).

Some rows can have multiple REGs in one field (e.g., "LZ43SCGP/CK95CMZN") or NaN for colour—handle gracefully.

Normalize comparisons by:

Uppercasing

Trimming spaces

Removing hyphens and internal spaces

Splitting multiple REGs by / and checking each

I will provide the JSON at app/src/main/assets/vehicle_records.json. Ensure the app reads from there.

Tech & Architecture
Language: Kotlin

Min SDK: 24; Target SDK: 34 (or latest stable)

Build system: Gradle (Android Gradle Plugin latest stable)

Architecture: MVVM + Repository

UI: Jetpack Compose

Navigation: Navigation Compose

Camera: CameraX

OCR: ML Kit Text Recognition (on-device, Latin)

JSON parsing: Moshi or Kotlinx Serialization

DI: Hilt

Concurrency: Kotlin Coroutines/Flows

Logging: Timber

Tests: JUnit5, Robolectric for unit tests; AndroidX Test + Espresso for instrumentation

App UX (Jetpack Compose)
Screen A: Search by Text

TextField: “Enter registration plate”

Button: “Search”

Validation (non-empty). On search, normalize and match.

Show result card:

Staff name (large)

Vehicle make + colour

Exact matched REG (as stored)

If multiple matches (e.g., same REG accidentally duplicated), list them.

If no result: show “No matching vehicle found.”

Screen B: Search by Photo

Live camera preview (CameraX) + shutter button

After capture, run ML Kit OCR

Auto-pick the most ‘plate-like’ candidate: longest contiguous alphanumeric (A–Z0–9) string length ≥ 5

Show detected string for user confirmation/edit

Button: “Search”

Display results same as Screen A

Bottom navigation (or top tabs) to switch between Text and Camera.

Matching Rules
Normalization pipeline (apply to both input and registry):

toUpperCase(Locale.ROOT)

Remove all spaces and hyphens: [\\s-]

For registry entries with multiple plates in one field (e.g., "A/B"), split on / and normalize each.

Optional fuzzy fallback (toggleable in code): Levenshtein distance ≤ 1 to catch OCR single-character errors—if multiple candidates tie, show a chooser list with confidence hints. Implement as a utility but off by default.

Modules & Files
Package: com.whodaparking.app

Project structure:

app/ Android app

app/src/main/assets/vehicle_records.json (place-holder; code expects it)

app/src/main/java/com/whodaparking/app/

MainActivity.kt (NavHost)

ui/ (Compose screens: TextSearchScreen, CameraSearchScreen, ResultCard, theming)

data/

model/VehicleRecord.kt

VehicleRepository.kt (loads + indexes data; provides findByReg(input: String): List<VehicleRecord>)

VehicleDataSource.kt (reads JSON from assets)

ocr/

OcrAnalyzer.kt (ML Kit wrapper)

PlateHeuristics.kt (extract best candidate, normalization, optional fuzziness)

di/ (Hilt modules)

util/Normalization.kt, util/Levenshtein.kt

Provide theming with Material 3 (dynamic color if available).

Permissions & Privacy
Request camera permission at runtime only on Camera tab.

All OCR runs on-device; no network calls for OCR or matching.

No analytics. Add a basic privacy note in the README.

Dependencies (Gradle)
Add latest stable versions for:

androidx.core:core-ktx

androidx.activity:activity-compose

androidx.compose:* BOM + Material3

androidx.navigation:navigation-compose

androidx.camera:camera-core, camera-camera2, camera-lifecycle, camera-view

ML Kit text recognition: com.google.mlkit:text-recognition (Latin)

com.squareup.moshi:moshi-kotlin or org.jetbrains.kotlinx:kotlinx-serialization-json

com.google.dagger:hilt-android + kapt compiler

com.jakewharton.timber:timber

Test deps: JUnit5, Robolectric, AndroidX Test, Espresso, Truth/AssertJ

Kotlin kapt/ksp as needed.

ProGuard / R8
Add rules for ML Kit & Moshi/Serialization to avoid obfuscation issues. Provide a minimal proguard-rules.pro.

Unit & UI Tests
VehicleRepositoryTest: JSON load, normalization, multi-plate splitting

NormalizationTest: exact normalization behavior

LevenshteinTest: sanity cases

OcrHeuristicsTest: plate extraction function from sample OCR blocks (mocked)

TextSearchScreenTest (Robolectric): typing + result states

Accessibility & UX Polish
Large tap targets, high contrast

Content descriptions on camera and result items

Snackbar for errors (e.g., camera unavailable)

Graceful empty/data load failures with retry

CI: GitHub Actions
Add .github/workflows/android-ci.yml:

Triggers on PR and push to main

Set up JDK, Gradle cache

Run ./gradlew ktlintCheck (if you add ktlint), ./gradlew test, and ./gradlew assembleDebug

Upload app/build/outputs/apk/debug/*.apk as a workflow artifact

Git Setup
Initialize Git

Create initial commit with generated project + README + license

Add remote origin set to https://github.com/schalklotz/WhoDaParking

Push main branch

Include a short Conventional Commits example and enable a simple .editorconfig

README.md
Include:

App overview & screenshots placeholders

How to build & run (Android Studio + CLI)

Where to put/replace vehicle_records.json

Data normalization rules

Troubleshooting OCR (lighting, distance)

Privacy note

License (MIT)

Implementation Tasks (do all)
Scaffold Android project with the above settings and dependencies.

Implement MVVM screens: TextSearchScreen and CameraSearchScreen.

Implement asset JSON loading with repository + in-memory index (map of normalized REG → list of records). Include multi-plate support.

Implement normalization + (optional) Levenshtein utilities.

Wire CameraX preview, capture to bitmap, pass to ML Kit OCR, extract best candidate with heuristics, allow user to edit before search.

Result rendering, multiple matches list, and “no match” state.

Add tests listed above.

Create CI workflow.

Initialize Git, set remote to https://github.com/schalklotz/WhoDaParking, push.

Write a clear README.md.

Deliverables
A complete Android Studio project under /WhoDaParking ready to open & run.

assets/vehicle_records.json placeholder and clear instructions.

CI workflow file.

Tests passing locally.

Git repo initialized and pushed.

Short demo GIF generation script (optional) using adb or gradlew connectedCheck.

Notes
Keep code idiomatic, documented, and small-functioned.

Prefer pure functions for normalization/matching to simplify tests.

Fail gracefully if JSON is missing or malformed (show a one-time dialog pointing to README).

Now generate the full project with all files, Gradle configs, code, tests, CI, and README. Include any shell commands (git and gradle) I need to run locally after you generate the project.