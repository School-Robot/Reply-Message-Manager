package tk.mcsog

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import net.mamoe.mirai.message.data.QuoteReply
import net.mamoe.mirai.utils.info

object ReplyMessageManager : KotlinPlugin(
    JvmPluginDescription(
        id = "tk.mcsog.reply-message-manager",
        name = "Reply Message Manager",
        version = "0.1.0",
    ) {

        author("MCSOG-f00001111")
    }
) {
    override fun onEnable() {
        Config.reload()
        logger.info { "Plugin loaded" }
        globalEventChannel().subscribeAlways<GroupMessageEvent> {
            val m = message.serializeToMiraiCode()
            if (sender.permission > MemberPermission.MEMBER) {
                if (m == "/rmm") {
                    if (Config.num.contains(group.id)) {
                        Config.num.remove(group.id)
                        group.sendMessage("已关闭回复管理")
                    } else {
                        Config.num.add(group.id)
                        group.sendMessage("已开启回复管理")
                    }
                }
                if (Config.num.contains(group.id)) {
                    if (message.contains(QuoteReply)) {
                        for (ori_m in message) {
                            if (ori_m is QuoteReply) {
                                val quote = ori_m.source
                                if (m == "撤回"){
                                    if (group.botPermission > MemberPermission.MEMBER || quote.fromId == quote.botId) {
                                        quote.recall()
                                    } else {
                                        group.sendMessage("权限不足")
                                    }
                                }else if (m.startsWith("禁言")){
                                    val times: Int = m.substring(2).toInt()
                                    if (times in 1..2592000) {
                                        if (group.botPermission > MemberPermission.MEMBER) {
                                            group[quote.fromId]?.mute(times)
                                        } else {
                                            group.sendMessage("权限不足")
                                        }
                                    }
                                }else if (m == "解禁"){
                                    if (group.botPermission > MemberPermission.MEMBER) {
                                        group[quote.fromId]?.unmute()
                                    } else {
                                        group.sendMessage("权限不足")
                                    }
                                }else if (m == "加精") {
                                    if (group.botPermission > MemberPermission.MEMBER) {
                                        group.setEssenceMessage(quote)
                                    } else {
                                        group.sendMessage("权限不足")
                                    }
                                }else if (m == "踢出") {
                                    if (group.botPermission > MemberPermission.MEMBER) {
                                        group[quote.fromId]?.kick("被管理员踢出")
                                    } else {
                                        group.sendMessage("权限不足")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        Config.save()
        logger.info { "Plugin unloaded" }
    }
}