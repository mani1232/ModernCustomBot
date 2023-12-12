import api.addon.ModernAddon
import kotlinx.serialization.Serializable

class MusicAddon: ModernAddon() {
    override suspend fun enableAddon() {
        getDefaultConfig<MusicAddonConfig>().loadDefaultFile(MusicAddonConfig("example")).await()
    }

    override suspend fun disableAddon() {

    }

    override suspend fun reloadAddon() {

    }

    @Serializable
    data class MusicAddonConfig(
        val testString: String
    )
}