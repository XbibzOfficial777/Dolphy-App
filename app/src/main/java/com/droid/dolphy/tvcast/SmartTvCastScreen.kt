package com.droid.dolphy.tvcast

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.droid.dolphy.DolphyButton
import com.droid.dolphy.DolphyStateCard
import com.droid.dolphy.M3SegmentedListItem
import com.droid.dolphy.MaterialBackground
import com.droid.dolphy.MaterialCard
import com.droid.dolphy.R
import com.droid.dolphy.SectionTopBar
import com.droid.dolphy.m3SegmentedItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
private fun castStatusLabel(status: CastStatus): String = when (status) {
    CastStatus.IDLE -> stringResource(R.string.cast_disconnect)
    CastStatus.CONNECTING -> stringResource(R.string.cast_connecting)
    CastStatus.CONNECTED -> stringResource(R.string.cast_connected)
    CastStatus.LOADING -> stringResource(R.string.cast_loading)
    CastStatus.PLAYING -> stringResource(R.string.cast_playing)
    CastStatus.PAUSED -> stringResource(R.string.cast_paused)
    CastStatus.ERROR -> stringResource(R.string.cast_connection_error)
}

@Composable
private fun CastMediaPreview(media: ServedCastMedia) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(20.dp)
    when {
        media.mimeType.startsWith("image/") -> {
            AsyncImage(
                model = media.file,
                contentDescription = media.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(shape)
                    .background(Color.Black),
                contentScale = ContentScale.Fit,
            )
        }
        media.mimeType.startsWith("video/") || media.mimeType.startsWith("audio/") -> {
            val player = remember(media.file) {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(media.file.absolutePath))
                    prepare()
                }
            }
            DisposableEffect(media.file) {
                onDispose { player.release() }
            }
            AndroidView(
                factory = { viewContext ->
                    PlayerView(viewContext).apply {
                        this.player = player
                        useController = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(shape)
                    .background(Color.Black),
            )
        }
    }
}

