package com.chilltrax.radio.presentation.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chilltrax.radio.R
import com.chilltrax.radio.presentation.theme.ChillBackground
import com.chilltrax.radio.presentation.theme.ChillBackgroundAlt
import com.chilltrax.radio.presentation.theme.ErrorRed
import com.chilltrax.radio.presentation.theme.NeonAzure
import com.chilltrax.radio.presentation.theme.NeonBlue
import com.chilltrax.radio.presentation.theme.NeonGradient
import com.chilltrax.radio.presentation.theme.NeonSky
import com.chilltrax.radio.presentation.theme.SurfaceDark
import com.chilltrax.radio.presentation.theme.SurfaceElevated
import com.chilltrax.radio.presentation.theme.TextPrimary
import com.chilltrax.radio.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationEditorScreen(
    viewModel: StationEditorViewModel,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                EditorEvent.Saved -> onNavigateBack()
                is EditorEvent.Message -> {
                    snackbarHostState.showSnackbar(event.message.resolve(context))
                }
            }
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
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(
                                if (uiState.isEditing) {
                                    R.string.editor_title_edit
                                } else {
                                    R.string.editor_title_new
                                },
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
                                tint = TextPrimary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = TextPrimary,
                        navigationIconContentColor = TextPrimary,
                    ),
                )
            },
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = NeonSky)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonSky,
                    )

                    StationTextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        label = stringResource(R.string.station_name),
                        placeholder = stringResource(R.string.station_name_hint),
                        isError = uiState.nameError,
                        errorText = stringResource(R.string.error_name_required),
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    )

                    StationTextField(
                        value = uiState.streamUrl,
                        onValueChange = viewModel::onUrlChange,
                        label = stringResource(R.string.stream_url),
                        placeholder = stringResource(R.string.stream_url_hint),
                        isError = uiState.urlError,
                        errorText = stringResource(R.string.error_url_invalid),
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next,
                    )

                    OutlinedButton(
                        onClick = viewModel::onCheckStream,
                        enabled = !uiState.isChecking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NeonAzure.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NeonSky,
                            disabledContentColor = TextSecondary,
                        ),
                    ) {
                        if (uiState.isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = NeonSky,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(19.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(
                                if (uiState.isChecking) R.string.checking else R.string.check_stream,
                            ),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    OutlinedButton(
                        onClick = viewModel::onLookup,
                        enabled = !uiState.isChecking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NeonBlue.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NeonSky,
                            disabledContentColor = TextSecondary,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TravelExplore,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.lookup_online),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    StationTextField(
                        value = uiState.genre,
                        onValueChange = viewModel::onGenreChange,
                        label = stringResource(R.string.genre),
                        placeholder = stringResource(R.string.genre_hint),
                        isError = false,
                        errorText = null,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    )

                    StreamHint()

                    Spacer(modifier = Modifier.height(4.dp))

                    SaveButton(
                        isSaving = uiState.isSaving,
                        onClick = viewModel::onSave,
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun StationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean,
    errorText: String?,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = {
            Text(
                text = placeholder,
                color = TextSecondary.copy(alpha = 0.6f),
            )
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        supportingText = if (isError && errorText != null) {
            { Text(errorText) }
        } else {
            null
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = SurfaceDark.copy(alpha = 0.75f),
            unfocusedContainerColor = SurfaceDark.copy(alpha = 0.55f),
            errorContainerColor = SurfaceDark.copy(alpha = 0.75f),
            cursorColor = NeonSky,
            focusedBorderColor = NeonSky,
            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
            errorBorderColor = ErrorRed,
            focusedLabelColor = NeonSky,
            unfocusedLabelColor = TextSecondary,
            errorLabelColor = ErrorRed,
            focusedSupportingTextColor = ErrorRed,
            unfocusedSupportingTextColor = TextSecondary,
            errorSupportingTextColor = ErrorRed,
        ),
    )
}

@Composable
private fun StreamHint() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceElevated.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(6.dp)
                .background(NeonSky, CircleShape),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.stream_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}

@Composable
private fun SaveButton(
    isSaving: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(18.dp, RoundedCornerShape(18.dp), spotColor = NeonAzure)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(NeonGradient))
            .alpha(if (isSaving) 0.7f else 1f)
            .clickable(enabled = !isSaving, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isSaving) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.saving),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
            }
        } else {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
