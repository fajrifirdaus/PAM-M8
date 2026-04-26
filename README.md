# 📱 Notes App — Pertemuan 8: Platform-Specific Features

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile  
**Program Studi:** Teknik Informatika — Institut Teknologi Sumatera  
**Nama:** Muhammad Fajri Firdaus  
**NIM:** 123140050  
**Branch:** `week-8`

---

## 📋 Deskripsi

Upgrade dari Notes App minggu-minggu sebelumnya dengan mengintegrasikan **Platform-Specific Features** menggunakan Kotlin Multiplatform (KMP). Pertemuan 8 menambahkan:

- **Koin Dependency Injection** — seluruh dependency di-wire secara otomatis
- **DeviceInfo** via `expect/actual` — model, produsen, OS, jenis perangkat
- **NetworkMonitor** via `expect/actual` — status koneksi real-time dengan Flow
- **BatteryInfo** via `expect/actual` *(bonus +10%)* — level, charging status, polling otomatis

---

## ✅ Checklist Tugas

| # | Komponen | Status |
|---|----------|--------|
| 1 | Setup Koin Dependency Injection untuk seluruh app | ✅ |
| 2 | Implementasi `DeviceInfo` dengan `expect/actual` | ✅ |
| 3 | Implementasi `NetworkMonitor` dengan `expect/actual` | ✅ |
| 4 | Tampilkan Device Info di Settings screen | ✅ |
| 5 | Tampilkan Network Status indicator di main screen | ✅ |
| 6 | Semua dependencies di-inject melalui Koin | ✅ |
| 🌟 | **BONUS:** `BatteryInfo` `expect/actual` implementation | ✅ |

---

## 🏗️ Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│                        COMPOSE UI LAYER                            │
│                                                                    │
│  App.kt ──► AppNavigation ──► Screen Composables                   │
│                                    │                               │
│              NetworkStatusBanner   │   koinViewModel<T>()          │
│              (realtime banner)     │   koinInject<T>()             │
└────────────────────────────────────┼───────────────────────────────┘
                                     │
┌────────────────────────────────────▼───────────────────────────────┐
│                       VIEWMODEL LAYER                              │
│                                                                    │
│  NotesViewModel(                                                   │
│    repository: NoteRepository,         ← injected by Koin          │
│    settingsManager: SettingsManager,   ← injected by Koin          │
│    deviceInfo: DeviceInfo,             ← expect/actual             │
│    networkMonitor: NetworkMonitor,     ← expect/actual             │
│    batteryInfo: BatteryInfo            ← expect/actual (BONUS)     │
│  )                                                                 │
│                                                                    │
│  NotesUiState {                                                    │
│    isConnected, deviceName, osVersion,                             │
│    manufacturer, isTablet, batteryLevel,                           │
│    isCharging, batteryStatus, ...                                  │
│  }                                                                 │
└────────────────────────────────────────────────────────────────────┘
         │                    │                    │
┌────────▼──────┐  ┌──────────▼──────┐  ┌─────────▼───────────────┐
│  DATA LAYER   │  │  PLATFORM LAYER │  │     KOIN DI GRAPH        │
│               │  │   (expect/actual)│  │                          │
│ NoteRepository│  │                 │  │ androidPlatformModule()  │
│ NewsRepository│  │ commonMain:     │  │  ├─ DatabaseDriverFactory│
│ SettingsManager  │  expect class   │  │  └─ SettingsFactory      │
│               │  │  DeviceInfo()   │  │                          │
│ SQLDelight DB │  │  NetworkMonitor()│  │ appModule               │
│ DataStore     │  │  BatteryInfo()  │  │  ├─ AppDatabase          │
│ Ktor HTTP     │  │                 │  │  ├─ SettingsManager      │
└───────────────┘  │ androidMain:    │  │  ├─ HttpClientFactory    │
                   │  actual class   │  │  ├─ NewsApi              │
                   │  DeviceInfo {   │  │  ├─ ArticleCache         │
                   │   Build.MODEL   │  │  ├─ NoteRepository       │
                   │   Build.VERSION │  │  ├─ NewsRepository       │
                   │  }              │  │  ├─ DeviceInfo()         │
                   │                 │  │  ├─ NetworkMonitor()     │
                   │  actual class   │  │  ├─ BatteryInfo()        │
                   │  NetworkMonitor │  │  ├─ viewModelOf(::Notes..)│
                   │  { callbackFlow │  │  ├─ viewModelOf(::News..)│
                   │    ConnMgr }    │  │  └─ viewModelOf(::Profile)│
                   │                 │  └──────────────────────────┘
                   │  actual class   │
                   │  BatteryInfo {  │
                   │   BatteryMgr    │
                   │   batteryLevel  │
                   │  }              │
                   └─────────────────┘
