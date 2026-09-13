# VerveDo Plus

> [!IMPORTANT]
> **This repository is a third-party fork of [Super12138/VerveDo](https://github.com/Super12138/VerveDo) — it is NOT the official version.**
>
> - **Original author / copyright holder: [Super12138](https://github.com/Super12138)**
> - **Maintainer of this fork: [xsnj-9527](https://github.com/xsnj-9527)**
> - Based on upstream **3.5.0** (versionCode 1243), licensed under **[GPL-3.0-only](./LICENSE)**
>
> The design, architecture and the vast majority of the code come from the original author. This fork only contains the limited changes listed below, and is released under GPL-3.0-only as well.
> Please report **upstream** issues to the [upstream repository](https://github.com/Super12138/VerveDo/issues). Only bugs in features added by this fork belong [here](https://github.com/xsnj-9527/VerveDoPlus/issues).

A simple to-do app that follows Material 3 Expressive, using Jetpack Compose.

[简体中文](./README.md) | **English**

## 🔀 Differences from upstream

Every change in this fork relative to upstream `3.5.0`:

| # | Change | Details |
| --- | --- | --- |
| 1 | **Custom text size** | New presets under Settings → Appearance → Text Size: Follow system / 80% / 85% / 90% / 95% / 100% / 110% / 120%. Implemented by overriding `LocalDensity.fontScale`, so it scales **all** text measured in sp (including hardcoded `fontSize` values), not just the theme typography |
| 2 | **Home screen widget** | Modelled after the system Notes card: accent-coloured icon + title + circular "+" button on top, pending tasks below. Tap any row to complete it; the "+" goes straight to Tasks → Add Task. Row count adapts to the widget height (up to 5); a "N more pending" line appears when they don't all fit |
| 3 | **App identity** | App name is `VerveDo Plus`; `applicationId` is `io.github.xsnj9527.vervedoplus` (upstream: `cn.super12138.todo`). Different package names, so **both can be installed side by side** |
| 4 | Version numbers | `versionCode 1244` / `versionName 3.5.0-plus.1`, distinct from upstream 3.5.0 so bug reports can be triaged correctly |

> [!NOTE]
> The internal Kotlin package is still `cn.super12138.todo`. This is **intentional**: keeping the upstream package structure makes it far easier to merge upstream fixes later. What changed is the external `applicationId`, not the internal package.

## 📦 Supported Versions

Supports `Android 8.0 (Oreo)` through `Android 17.0 (Cinnamon Bun)`

## ⬇️ Download

Download the APK from [Releases](https://github.com/xsnj-9527/VerveDoPlus/releases).

> [!WARNING]
> This fork is signed with its **own key**, which differs from both upstream and the F-Droid build.
> If you already have upstream VerveDo installed, you must uninstall it first (or export a backup via Settings → Data → Backup, then restore it afterwards).
> Since the `applicationId` also differs, the two versions can actually coexist with independent data.

## 🔨 Building

```bash
git clone https://github.com/xsnj-9527/VerveDoPlus.git
cd VerveDoPlus
./gradlew assembleRelease
```

Requires **JDK 21** and the Android SDK (compileSdk 37).

If no signing config is provided, release builds fall back to the debug key. To use your own key, supply it via `gradle.properties` or the command line:

```
releaseStoreFile=/path/to/your.jks
releaseStorePassword=...
releaseKeyAlias=...
releaseKeyPassword=...
```

## 📃 License

[GPL-3.0-only](./LICENSE)

As a derivative work of a GPL-3.0 project:

- The original copyright notices and the `LICENSE` file are kept intact and unmodified;
- This fork is likewise released under GPL-3.0-only and **cannot** be relicensed;
- The complete source of this fork is this repository, satisfying GPL-3.0 §6 ("Conveying Non-Source Forms");
- Any further redistribution of this fork must also comply with GPL-3.0.

## 🙏 Credits

- Upstream project: [Super12138/VerveDo](https://github.com/Super12138/VerveDo)
- The excellent open-source libraries used by upstream are listed in-app under Settings → About → Licences
