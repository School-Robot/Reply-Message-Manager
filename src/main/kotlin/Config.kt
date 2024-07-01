package tk.mcsog

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("Config") {
    val num: MutableList<Long> by value()
}