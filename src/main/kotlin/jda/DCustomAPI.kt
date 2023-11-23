package jda

import configuration.ConfigFile
import configuration.dataConfigs.Custom
import configuration.dataConfigs.CustomDiscordConfig

class DCustomAPI {

    companion object {
        private val sortedMap: MutableMap<DiscordInteractionEnum, MutableMap<String, MutableList<Custom>>> = mutableMapOf()

        fun sort(configs: MutableList<ConfigFile<CustomDiscordConfig>>, clear: Boolean) {
            if (clear) sortedMap.clear()
            configs.mapNotNull { it.data }.forEach {
                if (sortedMap.containsKey(it.interactionType) && sortedMap[it.interactionType] != null) {
                    it.custom.forEach { (id, value) ->
                        sortedMap[it.interactionType]!!.getOrPut(id) {
                            mutableListOf()
                        }.addAll(value)
                    }
                } else {
                    sortedMap[it.interactionType] = it.custom
                }
            }
        }

        fun getSortedMap(): MutableMap<DiscordInteractionEnum, MutableMap<String, MutableList<Custom>>> {
            return sortedMap
        }
    }

}