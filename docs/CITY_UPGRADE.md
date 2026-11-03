# 城市与推进特效升级 · 2026-10-23

根据用户要求，保留原项目车身与座舱几何，增加街道和天空，增强悬浮推进效果，并将控制面板游戏化。

## 实现

- 原项目 `js/render/effects.js` 中的 4.5 段 shock diamonds、白蓝喷芯、脉冲长度及按推力变化的视觉思路，改编为 PICO 原生分层喷流网格。
- 4 个向下悬浮喷口、4 个后部推进喷口、16 个重复使用的喷气粒子、4 个扩散气浪环。
- 悬停状态保留喷流；飞行状态按平滑的专注力增益增强尾焰。后台生命周期仍会暂停飞行。
- 静态城市采用原项目 cityGround/citySky 的沥青、车道线、路口、人行道、黄昏地平线与城市灯光元素重新制作，未声称移植整个浏览器城市模拟器。
- 程序化天空穹顶、稀疏云、街灯、城市天际线与路线环，约 13 个材质合批组；喷流/粒子共享克隆资源，避免逐个重新读取 GLB。
- 原生 SpatialUI 操作按钮和滑条，PicoTheme 原生配色未修改。独立深色数据面板用无衬线粗体中文、等宽大数字显示专注力/倍率/速度。

## 空间与界面

地面为 Y=0。飞车长 0.9m，初始位置 (0,1.5,-2.5)m；固定头部观察，应用不移动用户相机。HUD 1100×540dp，位于 (0,0.92,-1.65)m。飞行约束保持 x∈[-1.5,1.5]、y∈[0.7,2.4]、z∈[-4.5,-1.5]m；街道是环境，不代表已经开放无限城市飞行。

唯一应用绘制的数据面板表面 `flight-telemetry-console`：fill #142033，alpha 0.95，圆角 28dp；外层布局透明，不叠加玻璃材质。白色主文字、浅灰次文字、青色专注力、琥珀色倍率。字体为系统 SansSerif 与 Monospace，中文不再使用默认衬线回退。

之前 .scratch/design-spec.json revision 2 与网页预览属于早期 MR 房间方案，本次视觉基线以本文件和 PICO 实际运行截图为准。沿用用户已授权的直接 SDK 实现；之前官方工作流 JSON Schema 错误与 Editor 启动超时不再重复阻挡实现。这里使用本地 GLB 与运行时 Entity 特效，没有伪造 Editor bundle 交付。

## 验证范围

assembleDebug 与 11 项 FlightEngine 测试通过。PICO 模拟器安装、启动与真实截图确认城市、车模、喷流和 HUD 正常。真机手势、真实 BrainLink 与真机帧率仍待设备验证。

## 原生交互选择更新

新增 FlightInteraction WindowContainer，直接承载三种原生 Option。Stage 附属面板不支持直接承载该弹窗，因此由 LocalSpatialNavigator 打开独立窗口。选择期间引擎暂停并锁定操作，确认应用权限，取消保留模式。实际显示见 deliverables/FocusFlight-Interaction.png；完整指针点击流程尚待人工验证。

玻璃背景修复：移除嵌套 AlertDialog 表面，FlightInteraction 直接使用系统默认 Material.Regular；无自绘根背景、无配色覆盖。PicoTheme 包裹窗口，Option/Button 使用组件默认颜色与交互。城市作为应用环境背景保留。构建及本次窗口的设计样式检查通过，模拟器截图确认背景透视；真机效果待验证。
