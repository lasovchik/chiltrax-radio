package com.chilltrax.radio.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.chilltrax.radio.domain.model.Station
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

data class PlayerState(
    val isConnected: Boolean = false,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentStationId: Long? = null,
    val volume: Float = 1f,
)

@Singleton
class RadioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _errors = Channel<String>(Channel.BUFFERED)
    val errors = _errors.receiveAsFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var pendingStation: Station? = null
    private var pendingPlay: Boolean = false
    private var released: Boolean = false

    private val artworkBytes: ByteArray by lazy(LazyThreadSafetyMode.NONE) {
        ArtworkFactory.create()
    }

    private val controllerListener = object : MediaController.Listener {
        override fun onDisconnected(controller: MediaController) {
            if (this@RadioPlayer.controller === controller) {
                this@RadioPlayer.controller = null
                controllerFuture = null
                _state.update {
                    it.copy(
                        isConnected = false,
                        isPlaying = false,
                        isBuffering = false,
                    )
                }
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            syncState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            syncState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            syncState()
        }

        override fun onVolumeChanged(volume: Float) {
            _state.update { it.copy(volume = volume.coerceIn(0f, 1f)) }
        }

        override fun onPlayerError(error: PlaybackException) {
            _state.update { it.copy(isBuffering = false) }
            _errors.trySend(error.message ?: "Ошибка воспроизведения")
            syncState()
        }
    }

    fun connect() {
        if (released || controllerFuture != null) return

        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java),
        )
        val future = MediaController.Builder(context, sessionToken)
            .setListener(controllerListener)
            .buildAsync()
        controllerFuture = future

        future.addListener(
            {
                runCatching { future.get() }
                    .onSuccess { connectedController ->
                        if (released) {
                            connectedController.release()
                            return@onSuccess
                        }
                        controller = connectedController
                        connectedController.addListener(playerListener)
                        syncState(connectedController)

                        val station = pendingStation
                        val shouldPlay = pendingPlay
                        pendingStation = null
                        pendingPlay = false
                        if (station != null && shouldPlay) {
                            playNow(connectedController, station)
                        }
                    }
                    .onFailure {
                        controllerFuture = null
                        _state.update { it.copy(isConnected = false) }
                        _errors.trySend("Не удалось подключиться к сервису воспроизведения")
                    }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    fun play(station: Station) {
        val currentController = controller
        if (currentController == null) {
            pendingStation = station
            pendingPlay = true
            connect()
            return
        }
        playNow(currentController, station)
    }

    fun resume() {
        val current = controller ?: return
        if (current.playbackState == Player.STATE_IDLE) {
            current.prepare()
        }
        current.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun stop() {
        controller?.stop()
        syncState()
    }

    fun setVolume(value: Float) {
        val clamped = value.coerceIn(0f, 1f)
        controller?.volume = clamped
        _state.update { it.copy(volume = clamped) }
    }

    fun release() {
        released = true
        controller?.release()
        controller = null
        controllerFuture?.let { future ->
            if (!future.isDone) future.cancel(true)
        }
        controllerFuture = null
    }

    private fun playNow(controller: MediaController, station: Station) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(station.id.toString())
            .setUri(station.streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(station.name)
                    .setArtist(station.displayGenre)
                    .setAlbumTitle("Chilltrax Radio")
                    .setArtworkData(artworkBytes, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                    .build(),
            )
            .build()

        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()

        _state.update {
            it.copy(
                currentStationId = station.id,
                isBuffering = true,
                isPlaying = false,
            )
        }
    }

    private fun syncState(activeController: MediaController? = controller) {
        val current = activeController ?: return
        _state.update {
            it.copy(
                isConnected = true,
                isPlaying = current.isPlaying,
                isBuffering = current.playbackState == Player.STATE_BUFFERING,
                currentStationId = current.currentMediaItem?.mediaId?.toLongOrNull(),
                volume = current.volume.coerceIn(0f, 1f),
            )
        }
    }
}
