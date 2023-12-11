package configuration.dataConfigs

import jda.DCustomAPI
import jda.DiscordInteractionEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.events.GenericEvent

@Serializable
@SerialName("discordConfig")
data class CustomDiscordConfig(
    val interactionType: DiscordInteractionEnum = DiscordInteractionEnum.ON_MESSAGE_RECEIVE,
    val custom: MutableMap<String, MutableList<Custom>> = mutableMapOf()
)

@Serializable
abstract class Custom

interface Filter {
    val denyId: String?
    val whitelist: Boolean

    fun isCan(event: GenericEvent): Boolean
    fun denyRun(event: GenericEvent, interactionType: DiscordInteractionEnum) {
        if (denyId == null) {
            return
        }

        val denyCustoms = DCustomAPI.getSortedMap()[interactionType]
        if (!denyCustoms.isNullOrEmpty()) {
            val customs = denyCustoms[denyId]
            if (!customs.isNullOrEmpty()) {
                customs.forEach {
                    if (it is Filter) {
                        if (!it.isCan(event)) {
                            it.denyRun(event, interactionType)
                            return
                        }
                    } else if (it is Action) {
                        it.run(event)
                    }
                }
            }
        }
    }

}

fun interface Action {
    fun run(event: GenericEvent)
}