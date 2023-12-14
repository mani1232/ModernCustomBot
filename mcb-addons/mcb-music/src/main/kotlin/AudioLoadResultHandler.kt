import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler
import dev.arbjerg.lavalink.client.Link
import dev.arbjerg.lavalink.client.protocol.LoadFailed
import dev.arbjerg.lavalink.client.protocol.PlaylistLoaded
import dev.arbjerg.lavalink.client.protocol.SearchResult
import dev.arbjerg.lavalink.client.protocol.TrackLoaded
import kotlinx.coroutines.runBlocking

class AudioLoadResultHandler(private val addon: MusicAddon, private val link: Link) : AbstractAudioLoadResultHandler() {
    override fun ontrackLoaded(result: TrackLoaded) {
        link.createOrUpdatePlayer().setTrack(result.track).subscribe()
        runBlocking {
            if (addon.musicConfig.data.await()!!.logTrackResult) {
                addon.logger.info("Loaded ${result.track.info.title} track (${link.guildId})")
            }
        }
    }

    override fun onPlaylistLoaded(result: PlaylistLoaded) {
        runBlocking {
            if (addon.musicConfig.data.await()!!.logTrackResult) {
                addon.logger.info("Loaded ${result.tracks.size} tracks from playlist (${link.guildId})")
            }
        }
    }

    override fun onSearchResultLoaded(result: SearchResult) {
        addon.logger.warn("Search result feature not supported")
    }

    override fun noMatches() {
        // TODO add listener
    }

    override fun loadFailed(result: LoadFailed) {
        // TODO add listener
    }
}
