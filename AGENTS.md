<!-- pico-cli:plugin-context:pico-spatial-agentic-tools:start -->
## Plugin Context

Also read `./PICO-SPATIAL-AGENTIC-TOOLS.AGENTS.md` for PICO Spatial plugin guidance.
<!-- pico-cli:plugin-context:pico-spatial-agentic-tools:end -->

<!-- pico-cli:plugin-context:pico-general-agentic-tools:start -->
## Plugin Context

Also read `./PICO-GENERAL-AGENTIC-TOOLS.AGENTS.md` for PICO Spatial plugin guidance.
<!-- pico-cli:plugin-context:pico-general-agentic-tools:end -->

## Minimal Spatial onboarding demo

- Purpose: compile and smoke-test the official planar PICO template; the window displays `PICO Spatial Ready`.
- Temporary demo application ID: `com.example.picosmokedemo.p7c91e42a`. Preserve this identity for subsequent runs.
- SDK: Spatial BOM 6.1.9; knowledge line 6.1; Android API 35; ARM64.
- `Main.kt` owns `DefaultWindowContainer` and wraps content in `PicoTheme`; `content/HomePage.kt` provides the minimal SpatialUI text.
- `platform/SpatialApplication.kt` starts the Spatial app; `platform/LaunchActivity.kt` is its launcher. The manifest retains system-default glass.
- All 2D UI must use SpatialUI and PicoTheme; no Compose Material/Material3, native palette overrides, custom root backgrounds, or custom hover implementation.
- Build: set JAVA_HOME to a Java 21 JDK, then run `./gradlew assembleDebug`. This machine's JDK is `/Users/apple/.pico/primer-cli/jdk/jdk-21.0.12.1+1/Contents/Home`.
- When network access requires the existing local proxy, pass `-Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7897 -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7897` to Gradle. Do not commit proxy settings into shared project configuration.
- APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Device installation and launch require a compatible PICO device/emulator and the spatial-emulator-usage skill. Build success alone does not verify device behavior.
- Natural next step: install this demo on a PICO target, verify its window, then design the intended application before adding product UI.
