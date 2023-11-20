package configuration.dataConfigs

import jda.DiscordListeners
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory


@Serializable
@SerialName("botConfig")
data class BotConfig(
    val bots: List<Bot> = ArrayList()
)

@Serializable
sealed class Bot

@SerialName("discord")
@Serializable
data class DiscordBot(
    val token: String,
    val intents: MutableList<GatewayIntent> = mutableListOf(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
    ) : Bot(), BotImpl<JDA> {
    @Transient
    private var bot: JDA? = null
    override fun init() {
        try {
            bot = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.DEFAULT))
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS).enableIntents(intents).build()
            bot!!.addEventListener(DiscordListeners())
        } catch (e: InvalidTokenException) {
            LoggerFactory.getLogger("DiscordBot-Builder").error("Error with token: $token, message: ${e.message}")
        }
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
