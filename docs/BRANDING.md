# Branding: Lucent

## What changed

- **Display name**: "Just Player" → **Lucent** (`app_name` in `strings.xml`, both the `main`
  and `legacy` flavors).
- **App icon**: new adaptive icon — a diagonal violet→cyan gradient (`@color/lucent_violet`
  `#7A53FF` → `@color/lucent_cyan` `#3DE0C8`, matching `PlayerColors.AccentGradient` in
  `core-design-system` exactly) with a soft glow and a rounded play triangle. Replaces the
  original flat green background + sharp white triangle.
  - `app/src/main/res/drawable/ic_launcher_background.xml` / `ic_launcher_foreground.xml` —
    vector, used on API 26+ (adaptive icon).
  - `app/src/main/res/mipmap-{m,h,xh,xxh,xxxh}dpi/ic_launcher.png` — flat raster fallback for
    API < 26, rendered from the same design (Pillow-generated, visually verified — see below —
    then downscaled, so these are pixel-accurate to the intended design, not a hand-approximation).
  - `web_hi_res_512.png` (repo root) and `fastlane/metadata/android/en-US/images/icon.png` —
    same design at store-listing resolution.
- **Release artifact naming**: `archivesBaseName` in `app/build.gradle` — `Just.Player.v*` →
  `Lucent.v*`. Purely the output `.apk` filename prefix.
- **README** title and a new attribution line crediting the upstream project.

## What deliberately did NOT change, and why

- **`applicationId` stays `com.brouken.player`.** This is the app's actual identity as far as
  Android, the Play Store, and every existing install are concerned. Changing it would make this
  register as a *different app* — existing users would not get an update, they'd need to
  install a second, separate app alongside (or instead of) the original. That's a real
  business/distribution decision, not a branding one, and not mine to make unilaterally.
- **README badges** (GitHub releases, Google Play, F-Droid, Weblate, subreddit) still point to
  the **upstream** `moneytoo/Player` project's actual accounts — its Play Store listing, its
  F-Droid page, its subreddit. They're accurate *as descriptions of the upstream project this is
  forked from*, but they are not this fork's own accounts. Repointing them to placeholder/
  nonexistent "Lucent" accounts would be worse than leaving them as-is. If/when this fork
  publishes its own Play Store listing, F-Droid entry, etc., those badges should be swapped for
  the real ones then — not before.
- **`com.brouken.player.yml`** (F-Droid submission metadata, `AuthorName: Marcel Dopita`) is the
  *upstream author's* actual F-Droid metadata. Left untouched — relabeling someone else's
  F-Droid submission under a new name would misrepresent whose submission it is.

## Icon design process

Generated with Pillow (Python) rather than hand-authored blind: a 1024px master was rendered,
visually inspected (including simulated at 48×48 — the smallest real launcher size — and with a
circular adaptive-icon mask applied) before being finalized and downscaled to every required
size. The vector adaptive-icon version approximates the raster's soft glow with four concentric
low-opacity circles (VectorDrawable has no blur primitive) and uses a sharp-cornered triangle
rather than the raster's rounded one, since hand-writing a verified rounded-corner path in raw
vector syntax carries real risk of a subtle, uncatchable-without-a-renderer error — a deliberate,
stated trade-off, not an oversight.
