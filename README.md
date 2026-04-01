#Building SWAGAT DARTA PRANALI
# स्वागत  (Swagat)

A **Java-based smart college event entry and registration system** built to replace the traditional manual Excel workflow.

This application helps colleges manage **student event check-in, registration, and attendance records** through a simple GUI, while allowing all collected data to be **exported into Excel format in one click**.

---

## Problem Statement

In many colleges, student entries during events are still managed manually using Excel sheets.

This traditional process has several issues:

* slow manual typing
* duplicate student entries
* difficult record tracking
* delayed reporting
* human error in attendance logs
* poor scalability for large events

**स्वागत** solves this by introducing a **GUI-based desktop application in Java**.

---

## Features

* Java Swing GUI
* Student entry form
* Live table preview
* One-click Excel export (`.xlsx`)
* Easy data management
* Fast event check-in workflow
* Simple and clean interface
* College-friendly design

---

## Tech Stack

* **Java (Swing)** → GUI
* **Apache POI** → Excel generation
* **Maven** → dependency management
* **IntelliJ IDEA / VS Code** → development

---

## Project Structure

```text
Welcome/
 ├── src/
 │   └── main/java/WelcomeApp.java
 ├── pom.xml
 └── welcome_entries.xlsx
```

---

## Installation

### 1) Clone the repository

```bash
git clone https://github.com/your-username/welcome-app.git
cd welcome-app
```

### 2) Install dependencies

Make sure Java and Maven are installed.

```bash
mvn clean install
```

### 3) Run the app

```bash
mvn exec:java
```

Or run directly from your IDE.

---

## How It Works

1. Open the app
2. Enter student details
3. Click **Add Entry**
4. Data appears in the table
5. Click **Export to Excel**
6. Excel file is generated instantly

Generated file:

```text
welcome_entries.xlsx
```

---

## Future Improvements

Planned upgrades:

* Login system
* MySQL / SQLite database
* Duplicate detection
* QR code student scanning
* Department-wise analytics
* Admin dashboard
* PDF report export
* Search and filter system
* Cloud backup

---

## Real Use Case

Designed for:

* college welcome programs
* seminars
* hackathons
* sports events
* certificate distribution
* guest sessions
* attendance-based workshops

---

## Why This Project Matters

This is not just a college assignment.

It solves a **real administrative problem** by replacing slow manual Excel workflows with a faster digital system.

The goal is to create a **practical software product that can be directly used in Nepali colleges**.

---

## Author

Developed by **Alson Basnet**

Project Name: **स्वागत**

---


