# Hook maintenance

Reddidn't has two independent ad-blocking features. Each feature declares:

- a stable string to search for with DexKit;
- a small predicate over stable package, return-type, and parameter-envelope signals;
- the remote-preference key that enables the hook.

The current strings are `promoted_post_unit` for feed ads and `blank_ad_container` for comment ads. They were observed on Reddit 2026.28.0 (`2628061`) and are test fixtures, not a promise of continued compatibility.

## Current compatibility baseline

Static analysis of Reddit 2026.28.0 found:

- `promoted_post_unit` remains in the feed-ad renderer, but Compose and feed parameter types are now obfuscated;
- the same promoted string also appears in a shared synthetic dispatcher, so the matcher additionally requires the stable ads feed-composable package and a Compose-style method envelope;
- `conversation_screen_ad` identifies the loaded comment-ad renderer, but hooking it allows Reddit's earlier loading placeholder to appear briefly;
- `blank_ad_container` identifies the parent comment-ad slot dispatcher, which handles placeholder, loaded-ad, and empty states, so intercepting it prevents the slot from being composed at all.

JADX's normal output skips the large dispatcher after a type-inference failure and therefore hides its string literals. Re-run single-class decompilation with debug comments and bad-code output when validating this anchor rather than concluding that it was removed from the bytecode.

The exact observed shapes live in `AdBlockMatcherTest` as regression fixtures. Production rules intentionally use parameter ranges and stable relationships instead of exact obfuscated signatures. Discovery fails closed unless exactly one method matches a feature. Ad delivery is nondeterministic, so matcher selection, hook installation, and enabled/disabled visual behavior should all be rechecked when adapting a new Reddit APK.

## Adapting to a Reddit update

1. Obtain the target APK and confirm its exact version.
2. Search the decompiled APK for the existing target strings.
3. Compare candidate method structures with `PromotedPostBlocker` and `CommentAdBlocker`.
4. Change only the relevant feature's target string or structural predicate. Prefer broad, stable constraints paired with the single-candidate safety check.
5. Add the newly observed method shape as a unit-test fixture before device testing.
6. Install a debug build, restart Reddit, and check the `Reddidnt` logs for candidate descriptors and installed hook counts.
7. Exercise feeds and comment threads with each toggle both enabled and disabled.

Do not add obfuscated class or method names to production matching rules. Do not install multiple broadly matched hooks: ambiguous discovery must fail closed. If a broad discovery tool becomes necessary, build it as separate debug tooling rather than adding alternate production hook paths.
