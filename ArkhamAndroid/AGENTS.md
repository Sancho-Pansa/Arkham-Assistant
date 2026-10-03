"ArkhamAndroid" or simply "Android"
# Overview
This directory is a part of general Arkham Assistant project.
This subdirectory contains an Android application, which use game logic written "Core" component.

# Tech stack
- Kotlin
- Jetpack Compose
- Coroutines (where appropriate)
- "Core" module as a source of game logic

# Architecture considerations
- "Core" domain models are the game's logic and state;
- ViewModel - UI state and application coordination
- Compose - rendering and UI

ViewModels and Composables should not duplicate game rules, already established in "Core".

Other considerations will be added later.