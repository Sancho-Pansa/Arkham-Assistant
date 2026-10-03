"ArkhamCore" or simply "Core"

# Overview
This directory is the part of general Arkham Assistant project.
This subproject contains platform-independent logic for the original tabletop game written in Java.

It contains:
- game rules;
- domain models;
- game state;
- calculations and utils.

# Tech stack
- Java (version 17-24)
- JUnit 6 for testing
- Gradle for dependency management (with legacy elements of Maven)

# Responsibilities
This module owns:
- domain models;
- game state;
- game logic;
- game rules;
- game calculations;
- platform-independent validation;
- game data in JSON files

# Rules
The "Core" must remain independent of Android implementation, no Android SDK dependency is allowed; the same is true for any other UI framework dependency.

# Design guidelines
- Domain objects should be independent from presentation models.
- Prefer explicit domain types over generic maps or loosely typed structures.
- Avoid introducing framework dependencies for simple problems.
- Preserve backward compatibility unless the task explicitly requires an API change.

# Important consideration
This subproject has a lasting history of slow changes over vast period of time. Thus, keep in mind that it contains a lot of "legacy" components and inconsistency accross different files.
If you have detected such inconsistency or severe violation of architectural pattern, mention it in response.
Architecture review is appreciated, but only as advice and guides in order for human user to make the changes (unless commanded to do it after such review).
