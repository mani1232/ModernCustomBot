package api.discord

import api.addon.AddonData
import api.configuration.ConfigFile
import api.configuration.configType.Action
import api.configuration.configType.Custom
import api.configuration.configType.Filter
import api.discord.dataConfigs.CustomDiscordConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import net.dv8tion.jda.api.events.GenericEvent
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

class DCustomAPI() {

    constructor(data: AddonData) : this() {
        this.pluginData = data
    }

    private lateinit var pluginData: AddonData

    suspend fun registerAddonCustoms(vararg type: KType) {
        registerAddonCustoms(type.toList(), pluginData)
    }

    suspend fun registerAddonInteraction(name: String) {
        registerInteraction(listOf("${pluginData.info.pluginName}_${name}"))
    }

    suspend fun registerAddonInteraction(names: List<String>) {
        registerInteraction(names.map { "${pluginData.info.pluginName}_${it}" })
    }

    suspend fun registerAddonCustoms(types: List<KType>) {
        registerAddonCustoms(types, pluginData)
    }

    companion object {
        private val availableInteractionNames = Collections.synchronizedList(mutableListOf<String>())
        private val customsTypesList = Collections.synchronizedList(mutableListOf<KType>())
        private val sortedMap =
            Collections.synchronizedMap(mutableMapOf<String, Deferred<MutableMap<String, MutableList<Custom>>>>())
        private lateinit var customsModule: Deferred<SerializersModule>

        suspend fun registerCustoms(list: List<KType>) = coroutineScope {
            launch {
                if (list.isNotEmpty()) {
                    customsTypesList.addAll(list)
                    customsModule = async {
                        SerializersModule {
                            polymorphic(Custom::class) {
                                customsTypesList.forEach {
                                    subclass(it.classifier as KClass<Custom>, serializer(it) as KSerializer<Custom>)
                                }
                            }
                        }
                    }
                }
            }
        }

        fun registerInteraction(names: List<String>) {
            availableInteractionNames.addAll(names)
        }

        suspend fun registerAddonCustoms(list: List<KType>, data: AddonData) = coroutineScope {
            launch {
                if (list.isNotEmpty()) {
                    customsTypesList.addAll(list)
                    customsModule = async {
                        SerializersModule {
                            polymorphic(Custom::class) {
                                customsTypesList.forEach {
                                    val kclass = it.classifier as KClass<Custom>
                                    val annotation =
                                        kclass.annotations.firstOrNull { annotation -> annotation.annotationClass == SerialName::class }

                                    if (kclass.annotations.firstOrNull { annotationSerializable -> annotationSerializable.annotationClass == Serializable::class } == null) {
                                        data.logger.error("Annotation @Serializable for ${kclass.simpleName} class not found, contact to developer")
                                        return@forEach
                                    }

                                    if (annotation == null) {
                                        data.logger.error("Annotation @SerialName(name: String) for ${kclass.simpleName} class not found, contact to developer")
                                        return@forEach
                                    }

                                    if (annotation is SerialName && !annotation.value.startsWith("${data.info.pluginName}_")) {
                                        data.logger.error("For annotation @SerialName you need set value like: ${data.info.pluginName}_${annotation.value}")
                                        return@forEach
                                    }

                                    subclass(kclass, serializer(it) as KSerializer<Custom>)
                                }
                            }
                        }
                    }
                }
            }
        }

        fun getCustoms(): MutableList<KType> {
            return customsTypesList
        }

        suspend fun getCustomsModule(): SerializersModule {
            return customsModule.await()
        }

        fun clearAll() {
            sortedMap.clear()
            customsTypesList.clear()
        }

        suspend fun runCustoms(eventEnum: String, event: GenericEvent) {
            var tempData = mutableMapOf<String, Any>()

            val customs = getBundle(eventEnum)
            if (!customs.isNullOrEmpty()) {
                customs.forEach { map ->
                    map.value.forEach {
                        it.tempData = tempData
                        it.logger = LoggerFactory.getLogger("${map.key}-${it::class.simpleName}")
                        if (it is Filter) {
                            if (!it.isCan(event) == it.whitelist) {
                                it.denyRun(event, eventEnum)
                                return@forEach
                            }
                        } else if (it is Action) {
                            it.run(event)
                        }
                        tempData = it.tempData
                    }
                }
            }
        }

        suspend fun sort(configs: MutableList<ConfigFile<CustomDiscordConfig>>) = coroutineScope {
            val log = LoggerFactory.getLogger("Sort-System")
            configs.mapNotNull { it.data.await() }.forEach {
                if (!availableInteractionNames.contains(it.interactionType)) {
                    log.warn("${it.interactionType} not registered and skipped")
                    return@forEach
                }

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

        suspend fun replaceBundle(interaction: String, bundleId: String, customs: MutableList<Custom>) {
            sortedMap[interaction]?.await()?.replace(bundleId, customs)
        }

        suspend fun getBundle(interaction: String): Map<String, List<Custom>>? {
            return sortedMap.getOrDefault(interaction, null)?.await()
        }
    }

}