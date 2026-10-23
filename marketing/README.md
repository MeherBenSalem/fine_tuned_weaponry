# Marketing assets: 3.0 "Rise of the Runeborn"

Listing copy and artwork for CurseForge and Modrinth. Nothing here ships in the mod JARs.

## Listing

| File | Use |
| --- | --- |
| [`curseforge/description.md`](curseforge/description.md) | Full project description (Markdown; also works on Modrinth) |
| [`curseforge/summary.txt`](curseforge/summary.txt) | Short project summary (209 characters) |
| [`../patchnotes/3.0.0.md`](../patchnotes/3.0.0.md) | File changelog used by the publish workflow |

Image links in the description point at
`raw.githubusercontent.com/MeherBenSalem/fine_tuned_weaponry/main/marketing/images/...`, so they work
once this folder is on `main`. If the repository is renamed, replace the repository part of each URL.
To host on CurseForge instead, upload the images to the project gallery and replace each URL.

## Images

| File | Size | Use |
| --- | --- | --- |
| `images/banner.png` | 1800×600 | Description header |
| `images/icon.png` | 512×512 | Project avatar (the crowned colossal sword from the mod icon) |
| `images/card-levelup.png` | 1600×900 | Shrine Level Up with the RPG level preview |
| `images/card-armaments.png` | 1600×900 | Weapon tooltips: scaling, requirements, arts and spells |
| `images/card-forge.png` | 1600×900 | Reinforce and Infuse |
| `images/card-shrine.png` | 1600×900 | Kindling a shrine, and the merchant |
| `images/card-sovereigns.png` | 1600×900 | Morgrath, Kaelthar and Sylvara |
| `images/card-ras.png` | 1600×900 | Shared leveling with RPG Attribute System |
| `images/card-codex.png` | 1600×900 | The Codex's Sovereigns chapter |
| `images/card-hud.png` | 1600×900 | HUD with a Sovereign boss bar |
| `images/card-config.png` | 1600×900 | `runeborn-server.json` highlights |
| `images/header-*.png`, `images/divider.png` | 1400 wide, transparent | Section headings for the description |
| `images/gifs/shrine-kindle.gif` | 720×405, 4.9 s | Kindling an Ember Shrine: flame column, banner, runes counting in |
| `images/gifs/sovereign-summon.gif` | 720×405, 4.9 s | Raising the Sigil of Morgrath: lightning, ash and the Sovereign's banner |
| `images/gifs/level-up-flow.gif` | 800×450, 6.9 s | Staging five points at a shrine, confirming, and the RPG Attribute System level-up banner |
| `images/screenshots/*.jpg` | 1920×1080 | Gallery uploads and card art |

### How the screenshots were captured

Every screenshot is an unedited capture of the 1.21.1 Fabric development client at 1920×1080, GUI
scale 3, render distance 8, with software rendering and the default configuration. They come from the
scripted showcase in
[`versions/1.21.1/common/.../client/Showcase.java`](../versions/1.21.1/common/src/main/java/io/nightbeam/runeborn/client/Showcase.java):

- The world is a normal world with seed `runeborn`. The scene is a cherry grove clearing at
  847, 607, at time 12700 (sunset), looking west.
- The script kindles an Ember Shrine (`kindled`, `shrine`), prepares a level 58 Ronin and opens each
  menu. Sovereigns are posed with their AI off and the world frozen (`/tick freeze`), so nothing in the
  frame is staged by hand. `shrine`, `boss-morgrath` and `boss-sylvara` have the HUD hidden (F1).
- `levelup`, `ras-screen` and `ras-hud` come from a second pass with RPG Attribute System 5.0.0 and
  jauml installed (`RUNEBORN_SHOWCASE=ras`). That pass buys three rune levels, so the RPG level is 3.

To recapture, generate the world with the dedicated server (`level-name=showcase`,
`level-type=minecraft:normal`, `level-seed=runeborn` in `fabric/runs/server/server.properties`), copy
it to `fabric/runs/client/saves/showcase`, set `guiScale:3`, and run from `versions/1.21.1`:

```bash
RUNEBORN_SHOWCASE=1 ./gradlew :fabric:runClient      # full set
RUNEBORN_SHOWCASE=ras ./gradlew :fabric:runClient    # RPG Attribute System set (its jar and jauml in runs/client/mods)
```

Screenshots land in `fabric/runs/client/screenshots/runeborn_<name>.png`. Save them as
`images/screenshots/<name>.jpg`.

### How the GIFs were recorded

The same script records them, saving one frame per client tick with the game slowed to 2 ticks per
second (`/tick rate 2`, a tenth of normal speed) so software rendering draws every tick. Played back
at 20 fps, they run at real game speed. Runeborn's animations follow game ticks; RPG Attribute System's
follow `-Dras.uiTimeScale=0.1`, which matches.

```bash
RUNEBORN_SHOWCASE=gifs ./gradlew :fabric:runClient       # shrine-kindle, sovereign-summon
JAVA_TOOL_OPTIONS=-Dras.uiTimeScale=0.1 RUNEBORN_SHOWCASE=gifs-ras ./gradlew :fabric:runClient   # level-up-flow
```

Frames land in `fabric/runs/client/screenshots/runeborn_gif-<name>-<frame>.png`. Encode them with
ffmpeg (`level-up-flow` also crops to the menu with `crop=1440:810:240:135,` before `scale=800`):

```bash
ffmpeg -framerate 20 -i runeborn_gif-kindle-%03d.png -vf "scale=720:-1:flags=lanczos,split[a][b];\
[a]palettegen=stats_mode=diff[p];[b][p]paletteuse=dither=bayer:bayer_scale=5:diff_mode=rectangle" \
  -loop 0 shrine-kindle.gif
```

## Regenerating the artwork

The cards, banner, icon and headers are HTML rendered with Playwright from the screenshots above and
the bundled fonts:

```bash
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs              # everything
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs banner icon  # selected assets
```

- `source/backdrop.jpg` is the blurred background scene, a downscaled copy of `screenshots/shrine.jpg`.
- `source/blade.png` is the crowned colossal sword sprite used in the mod icon (`tools/assetgen`).
- `source/fonts/` holds Cinzel and EB Garamond with their SIL Open Font License 1.1 texts. The script
  uses only the files in `source/` and the screenshots.

## Wording

Describe the mod as soulslike. Do not use other games' names, logos or art in listing images or
titles. Every number in the copy and on the cards comes from the core catalog or the default
`config/runeborn-server.json`. Update the copy when those change.
