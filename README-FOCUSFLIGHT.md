# MindSoul 念力飞车 1.1

PICO 系统主空间应用。应用 ID 保持 com.example.picosmokedemo.p7c91e42a；Spatial SDK 6.1.9，Android API 35，ARM64。本包为 Debug 本地调试签名，未上架。

## 本次更新
- 默认汽车及其推进器、悬浮光环、粒子、碰撞范围整体放大 1.5 倍，车身约从 32.4 cm 增至 48.6 cm。
- 训练目标光环同步放大 1.5 倍，车辆操作速度和活动范围保留。
- 启动图标、圆形图标与账号窗口使用用户提供的新橙色汽车 Logo；保留原图内容，移除旧 3D 图标配置。
- 版本号 1.1 / versionCode 2。

## 安装和使用
使用 pico-cli app install <APK路径> --device <设备ID> 安装。
使用 pico-cli app launch com.example.picosmokedemo.p7c91e42a --activity .platform.LaunchActivity --device <设备ID> 启动。
账号服务尚未配置，可选择“游客试玩”进入；右侧控制台提供开始、悬停、升降、转向、复位和专注力模拟控制。BrainLink 接口保留，未连接真实设备时不代表真实脑电。

## 构建
使用 Java 21 执行 ./gradlew assembleDebug testDebugUnitTest。首次构建需能下载 Gradle 依赖，Android SDK API 35 路径由本机 local.properties 配置。不要将本机 SDK 绝对路径提交到工程。

## 验证
Debug 构建和 21 项单元测试通过，原生 UI 样式检查 0 错误、0 警告。APK 已确认包含新图标。此次大小与图标更新尚未做新的头显/模拟器视觉验收。

原始汽车及移植范围、许可证见 THIRD_PARTY_NOTICES.md。账号 API 与 BrainLink 接口文档位于 docs/。

### 1.2 空中寻金
首次启动随机生成 20 枚原版 OGA 金币；驾驶或拖动车身触碰即消失并 +1 分。金币旋转展示。游客试玩/进入飞车时同时打开原生独立得分窗口，也可从控制台重新打开。
得分窗口支持补充 20 枚（场上最多 60 枚）、清零重开和拾取音效开关。得分只保留本次进程；复位飞车不计瞬移路径得分。金币使用可到达区域内的随机位置，生成时避开车身。声音默认开启，使用 StarNinjas CC0 真实硬币音效。
