package api.configuration.configType

import api.discord.DCustomAPI
import api.discord.DiscordInteractionEnum
import net.dv8tion.jda.api.events.GenericEvent
import kotlin.reflect.KClass

interface Filter {
    val denyId: String?
    val whitelist: Boolean

    fun isCan(event: GenericEvent): Boolean
    suspend fun denyRun(event: GenericEvent) {
        if (denyId == null) {
            return
        }

        val interactionType =  DiscordInteractionEnum.entries.firstOrNull { it.kType.classifier as KClass<GenericEvent> == event::class }

        if (interactionType == null) {
            return
        }

        val denyCustoms = DCustomAPI.getBundle(interactionType)
        if (!denyCustoms.isNullOrEmpty()) {
            val customs = denyCustoms[denyId]
            if (!customs.isNullOrEmpty()) {
                customs.forEach {
                    if (it is Filter) {
                        if (!it.isCan(event)) {
                            it.denyRun(event)
                            return@forEach
                        }
                    } else if (it is Action) {
                        it.run(event)
                    }
                }
            }
        }
    }
}