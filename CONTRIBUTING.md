# Contributing to Landlord

Thanks for taking an interest. Landlord started life as a closed-source paid
plugin and is now MIT-licensed, so a lot of the code still reflects its origins
— hardcoded coordinates, no test suite, a few Norwegian comments. Cleanups in
those areas are as welcome as new features.

## Getting set up

You need a JDK 17 or newer, Maven, and Docker.

```bash
git clone https://github.com/Johannett321/LandlordV2.git
cd LandlordV2
cp .env.example .env    # then set MINECRAFT_OPS=YourMinecraftUsername
make dev
```

You also need the `lladv` world in `run/data/lladv` or Landlord will stop the
server shortly after boot — see [docs/WORLD.md](docs/WORLD.md).

Read [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) before making a first change;
it explains how `Main.onEnable()` wires everything together and where each kind
of logic lives.

## The development loop

There are no automated tests. Everything is verified by running the game:

```bash
make build     # recompile to target/Landlord.jar
make stop      # stop the server
make up        # start it again with the new jar
```

`make dev` does the build and start in one go. Use `make console` to attach to
the server console (detach with `Ctrl-P` `Ctrl-Q`) and `make reset` to wipe all
server state when you need a clean game.

To speed up testing, `Properties.java` has two switches:

```java
public static final boolean DEV_CHEAT_MODE = false;  // skip restrictions
public static final boolean DEV_UNLOCK_ALL = false;  // unlock all commands
public static final boolean DEBUG_LOGGING  = false;  // verbose logging
```

## Conventions

**Never hardcode user-facing text.** Every string a player sees goes in the
language files:

```java
Tools.tellPlayer(new InfoChat(), player, LangDict.getString("bank.taxPaid"), ChatColor.GREEN);
```

Add the key to all three files in `src/main/resources/languages/` (`en.json`,
`nb-no.json`, `nn-no.json`). If you cannot write Norwegian, copy the English
string in and say so in the pull request — it is better than a missing key.
`languagechecker.py` reports keys that are missing from a translation:

```bash
python3 src/main/resources/languagechecker.py
```

**Adding a command** takes three steps:

1. Create the class in `commands/`, implementing `CommandExecutor` and
   registering itself in its constructor.
2. Instantiate it in `Main.onEnable()`.
3. Declare it in `src/main/resources/plugin.yml`.

**Balance changes** belong in `StaticValues.java`, not scattered through the
code.

**Chat output** should go through a `ChatEntity` (`InfoChat`, `WarningChat`,
`ErrorChat`, …) rather than raw `player.sendMessage`, so prefixes and colours
stay consistent.

## Pull requests

* Branch off `main`.
* Keep the change focused — one feature or fix per PR.
* Say how you tested it. "Started a game, levelled to S1L3, confirmed the arena
  event fires" is exactly the right level of detail.
* Match the surrounding code style. The project is not formatter-enforced.

## Good first contributions

* **Move the hardcoded `lladv` coordinates into a config file.** This is the
  single most valuable change anyone could make — it would decouple the plugin
  from one specific hand-built world. See [docs/WORLD.md](docs/WORLD.md).
* **Add a translation.** Copy `en.json` to a new language code and translate.
* **Make the hardcoded chat prefixes configurable.** `Main.onPlayerChat()`
  matches specific usernames for `[CREATOR]` and `[BUILDER]` tags.
* **Add tests.** There is no test infrastructure at all; even a few unit tests
  around `Bank` tax maths would be a real improvement.
* **Reconcile the version numbers.** `pom.xml` says `2.0.0`, `plugin.yml` says
  `2.0`, and `StaticValues.VERSION_TEXT` says `v2.1.0`.

## Reporting bugs

Include your server version and platform (Paper/Spigot), the Landlord version,
what you did, what you expected, and the relevant server log. If the server
crashed, the stack trace is the most useful part.

## License

By contributing you agree that your contributions are licensed under the
[MIT License](LICENSE).
