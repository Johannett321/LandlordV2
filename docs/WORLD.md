# The `lladv` world

Landlord is not a self-contained plugin. A large part of the game — the arena,
the lounge, the treasury voting hall, the adventure maps — is **hand-built
Minecraft geometry** living in a separate world named `lladv` ("Landlord
adventure"). The plugin teleports players to fixed coordinates inside it.

Without that world the plugin cannot function, and it says so loudly:

```
------------------------------------------------------------------
[LANDLORD] ERROR: COULD NOT FIND LLADV WORLD. SHUTTING DOWN SERVER
------------------------------------------------------------------
```

`Main.waitForLladvWorld()` starts polling 10 seconds after the plugin enables and
gives the world up to two minutes to appear before stopping the server. The grace
period matters on a first boot: Multiverse only imports a world once the server is
fully started, which can be well over a minute after Landlord enables.

## Installing it

1. Download `lladv-world.zip` from the
   [latest release](https://github.com/Johannett321/LandlordV2/releases). The
   world is ~250 MB uncompressed, which is why it ships as a release asset
   rather than living in the repository.
2. Unpack it so the world folder sits at `run/data/lladv` (Docker) or
   `<server>/lladv` (existing server). The folder must contain `level.dat`.
3. Start the server. With Docker, `docker-compose.yml` already runs
   `mv import lladv normal` over RCON on boot. On an existing server, run it
   yourself once from the console:

   ```
   mv import lladv normal
   ```

Multiverse remembers imported worlds, so this is a one-time step.

## Why the main world matters too

`lladv` is the *second* world. Player plots are built in the server's primary
world (`Bukkit.getWorlds().get(0)`), which is generated normally — it does not
need to be hand-built. `lladv` only holds the shared, authored locations.

## Fixed coordinates

If you rebuild or replace the world, these are the positions the code expects.
All of them are literal `Location` values in Java; there is no config file.

| Location | Coordinates | Defined in |
| --- | --- | --- |
| Game start / spawn | `171.5, 66, -182.5` | `StaticValues.GAME_START_LOCATION` |
| Admin warp (`/adm lladv`) | `194, 81, -112` | `commands/Adm.java` |
| Jail | `203.5, 74, -146.5` | `playerevents/JailEvent.java` |
| VIP lounge | `107, 68, -871` | `playerevents/LoungeEvent.java` |
| Lounge jukebox / hopper | `101, 69, -875` and `101, 70, -875` | `playerevents/LoungeEvent.java` |
| Arena spawn | `268.5, 64, -167.5` | `events/arenafight/ArenaFightEvent.java` |
| Arena firework | `257, 73, -142` | `events/arenafight/ArenaFightEvent.java` |
| Arena mob port 1–4 | `276,65,-115` · `271,65,-139` · `287,65,-160` · `248,65,-155` | `events/arenafight/Ports.java` |
| Treasury vote hall | `202.5, 92, -1081.5` | `events/taxevents/ChooseTreasuryEvent.java` |
| Treasury gold box | `202.5, 95, -1077.5` | `events/taxevents/ChooseTreasuryEvent.java` |
| Mystery event spawn | `-283.5, 58, -160.5` | `events/mystery/Mystery1.java` |
| Adventure — Valley Village | `528, 68, -893` | `events/adventure/ValleyVillageAdventure.java` |
| Adventure — Icy Hills | `217, 157, -1408` | `events/adventure/IcyHillsEvent.java` |
| Adventure — Lumber Mine Forest | `-1106, 70, -2745` | `events/adventure/LumberMineForestAdventure.java` |
| Adventure — Siberian Cottage | `-2399, 77, 176` | `events/adventure/SibirianCottageWinterAdventure.java` |
| Adventure — Mesa Hillside | `10260, 79, -37` | `events/adventure/MesaHillsideAdventure.java` |
| Test event start | `-275, 113, -91` | `events/TestEvent.java` |

## Building your own

Nothing stops you from shipping a different `lladv`. You need, at minimum:

* a safe spawn platform at the game start location,
* an enclosed arena around `268, 64, -167` with four mob spawn ports,
* a jail cell players cannot escape,
* a treasury hall around `202, 92, -1081`,

and then whichever adventure sites you want to keep — unused adventure events
can simply be removed from `LandlordEventManager`.

A cleaner long-term fix would be to move all of these into a config file so the
world and the code stop being coupled. Contributions welcome.
