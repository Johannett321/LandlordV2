# Security Policy

## Supported versions

Landlord is a hobby project maintained on a best-effort basis. Only the latest
commit on `main` is supported — there are no backported fixes for older builds.

| Version | Supported |
| --- | --- |
| `main` | ✅ |
| Older tags / paid 1.x builds | ❌ |

## Reporting a vulnerability

**Please do not open a public issue for a security problem.**

Report it privately through GitHub:

➡️ **[Open a private security advisory](https://github.com/Johannett321/LandlordV2/security/advisories/new)**

Please include what an attacker can do, the steps to reproduce it, and your
server software and version. You will get a response as soon as the maintainer
can look at it; this is a spare-time project, so please allow some time before
disclosing publicly.

## What counts as a vulnerability here

Landlord runs inside a Minecraft server, so the interesting cases are things a
player can do that the game should not allow:

* Bypassing the economy — creating money, dodging tax, free chunks or items
* Escaping jail, the arena, or another player's land restrictions
* Reading or modifying another player's data
* Crashing or hanging the server from in-game input
* Anything reachable through the built-in web dashboard on port `25566`

### Note on the dashboard

The dashboard has **no authentication** and exposes player names, statuses and
bank balances to anyone who can reach the port. That is by design — it is meant
for a LAN party screen. Do not expose port `25566` to the internet. Reports that
the dashboard is unauthenticated are not treated as vulnerabilities, but ways to
reach *beyond* read-only game data through it certainly are.

### Out of scope

* Anything requiring operator or console access — operators can already do
  everything
* Vulnerabilities in Paper, Spigot, Vault, EssentialsX or Multiverse-Core;
  report those to their respective projects
* Cheating via a modified client that the server would accept anyway