```

### Alur Dependency Injection (Koin)

```
MyApplication.onCreate()
  │
  ├─ AndroidNetworkMonitorHelper.init(this)   ← init Context helper
  ├─ AndroidBatteryHelper.init(this)          ← init Context helper
  │
  └─ startKoin {
       androidLogger(Level.DEBUG)
       androidContext(this)
       modules(
         androidPlatformModule(),   ← DatabaseDriverFactory, SettingsFactory
         appModule                  ← semua dependency lainnya
       )
     }
```

### Alur expect/actual Pattern

```
commonMain/platform/
  DeviceInfo.kt       → expect class DeviceInfo() { ... }
  NetworkMonitor.kt   → expect class NetworkMonitor() { ... }
  BatteryInfo.kt      → expect class BatteryInfo() { ... }

androidMain/platform/
  DeviceInfo.android.kt      → actual class DeviceInfo { Build.MODEL, ... }
  NetworkMonitor.android.kt  → actual class NetworkMonitor { ConnectivityManager }
  BatteryInfo.android.kt     → actual class BatteryInfo { BatteryManager }
```

---

## 📁 Struktur Project

```
composeApp/src/
├── commonMain/kotlin/org/example/project/
│   ├── App.kt                          ← Entry point, koinViewModel()
│   ├── platform/
│   │   ├── DeviceInfo.kt               ← expect class DeviceInfo
│   │   ├── NetworkMonitor.kt           ← expect class NetworkMonitor
│   │   └── BatteryInfo.kt              ← expect class BatteryInfo (BONUS)
│   ├── di/
│   │   ├── AppModule.kt                ← Koin module definitions
│   │   ├── Factory.kt                  ← expect DatabaseDriverFactory, SettingsFactory
│   │   └── KoinHelper.kt              ← initKoin() untuk iOS/Desktop
│   ├── viewmodel/
│   │   ├── NotesViewModel.kt           ← inject DeviceInfo, NetworkMonitor, BatteryInfo
│   │   ├── NewsViewModel.kt
│   │   └── ProfileViewModel.kt
│   ├── components/
│   │   └── NetworkStatusBanner.kt      ← Animated offline/online banner
│   └── screens/
│       └── SettingsScreen.kt           ← Device Info + Battery display
│
├── androidMain/kotlin/org/example/project/
│   ├── MyApplication.kt                ← startKoin + init Context helpers
│   ├── platform/
│   │   ├── DeviceInfo.android.kt       ← actual: Build.MODEL, Build.VERSION
│   │   ├── NetworkMonitor.android.kt   ← actual: ConnectivityManager + callbackFlow
│   │   └── BatteryInfo.android.kt      ← actual: BatteryManager (BONUS)
│   └── di/
│       └── Factory.android.kt          ← actual: AndroidSqliteDriver, SharedPreferences
│
└── commonMain/sqldelight/
    └── org/example/project/db/
        ├── NoteEntity.sq
        └── ArticleEntity.sq
