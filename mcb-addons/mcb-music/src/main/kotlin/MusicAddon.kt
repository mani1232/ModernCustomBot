import api.addon.ModernAddon
import api.configuration.configType.Action
import api.configuration.configType.Custom
import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler
import dev.arbjerg.lavalink.client.LavalinkClient
import dev.arbjerg.lavalink.client.getUserIdFromToken
import dev.arbjerg.lavalink.client.loadbalancing.IRegionFilter
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import java.net.URI
import kotlin.reflect.typeOf


class MusicAddon : ModernAddon() {

    companion object {
        val lavaLinkClientsMap = mutableMapOf<Long, LavalinkClient>()
        lateinit var instance: MusicAddon
    }

    val musicConfig = getDefaultConfig<MusicAddonConfig>()
    override suspend fun enableAddon(): Unit = coroutineScope {
        instance = this@MusicAddon
        musicConfig.loadDefaultFile(MusicAddonConfig(nodes = mutableListOf(LavaLinkNodeConfig())))
                .await()
        if (musicConfig.data.await() != null) {
            api.registerAddonCustoms(
                typeOf<StartPlayUrl>(),
                typeOf<PauseTrack>(),
            )
            logger.info("Addon enabled!")
        }
    }

    override suspend fun disableAddon() {
        lavaLinkClientsMap.forEach { (id, client) ->
            client.nodes.forEach { it.getPlayers().subscribe { playerList -> playerList.forEach { player -> it.updatePlayer(player.guildId) { update ->
                update.setEncodedTrack(
                    null
                ).setPaused(false)
            }.subscribe {  } } } }
        }
        lavaLinkClientsMap.clear()
        logger.info("Addon disabled, for add other bots you need full reload ModernCustomBot!")
    }

    override suspend fun botBuilder(token: String, botBuilder: Any): Any {
        return if (botBuilder is JDABuilder) {
            val botId = getUserIdFromToken(token)
            val client = LavalinkClient(
                botId
            )
            client.loadBalancer.addPenaltyProvider(VoiceRegionPenaltyProvider())
            botBuilder.setVoiceDispatchInterceptor(JDAVoiceUpdateListener(client))
            musicConfig.data.await()!!.nodes.filter { it.name.isNotEmpty() }.forEach { it.initNode(client) }
            lavaLinkClientsMap[botId] = client
            botBuilder
        } else {
            botBuilder
        }
    }

}

@Serializable
data class MusicAddonConfig(
    val logTrackResult: Boolean = false,
    val whiteListedBotIds: List<String> = mutableListOf(),
    val nodes: MutableList<LavaLinkNodeConfig> = mutableListOf(),
)

@Serializable
data class LavaLinkNodeConfig(
    val name: String = "",
    val uri: String = "",
    val password: String = "",
    val regionGroup: Region = Region.EUROPE
) {
    fun initNode(client: LavalinkClient) {
        client.addNode(name, URI.create(uri), password, regionGroup.group)
    }
}

@Serializable
@SerialName("ModernMusicAddon_StartPlayUrl")
data class StartPlayUrl(
    val idOfStringData: String
) : Custom(), Action {
    override fun run(event: GenericEvent) {
        val url = tempData[idOfStringData]

        if (event !is GenericGuildEvent) {
            logger.warn("ModernMusicAddon_StartPlayUrl action can run only from guild")
            return
        }

        if (url == null || url !is String) {
            logger.warn("$idOfStringData is not found or not string")
            return
        }

        val client = MusicAddon.lavaLinkClientsMap[event.jda.selfUser.idLong]

        if (client == null) {
            logger.warn("${event.jda.selfUser.name} not whitelisted for MusicAddon, please use botFilter or add it")
            return
        }

        val link = client.getLink(event.guild.idLong)
        link.loadItem(idOfStringData).subscribe(AudioLoadResultHandler(MusicAddon.instance, link))
    }
}

@Serializable
@SerialName("ModernMusicAddon_PauseTrack")
data class PauseTrack(
    val emptyValue: String = ""
) : Custom(), Action {
    override fun run(event: GenericEvent) {

        if (event !is GenericGuildEvent) {
            logger.warn("ModernMusicAddon_StartPlayUrl action can run only from guild")
            return
        }

        val client = MusicAddon.lavaLinkClientsMap[event.jda.selfUser.idLong]

        if (client == null) {
            logger.warn("${event.jda.selfUser.name} not whitelisted for MusicAddon, please use botFilter or add it")
            return
        }

        val link = client.getLink(event.guild.idLong)
        link.getPlayer().subscribe {
            link.createOrUpdatePlayer().setPaused(!it.paused).subscribe()
        }
    }
}

enum class Region(val group: IRegionFilter) {
    US(RegionGroup.US),
    ASIA(RegionGroup.ASIA),
    EUROPE(RegionGroup.EUROPE)
}