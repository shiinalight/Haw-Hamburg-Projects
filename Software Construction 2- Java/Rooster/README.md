# Rooster Drawing Tool

A modular Java Swing project that demonstrates **object-oriented design**, **2D graphics programming**, and **GUI interaction** through the creative task of drawing and animating a rooster.  
Developed as part of the *Software Development Labs (PRP2)* at **HAW Hamburg**, this project evolves across three lab stages — from basic modeling to dynamic graphical interaction.

---

## Overview

The **Rooster Drawing Tool** visualizes a rooster assembled from multiple object-oriented components such as the head, beak, comb, wings, and tail.  
Each part is encapsulated in its own class, following composition principles, and rendered on a `DrawingArea` within a Swing window.  

Through three development phases, the project explores:
- **Lab 1:** Object-oriented modeling and composition  
- **Lab 2:** Scene building, aggregation, inheritance, and random variation  
- **Lab 3:** GUI control with buttons and actions to modify the rooster dynamically  

---

## Object-Oriented Design

The project follows clean OOP design with meaningful class responsibilities and relationships.  

### Main Classes

| Class | Role |
|-------|------|
| `TestDrawingTool` | Main entry point that launches the application window. |
| `DrawingArea` | Extends `JPanel`; handles all drawing via `paintComponent(Graphics g)`. |
| `Scene` | Aggregates and manages multiple rooster objects. |
| `Rooster` | The main composite class that assembles and draws all body parts. |
| `Head`, `Body`, `Wing`, `Tail`, `Leg` | Core components composing the rooster. |
| `Eye`, `Comb`, `Beak_Top_Triangle`, `Beak_Bottom_Triangle` | Sub-components nested within `Head`. |
| `RandomNumber` | Utility class generating controlled random variations for scene diversity. |
| `ControlPanel` | GUI class providing buttons/sliders to modify rooster appearance. |

### Simplified UML Structure

```
+---------------------+
|     TestDrawingTool |
+---------------------+
           |
           v
+---------------------+
|     DrawingArea     |
+---------------------+
           |
           v
+---------------------+
|        Scene        |<>-- multiple --> Rooster
+---------------------+
                          |
                          +-- Head
                          |     +-- Eye
                          |     +-- Comb
                          |     +-- Beak_Top_Triangle
                          |     +-- Beak_Bottom_Triangle
                          |
                          +-- Body
                          +-- Wing
                          +-- Tail
                          +-- Leg
```

Each component class exposes:
```java
public void draw(Graphics g)
```
and maintains its own position, size, and color constants.  
This modularity allows flexible rendering and easy scaling of parts.

---

## Running the Application

### Prerequisites
- Java JDK 17 or later  
- Eclipse, IntelliJ, or any Java IDE with AWT/Swing support

### Steps
1. Clone or download this repository:
   ```bash
   git clone https://github.com/yourusername/Rooster.git
   cd Rooster/src
   ```
2. Open the project in your IDE and ensure the package is named:
   ```
   package drawingTool;
   ```
3. Run the main class:
   ```bash
   TestDrawingTool.java
   ```
4. The graphical window will open and display your rooster.  
   Use GUI controls (buttons/sliders) to apply variations such as colors or positions.

---

## Features

- **Modular OOP Structure:** Each body part implemented as an independent class  
- **Composite Relationships:** Rooster composed of multiple nested objects  
- **Scene Management:** Multiple rooster instances with random sizes and positions  
- **Interactive GUI:** Buttons and sliders to control color and shape variations  
- **Scalable Graphics:** Drawn using Java AWT/Swing primitives (`fillOval`, `fillPolygon`, etc.)  
- **Code Conventions:** Clean, consistent formatting and meaningful identifiers  

---

## Key Learnings

- Applied **composition, inheritance, and aggregation** in a tangible visual domain  
- Built **interactive GUIs** with event handling (`ActionListener`, `actionPerformed`)  
- Implemented **parameterized drawing methods** (`drawAt(int left, int bottom)`)  
- Gained practical experience with **object hierarchies**, **UML modeling**, and **scene rendering**

---

## Future Improvements

- Add simple **animation** (wing flapping or walking) using a `Timer`  
- Implement **color themes** for different rooster variations  
- Add **mouse interaction** to drag or select objects in the scene  
- Refactor GUI using **JavaFX** for modern design

---

## Author

**Nooshin Pourkamali**  
Information Engineering Student, HAW Hamburg  
Passionate about combining **software engineering**, **creativity**, and **computational design**.   

---

**License:** MIT  
**Course:** Software Development II (PRP2) — HAW Hamburg  
**Instructor:** Prof. Dr. B. Gottfried
