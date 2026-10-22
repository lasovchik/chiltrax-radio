package com.chilltrax.radio.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chilltrax.radio.R
import com.chilltrax.radio.domain.model.Station
import com.chilltrax.radio.presentation.home.components.AudioVisualizer
import com.chilltrax.radio.presentation.home.components.VinylRecord
import com.chilltrax.radio.presentation.theme.CardGradient
import com.chilltrax.radio.presentation.theme.ChillBackground
import com.chilltrax.radio.presentation.theme.ChillBackgroundAlt
import com.chilltrax.radio.presentation.theme.ErrorRed
import com.chilltrax.radio.presentation.theme.NeonBlue
import com.chilltrax.radio.presentation.theme.NeonCyan
import com.chilltrax.radio.presentation.theme.NeonGradient
import com.chilltrax.radio.presentation.theme.NeonPurple
import com.chilltrax.radio.presentation.theme.NeonViolet
import com.chilltrax.radio.presentation.theme.SuccessGreen
import com.chilltrax.radio.presentation.theme.SurfaceDark
import com.chilltrax.radio.presentation.theme.SurfaceElevated
import com.chilltrax.radio.presentation.theme.TextPrimary
import com.chilltrax.radio.presentation.theme.TextSecondary

@Composable
fun PlayerScreen(
    viewModel: RadioViewModel,
    onAddStation: () -> Unit,
    onEditStation: (Station) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var stationToDelete by remember { mutableStateOf<Station?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message.resolve(context))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ChillBackground, ChillBackgroundAlt, ChillBackground),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .size(360.dp)
                .offset(x = (-120).dp, y = (-90).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.22f), Color.Transparent),
                    ),
                    CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = 210.dp, y = 420.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonBlue.copy(alpha = 0.16f), Color.Transparent),
                    ),
                    CircleShape,
                ),
        )

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .shadow(20.dp, CircleShape, spotColor = NeonPurple)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(NeonGradient))
                        .clickable(onClick = onAddStation),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.cd_add),
                        tint = Color.White,
                        modifier = Modifier.size(30.dp),
                    )
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 110.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    AppHeader(isOnline = uiState.isOnline)
                }

                item {
                    AnimatedVisibility(
                        visible = !uiState.isOnline,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        OfflineBanner()
                    }
                }

                item {
                    PlayerCard(
                        station = uiState.selectedStation,
                        isPlaying = uiState.isPlaying,
                        isBuffering = uiState.isBuffering,
                        isConnected = uiState.isConnected,
                        volume = uiState.volume,
                        onTogglePlayback = viewModel::onTogglePlayback,
                        onVolumeChange = viewModel::onVolumeChange,
                    )
                }

                item {
                    StationsHeader(count = uiState.stations.size)
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = NeonViolet)
                        }
                    }
                } else if (uiState.stations.isEmpty()) {
                    item {
                        EmptyStations(onAddStation = onAddStation)
                    }
                } else {
                    items(
                        items = uiState.stations,
                        key = { it.id },
                    ) { station ->
                        StationCard(
                            station = station,
                            isSelected = station.id == uiState.selectedStation?.id,
                            isCurrent = station.id == uiState.currentStationId,
                            isPlaying = uiState.isPlaying,
                            onClick = { viewModel.onStationClick(station) },
                            onEdit = { onEditStation(station) },
                            onDelete = { stationToDelete = station },
                        )
                    }
                }
            }
        }
    }

    stationToDelete?.let { station ->
        AlertDialog(
            onDismissRequest = { stationToDelete = null },
            containerColor = SurfaceElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text(stringResource(R.string.delete_station_title)) },
            text = {
                Text(stringResource(R.string.delete_station_message, station.name))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteStation(station)
                        stationToDelete = null
                    },
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = ErrorRed,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { stationToDelete = null }) {
                    Text(stringResource(R.string.cancel), color = TextSecondary)
                }
            },
        )
    }
}

@Composable
private fun AppHeader(isOnline: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(NeonGradient)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Radio,
                contentDescription = stringResource(R.string.cd_radio),
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = Brush.linearGradient(listOf(TextPrimary, NeonViolet)),
                    letterSpacing = MaterialTheme.typography.titleLarge.letterSpacing,
                ),
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.app_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OnlineBadge(isOnline = isOnline)
    }
}

@Composable
private fun OnlineBadge(isOnline: Boolean) {
    val color = if (isOnline) SuccessGreen else ErrorRed
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(
                if (isOnline) R.string.status_online else R.string.status_offline,
            ),
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

@Composable
private fun OfflineBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ErrorRed.copy(alpha = 0.10f))
            .border(1.dp, ErrorRed.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.WifiOff,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.offline_message),
            style = MaterialTheme.typography.bodyMedium,
            color = ErrorRed,
        )
    }
}

