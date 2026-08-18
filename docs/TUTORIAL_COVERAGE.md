# Tutorial Coverage

Standing rule, effective from the Compose-UI phase onward: **every user-facing feature added to
this app must ship with an in-app explanation of how to use it.** This file tracks which
features have that, so it's a checklist to update — not just a promise. When a phase adds or
changes user-facing behavior, update this table in the same patch.

## Mechanism

Two onboarding systems currently coexist, on purpose — they cover different UI stacks:

| Mechanism | Where it lives | Used for |
|---|---|---|
| `TapTargetView` coach-marks | `PlayerActivity.java`, gated by `Prefs.firstRun` / `markFirstRun()` | The legacy XML control surface (pre-existing, from before this modernization effort) |
| `FeatureTourOverlay` (Compose) | `core-design-system`, gated by a per-feature `Prefs` boolean + `markXTourSeen()` method, mirroring the `firstRun` pattern exactly | Every new Compose-based surface added from Phase 1 onward |

**New features extend `FeatureTourOverlay`, not a new mechanism.** Adding a third onboarding
system would fragment the experience; the point of this file is to make that an easy rule to
follow, not an easy rule to forget.

## Coverage table

| Feature | Phase | Tutorial | Status |
|---|---|---|---|
| Opening a file (`buttonOpen`) | pre-existing | `TapTargetView` coach-mark on first run | ✅ Done (inherited) |
| New Compose control surface (glass controls, scrubber) | Phase 1 | `FeatureTourOverlay`, 3 steps, `Prefs.hasSeenComposeControlsTour` | ✅ Done |
| Library icon → Continue Watching / History / Favorites | Phase 2 | Covered as step 2 of the same Compose-controls tour above | ✅ Done |
| Settings → Experimental toggle | Phase 2 | Explained via the preference's own summary text (on/off states), not a separate tour | ✅ Done (lightweight — a single toggle doesn't need a full tour) |
| Gestures (swipe brightness/volume/seek), track selection, subtitle styling, PiP | pre-existing / not yet ported to Compose UI | None yet for the new UI (legacy UI has no dedicated tour either — a real pre-existing gap) | ⚠️ Not covered — flagged, not silently skipped |
| Equalizer settings screen (`EqualizerActivity`) | Phase 3 | Explained via the preference's own summary text, same lightweight treatment as the Experimental toggle — it's a settings screen, not a new part of the playback UI, so a full tour would be overkill | ✅ Done (lightweight) — **but note the screen itself doesn't yet affect playback**, which is stated directly in its summary text rather than only in code comments |
| 10-band graphic EQ DSP engine (`core-audio-dsp`) | Phase 3 | N/A — not yet wired to playback, nothing for a user to learn how to use yet | ⏳ Add a real tour once the engine is connected to actual audio output |

## How to add tutorial coverage for a new feature

1. Add `TourStep`s describing the feature to the relevant `FeatureTourOverlay` call site (or a
   new one, if the feature lives in a screen that doesn't have a tour yet) — for a full playback
   or navigation feature. For a simple settings toggle/screen, a clear preference summary string
   is proportionate instead (see the Experimental toggle and Equalizer entry above).
2. If using `FeatureTourOverlay`: add a `Prefs` boolean + `markXTourSeen()` method, copying the
   exact pattern of `hasSeenComposeControlsTour` / `markComposeControlsTourSeen()` in `Prefs.java`.
3. Add a row to the coverage table above in the same patch — not as a follow-up.
