# Reddidn't 2.0.0

Reddidn't 2.0.0 is a major rebuild focused on making the module reliable and maintainable.

## Highlights

- Updated promoted-post and comment-ad blocking for Reddit 2026.28.0, with matchers designed to tolerate minor nearby-version drift.
- Migrated the module to modern libxposed API 101 and LSPosed 2.0.x.
- Simplified the app to two independent ad-blocking toggles with a focused Material 3 interface.
- Prevented the comment-ad loading placeholder from flashing before it is blocked.
- Added installed-Reddit compatibility information and guidance for newer Reddit versions.
- Added matcher tests, maintenance documentation, Obtainium installation, and public contribution guidance.

## Breaking Changes

- Features unrelated to ad blocking have been removed.
- An API-101-compatible LSPosed 2.0.x installation is required.

## Compatibility

This release was developed and validated with Reddit 2026.28.0 (`2628061`). Nearby newer versions may work, but compatibility is not guaranteed until they have been tested.
