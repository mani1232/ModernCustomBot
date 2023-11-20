package configuration.dataConfigs

import jda.DiscordListeners
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent


@Serializable
@SerialName("BotConfig")
data class BotConfig(
    val bots: List<Bot> = ArrayList()
) : GenericData()

@Serializable
sealed class Bot

@SerialName("discord")
@Serializable
data class DiscordBot(val token: String) : Bot(), BotImpl<JDA> {
    private var bot: JDA? = null
    override fun init() {
        bot = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.DEFAULT)).build()
        bot!!.addEventListener(DiscordListeners())
    }

    override fun get(): JDA? {
        return bot
    }

}

@SerialName("telegram")
@Serializable
data class TelegramBot(val token: String) : Bot(), BotImpl<Any> {
    override fun init() {
        return
    }

    override fun get(): Any {
        return ""
    }

}

interface BotImpl<T> {
    fun init()
    fun get(): T?
}
