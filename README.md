# 2D Roguelike Action Game

**Course:** Computer Programming Practice - Final Project  
**Language:** Java

## About The Project

This project is a fully functional 2D Roguelike Action Game developed as the final project for our Computer Programming Practice course (National Central University). Built entirely from scratch in Java, the game features dynamic platforming, robust combat systems, intelligent enemies (Swordsman, Gunsman), finite-state-machine-driven AI, and tile-based map generation. 

Instead of relying on heavy third-party game engines, this project utilizes a custom-built 2D game engine to handle everything from entity management to collision detection and physics.

## Technology Stack & Requirements

The project is developed under the following Java environment and libraries installed/configured:

### Environment
* **Java Version:** Java 24 (Specifically `24.0.1 2025-04-15` or later)

### Packages & Dependencies
* **[JavaFX](https://openjfx.io/):** Used for rendering the game window, graphical user interfaces (UI), and scene management.
* **[JSON-Java](https://github.com/stleary/JSON-java) (`org.json`):** Utilized for parsing and managing structured game data, such as tilemap configurations and save states.

## My Contributions

For this project, my primary responsibility was architecting the underlying **Game Engine** and implementing the core **Game Loop**. My work forms the backbone of the game's execution, physics, and state management.

### 1. The Game Loop
* Implemented the main execution loop that drives the game.
* Managed frame timing, delta-time calculations, and synchronized the `update()` (logic/physics) and `render()` (drawing) cycles to ensure smooth gameplay.

### 2. Custom Game Engine (`src/app/data/scripts/engine` directory)
I designed and built the internal 2D engine components from scratch, which include:

* **Entity System (`engine.entity`):**
    * Created the base `Entity` classes and `EntityGroup` management.
    * Implemented a Chunk Manager (`ChunkGroupManager`, `RectChunkGroupManager`) for optimized rendering and spatial partitioning.
    * Built the physics foundations, including `Gravity`, `SpeedVector`, and dynamic update strategies.
* **Finite State Machine (FSM) (`engine.FSM`):**
    * Developed a flexible FSM system (`StateMachine`, `StateExec`, `StateTransition`, `StateTransCond`).
    * This architecture allows complex behaviors and animations for the Player and Enemies to be easily managed and decoupled from the main logic.
* **Collision Detection System (`engine.collision`):**
    * Implemented Axis-Aligned Bounding Box (AABB) collision algorithms (`RectToRect`).
    * Created `HandleCollision` and `HitBox` logic to manage physics boundaries, weapon strikes, and platforming mechanics.
* **Engine Tools (`engine.tools`):**
    * Developed the `GameCamera` for smooth player tracking.
    * Implemented an `ActionTimer` for cooldowns and animation pacing.

## How to Run

1. Ensure **Java 24** and the **JavaFX SDK** are installed and configured in your system.
2. Clone this repository.
3. Import the project into your preferred Java IDE (IntelliJ IDEA, Eclipse).
4. Ensure `json-java` is added to your project dependencies (via Maven/Gradle or direct JAR).
5. Add the necessary VM options to include the JavaFX modules (e.g., `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.media`).
6. Run the main application class: `src/app/Main.java`.

## Repository Structure Highlights

* `/src/app/Main.java` - Application entry point.
* `/src/app/data/scripts/engine/` - The core custom game engine (My Contribution).
* `/src/app/data/scripts/Game.java` - Main game container and loop execution (My Contribution).
* `/src/app/data/scripts/game/` - Implementation of specific game logic (Player, Enemies, UI, Map).
* `/src/app/data/images/` - Sprites, UI elements, and tilemaps.
* `/src/app/data/info/` - Contains `.json` and `.csv` files used for game configurations and maps.
* `/src/app/data/music/` - Audio assets for BGM and SFX.