# ArmWrestling Competition - Project README

**Enterprise Package Structure | Java Swing Application | Tournament Management**

---

## 📋 Project Overview

The ArmWrestling Competition is a comprehensive desktop application built with Java Swing that manages arm wrestling tournaments with advanced bracket logic, real-time round management, and dynamic winner tracking.

### 🎯 Key Features

- **Tournament Management**: Create, manage, and track arm wrestling tournaments
- **Dynamic Rounds**: Automatically progress through tournament rounds with intelligent bracket logic
- **Tabbed Interface**: Navigate between different tournament rounds
- **Winner Selection**: Select winners with real-time validation and highlighting
- **Results Tracking**: View tournament standings with real-time updates
- **CSV Import**: Load wrestler data from CSV files
- **Advanced Logic**: Semifinal, Final, and Super-Final handling

---

## 📦 Package Structure

```
com.objectedge.artem.ai.poc/
│
├── Main.java (Entry Point)
│
├── models/                      # Data Models
│   ├── Armwrestler.java        # Wrestler data model
│   └── TournamentState.java     # Tournament state management
│
├── helpers/                     # Utilities & Factories
│   ├── CSVLoader.java          # CSV file I/O utilities
│   └── MatchPanelFactory.java  # UI component factory
│
├── managers/                    # Business Logic
│   ├── TournamentProgression.java      # Tournament logic
│   ├── TournamentTableManager.java     # Results table
│   ├── RoundTabManager.java            # Tab management
│   └── RoundDisplayManager.java        # Round rendering
│
└── forms/                       # UI Components
    ├── CompetitionForm.java     # Main application window
    ├── ArmwrestlerForm.java     # Wrestler management
    └── RoundTab.java            # Round tab component
```

---

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- No external dependencies required (uses standard Swing library)

### Running the Application

#### From Command Line
```bash
cd src
javac -encoding UTF-8 -d . com/objectedge/artem/ai/poc/*.java com/objectedge/artem/ai/poc/*/*.java
java -cp . com.objectedge.artem.ai.poc.Main
```

#### From IDE

**IntelliJ IDEA:**
1. Open project
2. Mark `src/` as Source Root
3. Right-click `Main.java` → Run 'Main'

**Eclipse:**
1. Import project
2. Build project
3. Run → Run As → Java Application

**VS Code:**
1. Install Extension Pack for Java
2. Open `Main.java`
3. Click "Run" button

---

## 📝 Application Usage

### 1. Starting a Tournament
- Click "Manage Armwrestlers"
- Add wrestlers or load from CSV
- Click "Start Competition"

### 2. Managing Rounds
- Tournament automatically creates tabs for each round
- Select winner by clicking wrestler button
- Red borders highlight incomplete pairs
- Click "Next Round" to advance

### 3. Tracking Results
- Tournament table shows standings in real-time
- View wins, losses, and elimination status
- Standings auto-sort by performance

---

## 🏗️ Architecture

### Dependency Flow
```
models
  ↓
helpers (depends on: models)
  ↓
managers (depends on: models, helpers)
  ↓
forms (depends on: models, helpers, managers)
  ↓
Main
```

### Class Responsibilities

**Models**
- `Armwrestler`: Represents a wrestler with stats
- `TournamentState`: Manages tournament state and rounds

**Helpers**
- `CSVLoader`: Handles CSV file I/O
- `MatchPanelFactory`: Creates UI components

**Managers**
- `TournamentProgression`: Handles tournament logic
- `TournamentTableManager`: Manages results table
- `RoundTabManager`: Manages round tabs
- `RoundDisplayManager`: Renders round content

**Forms**
- `CompetitionForm`: Main application window
- `ArmwrestlerForm`: Wrestler management dialog
- `RoundTab`: Individual round tab

---

## 🔄 Tournament Logic

### Round Structure
1. **Round 1**: All wrestlers in top section
2. **Round 2+**: Winners in top, losers in bottom
3. **Semifinal**: Top winner vs bottom winner
4. **Final**: Championship match
5. **Super-Final**: If bottom winner has 1 loss

### Advancement Rules
- Winners advance to top section
- Losers drop to bottom section
- 2 losses = elimination
- Bye wrestlers advance automatically

---

## 📊 Data Models

### Armwrestler
```java
- id: int (unique identifier)
- name: String
- surname: String
- age: int
- hand: String ("left" or "right")
- wins: int
- losses: int
```

### TournamentState
```java
- topSectionWrestlers: List
- bottomSectionWrestlers: List
- topRoundOutcome: Map (match results)
- bottomRoundOutcome: Map (match results)
- currentRound: int
- isFinal: boolean
- isSemifinal: boolean
- isSuperFinal: boolean
```

---

## 🎨 UI Components

### Main Window (CompetitionForm)
- Title bar
- Manage Armwrestlers button
- Tabbed interface for rounds
- Tournament results table
- Next Round button

### Wrestler Management (ArmwrestlerForm)
- Wrestler list
- Add/Edit/Delete options
- CSV import capability
- Start Competition button

### Round Display
- Round label
- Top section (winners)
- Bottom section (losers)
- Pair organization with borders
- Winner selection buttons

---

## 🧪 Testing

### Manual Testing Checklist
- [ ] Load predefined wrestlers
- [ ] Import wrestlers from CSV
- [ ] Create tournament
- [ ] Select winners in Round 1
- [ ] Navigate between rounds via tabs
- [ ] Verify results table updates
- [ ] Advance to Semifinal
- [ ] Complete Final and Super-Final
- [ ] Verify tournament conclusion

### Known Limitations
- No database persistence (in-memory only)
- No undo/redo functionality
- No tournament save/load

---

## 🔧 Development

### Adding New Features

To add new managers:
```bash
src/com/objectedge/artem/ai/poc/managers/NewManager.java
```

To add new forms:
```bash
src/com/objectedge/artem/ai/poc/forms/NewForm.java
```

To add new models:
```bash
src/com/objectedge/artem/ai/poc/models/NewModel.java
```

### Code Style
- Follow Java naming conventions
- Use package declarations
- Document public methods
- Keep classes focused on single responsibility

---

## 📈 Future Enhancements

- [ ] Database persistence
- [ ] Tournament save/load functionality
- [ ] Undo/redo support
- [ ] Enhanced reporting
- [ ] Tournament statistics
- [ ] Export to PDF
- [ ] Multi-tournament management
- [ ] Player rankings system

---

## 📄 Files & Documentation

- `PACKAGE_MIGRATION_COMPLETE.md` - Migration details
- `PACKAGE_REORGANIZATION_SUMMARY.md` - Detailed statistics
- `MIGRATION_SUCCESS.txt` - Success checklist
- `README.md` - This file

---

## 📧 Support

For questions or issues, refer to:
1. Code comments
2. Method javadoc
3. Package structure

---

## ✅ Project Status

**Status**: ✅ COMPLETE & OPERATIONAL

- All 12 classes migrated
- Enterprise package structure implemented
- All features functional
- Ready for production use

---

**Last Updated**: February 19, 2026
**Version**: 1.0.0
**License**: Private/Internal Use


