import api.discord.DCustomAPI
import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler
import dev.arbjerg.lavalink.client.Link
import dev.arbjerg.lavalink.client.protocol.LoadFailed
import dev.arbjerg.lavalink.client.protocol.PlaylistLoaded
import dev.arbjerg.lavalink.client.protocol.SearchResult
import dev.arbjerg.lavalink.client.protocol.TrackLoaded
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.guild.GenericGuildEvent

class AudioLoadResultListener(private val addon: MusicAddon, private val link: Link, private val event: GenericGuildEvent) : AbstractAudioLoadResultHandler() {
    override fun ontrackLoaded(result: TrackLoaded) {
        link.createOrUpdatePlayer().setTrack(result.track).subscribe()
        runBlocking {
            DCustomAPI.runCustoms(MusicAddonInteractions.TRACK_LOADED.name, event)

            if (addon.musicConfig.data.await()!!.logTrackResult) {
                addon.logger.info("Loaded ${result.track.info.title} track (${link.guildId})")
            }
        }
    }

    override fun onPlaylistLoaded(result: PlaylistLoaded) {
        runBlocking {
            DCustomAPI.runCustoms(MusicAddonInteractions.PLAYLIST_LOADED.name, event)

            if (addon.musicConfig.data.await()!!.logTrackResult) {
                addon.logger.info("Loaded ${result.tracks.size} tracks from playlist (${link.guildId})")
            }
        }
    }

    override fun onSearchResultLoaded(result: SearchResult) {
        addon.logger.warn("Search result feature not supported")
    }

    override fun noMatches() {
        runBlocking {
            DCustomAPI.runCustoms(MusicAddonInteractions.NO_MATCHES.name, event)
        }
    }
    override fun loadFailed(result: LoadFailed) {
        runBlocking {
            DCustomAPI.runCustoms(MusicAddonInteractions.LOAD_FAILED.name, event)
        }
    }
}
