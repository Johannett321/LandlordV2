<div align="center">

# Landlord

**A Minecraft survival game mode about land, money and taxes.**

You start with a single 16×16 chunk. Everything else — more land, better tools,
a business, a cabin — has to be earned, bought, and taxed.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://adoptium.net/)
[![Paper / Spigot 1.21](https://img.shields.io/badge/Paper%20%7C%20Spigot-1.21-brightgreen.svg)](https://papermc.io/)

📺 **[Watch the trailer](https://www.youtube.com/watch?v=hWlZg3kGshs)** · 🎓 **[How to play](https://www.youtube.com/watch?v=0k2bbCHc-2E)**

</div>

---

## What is Landlord?

Landlord is a Bukkit/Spigot plugin that turns a vanilla Minecraft server into a
survival economy game for up to 8 players.

When the admin starts the game, everyone gets five minutes to gather whatever
they can carry. Then the world closes in: each player is teleported into their
own 16×16 chunk and the borders go up. From that point on, progress is bought,
not mined.

You level up by donating items to the Landlord. Each level pays out chunk
points, which buy adjacent land and tear down the walls between you and your
neighbours. Meanwhile the Landlord takes a cut of everything — every purchase,
every sale, and a standing tax on every chunk you own.

### Core mechanics

| System | What it does |
| --- | --- |
| **Chunks** | Start with one 16×16 chunk. Spend chunk points earned from leveling to claim adjacent land. |
| **Leveling** | 3 seasons of 9 levels. Donate the required items with `/donate` to advance and unlock new commands. |
| **Taxes** | A cut on every transaction plus a recurring per-chunk tax. Check exposure with `/bal`. |
| **Economy** | Sell items at fluctuating prices with `/sell`, pay other players with `/pay`. |
| **Stocks** | Six tradable companies (MineDonalds, GoldDiggers, BlockBNB, ElCarts, CakeFarmers, NetherExpress) driven by pre-generated market flow data. |
| **Businesses** | Buy an Export, Insurance, or Bank business for passive income — and the risk that comes with it. |
| **Renting** | Rent a pickaxe, axe, shovel, sword, elytra or turtle shell. Fail to return it and you pay full price, or go to jail. |
| **Treasury** | Players elect a Treasury Chancellor who spends collected tax on server-wide perks. |
| **Events** | Arena fights, adventure maps, mystery events and tax events fire between levels. |
| **Extras** | Roulette, mystery chests, daily bonuses, cabins, shops, jail, chunk guards, VIP lounge. |

### Live dashboard

The plugin runs a small Jetty web server on port **25566**. Open
<http://localhost:25566> while a game is running for a live view of who is
online, the current season and level, the items still needed to level up, every
player's status, and everyone's bank balance. It refreshes every 5 seconds and
is designed to be thrown up on a second screen or a TV during a LAN party.

---

## Quick start

**Requirements:** [Docker](https://docs.docker.com/get-docker/), a JDK 17 or
newer, and Maven.

> **⚠️ Landlord needs the `lladv` world.** Its arena, lounge, adventure sites
> and starting area are hand-built at fixed coordinates in a custom world named
> `lladv`, and the plugin **stops the server if that world never loads**.
> Download it from the
> [latest release](https://github.com/Johannett321/LandlordV2/releases) and
> unpack it to `run/data/lladv` (so that `run/data/lladv/level.dat` exists)
> before you start. See [docs/WORLD.md](docs/WORLD.md).

```bash
git clone https://github.com/Johannett321/LandlordV2.git
cd LandlordV2
cp .env.example .env    # set MINECRAFT_OPS=YourMinecraftUsername
make dev
```

`make dev` compiles the plugin and boots a fully configured Paper server on
`localhost:25565`. On first run it downloads Paper plus the three plugins
Landlord depends on (Vault, EssentialsX, Multiverse-Core) and generates the
world, so give it a few minutes.

Then join `localhost` from your Minecraft client and run `/landlord` to start a
game, and open <http://localhost:25566> for the live dashboard. New players get
an in-game Playguide book on first join — the
[how-to-play video](https://www.youtube.com/watch?v=0k2bbCHc-2E) covers the same
ground.

### Other make targets

```bash
make build     # compile to target/Landlord.jar only
make up        # start the server without rebuilding
make logs      # follow the server log
make console   # attach to the interactive server console
make stop      # stop the server
make reset     # delete all server state and start over
```

### Installing on an existing server

If you already run a Paper or Spigot 1.21 server, skip Docker entirely:

```bash
mvn package -Dlandlord.outputDirectory=/path/to/server/plugins
```

Make sure Vault, an economy provider (EssentialsX works), and Multiverse-Core
are installed, and that the `lladv` world is imported with
`/mv import lladv normal`.

---

## Configuration

Landlord writes a `landlord.config` file into its plugin data folder on first
run:

```properties
LANGUAGE=en
```

`LANGUAGE` accepts `en`, `nb-no` (Norwegian Bokmål) or `nn-no` (Norwegian
Nynorsk). Players can also switch their own language in-game with
`/changelang <code>`. Translations live in
[`src/main/resources/languages/`](src/main/resources/languages/) — adding a new
one is just a new JSON file.

Game balance constants (prices, tax rates, max players) are collected in
[`StaticValues.java`](src/main/java/com/johansvartdal/landlord/StaticValues.java).

---

## Commands

Most commands are locked until you reach the level that unlocks them.

| Command | Description |
| --- | --- |
| `/landlord <command>` | Start and administer the game |
| `/buychunk [now\|info]` | Claim the chunk you are looking at |
| `/upgrade [info\|accept]` | See level requirements and level up |
| `/donate` | Donate the held item toward the next level |
| `/bal` | Balance and tax overview |
| `/sell [all\|info]` | Sell the item in your hand |
| `/pay <user> <amount>` | Pay another player |
| `/stocks [buy\|sell\|info]` | Trade stocks |
| `/business <command>` | Run your business |
| `/rent [name]` | Rent a tool |
| `/home`, `/sethome` | Teleport home, set your home |
| `/trade`, `/settrade` | Teleport to the shared trade chunk |
| `/visit [user\|accept\|reject]` | Visit another player's land |
| `/sendhome <user>` | Send a visitor off your land |
| `/wilderness` | Teleport to the wilderness |
| `/treasury` | Treasury Chancellor commands |
| `/claimbonus` | Claim the daily reward |
| `/joinroulette` | Join the roulette |
| `/capture` | Capture a nearby animal |
| `/fly [now\|end\|info]` | Paid flight |
| `/chunkguard` | Keep your chunks loaded |
| `/cabin <command>` | Buy and manage a cabin |
| `/shop <command>` | Buy and manage a shop |
| `/lounge [now\|info]` | VIP lounge access |
| `/millionaire <command>` | Millionaire perks |
| `/status` | Status of all players |
| `/changelang <code>` | Change language |
| `/day`, `/night` | Clear weather, set time |
| `/adm` | Admin commands |

---

## Documentation

* **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** — how the codebase is laid out
* **[docs/WORLD.md](docs/WORLD.md)** — the `lladv` world and its fixed coordinates
* **[CONTRIBUTING.md](CONTRIBUTING.md)** — how to build, test and submit changes

---

## Contributing

Contributions are welcome. Bug reports, translations, balance tweaks and new
levels or events are all useful. Start with
[CONTRIBUTING.md](CONTRIBUTING.md).

## License

[MIT](LICENSE) © Johan Svartdal

Landlord was previously a paid, closed-source plugin. It is now free and open
source — there is no licence key, no subscription and no payment of any kind.
