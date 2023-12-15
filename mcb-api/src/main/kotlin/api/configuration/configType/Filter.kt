package api.configuration.configType

import api.discord.DCustomAPI
import net.dv8tion.jda.api.events.GenericEvent

interface Filter {
    val denyId: String?
    val whitelist: Boolean

    fun isCan(event: GenericEvent): Boolean
    suspend fun denyRun(event: GenericEvent, denyInteractionType: String) {
        if (denyId == null) {
            return
        }

        val denyCustoms = DCustomAPI.getBundle(denyInteractionType)
        if (!denyCustoms.isNullOrEmpty()) {
            val customs = denyCustoms[denyId]
            if (!customs.isNullOrEmpty()) {
                customs.forEach {
                    if (it is Filter) {
                        if (!it.isCan(event)) {
                            it.denyRun(event, denyInteractionType)
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