# OPAC Fixes

A Fabric Minecraft mod that provides claim protection compatibility fixes for **Open Parties and Claims (OPAC)** when used alongside other popular mods.

## Overview

Some mods implement custom explosion or block placement/breaking mechanics that bypass standard Fabric event listeners, allowing players or mobs to bypass OPAC claims protection. This mod surgically intercepts these behaviors to ensure that claims remain protected.

## Features

- **Mutant Monsters**: Prevents mutated explosions (e.g. Mutant Creepers) from destroying blocks and harming entities inside protected claims.
- **Supplementaries**: Prevents Red Merchants' bombs from bypass griefing blocks and entities in protected claims.
- **Botania**: Prevents the Ring of Loki from bypass-placing or bypass-breaking blocks inside protected claims using blueprints.
- **Conditional Loading**: Compatibility mixins for Mutant Monsters, Supplementaries, and Botania only load if the respective mods are detected at runtime.

## Installation

1.  Ensure you have **Fabric Loader** and **Fabric API** installed for Minecraft 1.20.1.
2.  Requires **Open Parties and Claims (OPAC)** to be present.
3.  Drop this mod into your `mods` folder.

## Development Setup

This project uses Gradle. To set up the workspace:

1.  Clone the repository.
2.  Open in your IDE.
3.  Run `./gradlew genSources` to generate Minecraft source code.
4.  Run `./gradlew build` to compile.

## License

See [License.txt](License.txt) for details.
