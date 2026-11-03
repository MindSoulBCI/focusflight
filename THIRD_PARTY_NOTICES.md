# Source provenance

The local prototype adapts BODY and GLASS procedural vehicle geometry and the loft geometry helper from Ozgur Ozer's repository:
https://github.com/ozgrozer/tesla-roadster-sim
Pinned source revision: 3e7d11beb21d2e5dbb16bdc7d321093372127237.

The source reference is recorded here for attribution, not displayed in the product. No original branded plates, logos, driver, dashboard, product names or screens are included in the packaged model. Lift pods and nozzles were added locally. The web simulator itself is not embedded; its geometry is converted to GLB and the interaction/flight logic is implemented natively for PICO Spatial SDK.

No root project license was found at the inspected revision. This is a local development prototype; public redistribution of the derived geometry requires clarification of permission from its author. This notice does not imply that the upstream project is licensed under the license of its vendored dependencies.

Three.js is used only by the local conversion tool. Its upstream license is preserved alongside the development source clone at .scratch/upstream/flight-reference/vendor/three/LICENSE. It is not bundled as JavaScript in the APK.

City/effects update: `tools/export-world-fx.mjs` adapts the shock-diamond profile from upstream `js/render/effects.js` and references the visual structure of `cityGround.js` / `citySky.js`. It produces new mesh assets for the native runtime; the original WebGL shaders, full city simulation, browser renderer and weather system are not embedded.

## Airborne coin collection (1.2)
- Model: Low poly coin 3d, iamsujitcu, https://opengameart.org/content/low-poly-coin-3d — CC0 1.0 (https://creativecommons.org/publicdomain/zero/1.0/).
  Original coin.blend and texture archive retained in assets-source/coins. Original mesh and colour texture converted to GLB at 44 mm diameter; rotation driven by Spatial SDK. Reproduction script: tools/convert_coin.py (extract texture archive to .scratch/coins/coin_textures first).
- Pickup sound: 12 Coin Sound Effects, StarNinjas, https://opengameart.org/content/12-coin-sound-effects — CC0 1.0. Author: https://opengameart.org/users/starninjas . Original archive retained; coin.1.ogg used unchanged as res/raw/coin_pickup.ogg.
