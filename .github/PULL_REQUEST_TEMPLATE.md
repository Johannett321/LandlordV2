<!--
Thanks for contributing! Keep the change focused — one feature or fix per PR.
See CONTRIBUTING.md for conventions.
-->

## What does this change?

<!-- A sentence or two. Link any related issue with "Fixes #123". -->

## How did you test it?

<!--
There is no automated test suite, so say what you actually ran. For example:
"Started a game, levelled to S1L3, confirmed the arena event fires and the
rented pickaxe is returned correctly."
-->

## Checklist

- [ ] It builds (`make build`)
- [ ] I ran it on a server and the behaviour is what I describe above
- [ ] User-facing strings go through `LangDict.getString(...)`, with the key added
      to `en.json`, `nb-no.json` and `nn-no.json`
- [ ] New commands are registered in `Main.onEnable()` **and** declared in `plugin.yml`
- [ ] Balance values live in `StaticValues.java`, not scattered through the code
