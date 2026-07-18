# Contributing to Reddidn't

Contributions are welcome. In particular, help adapting the matchers to newer Reddit versions is greatly appreciated.

## Supporting New Reddit Versions

Compatibility pull requests should remain focused and preserve the matchers' fail-closed behavior. Please:

- State the Reddit version and version code you tested.
- Prefer stable strings and structural characteristics over obfuscated class or method names.
- Update the matcher fixtures and maintenance documentation when discovery rules change.
- Include relevant discovery logs and device-testing results when available.
- Run the unit tests, lint checks, and debug and release builds before submitting the pull request.

See [docs/HOOK_MAINTENANCE.md](docs/HOOK_MAINTENANCE.md) for the current matching architecture and adaptation workflow.

## New Features

Features beyond ad blocking are welcome when they fit the project and remain isolated from the existing blockers. Before starting a substantial feature, please open an issue so its design and maintenance cost can be discussed.

Please propose a new feature only if you intend to maintain it after merging, including adapting it when Reddit updates break it. This is not a permanent obligation, but the project should not accumulate features without an active maintainer. If you can no longer maintain a feature, let the project know; an unmaintained feature may eventually be removed to keep Reddidn't reliable and approachable.

## Pull Requests

- Keep each pull request focused on one change.
- Explain what changed, why it is needed, and how it was tested.
- Do not commit APKs, keystores, credentials, decompiled Reddit sources, IDE state, or other generated files.
- Keep diagnostics concise and avoid high-frequency logging in release builds.
- Follow the existing code style and favor small, understandable implementations.

Bug reports and documentation improvements are also welcome, even when you cannot provide a complete fix.
