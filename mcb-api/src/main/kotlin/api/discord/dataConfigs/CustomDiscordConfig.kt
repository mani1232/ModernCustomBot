package api.discord.dataConfigs

import api.configuration.configType.Custom
import api.discord.DiscordInteractionEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("discordConfig")
data class CustomDiscordConfig(
    val interactionType: DiscordInteractionEnum = DiscordInteractionEnum.ON_MESSAGE_RECEIVE,
    val custom: MutableMap<String, MutableList<Custom>> = mutableMapOf()
)

