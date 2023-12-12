import api.addon.ModernAddon
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable

class MusicAddon : ModernAddon() {
    override suspend fun enableAddon() = coroutineScope {
        getDefaultConfig<MusicAddonConfig>().loadDefaultFile(MusicAddonConfig("example")).await()
        logger().info("Addon enabled!")
    }

    override suspend fun disableAddon() = coroutineScope {

    }

    override suspend fun reloadAddon() = coroutineScope {

    }

    @Serializable
    data class MusicAddonConfig(
        val testString: String
    )
}