```

---

## 📸 Screenshot

### Device Info — Settings Screen

<img width="200" height="444" alt="Image" src="https://github.com/user-attachments/assets/0694a6ee-17a6-4aa9-884f-d712db2f56fb" />

### Network Status Indicator — Online (normal)

<img width="200" height="444" alt="image" src="https://github.com/user-attachments/assets/e5855b1b-0390-4df8-8c23-53920d55d3f7" />

### Network Status Indicator — Offline

<img width="200" height="444" alt="image" src="https://github.com/user-attachments/assets/3e41822d-8846-4d1a-be5c-6616d14a6e82" />


### Network Status Indicator — Kembali Online

<img width="200" height="444" alt="image" src="https://github.com/user-attachments/assets/615f7f99-5ab6-4c84-aeb8-336e7a5be3e8" />


---

## 🎬 Video Demo (45 detik)

> **Video Demonstrasi:** [Video ▶️](https://drive.google.com/file/d/1VzQCtVuMDzxr-IzX2RfYK64QHMP759az/view?usp=drivesdk)

### Skrip Demo (~45 detik):

| Waktu | Aksi | Yang Ditunjukkan |
|-------|------|-----------------|
| 0–5s | Buka app | App launch, Koin DI berhasil, tidak ada crash |
| 5–10s | Buka Settings screen | Device Info tampil: model, produsen, OS, jenis perangkat |
| 10–18s | Scroll ke Battery section | Level baterai realtime, badge status (Discharging / Charging) |
| 18–22s | Tap tombol Refresh ↻ | BatteryInfo di-poll ulang secara manual |
| 22–30s | Kembali ke home, aktifkan Airplane Mode | Banner merah "Tidak ada koneksi internet" muncul dengan animasi slide-in |
| 30–38s | Matikan Airplane Mode | Banner merah hilang → banner hijau "Koneksi pulih" muncul 2 detik |
| 38–45s | Scroll news list & notes | Fitur normal tetap bekerja, DI terbukti semua screen berjalan |

---

## 🔧 Implementasi Detail

### 1. Koin DI Setup

**`AppModule.kt`** (commonMain):
```kotlin
val appModule = module {
    // Database
    single<AppDatabase> { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<SettingsManager> { SettingsManager(get<SettingsFactory>().createSettings()) }

    // Networking
    single { HttpClientFactory.create() }
    single { NewsApi(get()) }
    single { ArticleCache() }

    // Repositories
    single { NoteRepository(get()) }
    single { NewsRepository(api = get(), cache = get()) }

    // Platform APIs (expect/actual)
    single { DeviceInfo() }
    single { NetworkMonitor() }
    single { BatteryInfo() }    // BONUS

    // ViewModels
    viewModelOf(::NotesViewModel)
    viewModelOf(::NewsViewModel)
    viewModelOf(::ProfileViewModel)
}
```

**`MyApplication.kt`** (androidMain):
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidNetworkMonitorHelper.init(this)  // init sebelum Koin
        AndroidBatteryHelper.init(this)          // init sebelum Koin
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            modules(androidPlatformModule(), appModule)
        }
    }
}
```

### 2. expect/actual — DeviceInfo

**commonMain:**
```kotlin
expect class DeviceInfo() {
    fun getDeviceName(): String
    fun getOsVersion(): String
    fun getAppVersion(): String
    fun getManufacturer(): String
    fun isTablet(): Boolean
}
```

**androidMain:**
```kotlin
actual class DeviceInfo {
    actual fun getDeviceName(): String = Build.MODEL
    actual fun getOsVersion(): String =
        "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    actual fun getAppVersion(): String = "1.0.0"
    actual fun getManufacturer(): String =
        Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
    actual fun isTablet(): Boolean {
        val metrics   = Resources.getSystem().displayMetrics
        val widthInch  = metrics.widthPixels  / metrics.xdpi
        val heightInch = metrics.heightPixels / metrics.ydpi
        return sqrt(widthInch.pow(2) + heightInch.pow(2)) >= 7.0f
    }
}
```

### 3. expect/actual — NetworkMonitor

**commonMain:**
```kotlin
expect class NetworkMonitor() {
    fun isConnected(): Boolean
    fun observeConnectivity(): Flow<Boolean>
}
```

**androidMain:**
```kotlin
actual class NetworkMonitor {
    actual fun isConnected(): Boolean = AndroidNetworkMonitorHelper.isConnected()
    actual fun observeConnectivity(): Flow<Boolean> =
        AndroidNetworkMonitorHelper.observeConnectivity()
}

object AndroidNetworkMonitorHelper {
    fun observeConnectivity(): Flow<Boolean> = callbackFlow {
        val cm = ctx.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        trySend(isConnected())
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(isConnected()) }
        }
        cm.registerNetworkCallback(request, callback)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
```

### 4. expect/actual — BatteryInfo (BONUS)

**commonMain:**
```kotlin
expect class BatteryInfo() {
    fun getBatteryLevel(): Int        // 0–100
    fun isCharging(): Boolean
    fun getBatteryStatus(): String    // "Charging" | "Discharging" | "Full" | "Unknown"
}
```

**androidMain:**
```kotlin
actual class BatteryInfo {
    actual fun getBatteryLevel(): Int {
        val bm = ctx.getSystemService(BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)
    }
    actual fun isCharging(): Boolean { ... }
    actual fun getBatteryStatus(): String { ... }
}
```

### 5. NetworkStatusBanner Composable

```kotlin
@Composable
fun NetworkStatusBanner(isConnected: Boolean, modifier: Modifier = Modifier) {
    // Offline → banner merah slide-in dari atas
    AnimatedVisibility(
        visible = !isConnected,
        enter   = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit    = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Surface(color = MaterialTheme.colorScheme.error) {
            Row { Icon(CloudOff); Text("Tidak ada koneksi internet") }
        }
    }

    // Online kembali → banner hijau muncul 2 detik
    AnimatedVisibility(visible = showOnlineBanner, ...) {
        Surface(color = MaterialTheme.colorScheme.tertiary) {
            Row { Icon(CloudQueue); Text("Koneksi pulih") }
        }
    }
}
```

---

## 📦 Dependencies (build.gradle.kts)

```kotlin
commonMain.dependencies {
    // Koin Core + Compose
    implementation("io.insert-koin:koin-core:3.5.3")
    implementation("io.insert-koin:koin-compose:1.1.2")
    implementation("io.insert-koin:koin-compose-viewmodel:1.1.2")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Ktor (Networking)
    implementation("io.ktor:ktor-client-core:2.3.9")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.9")

    // SQLDelight
    implementation("app.cash.sqldelight:runtime:2.0.2")

    // Multiplatform Settings (DataStore alternative)
    implementation("com.russhwolf:multiplatform-settings:1.1.1")
    implementation("com.russhwolf:multiplatform-settings-coroutines:1.1.1")
}

androidMain.dependencies {
    // Koin Android
    implementation("io.insert-koin:koin-android:3.5.3")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")

    // Ktor Android engine
    implementation("io.ktor:ktor-client-android:2.3.9")

    // SQLDelight Android driver
    implementation("app.cash.sqldelight:android-driver:2.0.2")
}
```

---

## 🚀 Cara Menjalankan

```bash
# Clone repository
git clone https://github.com/<username>/notes-app-kmp.git
cd notes-app-kmp
git checkout week-8

# Jalankan di Android (emulator/device)
./gradlew :composeApp:installDebug

# Atau buka di Android Studio → Run 'composeApp'
```

**Minimum Requirements:**
- Android SDK 24+
- Kotlin 1.9.22+
- Compose Multiplatform 1.6.x

---

## 🏆 Rubrik Penilaian

| Komponen | Bobot | Status |
|----------|-------|--------|
| Koin DI Setup — all dependencies properly injected | 25% | ✅ |
| expect/actual Pattern — DeviceInfo & NetworkMonitor implemented | 25% | ✅ |
| UI Integration — Device info & network indicator displayed | 20% | ✅ |
| Architecture — Clean separation, proper modules | 20% | ✅ |
| Code Quality — Clean code, documentation | 10% | ✅ |
| **BONUS:** BatteryInfo expect/actual implementation | +10% | ✅ |

---

## 📚 Referensi

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Koin Documentation](https://insert-koin.io/docs/quickstart/kotlin)
- [expect/actual Guide](https://kotlinlang.org/docs/multiplatform-connect-to-apis.html)
- [Accompanist Permissions](https://google.github.io/accompanist/permissions)

---

*Institut Teknologi Sumatera — Program Studi Teknik Informatika — 2025/2026*
