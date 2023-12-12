package api.discord

import api.configuration.ConfigFile
import api.configuration.configType.Custom
import api.discord.dataConfigs.CustomDiscordConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DCustomAPI {

    companion object {
        private val sortedMap: MutableMap<DiscordInteractionEnum, Deferred<MutableMap<String, MutableList<Custom>>>> =
            mutableMapOf()

        fun clear() {
            sortedMap.clear()
        }

        suspend fun sort(configs: MutableList<ConfigFile<CustomDiscordConfig>>) = coroutineScope {
            configs.mapNotNull { it.data.await() }.forEach {
                if (sortedMap.containsKey(it.interactionType) && sortedMap[it.interactionType] != null) {
                    it.custom.forEach { (id, value) ->
                        sortedMap[it.interactionType]!!.await().getOrPut(id) {
                            mutableListOf()
                        }.addAll(value)
                    }
                } else {
                    sortedMap[it.interactionType] = async { it.custom }
                }
            }
        }

        suspend fun replaceBundle(interaction: DiscordInteractionEnum, bundleId: String, customs: MutableList<Custom>) {
            sortedMap[interaction]?.await()?.replace(bundleId, customs)
        }

        suspend fun getBundle(interaction: DiscordInteractionEnum): Map<String, List<Custom>>? {
            return sortedMap.getOrDefault(interaction, null)?.await()
        }
    }

}