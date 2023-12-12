package addon

import api.addon.AddonData
import api.addon.Info
import api.addon.Manager
import api.addon.ModernAddon
import api.configuration.ConfigDefaults
import api.configuration.ConfigFile
import com.charleskorn.kaml.decodeFromStream
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader

class AddonManager(mainPath: String) {
    private val folder: File = File(mainPath, "/addons")
    private val plugins = mutableListOf<NativeAddonData>()
    private val logger = LoggerFactory.getLogger("Addon-System")

    init {
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    suspend fun initAddons() = coroutineScope {
        val asyncAddons = folder.listFiles()?.filter { !it.name.startsWith("_") && it.extension == "jar" }?.map {
            async {
                val url = it.toURI().toURL()
                val classloader = URLClassLoader(arrayOf(url))
                val addonConfigStream = classloader.getResourceAsStream("addon.yml")
                if (addonConfigStream != null) {
                    try {
                        val addonConfig = ConfigDefaults.getConfiguredYaml(SerializersModule {  }).decodeFromStream<AddonConfigData>(addonConfigStream)
                        val mainClass = classloader.loadClass(addonConfig.main)
                        val pluginFolder = File(folder, "/${addonConfig.name}")
                        if (!pluginFolder.exists()) {
                            pluginFolder.mkdirs()
                        }
                        val addon = mainClass.getConstructor().newInstance() as ModernAddon
                        addon.setData(AddonData(
                            Info(pluginName = addonConfig.name, pluginVersion = addonConfig.version),
                            Manager(addonDirectory = pluginFolder, urlClassLoader = classloader),
                            LoggerFactory.getLogger(mainClass)
                        ))
                        logger.info("Addon ${addon.info.pluginName} version ${addon.info.pluginVersion} loaded")
                        NativeAddonData(it, addon)
                    } catch (e: SerializationException) {
                        logger.error("Filed load addon.yml", e)
                        null
                    }
                } else {
                    logger.error("Not found addon.yml in ${it.name}")
                    null
                }
            }
        } ?: listOf()

        plugins.addAll(asyncAddons.awaitAll().filterNotNull())
    }

    suspend fun enableAddons() = coroutineScope {
        plugins.forEach { it.addon.enableAddon() }
    }

    suspend fun disableAddons() = coroutineScope {
        plugins.forEach { it.addon.disableAddon() }
    }

    suspend fun reloadAddons() = coroutineScope {
        plugins.forEach { it.addon.enableAddon() }
    }

}

data class NativeAddonData(
    val file: File,
    val addon: ModernAddon,
)

@Serializable
data class AddonConfigData(
    val name: String,
    val main: String,
    val version: String,
    val authors: List<String>,
)