@Composable
private fun PlayerCard(
    station: Station?,
    isPlaying: Boolean,
    isBuffering: Boolean,
    isConnected: Boolean,
    volume: Float,
    onTogglePlayback: () -> Unit,
    onVolumeChange: (Float) -> Unit,
) {
    val shape = RoundedCornerShape(32.dp)
    val statusColor = when {
        isBuffering -> NeonCyan
        isPlaying -> SuccessGreen
        else -> TextSecondary
    }
    val statusText = when {
        isBuffering -> stringResource(R.string.buffering)
        isPlaying -> stringResource(R.string.live)
        isConnected -> stringResource(R.string.ready)
        else -> stringResource(R.string.paused)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 28.dp,
                shape = shape,
                ambientColor = NeonPurple.copy(alpha = 0.30f),
                spotColor = NeonBlue.copy(alpha = 0.30f),
            )
            .clip(shape)
            .background(Brush.linearGradient(CardGradient), shape)
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        NeonPurple.copy(alpha = 0.85f),
                        NeonBlue.copy(alpha = 0.55f),
                        NeonViolet.copy(alpha = 0.35f),
                    ),
                ),
                shape = shape,
            )
            .padding(horizontal = 22.dp, vertical = 26.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VinylRecord(
                isPlaying = isPlaying,
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .shadow(24.dp, CircleShape, spotColor = NeonPurple),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(statusColor, CircleShape),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.now_playing),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = station?.name ?: stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            val genreText = station?.displayGenre ?: stringResource(R.string.default_genre)
            val qualityText = stringResource(R.string.stream_quality)

            Text(
                text = "$genreText  •  $qualityText",
                style = MaterialTheme.typography.bodyMedium,
                color = NeonViolet.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(14.dp))

            AudioVisualizer(
                isPlaying = isPlaying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
            )

            Spacer(modifier = Modifier.height(18.dp))

            PlayPauseButton(
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                enabled = station != null,
                onClick = onTogglePlayback,
            )

            Spacer(modifier = Modifier.height(16.dp))

            VolumeRow(
                volume = volume,
                onVolumeChange = onVolumeChange,
            )
        }
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    isBuffering: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.45f,
        label = "play_alpha",
    )

    Box(contentAlignment = Alignment.Center) {
        if (isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.size(94.dp),
                color = NeonViolet.copy(alpha = 0.55f),
                strokeWidth = 2.dp,
                trackColor = Color.White.copy(alpha = 0.06f),
            )
        }

        Box(
            modifier = Modifier
                .size(84.dp)
                .shadow(22.dp, CircleShape, spotColor = NeonPurple)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(NeonGradient),
                    alpha = alpha,
                )
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp,
                )
            } else {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(
                        if (isPlaying) R.string.cd_pause else R.string.cd_play,
                    ),
                    tint = Color.White,
                    modifier = Modifier.size(42.dp),
                )
            }
        }
    }
}

@Composable
private fun VolumeRow(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = when {
                volume <= 0.01f -> Icons.AutoMirrored.Rounded.VolumeOff
                volume < 0.5f -> Icons.AutoMirrored.Rounded.VolumeDown
                else -> Icons.AutoMirrored.Rounded.VolumeUp
            },
            contentDescription = stringResource(R.string.cd_volume),
            tint = TextSecondary,
            modifier = Modifier.size(20.dp),
        )

        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1f,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = NeonViolet,
                activeTrackColor = NeonPurple,
                inactiveTrackColor = Color.White.copy(alpha = 0.12f),
            ),
        )

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun StationsHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.my_stations),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(NeonPurple.copy(alpha = 0.18f))
                .padding(horizontal = 9.dp, vertical = 2.dp),
        ) {
            Text(
                text = stringResource(R.string.stations_count, count),
                style = MaterialTheme.typography.labelSmall,
                color = NeonViolet,
            )
        }
    }
}

@Composable
private fun StationCard(
    station: Station,
    isSelected: Boolean,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    val accentBorder = Brush.linearGradient(
        listOf(
            NeonPurple.copy(alpha = 0.85f),
            NeonBlue.copy(alpha = 0.55f),
        ),
    )
    val defaultBorder = SolidColor(Color.White.copy(alpha = 0.07f))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = if (isSelected) {
                    Brush.linearGradient(
                        listOf(
                            NeonPurple.copy(alpha = 0.18f),
                            NeonBlue.copy(alpha = 0.07f),
                        ),
                    )
                } else {
                    Brush.linearGradient(listOf(SurfaceDark, SurfaceDark))
                },
                shape = shape,
            )
            .border(
                width = 1.dp,
                brush = if (isSelected) accentBorder else defaultBorder,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected) {
                        Brush.linearGradient(NeonGradient)
                    } else {
                        Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated))
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Radio,
                contentDescription = null,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) TextPrimary else TextPrimary.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = station.displayGenre,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (isCurrent) {
            Icon(
                imageVector = Icons.Rounded.GraphicEq,
                contentDescription = if (isPlaying) stringResource(R.string.play) else null,
                tint = NeonViolet,
                modifier = Modifier
                    .size(22.dp)
                    .padding(end = 2.dp),
            )
        }

        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = stringResource(R.string.cd_edit),
                tint = TextSecondary,
                modifier = Modifier.size(20.dp),
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Rounded.DeleteOutline,
                contentDescription = stringResource(R.string.cd_delete),
                tint = ErrorRed.copy(alpha = 0.85f),
                modifier = Modifier.size(21.dp),
            )
        }
    }
}

@Composable
private fun EmptyStations(onAddStation: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark.copy(alpha = 0.75f))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(24.dp))
            .padding(vertical = 36.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Rounded.Radio,
            contentDescription = null,
            tint = NeonViolet.copy(alpha = 0.7f),
            modifier = Modifier.size(44.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.no_stations),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.no_stations_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onAddStation) {
            Text(
                text = stringResource(R.string.add_first_station),
                color = NeonViolet,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
