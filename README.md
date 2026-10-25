# NoSpawnInAdminClaims

A tiny Paper plugin that stops hostile mobs from spawning naturally inside
[GriefPrevention](https://github.com/GriefPrevention/GriefPrevention) **admin claims**.

Made for a floating spawn island: the island is an admin claim so players cannot build on it,
and this plugin keeps zombies, skeletons and phantoms off it at night without covering every
block in torches.

## What it blocks

Spawns the world makes on its own, inside an admin claim:

- darkness spawns, patrols, raids, village invasions
- phantoms (configurable)
- slimes and magma cubes (configurable)
- lightning conversions, silverfish from infested blocks, drowned conversion, endermites from
  ender pearls, trial spawners

## What it never blocks

- spawner blocks
- spawn eggs
- breeding
- `/summon`
- anything spawned by another plugin

So an op can still place a spawner or spawn-egg a villager on the island.

## Requirements

- Paper 26.1 or newer (built against 26.3)
- GriefPrevention 16.18 or newer

## Install

Drop `NoSpawnInAdminClaims-<version>.jar` into `plugins/` next to `GriefPrevention.jar` and
restart. The default config protects admin claims only.

## Config

`plugins/NoSpawnInAdminClaims/config.yml`

| Key | Default | Meaning |
| --- | --- | --- |
| `only-admin-claims` | `true` | `false` extends the protection to every claim on the server |
| `block-phantoms` | `true` | cancel phantom spawns over players in the claim |
| `block-slimes` | `true` | cancel slimes and magma cubes |
| `block-passive` | `false` | also cancel animals and ambient mobs |
| `debug` | `false` | log every cancelled spawn |

Changes take effect after a restart.

## Build

With Maven (fetches Paper API and GriefPrevention from their repositories):

```bash
mvn -q package
# target/NoSpawnInAdminClaims-<version>.jar
```

Without Maven, on a server that already has Paper and GriefPrevention installed, `build.sh`
compiles with a plain JDK against the jars Paper extracted under `libraries/` and the installed
`GriefPrevention.jar`. See the comments at the top of the script for the paths it expects.

## How it works

One listener on `CreatureSpawnEvent`. If the spawn reason is one of the "natural" reasons and
the entity is a monster (or a phantom / slime when enabled), it asks GriefPrevention for the
claim at the spawn location and cancels the event when that claim is an admin claim. Cancelled
spawns are counted and the total is printed on shutdown.

## License

MIT, see [LICENSE](LICENSE).
