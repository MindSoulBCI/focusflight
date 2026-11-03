# BrainLink 接口预留

目前没有接入真实脑电设备或厂商 SDK。默认“模拟数据”模式的滑条仅用于演示。

后续在 Android 服务中接入有授权的 BrainLink SDK，将厂商的专注力回调映射为：

```kotlin
import com.example.picosmokedemo.p7c91e42a.data.FlightSession
FlightSession.brainLink.onAttention(
    percent = 75f,                // SDK 的专注力结果，范围 0..100
    normalizedSignalQuality = 1f // 由接入层将厂商信号质量归一化为 0..1，1 为最佳
)
// 断开连接时：
FlightSession.brainLink.onDisconnected()
```

- 在界面切换到 BrainLink 后，等待新的有效回调，才显示真实百分比。
- 接口线程安全；回调使用 Android 单调时钟记录接收时间。
- 非有限值、越界百分比、信号质量低于 0.5 的数据不会进入加速计算。
- 2 秒没有新的有效数据，显示“BrainLink 数据过期”、百分比显示“—”，加速倍率回到 1。
- 断开时显示“BrainLink 未连接”。进入后台会悬停并清除旧数据，返回后需要手动开始。
- 当前未实现蓝牙扫描、配对、权限申请、原始脑电解析及厂商 SDK 绑定；不假设任何 BrainLink 私有协议。
- 本版本不记录或上传脑电数据。模拟和真实模式在界面有明确标识。