@Composable
fun SmartTvCastScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var googleDevices by remember { mutableStateOf<List<CastDevice>>(emptyList()) }
    var dlnaDevices by remember { mutableStateOf<List<CastDevice>>(emptyList()) }
    var googleState by remember { mutableStateOf(CastConnectionState()) }
    var dlnaState by remember { mutableStateOf(CastConnectionState()) }
    var activeProtocol by remember { mutableStateOf<CastProtocol?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var hasCompletedScan by remember { mutableStateOf(false) }
    var discoveryError by remember { mutableStateOf<String?>(null) }

    var servedMedia by remember { mutableStateOf(CastMediaServerManager.current) }
    var isPreparingMedia by remember { mutableStateOf(false) }
    var mediaError by remember { mutableStateOf<String?>(null) }

    val googleController = remember {
        GoogleCastController(
            context = context,
            onDevices = { googleDevices = it },
            onState = { state ->
                googleState = state
                if (activeProtocol == null && state.device != null && state.status != CastStatus.ERROR) {
                    activeProtocol = CastProtocol.GOOGLE_CAST
                }
            },
        )
    }
    val dlnaController = remember {
        DlnaController(
            context = context,
            onDevices = { dlnaDevices = it },
            onScanning = { scanning ->
                isScanning = scanning
                if (!scanning) hasCompletedScan = true
            },
            onDiscoveryError = { discoveryError = it },
            onState = { state ->
                dlnaState = state
                if (activeProtocol == null && state.device != null && state.status != CastStatus.ERROR) {
                    activeProtocol = CastProtocol.DLNA_UPNP
                }
            },
        )
    }

    val requiredPermissions = remember {
        if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
    fun permissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    var hasPermissions by remember { mutableStateOf(permissionsGranted()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        hasPermissions = permissionsGranted()
    }

    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                isPreparingMedia = true
                mediaError = null
                try {
                    servedMedia = withContext(Dispatchers.IO) {
                        CastMediaServerManager.serve(context, uri)
                    }
                } catch (e: Exception) {
                    mediaError = e.message ?: e.javaClass.simpleName
                } finally {
                    isPreparingMedia = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermissions) permissionLauncher.launch(requiredPermissions)
    }
    LaunchedEffect(hasPermissions) {
        if (hasPermissions) {
            googleController.start()
            dlnaController.startDiscovery()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            googleController.close()
            dlnaController.close()
        }
    }

    val devices = googleDevices + dlnaDevices
    val connection = when (activeProtocol) {
        CastProtocol.GOOGLE_CAST -> googleState
        CastProtocol.DLNA_UPNP -> dlnaState
        null -> CastConnectionState()
    }
    val isConnected = connection.status in setOf(
        CastStatus.CONNECTED,
        CastStatus.LOADING,
        CastStatus.PLAYING,
        CastStatus.PAUSED,
    )
    val isBusy = connection.status == CastStatus.CONNECTING || connection.status == CastStatus.LOADING

    MaterialBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            SectionTopBar(
                title = stringResource(R.string.smarttv_cast_title),
                onBack = { navController.popBackStack() },
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    MaterialCard(modifier = Modifier.fillMaxWidth(), containerType = "primary") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                Icons.Default.Cast,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                stringResource(R.string.smarttv_cast_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                stringResource(R.string.smarttv_cast_card_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }

                if (!hasPermissions) {
                    item {
                        DolphyStateCard(
                            title = stringResource(R.string.cast_permission_title),
                            message = stringResource(R.string.cast_permission_message),
                            icon = Icons.Default.Wifi,
                            actionLabel = stringResource(R.string.cast_grant_permission),
                            onAction = { permissionLauncher.launch(requiredPermissions) },
                        )
                    }
                }

                item {
                    MaterialCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                stringResource(R.string.cast_media_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            servedMedia?.let { media ->
                                CastMediaPreview(media)
                                Text(
                                    media.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    "${media.mimeType} • ${stringResource(R.string.cast_local_url)}: ${media.contentUrl}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } ?: Text(
                                stringResource(R.string.cast_media_none),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (isPreparingMedia) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    Text(stringResource(R.string.cast_media_preparing))
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DolphyButton(
                                    onClick = {
                                        mediaPicker.launch(arrayOf("video/*", "audio/*", "image/*"))
                                    },
                                    enabled = !isPreparingMedia,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Icon(Icons.Default.VideoLibrary, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.smarttv_cast_choose_video))
                                }
                                if (servedMedia != null) {
                                    FilledTonalButton(
                                        onClick = {
                                            if (isConnected) {
                                                when (activeProtocol) {
                                                    CastProtocol.GOOGLE_CAST -> googleController.stopPlayback()
                                                    CastProtocol.DLNA_UPNP -> dlnaController.stopPlayback()
                                                    null -> Unit
                                                }
                                            }
                                            CastMediaServerManager.clear()
                                            servedMedia = null
                                        },
                                        enabled = !isPreparingMedia,
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text(stringResource(R.string.cast_media_clear))
                                    }
                                }
                            }
                        }
                    }
                }

                mediaError?.let { error ->
                    item {
                        DolphyStateCard(
                            title = stringResource(R.string.cast_media_error),
                            message = error,
                            icon = Icons.Default.ErrorOutline,
                        )
                    }
                }

                if (hasPermissions) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        stringResource(R.string.smarttv_cast_devices, devices.size),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        stringResource(R.string.cast_devices_subtitle),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                IconButton(
                                    onClick = { dlnaController.startDiscovery() },
                                    enabled = !isScanning,
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = stringResource(R.string.cast_refresh),
                                    )
                                }
                            }
                            if (isScanning) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                Text(
                                    stringResource(R.string.cast_scanning),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    discoveryError?.let { error ->
                        item {
                            DolphyStateCard(
                                title = stringResource(R.string.cast_discovery_error),
                                message = error,
                                icon = Icons.Default.ErrorOutline,
                                actionLabel = stringResource(R.string.retry),
                                onAction = { dlnaController.startDiscovery() },
                            )
                        }
                    }

                    if (googleState.status == CastStatus.ERROR && googleState.device == null && activeProtocol == null) {
                        item {
                            DolphyStateCard(
                                title = stringResource(R.string.cast_connection_error),
                                message = googleState.detail.orEmpty(),
                                icon = Icons.Default.ErrorOutline,
                            )
                        }
                    }

                    if (hasCompletedScan && !isScanning && devices.isEmpty()) {
                        item {
                            DolphyStateCard(
                                title = stringResource(R.string.smarttv_cast_no_devices),
                                message = stringResource(R.string.cast_no_devices_message),
                                icon = Icons.Default.Devices,
                                actionLabel = stringResource(R.string.cast_refresh),
                                onAction = { dlnaController.startDiscovery() },
                            )
                        }
                    }

                    m3SegmentedItems(devices, key = { "${it.protocol}:${it.id}" }) { index, count, device ->
                        val isActive = connection.device?.id == device.id && activeProtocol == device.protocol
                        M3SegmentedListItem(
                            index = index,
                            count = count,
                            headline = device.name,
                            supporting = "${stringResource(
                                if (device.protocol == CastProtocol.GOOGLE_CAST) R.string.cast_protocol_google
                                else R.string.cast_protocol_dlna,
                            )} • ${device.endpoint}",
                            leadingIcon = if (device.protocol == CastProtocol.GOOGLE_CAST) Icons.Default.Cast else Icons.Default.Devices,
                            selected = isActive,
                            showChevron = false,
                            trailingContent = {
                                if (isActive && isConnected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = stringResource(R.string.cast_connected),
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                } else {
                                    Text(
                                        stringResource(R.string.cast_connect),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                }
                            },
                            onClick = {
                                activeProtocol = device.protocol
                                when (device.protocol) {
                                    CastProtocol.GOOGLE_CAST -> googleController.connect(device)
                                    CastProtocol.DLNA_UPNP -> dlnaController.connect(device)
                                }
                            },
                        )
                    }

                    connection.device?.let { device ->
                        item {
                            MaterialCard(
                                modifier = Modifier.fillMaxWidth(),
                                containerType = if (connection.status == CastStatus.ERROR) "highest" else "secondary",
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isBusy) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(
                                                if (connection.status == CastStatus.ERROR) Icons.Default.ErrorOutline else Icons.Default.Cast,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                            )
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(device.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                castStatusLabel(connection.status),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                    connection.detail?.takeIf { it.isNotBlank() }?.let { detail ->
                                        Text(
                                            detail,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }

                                    if (connection.status == CastStatus.ERROR) {
                                        DolphyButton(
                                            onClick = {
                                                when (device.protocol) {
                                                    CastProtocol.GOOGLE_CAST -> googleController.connect(device)
                                                    CastProtocol.DLNA_UPNP -> dlnaController.connect(device)
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(R.string.retry))
                                        }
                                    } else if (isConnected) {
                                        DolphyButton(
                                            onClick = {
                                                servedMedia?.asCastMedia()?.let { media ->
                                                    when (device.protocol) {
                                                        CastProtocol.GOOGLE_CAST -> googleController.play(media)
                                                        CastProtocol.DLNA_UPNP -> dlnaController.play(media)
                                                    }
                                                }
                                            },
                                            enabled = servedMedia != null && !isBusy,
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(R.string.cast_play))
                                        }
                                        if (connection.status == CastStatus.PLAYING || connection.status == CastStatus.PAUSED) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                FilledTonalButton(
                                                    onClick = {
                                                        when (device.protocol) {
                                                            CastProtocol.GOOGLE_CAST -> googleController.togglePlayPause()
                                                            CastProtocol.DLNA_UPNP -> dlnaController.togglePlayPause()
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                ) {
                                                    Icon(
                                                        if (connection.status == CastStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                    )
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(
                                                        stringResource(
                                                            if (connection.status == CastStatus.PLAYING) R.string.cast_pause
                                                            else R.string.cast_resume,
                                                        ),
                                                    )
                                                }
                                                FilledTonalButton(
                                                    onClick = {
                                                        when (device.protocol) {
                                                            CastProtocol.GOOGLE_CAST -> googleController.stopPlayback()
                                                            CastProtocol.DLNA_UPNP -> dlnaController.stopPlayback()
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                ) {
                                                    Icon(Icons.Default.Stop, contentDescription = null)
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(stringResource(R.string.cast_stop))
                                                }
                                            }
                                        }
                                    }

                                    TextButton(
                                        onClick = {
                                            when (device.protocol) {
                                                CastProtocol.GOOGLE_CAST -> googleController.disconnect()
                                                CastProtocol.DLNA_UPNP -> dlnaController.disconnect()
                                            }
                                            activeProtocol = null
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                    ) {
                                        Text(stringResource(R.string.cast_disconnect))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        stringResource(R.string.cast_footnote),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
                item { Spacer(Modifier.navigationBarsPadding().height(8.dp)) }
            }
        }
    }
}
