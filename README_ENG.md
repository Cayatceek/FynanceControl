# Expense Tracker

A personal expense tracking application with support for multiple currencies, categories, charts, and limit notifications.

## 📋 Functional Requirements

### Core Features
- ✅ **Add Expenses** - quickly add new expenses with amount, category, and description
- ✅ **Edit and Delete Expenses** - modify or delete existing records with confirmation
- ✅ **Display Expenses by Category** - visual representation of categories with icons and colors
- ✅ **Confirmation Dialogs** - protection against accidental deletions
- ✅ **Local Database Storage** - using Room for offline data persistence
- ✅ **Limit Exceeded Notifications** - push notifications when monthly budget is exceeded

### Additional Features
- ✅ **Daily and Monthly Statistics** - track expenses for the day and month with progress bars
- ✅ **Search and Filter Expenses** - search by description and filter by categories
- ✅ **Expense Charts** - pie chart by categories and bar chart by days
- ✅ **Currency Settings** - support for 8 currencies (UZS default, RUB, USD, EUR, etc.)
- ✅ **Limit Configuration** - individual daily and monthly limits
- ✅ **App Profile** - version info, licenses, and features

## 🛠 Technology Stack

### Languages and Platform
- **Kotlin 2.0.21** - primary development language
- **Android SDK 36** - target platform
- **Min SDK 31** - minimum supported Android version

### Architecture and UI
- **Jetpack Compose** - modern declarative UI
- **Material Design 3** - UI components
- **Navigation Compose** - screen navigation
- **MVVM** - architectural pattern (Model-View-ViewModel)

### Data Storage
- **Room 2.6.1** - ORM for SQLite
- **DataStore Preferences** - app settings storage
- **Kapt** - annotation processing for Room

### Asynchronous Operations
- **Kotlin Coroutines** - async operations
- **Kotlin Flow** - reactive data streams
- **StateFlow** - UI state management

### Build System
- **Gradle 8.13** - build system
- **Kotlin DSL** - Gradle configuration
- **Version Catalog (libs.versions.toml)** - centralized version management

## 📁 Project Structure

```
MyApplication/
├── app/
│   ├── src/main/java/com/example/myapplication/
│   │   ├── MainActivity.kt                 # Application entry point
│   │   ├── data/                           # Data layer
│   │   │   ├── entity/
│   │   │   │   └── Expense.kt             # Expense entity
│   │   │   ├── dao/
│   │   │   │   └── ExpenseDao.kt          # DAO for database operations
│   │   │   ├── database/
│   │   │   │   └── AppDatabase.kt         # Room database
│   │   │   ├── repository/
│   │   │   │   ├── ExpenseRepository.kt   # Expense repository
│   │   │   │   └── SettingsRepository.kt  # Settings repository
│   │   │   ├── model/
│   │   │   │   └── Category.kt            # Category model
│   │   │   └── Converters.kt              # Room converters
│   │   ├── ui/                            # UI layer
│   │   │   ├── screens/                   # Application screens
│   │   │   │   ├── HomeScreen.kt          # Main screen
│   │   │   │   ├── AddExpenseScreen.kt    # Add/edit expense
│   │   │   │   ├── SettingsScreen.kt      # Settings
│   │   │   │   ├── ProfileScreen.kt       # Profile/About
│   │   │   │   └── StatisticsScreen.kt    # Statistics and charts
│   │   │   ├── navigation/
│   │   │   │   └── NavGraph.kt            # Navigation graph
│   │   │   ├── viewmodel/
│   │   │   │   └── ExpenseViewModel.kt    # ViewModel
│   │   │   └── theme/                     # App theme
│   │   │       ├── Color.kt
│   │   │       ├── Theme.kt
│   │   │       └── Type.kt
│   │   └── utils/                         # Utilities
│   ├── src/main/res/                      # Resources
│   └── build.gradle.kts                   # Module configuration
├── gradle/libs.versions.toml              # Version Catalog
├── build.gradle.kts                       # Root build file
└── settings.gradle.kts                    # Project settings
```

## 🏗 Application Architecture

### MVVM (Model-View-ViewModel)

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   UI Layer      │────▶│  ViewModel      │────▶│  Repository     │
│   (Compose)     │◀────│  (StateFlow)    │◀────│  (Flow)         │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                                        │
                                                        ▼
                                               ┌─────────────────┐
                                               │  Data Sources   │
                                               │  (Room/         │
                                               │   DataStore)    │
                                               └─────────────────┘
