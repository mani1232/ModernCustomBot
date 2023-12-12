package configuration.dataConfigs

import jda.DiscordListeners
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.requests.GatewayIntent
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
    private lateinit var bot: Deferred<JDA>
    override suspend fun init() = coroutineScope {
        try {
            bot = async { JDABuilder.createLight(token).enableIntents(intents).build() }
            bot.await().addEventListener(DiscordListeners())
            LoggerFactory.getLogger(bot.await().selfUser.id).info("Bot `${bot.await().selfUser.name}` started!")
            bot
        } catch (e: InvalidTokenException) {
            LoggerFactory.getLogger("DiscordBot-Builder").error("Error with token: $token, message: ${e.message}")
        }
    }

    override suspend fun get(): Deferred<JDA> {
        return bot
    }

    override suspend fun shutdown() {
        bot.await().awaitShutdown()
    }

}

@SerialName("telegram")
@Serializable
data class TelegramBot(val token: String) : Bot(), BotImpl<Any> {
    override suspend fun init() = coroutineScope {

    }

    override suspend fun get(): Deferred<Any> = coroutineScope {
        async {
            ""
        }
    }

    override suspend fun shutdown() {

    }

}

interface BotImpl<T> {
    suspend fun init(): Any
    suspend fun get(): Deferred<T>

    suspend fun shutdown()
}
