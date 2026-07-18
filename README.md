<div align="center">
    <img src="https://github.com/user-attachments/assets/d2c4a227-a671-46b2-82a7-8662bee3458d" alt="Reddidn't logo" width="120"/>

# Reddidn't

</div>

Reddidn't is a focused Reddit ad blocker built as a modern libxposed module. It uses DexKit to locate ad-rendering methods by stable strings and structural characteristics instead of relying on obfuscated class or method names.

## Features

- Block promoted posts in feeds.
- Block ads in comment threads.
- Toggle each blocker independently without restarting Reddit.

## Compatibility

The current matchers target Reddit 2026.28.0 (`2628061`) and tolerate minor signature drift in nearby releases. Later Reddit versions are best-effort until they have been tested on a device.

## Requirements

- A rooted Android device.
- An API-101-compatible LSPosed 2.0.x installation.
- Reddit selected in the module's scope.

LSPatch is not supported.

## Building

```bash
sh gradlew testDebugUnitTest assembleDebug
```

The hook-matching rules and maintenance workflow are documented in [docs/HOOK_MAINTENANCE.md](docs/HOOK_MAINTENANCE.md).

## Community

- [Telegram Channel](https://t.me/reddidntapp)
- [Telegram Discussion Group](https://t.me/reddidntappdiscussion)

## License

Reddidn't is licensed under the [GNU General Public License v3.0](LICENSE).