```

### Data Layer

#### Entity
**Expense** - main expense entity:
```kotlin
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Date
)
```

#### DAO (Data Access Object)
**ExpenseDao** - data access interface:
- `insert(expense: Expense)` - add expense
- `update(expense: Expense)` - update expense
- `delete(expense: Expense)` - delete expense
- `getAll(): Flow<List<Expense>>` - get all expenses
- `getByCategory(category: String)` - filter by category
- `search(query: String)` - search by description
- `getSum(start: Long, end: Long)` - sum for period

#### Repository
**ExpenseRepository** - abstraction over data source:
- Provides unified interface for data operations
- Uses Flow for reactive updates
- Performs operations in background thread

**SettingsRepository** - settings management:
- Stores currency, limits in DataStore
- Provides Flow for observing changes

### UI Layer

#### Screens

**HomeScreen** - main screen:
- Daily and monthly statistics
- Expense list with search
- Category filtering
- Bottom navigation

**AddExpenseScreen** - add expense:
- Amount and description fields
- Category dropdown
- Confirmation dialog

**SettingsScreen** - settings:
- Currency selection from 8 options
- Daily and monthly limit configuration

**StatisticsScreen** - statistics:
- Pie chart by categories
- Bar chart by days
- Category breakdown

**ProfileScreen** - profile:
- Application information
- License list
- App version

#### ViewModel

**ExpenseViewModel** - state management:
- Stores StateFlow for all UI states
- Handles user actions
- Interacts with repositories
- Checks limits and sends notifications

### Navigation

Uses Navigation Compose with graph:
- `home` - main screen
- `add_expense` - add expense
- `settings` - settings
- `profile` - profile
- `statistics` - statistics

## 🎨 Expense Categories

| Category | Icon | Color |
|----------|------|-------|
| Food | Restaurant | #4CAF50 (Green) |
| Transport | DirectionsCar | #2196F3 (Blue) |
| Entertainment | Movie | #9C27B0 (Purple) |
| Shopping | ShoppingCart | #FF9800 (Orange) |
| Health | LocalHospital | #E91E63 (Pink) |
| Bills | Receipt | #795548 (Brown) |
| Other | MoreHoriz | #607D8B (Gray) |

## 💱 Supported Currencies

| Code | Name | Symbol | Flag |
|------|------|--------|------|
| UZS | Uzbekistani Som | soʻm | 🇺🇿 |
| RUB | Russian Ruble | ₽ | 🇷🇺 |
| USD | US Dollar | $ | 🇺🇸 |
| EUR | Euro | € | 🇪🇺 |
| KZT | Kazakhstani Tenge | ₸ | 🇰🇿 |
| GBP | British Pound | £ | 🇬🇧 |
| JPY | Japanese Yen | ¥ | 🇯🇵 |
| CNY | Chinese Yuan | ¥ | 🇨🇳 |

## 🔧 Building the Project

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or newer
- Android SDK 36

### Build Commands
```bash
# Clean project
./gradlew clean

# Build debug version
./gradlew :app:assembleDebug

# Build release version
./gradlew :app:assembleRelease

# Install on device
./gradlew :app:installDebug
```

## 📄 Licenses

The project uses the following open source libraries:
- **AndroidX** - Apache License 2.0
- **Jetpack Compose** - Apache License 2.0
- **Room** - Apache License 2.0
- **Kotlin** - Apache License 2.0

## 📦 Installation

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or newer
- Android SDK 36

### Cloning and Running
```bash
# Clone the repository
git clone https://github.com/Cayatceek/FynanceControl.git

# Navigate to project folder
cd FynanceControl

# Open in Android Studio
# File -> Open -> Select project folder

# Sync Gradle
# Click "Sync Now" in the notification

# Run the application
# Click Run (green triangle) or Shift+F10
```

## 📱 User Guide

### Adding an Expense
1. Tap the **+** button on the home screen
2. Enter the expense amount
3. Select a category from the dropdown
4. Add a description (optional)
5. Tap "Add Expense"
6. Confirm the action in the dialog

### Changing Currency
1. Open **Settings** screen (gear icon)
2. Tap on "Current Currency" field
3. Select desired currency from the list
4. Changes apply automatically

### Setting Limits
1. Go to **Settings**
2. Enter your desired monthly limit
3. Enter your desired daily limit
4. Press Enter to save

### Viewing Statistics
1. Tap **Statistics** in bottom navigation
2. View the pie chart by categories
3. Examine the bar chart by days
4. Analyze category breakdown

### Search and Filter
- Use the search field on home screen to search by description
- Tap on category chip to filter
- Tap "All" to reset filter

## 🗄 Database Schema

### Table `expenses`
| Field | Type | Description |
|-------|------|-------------|
| id | INTEGER | Primary key, auto-increment |
| amount | REAL | Expense amount |
| category | TEXT | Category name |
| description | TEXT | Expense description |
| date | INTEGER | Timestamp in milliseconds |

### Indexes
- Primary key: `id`
- Date index: `date DESC` (for sorting)

## 🧪 Testing

### Running Unit Tests
```bash
./gradlew test
```

### Running Instrumented Tests
```bash
# Connect device or start emulator
./gradlew connectedAndroidTest
```

## 👨‍💻 Author

Cayatceek - Software engineer 
Expense Tracker v1.0.0
© 2026 All rights reserved
