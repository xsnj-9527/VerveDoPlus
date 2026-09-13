# VerveDo Plus

> [!IMPORTANT]
> **本仓库是 [Super12138/VerveDo](https://github.com/Super12138/VerveDo) 的第三方衍生版本（fork），不是官方版本。**
>
> - **原作者 / 版权所有者：[Super12138](https://github.com/Super12138)**
> - **本分支维护者：[xsnj-9527](https://github.com/xsnj-9527)**
> - 本分支基于上游 **3.5.0**（versionCode 1243）修改，遵循 **[GPL-3.0-only](./LICENSE)**
>
> 应用的设计、架构与绝大部分代码均出自原作者。本分支只做了下方列出的有限改动，并同样以 GPL-3.0-only 发布。
> 遇到**上游本身**的问题请反馈给[上游仓库](https://github.com/Super12138/VerveDo/issues)；只有本分支新增功能的 bug 才请提到[这里](https://github.com/xsnj-9527/VerveDoPlus/issues)。

一个简单的、遵循 Material 3 Expressive 的待办应用，使用 Jetpack Compose 编写。

## 🔀 与上游的差异

本分支相对于上游 `3.5.0` 的全部改动：

| # | 改动 | 说明 |
| --- | --- | --- |
| 1 | **自定义字号** | 「设置 → 外观和个性化 → 字体大小」新增档位：跟随系统 / 80% / 85% / 90% / 95% / 100% / 110% / 120%。通过覆写 `LocalDensity.fontScale` 实现，因此对**所有**以 sp 计量的文字生效（含代码中写死 `fontSize` 的地方），而不只是主题排版 |
| 2 | **桌面卡片（小组件）** | 形态参考系统「便签」卡片：顶部强调色图标 + 标题 + 圆形「+」，下方列出未完成任务。点任意一条即可勾选完成；右上角「+」直接进入「任务 → 添加任务」；条数按卡片高度自适应（最多 5 条），装不下时底部显示「还有 N 项未完成」 |
| 3 | **应用标识** | 应用名 `VerveDo Plus`；`applicationId` 为 `io.github.xsnj9527.vervedoplus`（上游为 `cn.super12138.todo`）。两者包名不同，**可以与上游版本共存** |
| 4 | 版本号 | `versionCode 1245` / `versionName 3.5.0-plus.2`，与上游 3.5.0 区分，便于排查问题时确认版本 |

> [!NOTE]
> 内部 Kotlin 包名仍保留 `cn.super12138.todo`。这是**有意为之**：保留上游包结构可以显著降低后续跟进上游修复时的合并成本。改变的是对外的 `applicationId`，不是内部包名。

## 📦 版本支持

支持 `Android 8.0 (Oreo)` 至 `Android 17.0 (Cinnamon Bun)`

## ⬇️ 下载

请从 [Releases](https://github.com/xsnj-9527/VerveDoPlus/releases) 下载 APK。

> [!WARNING]
> 本分支使用**自己的签名密钥**，与上游版本、以及 F-Droid 上的版本签名均不同。
> 如果你已经安装了上游 VerveDo，安装本分支前必须先卸载（或先用应用内「设置 → 数据 → 备份数据」导出，装好后再恢复）。
> 由于 `applicationId` 也不同，两个版本实际上可以同时安装、各自独立保存数据。

## 🔨 构建

```bash
git clone https://github.com/xsnj-9527/VerveDoPlus.git
cd VerveDoPlus
./gradlew assembleRelease
```

需要 **JDK 21** 与 Android SDK（compileSdk 37）。

Release 构建若未提供签名配置，会回退到 debug 签名。要使用自己的密钥，在 `gradle.properties` 或命令行提供：

```
releaseStoreFile=/path/to/your.jks
releaseStorePassword=...
releaseKeyAlias=...
releaseKeyPassword=...
```

## 📃 许可证

[GPL-3.0-only](./LICENSE)

本分支是 GPL-3.0 的衍生作品，因此：

- 原始版权声明与 `LICENSE` 文件完整保留，未作改动；
- 本分支同样以 GPL-3.0-only 发布，**不可**改用其他许可证；
- 本分支的完整源码即本仓库，符合 GPL-3.0 §6 关于「分发目标代码时须提供对应源码」的要求；
- 任何再次分发本分支的行为，同样需要遵守 GPL-3.0。

## 🙏 致谢

- 上游项目：[Super12138/VerveDo](https://github.com/Super12138/VerveDo)
- 上游所使用的优秀开源库，可在应用内「设置 → 关于 → 开源许可」中查看
