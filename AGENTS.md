# Project Overview
This project is an attempt to create a digital assistant for old tabletop game "Arkham Horror" (2nd edition).

Original tabletop game has a lot of concurrent mechanics, counters, and rules, which could be overwhelming for newbies and at late stages of the game. Automated assistant helps to keep track of player stats, inventory, abilities, etc.

This monorepository contains these components:
- "./ArkhamCore" or simply "Core" - platform-independent game logic written in Java
- "./ArkhamAndroid" or "Android" - Android application consuming the "core" module
- "./ArkhamFetcher" - legacy NodeJS utility for asset extraction. Exclude it from your context.

More specific information is written in corresponding sub-folders.

# General rules
- The game logic must be independent of Android APIs
- Do not introduce Android dependencies into "Core"