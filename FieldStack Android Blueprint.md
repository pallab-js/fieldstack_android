# **FieldStack Android — Comprehensive Development Blueprint**

*macOS Desktop → Android Mobile Counterpart | Kotlin | BudgetZen Design System*  
**Document Purpose**: One-stop guidance for AI-assisted development of the FieldStack Android app. Designed for vibecoding workflows on MacBook Air M1 (8GB RAM) with real-device testing via Gradle/ADB.

## **📋 Table of Contents**

1. [Project Vision & Core Concept](#bookmark=id.4himzrallj4y)  
2. [BudgetZen Design Adoption](#bookmark=id.fhpvs29997tl)  
3. [MVP Scope](#bookmark=id.pbzvsxl6j545)  
4. [PRD: Product Requirements](#bookmark=id.iqh5p9x7rlzu)  
5. [TRD: Technical Requirements](#bookmark=id.apo681979no4)  
6. [SDA: Software Design Architecture](#bookmark=id.flkc96qcavjh)  
7. [Development Workflow](#bookmark=id.xpbck63ov9xs)  
8. [Wireframes & UI Flow](#bookmark=id.irtn7rmbenrc)  
9. [Phased Implementation Plan](#bookmark=id.mfw92g6myqhz)  
10. [Testing & QA Strategy](#bookmark=id.p3lzz4cakg0v)  
11. [Deployment & Distribution](#bookmark=id.hfv457id4i22)  
12. [Appendix: Quick-Start Commands](#bookmark=id.mcd03sir6ivv)

## **Project Vision & Core Concept**

### **🎯 Core Idea**

**FieldStack** is a field operations management platform enabling teams to:

* Track field personnel, tasks, and assets in real-time  
* Log inspections, reports, and geotagged media offline-first  
* Sync data seamlessly when connectivity resumes  
* Visualize operational metrics via calm, encouraging dashboards

### **🔄 Desktop → Mobile Translation**

| Desktop (macOS/Tauri) | Android (Kotlin) |
| :---- | :---- |
| Rust backend logic | Kotlin Coroutines \+ ViewModel |
| Tauri window management | Android Activity/Fragment navigation |
| Next.js frontend | Jetpack Compose UI |
| Local SQLite via Rusqlite | Room Database |
| System tray notifications | Foreground service \+ WorkManager |

### **✨ Guiding Principles**

1. **Offline-First**: All core functions work without internet  
2. **Calm UX**: Adopt BudgetZen's anxiety-reducing design language  
3. **Lightweight**: Optimized for 8GB RAM development environment  
4. **Privacy-First**: Local-first data, explicit sync controls  
5. **Progressive**: MVP → Enhanced → Enterprise-ready phases

## **BudgetZen Design Adoption**

### **🎨 Color System Integration**

// res/values/colors.xml  
\<color name="primary"\>\#10B981\</color\>      \<\!-- Mint: CTAs, progress \--\>  
\<color name="secondary"\>\#38BDF8\</color\>    \<\!-- Sky: links, tips \--\>  
\<color name="tertiary"\>\#A8A29E\</color\>     \<\!-- Warm Gray: muted \--\>  
\<color name="neutral"\>\#78716C\</color\>      \<\!-- Stone: secondary text \--\>  
\<color name="background"\>\#FAFFFE\</color\>   \<\!-- App canvas \--\>  
\<color name="surface"\>\#FFFFFF\</color\>      \<\!-- Cards, modals \--\>  
\<color name="success"\>\#10B981\</color\>  
\<color name="warning"\>\#F59E0B\</color\>  
\<color name="error"\>\#EF4444\</color\>  
\<color name="info"\>\#38BDF8\</color\>

### **🔤 Typography Setup**

// Use Google Fonts via FontProvider  
val manrope \= FontFamily(Font(R.font.manrope\_extra\_bold, FontWeight.ExtraBold))  
val nunito \= FontFamily(Font(R.font.nunito\_regular, FontWeight.Normal))  
val sourceCodePro \= FontFamily(Font(R.font.source\_code\_pro\_regular))

// Type scale example  
Text(  
    text \= "Dashboard",  
    fontFamily \= manrope,  
    fontSize \= 28.sp,  
    fontWeight \= FontWeight.Bold,  
    lineHeight \= 33.6.sp, // 1.2x  
    letterSpacing \= 0.28.sp // 0.01em  
)

### **📐 Spacing & Elevation Tokens**

// Dimens.kt  
object Spacing {  
    val base \= 8.dp  
    val scale \= listOf(8, 16, 24, 32, 40, 48, 64, 80, 96).map { it.dp }  
    val componentPadding \= PaddingValues(horizontal \= 16.dp, vertical \= 12.dp)  
    val sectionSpacing \= 40.dp  
    val groupSpacing \= 24.dp  
}

object Radius {  
    val none \= 0.dp  
    val small \= 6.dp    // Chips  
    val medium \= 12.dp  // Buttons, cards  
    val large \= 16.dp   // Modals  
    val xl \= 24.dp      // Hero sections  
    val full \= 9999.dp  // Toggles  
}

object Elevation {  
    val subtle \= Shadow(  
        offset \= IntOffset(1, 3),  
        blurRadius \= 3.dp,  
        color \= Color.Black.copy(alpha \= 0.06f)  
    )  
    val medium \= Shadow(  
        offset \= IntOffset(4, 12),  
        blurRadius \= 12.dp,  
        color \= Color.Black.copy(alpha \= 0.08f)  
    )  
    // ... large, overlay variants  
}

### **🧩 Component Guidelines (BudgetZen-Aligned)**

| Component | BudgetZen Rule | Android Implementation |
| :---- | :---- | :---- |
| **Primary Button** | \#10B981 fill, white text, hover \#059669 | Button(colors \= ButtonDefaults.buttonColors(containerColor \= primary)) |
| **Card** | 12px radius, subtle shadow, hover elevation | Surface(shape \= RoundedCornerShape(12.dp), shadowElevation \= 2.dp) |
| **Input Field** | 44px height, 1.5px border, focus ring \#10B981 | OutlinedTextField(modifier \= Modifier.height(44.dp), colors \= textFieldColors(...)) |
| **Chip** | 6px radius, soft bg, active state mint tint | FilterChip(selected, onClick, label, colors \= chipColors(...)) |
| **Progress Bar** | Celebrate milestones with subtle animation | LinearProgressIndicator(progress, color \= primary, trackColor \= tertiary) \+ confetti on 100% |

### **✅ BudgetZen Do's for FieldStack Android**

* ✅ Use progress bars for task completion, sync status, goal tracking  
* ✅ Encouraging microcopy: "✅ 3/5 inspections done — great progress\!"  
* ✅ Highlight offline mode with calm sky-blue badge, not alarming red  
* ✅ Use Sky (\#38BDF8) for tips: "💡 Tip: Add photos to reports for richer context"  
* ✅ Progressive disclosure: Collapse advanced filters by default  
* ✅ One-glance dashboard: Today's tasks, sync status, quick-add FAB

### **❌ BudgetZen Don'ts**

* ❌ No red-heavy "over budget" patterns for minor delays  
* ❌ No auto-expanded forms; let users drill in intentionally  
* ❌ No hiding sync status behind menus — always visible in app bar  
* ❌ No overwhelming data tables on mobile; use card-based lists

## **MVP Scope**

### **🎯 MVP Definition (Phase 1\)**

*Minimum lovable product for field workers \+ supervisors*

#### **Core User Stories**

Feature: Field Task Management  
  As a field worker  
  I want to view my assigned tasks offline  
  So I can work without connectivity

Feature: Offline Report Logging  
  As a field worker    
  I want to create inspection reports with photos  
  So I can submit them when back online

Feature: Sync Status Visibility  
  As any user  
  I want to see real-time sync state  
  So I trust my data is safe

Feature: Calm Dashboard  
  As a supervisor  
  I want a BudgetZen-styled overview  
  So I feel informed, not anxious

#### **MVP Feature Matrix**

| Module | Included | Notes |
| :---- | :---- | :---- |
| 🔐 Auth | ✅ Email/password \+ biometric | Local token storage |
| 📋 Task List | ✅ Offline-first list, filter, search | Room DB \+ paging |
| 📝 Report Builder | ✅ Text, photo, location, signature | CameraX \+ ExifInterface |
| 🔄 Sync Engine | ✅ Manual \+ auto-sync when online | WorkManager \+ Retrofit |
| 📊 Dashboard | ✅ Today's tasks, sync badge, quick stats | BudgetZen cards \+ progress |
| ⚙️ Settings | ✅ Profile, sync preferences, theme | Encrypted SharedPreferences |
| 🔔 Notifications | ✅ Task reminders, sync complete | Foreground service \+ FCM (optional) |

#### **Out of Scope (Post-MVP)**

* Real-time team chat  
* Advanced analytics/export  
* Multi-organization support  
* Custom form builder  
* Barcode/QR scanning

## **PRD: Product Requirements**

### **👥 User Personas**

| Persona | Role | Key Needs |
| :---- | :---- | :---- |
| **Alex (Field Tech)** | Frontline worker | Offline access, quick logging, low cognitive load |
| **Jordan (Supervisor)** | Team lead | Team overview, progress tracking, gentle nudges |
| **Taylor (Admin)** | Ops manager | Data integrity, export, user management |

### **📱 User Journey Map**

\[Launch App\]   
   ↓  
\[Auth/Biometric\] → \[Dashboard: Today's Tasks\]   
   ↓  
\[Select Task\] → \[Task Detail \+ Start Button\]   
   ↓  
\[Log Report: Text/Photo/Location/Signature\]   
   ↓  
\[Save Draft / Submit\] → \[Sync Queue Indicator\]   
   ↓  
\[Background Sync\] → \[Success Celebration 🎉\]

### **🎯 Success Metrics (MVP)**

| Metric | Target | Measurement |
| :---- | :---- | :---- |
| Task completion rate | \+25% vs baseline | Analytics event tracking |
| Offline report success | 99.9% local save | Room DB transaction logs |
| Sync reliability | \<2% failure rate | WorkManager constraints \+ retry |
| User satisfaction (CSAT) | ≥4.5/5 | In-app micro-survey |
| Cold start time | \<1.8s on mid-tier device | Android Vitals \+ Perfetto |

### **🚫 Non-Goals**

* Replace desktop FieldStack parity in MVP  
* Support Android \< 8.0 (API 26\)  
* Real-time collaboration features  
* Advanced reporting/analytics engine

## **TRD: Technical Requirements**

### **🛠 Tech Stack**

| Layer | Technology | Rationale |
| :---- | :---- | :---- |
| **Language** | Kotlin 1.9+ | Official Android language, coroutine support |
| **UI Framework** | Jetpack Compose 1.5+ | Declarative, BudgetZen theming friendly |
| **Architecture** | MVVM \+ Repository | Clean separation, testable, Android best practice |
| **Local DB** | Room 2.6+ | SQLite abstraction, coroutine support, migration tools |
| **Network** | Retrofit 2.9 \+ OkHttp 4.11 | Industry standard, interceptor flexibility |
| **Async** | Kotlin Coroutines \+ Flow | Structured concurrency, backpressure handling |
| **DI** | Hilt 2.48+ | Compile-time DI, Android-optimized |
| **Background** | WorkManager 2.8+ | Guaranteed deferrable background sync |
| **Auth** | AndroidX Credentials \+ BiometricPrompt | Modern auth, hardware-backed security |
| **Media** | CameraX 1.3+ | Consistent camera across devices |
| **Location** | FusedLocationProviderClient | Battery-efficient, accurate location |
| **Testing** | JUnit5, Mockk, Compose Testing | Fast, reliable unit/UI tests |

### **📦 Project Structure (Modular)**

app/  
├── build.gradle.kts          \# Application module  
├── src/main/  
│   ├── AndroidManifest.xml  
│   ├── java/com/fieldstack/android/  
│   │   ├── FieldStackApp.kt          \# Application class \+ Hilt setup  
│   │   ├── ui/  
│   │   │   ├── theme/                \# BudgetZen Theme.kt, Type.kt, Color.kt  
│   │   │   ├── components/           \# Reusable BudgetZen components  
│   │   │   ├── dashboard/            \# Dashboard screen  
│   │   │   ├── tasks/                \# Task list/detail flows  
│   │   │   ├── reports/              \# Report builder  
│   │   │   ├── sync/                 \# Sync status UI  
│   │   │   └── settings/             \# Preferences  
│   │   ├── data/  
│   │   │   ├── local/                \# Room DAOs, entities, converters  
│   │   │   ├── remote/               \# Retrofit services, DTOs  
│   │   │   ├── repository/           \# Unified data source  
│   │   │   └── model/                \# Domain models  
│   │   ├── domain/  
│   │   │   ├── usecase/              \# Business logic (suspend functions)  
│   │   │   └── model/                \# Pure domain objects  
│   │   ├── di/                       \# Hilt modules  
│   │   ├── util/                     \# Extensions, helpers, constants  
│   │   └── worker/                   \# WorkManager workers  
│   └── res/  
│       ├── values/colors.xml         \# BudgetZen color tokens  
│       ├── values/typography.xml     \# Font definitions  
│       ├── values/dimens.xml         \# Spacing/elevation tokens  
│       ├── font/                     \# Manrope, Nunito, Source Code Pro  
│       └── drawable/                 \# BudgetZen icons, shapes  
├── build-logic/                      \# Convention plugins (optional)  
└── gradle.properties                 \# org.gradle.jvmargs=-Xmx2g (M1 optimization)

### **⚙️ Build Configuration (M1 8GB Optimized)**

\# gradle.properties  
org.gradle.jvmargs=-Xmx2048m \-Dfile.encoding=UTF-8 \-XX:+UseParallelGC  
org.gradle.parallel=true  
org.gradle.caching=true  
org.gradle.configuration-cache=true  
android.useAndroidX=true  
android.nonTransitiveRClass=true  
kotlin.code.style=official

// app/build.gradle.kts (key snippets)  
android {  
    namespace \= "com.fieldstack.android"  
    compileSdk \= 34  
      
    defaultConfig {  
        applicationId \= "com.fieldstack.android"  
        minSdk \= 26  // Android 8.0+  
        targetSdk \= 34  
        versionCode \= 1  
        versionName \= "0.1.0-mvp"  
          
        // M1 optimization: limit ABI splits during dev  
        ndk {  
            abiFilters \+= listOf("arm64-v8a") // Skip x86\_64 for real-device testing  
        }  
    }  
      
    buildTypes {  
        release {  
            isMinifyEnabled \= true  
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")  
        }  
    }  
      
    // Compose \+ Kotlin config  
    buildFeatures { compose \= true }  
    composeOptions { kotlinCompilerExtensionVersion \= "1.5.3" }  
    kotlinOptions { jvmTarget \= "17" }  
}

dependencies {  
    // Core  
    implementation("androidx.core:core-ktx:1.12.0")  
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")  
      
    // Compose  
    val composeBom \= platform("androidx.compose:compose-bom:2024.02.00")  
    implementation(composeBom)  
    implementation("androidx.compose.ui:ui")  
    implementation("androidx.compose.material3:material3") // BudgetZen theming base  
    implementation("androidx.compose.ui:ui-tooling-preview")  
      
    // Navigation  
    implementation("androidx.navigation:navigation-compose:2.7.7")  
      
    // Data  
    implementation("androidx.room:room-runtime:2.6.1")  
    ksp("androidx.room:room-compiler:2.6.1")  
    implementation("androidx.room:room-ktx:2.6.1")  
    implementation("com.squareup.retrofit2:retrofit:2.9.0")  
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")  
      
    // DI  
    implementation("com.google.dagger:hilt-android:2.48.1")  
    ksp("com.google.dagger:hilt-compiler:2.48.1")  
      
    // Background  
    implementation("androidx.work:work-runtime-ktx:2.9.0")  
      
    // Auth/Media/Location  
    implementation("androidx.credentials:credentials:1.2.0")  
    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha05")  
    implementation("androidx.camera:camera-camera2:1.3.1")  
    implementation("com.google.android.gms:play-services-location:21.1.0")  
      
    // Testing  
    testImplementation("junit:junit:4.13.2")  
    androidTestImplementation("androidx.test.ext:junit:1.1.5")  
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")  
}

### **🔐 Security Requirements**

1. **Data at Rest**: Room DB encrypted via SQLCipher (optional for MVP, add in Phase 2\)  
2. **Data in Transit**: TLS 1.3 \+ certificate pinning (Retrofit OkHttp interceptor)  
3. **Auth Tokens**: Stored in EncryptedSharedPreferences (AndroidX Security)  
4. **Biometric Auth**: Fingerprint/face unlock for app re-entry  
5. **Permissions**: Runtime requests with clear rationale (location, camera, storage)

### **🌐 Offline-First Sync Strategy**

// Repository pattern with conflict resolution  
interface TaskRepository {  
    // Local operations (always available)  
    suspend fun getTasks(): Flow\<List\<Task\>\>  
    suspend fun saveReport(report: Report): Result\<Unit\>  
      
    // Sync operations (network-aware)  
    suspend fun syncPendingChanges(): SyncResult  
    suspend fun pullLatestUpdates(): Result\<Unit\>  
}

// WorkManager sync worker  
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {  
    override suspend fun doWork(): Result {  
        return try {  
            repository.syncPendingChanges()  
            repository.pullLatestUpdates()  
            Result.success()  
        } catch (e: NetworkException) {  
            // Exponential backoff retry  
            Result.retry()  
        }  
    }  
}

## **SDA: Software Design Architecture**

### **🏗 High-Level Architecture Diagram**

┌─────────────────────────────────────┐  
│            UI Layer                 │  
│  ┌─────────┬─────────┬─────────┐    │  
│  │Dashboard│ Tasks   │ Reports │    │  
│  │Screen   │ Screen  │ Screen  │    │  
│  └────┬────┴────┬────┴────┬────┘    │  
│       │         │         │         │  
│  ┌────▼────┐┌──▼───┐┌───▼────┐      │  
│  │ViewModel││VM    ││VM      │      │  
│  │(Compose ││(Task ││(Report │      │  
│  │ State)  ││List) ││Builder)│      │  
│  └────┬────┘└──┬───┘└───┬────┘      │  
└───────┼────────┼────────┼───────────┘  
        │        │        │  
┌───────▼────────▼────────▼───────────┐  
│        Domain Layer                 │  
│  ┌─────────────────────────┐        │  
│  │ UseCases:               │        │  
│  │ • GetTodayTasks()       │        │  
│  │ • SubmitReport()        │        │  
│  │ • SyncData()            │        │  
│  └────────┬────────────────┘        │  
│           │                         │  
└───────────┼─────────────────────────┘  
            │  
┌───────────▼─────────────────────────┐  
│          Data Layer                 │  
│  ┌─────────────┬─────────────────┐  │  
│  │ Local Source│ Remote Source   │  │  
│  │ • Room DAO  │ • Retrofit API  │  │  
│  │ • DataStore │ • WebSocket?    │  │  
│  └──────┬──────┴──────┬──────────┘  │  
│         │             │             │  
│  ┌──────▼─────────────▼─────────┐   │  
│  │   Repository (Single Source) │   │  
│  │   • Merge local \+ remote     │   │  
│  │   • Conflict resolution      │   │  
│  │   • Offline queue management │   │  
│  └──────────────────────────────┘   │  
└─────────────────────────────────────┘

### **🔄 Data Flow: Report Submission (Offline Example)**

sequenceDiagram  
    participant UI as Compose Screen  
    participant VM as ReportViewModel  
    participant UC as SubmitReportUseCase  
    participant Repo as TaskRepository  
    participant DB as Room Database  
    participant WM as WorkManager  
    participant API as Remote Backend

    UI-\>\>VM: onReportSubmit(report)  
    VM-\>\>UC: execute(report)  
    UC-\>\>Repo: saveReportLocally(report)  
    Repo-\>\>DB: insert(report) \[Transaction\]  
    DB--\>\>Repo: Success  
    Repo--\>\>UC: Result.Success  
    UC-\>\>Repo: queueForSync(report.id)  
    Repo-\>\>WM: enqueue(SyncWorker)  
    WM--\>\>UC: Worker scheduled  
    UC--\>\>VM: Result.Success(local)  
    VM-\>\>UI: Show "Saved • Pending sync" chip  
      
    par Background Sync (when online)  
        WM-\>\>Repo: syncPendingChanges()  
        Repo-\>\>API: POST /reports (batch)  
        API--\>\>Repo: 200 OK \+ server IDs  
        Repo-\>\>DB: updateLocalWithServerIds()  
        Repo-\>\>WM: markSynced(report.id)  
        WM-\>\>UI: Broadcast sync complete  
        UI-\>\>User: Show BudgetZen success animation 🎉  
    end

### **🧩 Key Component Interfaces**

// Domain: Use cases (pure business logic)  
interface GetTodayTasksUseCase {  
    suspend operator fun invoke(userId: String): Flow\<List\<Task\>\>  
}

interface SubmitReportUseCase {  
    suspend operator fun invoke(report: Report): Result\<SyncStatus\>  
}

// Data: Repository contract  
interface FieldStackRepository {  
    // Local  
    fun observeTasks(userId: String): Flow\<List\<Task\>\>  
    suspend fun insertReport(report: Report): Long  
      
    // Sync  
    suspend fun syncPendingReports(): SyncResult  
    suspend fun refreshTasks(userId: String): Result\<Unit\>  
      
    // Utils  
    fun isOnline(): Flow\<Boolean\>  
    fun getSyncStatus(): Flow\<SyncState\>  
}

// UI: ViewModel (Compose-friendly)  
@HiltViewModel  
class DashboardViewModel @Inject constructor(  
    private val getTodayTasks: GetTodayTasksUseCase,  
    private val repository: FieldStackRepository  
) : ViewModel() {  
      
    private val \_uiState \= MutableStateFlow(DashboardUiState())  
    val uiState: StateFlow\<DashboardUiState\> \= \_uiState.asStateFlow()  
      
    init {  
        // Combine tasks \+ sync status \+ offline state  
        combine(  
            getTodayTasks(currentUserId),  
            repository.getSyncStatus(),  
            repository.isOnline()  
        ) { tasks, sync, online \-\>  
            DashboardUiState(  
                tasks \= tasks,  
                syncBadge \= when {  
                    \!online \-\> SyncBadge.Offline  
                    sync.hasPending \-\> SyncBadge.Pending(sync.pendingCount)  
                    else \-\> SyncBadge.Synced  
                }  
            )  
        }.collectIn(viewModelScope) { \_uiState.value \= it }  
    }  
      
    fun onQuickAddTask() { /\* navigation event \*/ }  
    fun onRefresh() { viewModelScope.launch { repository.refreshTasks(...) } }  
}

### **🎨 BudgetZen Theme Implementation**

// ui/theme/BudgetZenTheme.kt  
private val LightColorScheme \= lightColorScheme(  
    primary \= Color(0xFF10B981),  
    secondary \= Color(0xFF38BDF8),  
    tertiary \= Color(0xFFA8A29E),  
    background \= Color(0xFFFAFFFE),  
    surface \= Color(0xFFFFFFFF),  
    error \= Color(0xFFEF4444),  
    onPrimary \= Color.White,  
    onSecondary \= Color.White,  
    // ... full BudgetZen mapping  
)

@Composable  
fun BudgetZenTheme(  
    darkTheme: Boolean \= isSystemInDarkTheme(),  
    content: @Composable () \-\> Unit  
) {  
    val colors \= if (\!darkTheme) LightColorScheme else DarkColorScheme  
    val typography \= BudgetZenTypography // Manrope/Nunito setup  
      
    CompositionLocalProvider(  
        LocalElevation provides BudgetZenElevation,  
        LocalSpacing provides Spacing  
    ) {  
        MaterialTheme(  
            colorScheme \= colors,  
            typography \= typography,  
            content \= content  
        )  
    }  
}

// Usage in any screen  
@Composable  
fun DashboardScreen(viewModel: DashboardViewModel \= hiltViewModel()) {  
    BudgetZenTheme {  
        Scaffold(  
            containerColor \= MaterialTheme.colorScheme.background,  
            topBar \= { /\* BudgetZen-styled AppBar \*/ }  
        ) { padding \-\>  
            // Content with BudgetZen spacing tokens  
            Column(modifier \= Modifier.padding(padding).padding(Spacing.sectionSpacing)) {  
                // ...  
            }  
        }  
    }  
}

## **Development Workflow**

### **🔄 Vibecoding-Optimized Loop (M1 8GB)**

\[Edit Compose UI\]   
   ↓  
\[./gradlew :app:assembleDebug \-x lint \-x test\]  \# Skip heavy tasks during dev  
   ↓  (\~45-90s cold, \~15s incremental)  
\[adb install \-r app/build/outputs/apk/debug/app-debug.apk\]  
   ↓  (\~8-12s over USB)  
\[Test on physical device\]   
   ↓  
\[Logcat filter: "FieldStack"\] \+ Compose Layout Inspector  
   ↓  
\[Iterate\]

### **🛠 M1 8GB Performance Tips**

1. **Gradle Daemon**: Keep running; avoid \--stop during sessions  
2. **Build Cache**: Enable org.gradle.configuration-cache=true  
3. **ABI Filtering**: Build only arm64-v8a for real-device testing  
4. **Compose Compiler**: Use kotlinCompilerExtensionVersion matching Compose BOM  
5. **Emulator Alternative**: Prefer real device via USB debugging (faster deploy)  
6. **Memory Management**: Close Android Studio previews when not editing UI  
7. **Incremental Builds**: Use \--configuration-cache and avoid clean builds

### **📦 Dependency Management Strategy**

\# gradle/libs.versions.toml (recommended)  
\[versions\]  
compose-bom \= "2024.02.00"  
room \= "2.6.1"  
retrofit \= "2.9.0"  
hilt \= "2.48.1"

\[libraries\]  
androidx-compose-bom \= { group \= "androidx.compose", name \= "compose-bom", version.ref \= "compose-bom" }  
androidx-room-runtime \= { group \= "androidx.room", name \= "room-runtime", version.ref \= "room" }  
\# ... centralized versions prevent conflicts

\[plugins\]  
android-application \= { id \= "com.android.application", version \= "8.2.2" }  
kotlin-android \= { id \= "org.jetbrains.kotlin.android", version \= "1.9.22" }  
hilt \= { id \= "com.google.dagger.hilt.android", version.ref \= "hilt" }

### **🧪 Testing Strategy (Phased)**

| Phase | Unit Tests | Instrumented | UI Tests | Coverage Target |
| :---- | :---- | :---- | :---- | :---- |
| MVP | Repository, UseCase | SyncWorker, Auth | Critical flows only | 60%+ |
| Enhanced | \+ ViewModel | \+ Room migrations | \+ Edge cases | 75%+ |
| Enterprise | Full domain | \+ Performance tests | Visual regression | 85%+ |

// Example: Repository test with Mockk  
@Test  
fun \`submitReport saves locally and queues sync\`() \= runTest {  
    // Given  
    val mockDao \= mockk\<TaskDao\>()  
    val mockApi \= mockk\<FieldStackApi\>()  
    val repository \= FieldStackRepositoryImpl(mockDao, mockApi, mockk())  
    val report \= testReport()  
      
    coEvery { mockDao.insertReport(any()) } returns 1L  
    coEvery { mockApi.submitReport(any()) } returns SubmitResponse("srv\_123")  
      
    // When  
    val result \= repository.submitReport(report)  
      
    // Then  
    coVerify { mockDao.insertReport(match { it.id \== report.id }) }  
    coVerify { mockWorkManager.enqueue(any\<SyncWorker\>()) }  
    assertTrue(result.isSuccess)  
}

## **Wireframes & UI Flow**

### **🗺️ Information Architecture**

FieldStack Android  
├─ 🔐 Auth Flow  
│  ├─ Login (Email \+ Password)  
│  ├─ Biometric Prompt (optional)  
│  └─ Onboarding (3-screen calm intro)  
│  
├─ 🏠 Dashboard (Home)  
│  ├─ Sync Status Badge (top app bar)  
│  ├─ Today's Progress Card (BudgetZen progress bar)  
│  ├─ Quick Actions FAB (+ Task / \+ Report)  
│  └─ Recent Activity List (card-based)  
│  
├─ 📋 Tasks Module  
│  ├─ Task List (filter: Today / Pending / Completed)  
│  ├─ Task Detail (description, location, attachments)  
│  ├─ Start Task → Report Builder flow  
│  └─ Bulk actions (select \+ complete)  
│  
├─ 📝 Report Builder  
│  ├─ Step 1: Basic Info (title, category, priority)  
│  ├─ Step 2: Details (rich text, BudgetZen-styled editor)  
│  ├─ Step 3: Media (CameraX preview \+ gallery)  
│  ├─ Step 4: Location (map preview \+ manual override)  
│  ├─ Step 5: Signature (canvas draw \+ clear)  
│  └─ Review & Submit (offline-aware CTA)  
│  
├─ 🔄 Sync Center  
│  ├─ Pending Items List (with retry/delete)  
│  ├─ Last Sync Timestamp  
│  ├─ Manual Sync Button (with loading animation)  
│  └─ Network Status Helper (tips for weak signal)  
│  
└─ ⚙️ Settings  
   ├─ Profile & Preferences  
   ├─ Sync Preferences (Wi-Fi only toggle)  
   ├─ Privacy & Data (export/delete)  
   └─ About \+ BudgetZen Design Credit

### **🎨 Key Screen Wireframes (Text-Based)**

#### **Dashboard (BudgetZen Style)**

┌─────────────────────────────────┐  
│ 🟢 Synced • 2 min ago      \[⚙\] │ ← AppBar (Sky badge when offline)  
├─────────────────────────────────┤  
│ 👋 Hi, Alex                     │  
│                                 │  
│ 🎯 Today's Progress             │  
│ ┌─────────────────────────┐    │  
│ │ ████████░░░░░░ 60%     │    │ ← Mint progress bar  
│ │ 3/5 tasks completed    │    │  
│ │ ✨ Great progress\!     │    │ ← Encouraging microcopy  
│ └─────────────────────────┘    │  
│                                 │  
│ 📋 Your Tasks                   │  
│ ┌─────────────────────────┐    │  
│ │ 🔍 Inspect Site A      \[\>\]│    │ ← Card: BudgetZen radius \+ shadow  
│ │ 📍 Downtown • Due 2PM   │    │  
│ │ 🟡 In Progress          │    │  
│ └─────────────────────────┘    │  
│ ┌─────────────────────────┐    │  
│ │ 📸 Document Equipment  \[\>\]│    │  
│ │ 📍 Warehouse • Due 4PM  │    │  
│ │ ⚪ Not Started          │    │  
│ └─────────────────────────┘    │  
│                                 │  
│ \[ \+ \]  ← FAB (Primary mint, XL) │  
└─────────────────────────────────┘

#### **Report Builder (Step 3: Media)**

┌─────────────────────────────────┐  
│ ← Add Photos              \[✓\]  │ ← AppBar: Back \+ Save (disabled until valid)  
├─────────────────────────────────┤  
│ 📸 Attach Media                 │  
│                                 │  
│ ┌───────┐ ┌───────┐ ┌───────┐  │  
│ │  \[+\]  │ │ \[🖼\] │ │ \[🖼\] │  │ ← BudgetZen cards (12px radius)  
│ │ Add   │ │ Img1  │ │ Img2  │  │  
│ │ Photo │ │ 2.1MB │ │ 1.8MB │  │  
│ └───────┘ └───────┘ └───────┘  │  
│                                 │  
│ 💡 Tip: Photos help verify     │ ← Sky-colored info chip  
│    inspection conditions       │  
│                                 │  
│ ┌─────────────────────────┐    │  
│ │ 🗺️ Location: Auto-detected │    │  
│ │ 📍 40.7128° N, 74.0060° W│    │  
│ │ \[Change\] \[Use Current\]   │    │  
│ └─────────────────────────┘    │  
│                                 │  
│ \[ Save Draft \]  \[ Next → \]     │ ← Secondary \+ Primary buttons  
└─────────────────────────────────┘

#### **Sync Status (Offline State)**

┌─────────────────────────────────┐  
│ 📴 Offline • Changes saved locally │ ← Sky badge (not alarming red)  
├─────────────────────────────────┤  
│ 🔄 Sync Queue (3 items)         │  
│                                 │  
│ ┌─────────────────────────┐    │  
│ │ ✅ Report: Site A      \[🗑\]│    │ ← Soft success tint bg  
│ │    Saved 2:14 PM       │    │  
│ ├─────────────────────────┤    │  
│ │ ⏳ Report: Equipment   \[🗑\]│    │ ← Warm gray pending state  
│ │    Draft • 2:16 PM     │    │  
│ ├─────────────────────────┤    │  
│ │ ⏳ Task: Update Logs   \[🗑\]│    │  
│ │    Draft • 2:17 PM     │    │  
│ └─────────────────────────┘    │  
│                                 │  
│ 💡 Connect to Wi-Fi for faster │ ← Sky tip  
│    sync when available         │  
│                                 │  
│ \[ 🔄 Sync Now \]  \[ Settings \]  │ ← Ghost \+ Secondary buttons  
└─────────────────────────────────┘

### **🎬 Micro-Interactions (BudgetZen-Aligned)**

1. **Task Completion**: Gentle scale-up animation \+ mint confetti burst (Lottie)  
2. **Sync Success**: Progress bar fills → checkmark morph → subtle bounce  
3. **Offline Detection**: AppBar badge slides in with soft fade (no jarring alert)  
4. **Form Validation**: Field border animates to mint (success) or soft amber (warning), never harsh red  
5. **Loading States**: Skeleton screens with gentle pulse animation (BudgetZen subtle elevation)

## **Phased Implementation Plan**

### **🗓️ Phase 1: MVP Foundation (Weeks 1-4)**

**Goal**: Offline-capable task/report core with BudgetZen UI shell

| Week | Deliverables | Success Criteria |
| :---- | :---- | :---- |
| **W1** | • Project setup \+ BudgetZen theme • Auth flow (email \+ biometric) • Room DB schema (Task, Report) | ✅ App launches with themed UI ✅ Login persists via EncryptedSharedPreferences |
| **W2** | • Task list screen (offline) • Task detail \+ start flow • Basic report form (text only) | ✅ Can view/create tasks offline ✅ Room transactions pass instrumentation tests |
| **W3** | • Report media capture (CameraX) • Location attachment (FusedLocation) • Sync queue architecture | ✅ Photo reports save locally ✅ WorkManager schedules sync worker |
| **W4** | • Dashboard with progress cards • Sync status badge \+ manual trigger • MVP polish \+ bug bash | ✅ End-to-end offline report flow ✅ BudgetZen microcopy throughout ✅ \<2s cold start on test device |

**MVP Exit Criteria**:

* \[ \] All core user stories pass acceptance testing  
* \[ \] Offline report success rate ≥99.5% in field simulation  
* \[ \] Lighthouse-style perf score ≥85 (Compose metrics)  
* \[ \] Zero critical security findings (MobSF scan)

### **🚀 Phase 2: Enhanced Experience (Weeks 5-8)**

**Goal**: Rich interactions, team features, analytics foundation

| Focus Area | Key Features | BudgetZen Integration |
| :---- | :---- | :---- |
| **Collaboration** | • Task assignment comments • @mentions in reports • Team progress dashboard | Sky-colored callouts for team tips; progress bars for group goals |
| **Rich Reporting** | • Custom form fields • Barcode/QR scanning • PDF export (local) | Calm form progression; success celebrations on export complete |
| **Smart Sync** | • Wi-Fi only toggle • Background sync optimization • Conflict resolution UI | Gentle notifications; "Syncing in background" subtle badge |
| **Insights Lite** | • Weekly completion summary • Time-on-task metrics • Export CSV | Mint/warning/error chips for performance; no guilt-inducing red dashboards |

### **🌟 Phase 3: Enterprise Ready (Weeks 9-12)**

**Goal**: Scalability, admin controls, advanced integrations

| Focus Area | Key Features | Technical Enablers |
| :---- | :---- | :---- |
| **Admin Console** | • User/role management • Organization settings • Audit logs | Hilt multi-binding for org-scoped repositories |
| **Advanced Sync** | • Delta sync protocol • Conflict merge strategies • Offline conflict preview | Custom Retrofit converters \+ Room type converters |
| **Integrations** | • Webhook notifications • Calendar sync • Map provider plugins (OSM/Google) | Plugin architecture via interface \+ ServiceLoader |
| **Accessibility** | • Full TalkBack support • Dynamic text sizing • Color contrast validation | Compose semantics \+ AccessibilityDelegate |

## **Testing & QA Strategy**

### **🧪 Test Pyramid Implementation**

         E2E (5%)  
      ┌─────────────────┐  
      │ Critical flows: │  
      │ • Auth → Task → │  
      │   Report → Sync │  
      └────────┬────────┘  
               │  
    Integration (25%)  
┌─────────────────────────┐  
│ • Repository \+ Room     │  
│ • ViewModel \+ UseCase   │  
│ • WorkManager workers   │  
└────────┬────────────────┘  
         │  
   Unit (70%)  
┌─────────────────────┐  
│ • Pure domain logic │  
│ • Utils/extensions  │  
│ • BudgetZen tokens  │  
└─────────────────────┘

### **📱 Real-Device Testing Protocol (M1 \+ Android)**

\# 1\. Connect device via USB, enable developer options  
adb devices  \# Verify connection

\# 2\. Install debug build (arm64 only for speed)  
./gradlew :app:assembleDebug \-Pabi=arm64-v8a  
adb install \-r app/build/outputs/apk/debug/app-debug.apk

\# 3\. Launch with field simulation flags  
adb shell am start \-n com.fieldstack.android/.MainActivity \\  
  \--es simulate\_offline true \\  
  \--ei task\_count 10

\# 4\. Monitor performance  
adb logcat \-s FieldStack  \# Filter app logs  
adb shell dumpsys gfxinfo com.fieldstack.android  \# Frame metrics

\# 5\. Simulate network conditions  
adb shell svc wifi disable  \# Test offline flows  
adb shell svc wifi enable

\# 6\. Capture bug reports  
adb bugreport fieldstack\_mvp\_$(date \+%Y%m%d).zip

### **🎯 QA Checklist (MVP Sign-off)**

* \[ \] Offline report creation → sync → server receipt (end-to-end)  
* \[ \] BudgetZen colors render correctly on OLED/LCD screens  
* \[ \] Typography scales properly at 120%/150% system font size  
* \[ \] Biometric auth fallback to password works reliably  
* \[ \] Sync worker respects Wi-Fi-only preference  
* \[ \] No memory leaks (LeakCanary clean after 10 report cycles)  
* \[ \] Cold start \<2s on Pixel 4a (mid-tier reference device)

## **Deployment & Distribution**

### **📦 Build Variants Strategy**

// app/build.gradle.kts  
android {  
    flavorDimensions \+= "environment"  
    productFlavors {  
        create("dev") {  
            dimension \= "environment"  
            applicationIdSuffix \= ".dev"  
            buildConfigField("String", "API\_BASE", "\\"\[https://dev-api.fieldstack.com\](https://dev-api.fieldstack.com)\\"")  
        }  
        create("staging") {  
            dimension \= "environment"  
            applicationIdSuffix \= ".staging"  
            buildConfigField("String", "API\_BASE", "\\"\[https://staging-api.fieldstack.com\](https://staging-api.fieldstack.com)\\"")  
        }  
        create("prod") {  
            dimension \= "environment"  
            applicationIdSuffix \= ".prod"  
            buildConfigField("String", "API\_BASE", "\\"\[https://api.fieldstack.com\](https://api.fieldstack.com)\\"")  
        }  
    }  
}

### **🚀 Release Workflow (GitHub Actions Optimized for M1)**

\# .github/workflows/android-release.yml  
name: Android Release

on:  
  push:  
    tags: \[ "v\*.\*.\*" \]

jobs:  
  build:  
    runs-on: macos-latest  \# M1 runner preferred  
    steps:  
      \- uses: actions/checkout@v4  
        
      \- name: Set up JDK 17  
        uses: actions/setup-java@v4  
        with:  
          java-version: '17'  
          distribution: 'temurin'  
          cache: 'gradle'  
        
      \- name: Build Release APK  
        run: |  
          echo "$SIGNING\_KEY" | base64 \-d \> keystore.jks  
          ./gradlew :app:assembleProdRelease \\  
            \-Pandroid.injected.signing.store.file=keystore.jks \\  
            \-Pandroid.injected.signing.store.password="$STORE\_PASS" \\  
            \-Pandroid.injected.signing.key.alias="$KEY\_ALIAS" \\  
            \-Pandroid.injected.signing.key.password="$KEY\_PASS"  
        env:  
          SIGNING\_KEY: ${{ secrets.SIGNING\_KEY\_BASE64 }}  
          STORE\_PASS: ${{ secrets.STORE\_PASSWORD }}  
          KEY\_ALIAS: ${{ secrets.KEY\_ALIAS }}  
          KEY\_PASS: ${{ secrets.KEY\_PASSWORD }}  
        
      \- name: Upload Artifact  
        uses: actions/upload-artifact@v4  
        with:  
          name: fieldstack-android-release  
          path: app/build/outputs/apk/prod/release/app-prod-release.apk

### **📲 Distribution Channels**

| Channel | Use Case | Process |
| :---- | :---- | :---- |
| **GitHub Releases** | Early adopters, beta testers | Attach APK \+ checksum \+ release notes |
| **Firebase App Distribution** | Internal QA, stakeholder demos | ./gradlew appDistributionUploadDevDebug |
| **Google Play (Internal)** | Production rollout prep | Play Console internal testing track |
| **Direct APK (Enterprise)** | Field teams with MDM | Signed APK \+ installation guide PDF |

## **Appendix: Quick-Start Commands**

### **🚀 First-Time Setup (M1 Mac)**

\# 1\. Clone repo  
git clone \[https://github.com/pallab-js/fieldstack-android.git\](https://github.com/pallab-js/fieldstack-android.git)  
cd fieldstack-android

\# 2\. Install prerequisites (via Homebrew)  
brew install android-commandlinetools adoptopenjdk

\# 3\. Accept licenses \+ install SDK components  
sdkmanager \--licenses  
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

\# 4\. Open in Android Studio (M1 optimized)  
open \-a "Android Studio" .

\# 5\. First build (arm64 only for speed)  
./gradlew :app:assembleDebug \-Pabi=arm64-v8a

\# 6\. Install on connected device  
adb install \-r app/build/outputs/apk/debug/app-debug.apk

### **🔄 Daily Vibecoding Loop**

\# Terminal 1: Watch for changes \+ incremental build  
./gradlew :app:assembleDebug \--continuous \-x lint \-x test

\# Terminal 2: Deploy to device (alias for speed)  
alias deploy="adb install \-r app/build/outputs/apk/debug/app-debug.apk && adb shell am start \-n com.fieldstack.android/.MainActivity"  
deploy

\# Terminal 3: Monitor logs  
adb logcat \-s FieldStack,AndroidRuntime,WM-Worker

\# Optional: Compose layout inspection  
\# Android Studio → Tools → Layout Inspector → Select running process

### **🧹 Maintenance Commands**

\# Clean build cache (when stuck)  
./gradlew cleanBuildCache

\# Run only unit tests (fast feedback)  
./gradlew :app:testDevDebugUnitTest

\# Generate BudgetZen design token report  
./gradlew :app:generateDebugSources  \# Validates color/typography constants

\# Check dependency updates  
./gradlew dependencyUpdates \-Drevision=release

\# M1-specific: Clear Gradle daemon memory if sluggish  
./gradlew \--stop && ./gradlew :app:preBuild

🌿 **BudgetZen Reminder**: *"Calm, encouraging, goal-oriented — your operations, your peace of mind."* \> Every line of code, every pixel, every interaction should reduce anxiety and celebrate progress.