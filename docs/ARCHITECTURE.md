# Architecture

Landlord is a single Bukkit/Spigot plugin. There is no separate backend, no
database and no external service — everything runs inside the Minecraft server
process, and all state is JSON on disk.

```
Minecraft server (Paper/Spigot 1.21)
├── Vault + EssentialsX ....... economy provider (player balances)
├── Multiverse-Core ........... loads the `lladv` world
└── Landlord.jar
    ├── game logic ............ levels, chunks, tax, businesses, stocks
    ├── embedded Jetty ........ live dashboard on :25566
    └── JSON files ............ plugins/Landlord/*.json
```

## Entry point

[`Main.java`](../src/main/java/com/johansvartdal/landlord/Main.java) is the
`JavaPlugin`. Its `onEnable()` is the wiring diagram for the whole plugin, in
order:

1. `Configurator` creates the plugin data folders.
2. `CustomConfig` reads `landlord.config` (language).
3. `Bank.setupEconomy()` grabs the Vault economy provider — **hard fails if
   Vault or an economy plugin is missing**.
4. Managers are constructed (`LevelManager`, `StockManager`, `ChunkBuilder`,
   `RentManager`, `PlayerDataManager`, …).
5. Every command class is instantiated — each one registers itself in its own
   constructor via `plugin.getCommand(...).setExecutor(this)`.
6. The Jetty dashboard starts.
7. `waitForLladvWorld()` polls for the `lladv` world and stops the server if it
   has not loaded within two minutes.

`Main` also handles the chat, join and quit events directly, including the
chat prefixes (`[CREATOR]`, `[BUILDER]`, `[MILLIONAIRE]`, `[VIP]`,
`[SPECTATOR]`).

## Package map

| Package | Responsibility |
| --- | --- |
| `commands/` | One class per `/command`. Each registers itself in its constructor and implements `CommandExecutor`. |
| `levels/` | The progression spine. `LevelManager` owns the current level; `S1Level1` … `S3Level5` each implement `LevelInterface` to declare their item requirements, rewards, roulette price, in-game book and the event that fires before them. |
| `events/` | Server-wide scripted events. `LandlordEventManager` runs one at a time. Subpackages: `arenafight/` (wave-based mob arenas), `adventure/` (hand-built map locations), `mystery/`, `taxevents/`. |
| `playerevents/` | Per-player state machines — jail, flight, mining trips, wilderness, lounge. `PlayerEventManager` guarantees one active event per player and cleans up on disconnect. |
| `business/` | `EXPORT`, `INSURANCE` and `BANK` businesses, each with their own income and risk model. |
| `stocks/` | Six tradable companies. Prices are replayed from pre-generated CSVs in `resources/marketflow/`, not computed live. |
| `renting/` | `RentableItem` and its subclasses (pickaxe, axe, shovel, sword, elytra, turtle shell). Non-return triggers a forced purchase or jail. |
| `mysterychest/` | Loot-table chests, from `BasicMysteryChest` up to `PlatinumMysteryChest`. |
| `chatentities/` | Message presentation. Each `ChatEntity` is a prefix/colour style — `InfoChat`, `WarningChat`, `ErrorChat`, `BankChat`, `HintChat`, … |
| `webserver/` | The Jetty dashboard: `PageHome` serves `web/home.html`, `HomeDataSupplier` serves the JSON it polls. |
| `lan/` | Optional LAN-party hardware integration — synchronised audio (`AudioLayer`, jlayer) and lighting cues. Safe to ignore for normal servers: on a machine with no audio device it disables itself and logs a line. Note that `countdown.wav` currently lives under `src/main/java/.../lan/rawaudio/`, which Maven does not put on the classpath, so audio playback is effectively a no-op until it moves to `src/main/resources/`. `LanLightsController` is an empty stub. |

## Key singletons

Most shared state hangs off `public static` fields on `Main` or on manager
classes. The important ones:

* **`Bank`** — every money movement goes through it. It applies tax, tracks the
  treasury balance and the elected Treasury Chancellor, and delegates actual
  balances to Vault.
* **`PlayerDataManager` / `PlayerData`** — per-player state (chunks owned, home
  location, level, status). One JSON file per player under
  `plugins/Landlord/players/`.
* **`Properties`** — game-wide state, chiefly the `GameState` enum
  (`NOT_STARTED`, `PREPARATIONS`, `EVENT_RUNNING`, `NORMAL`). Persisted to
  `Properties.json`.
* **`ChunkBuilder`** — builds and removes the glass borders between chunks when
  land is claimed.
* **`LangDict`** — translation lookup. `LangDict.getString("bank.someKey")`
  resolves against the loaded language JSON.
* **`StaticValues`** — all balance constants (prices, tax rates, `MAX_PLAYERS`)
  in one place. Start here when tuning the game.

## Persistence

There is no database. `Tools.read()` / `Tools.write()` read and write plain
files inside the plugin data folder, and everything is serialised as JSON via
`json-simple`:

| File | Contents |
| --- | --- |
| `plugins/Landlord/landlord.config` | Language setting |
| `plugins/Landlord/Properties.json` | Game state |
| `plugins/Landlord/players/<name>.json` | Per-player data |
| `plugins/Landlord/ReplacedBlocks/` | Blocks to restore after events |

`Tools.readInternal()` reads resources bundled inside the jar (the dashboard
HTML, market flow CSVs, language files).

## Resources

| Path | Purpose |
| --- | --- |
| `resources/plugin.yml` | Bukkit manifest and command declarations |
| `resources/languages/*.json` | `en`, `nb-no`, `nn-no` translations |
| `resources/marketflow/*.csv` | Pre-generated stock price series |
| `resources/web/` | Dashboard HTML and favicon |
| `resources/*.py` | Developer tooling, not shipped behaviour — `flowgenerator.py` generates new market flow CSVs, `language.py` bulk-edits translation files, `languagechecker.py` reports keys missing from a translation |

## Build

Maven with two notable plugins:

* **maven-shade-plugin** relocates Jetty to
  `com.johansvartdal.landlord.shaded.jetty` so it cannot collide with anything
  else on the server classpath. This is why `Landlord.jar` is ~24 MB.
* **maven-jar-plugin** honours `-Dlandlord.outputDirectory=...` so you can drop
  the jar straight into a server's `plugins/` folder.

Lombok is used for `@Getter`/`@Setter`/`@Slf4j`. It must stay at 1.18.30 or
newer to compile on JDK 21.

## Things worth knowing before you change code

* **Coordinates are hardcoded.** Arenas, the lounge, adventure sites and the
  game start position are literal `Location` values pointing into the `lladv`
  world. See [WORLD.md](WORLD.md).
* **Command classes register themselves.** Adding a command means creating the
  class, instantiating it in `Main.onEnable()`, *and* declaring it in
  `plugin.yml`.
* **User-facing strings belong in the language files**, not in Java. Add the key
  to all three JSON files and read it with `LangDict.getString(...)`.
* **There are no automated tests.** Changes are verified by running the dev
  server. See [CONTRIBUTING.md](../CONTRIBUTING.md).
