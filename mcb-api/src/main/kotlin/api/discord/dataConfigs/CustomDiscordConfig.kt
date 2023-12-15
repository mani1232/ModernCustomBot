package api.discord.dataConfigs

import api.configuration.configType.Custom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("discordConfig")
data class CustomDiscordConfig(
    val interactionType: String = "ON_MESSAGE_RECEIVE",
    val custom: MutableMap<String, MutableList<Custom>> = mutableMapOf()
)

