package configuration.dataConfigs

import api.addon.AddonManager
import api.configuration.BotImpl
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
    private lateinit var bot: Deferred<JDA?>
    override suspend fun init(addonManager: AddonManager) = coroutineScope {
        bot = async {
            try {
                val builder = addonManager.botBuilder(token, JDABuilder.createLight(token).enableIntents(intents))
                if (builder is JDABuilder) {
                    val jda = builder.addEventListeners(DiscordListeners()).build().awaitReady()
                    LoggerFactory.getLogger(jda.selfUser.id).info("Bot `${jda.selfUser.name}` started!")
                    jda
                } else {
                    null
                }
            } catch (e: InvalidTokenException) {
                LoggerFactory.getLogger("DiscordBot-Builder").error("Error with token: $token, message: ${e.message}")
                null
            }
        }
    }

    override suspend fun get(): Deferred<JDA?> {
        return bot
    }

    override suspend fun shutdown() {
        if (bot.await() != null) {
            bot.await()!!.awaitShutdown()
        }
    }

}

@SerialName("telegram")
@Serializable
data class TelegramBot(val token: String) : Bot(), BotImpl<Any> {
    override suspend fun init(addonManager: AddonManager) = coroutineScope {

    }

    override suspend fun get(): Deferred<Any> = coroutineScope {
        async {
            ""
        }
    }

    override suspend fun shutdown() {

    }

}