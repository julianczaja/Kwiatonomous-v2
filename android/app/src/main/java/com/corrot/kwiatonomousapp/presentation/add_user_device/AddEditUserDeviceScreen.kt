package com.corrot.kwiatonomousapp.presentation.add_user_device

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.AppSettingsToggleItem
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancel
import com.corrot.kwiatonomousapp.common.components.DefaultScaffold
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@Composable
fun AddEditUserDeviceScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: AddEditUserDeviceViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                AddEditUserDeviceViewModel.Event.NAVIGATE_UP -> {
                    kwiatonomousAppState.navController.navigateUp()
                }
            }
        }
    }

    DefaultScaffold(
        scaffoldState = kwiatonomousAppState.scaffoldState,
        topBar = {
            DefaultTopAppBar(
                title = if (viewModel.isEditMode)
                    stringResource(R.string.edit_device)
                else
                    stringResource(R.string.add_device),
                onNavigateBackClicked = { kwiatonomousAppState.navController.popBackStack() }
            )
        }
    ) { padding ->
        AddEditUserDeviceScreenContent(
            state = viewModel.state.value,
            isEditMode = viewModel.isEditMode,
            padding = padding,
            onDeviceImageNameChanged = { viewModel.onDeviceImageNameChanged(it) },
            onDeviceIdChanged = { viewModel.onDeviceIdChanged(it) },
            onDeviceNameChanged = { viewModel.onDeviceNameChanged(it) },
            onNotificationsOnChanged = { viewModel.onNotificationsOnChanged(it) },
            onDoneClicked = { viewModel.onDoneClicked() },
            confirmError = { viewModel.confirmError() }
        )
    }
}

@ExperimentalPagerApi
@Composable
fun AddEditUserDeviceScreenContent(
    state: AddEditUserDeviceState,
    padding: PaddingValues,
    isEditMode: Boolean,
    onDeviceImageNameChanged: (String) -> Unit,
    onDeviceIdChanged: (String) -> Unit,
    onDeviceNameChanged: (String) -> Unit,
    onNotificationsOnChanged: (Boolean) -> Unit,
    onDoneClicked: () -> Unit,
    confirmError: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            item {
                if (isEditMode) {
                    // Wait until data is loaded to initiate pager with proper page
                    if (state.deviceImageName.isNotEmpty()) {
                        val initialPage = getPageByUserDeviceImageName(
                            context,
                            state.deviceImageName
                        )
                        UserDeviceImagePager(
                            context = context,
                            initialPage = initialPage,
                            onImageNameChanged = { onDeviceImageNameChanged(it) }
                        )
                    }
                } else {
                    UserDeviceImagePager(
                        context = context,
                        onImageNameChanged = { onDeviceImageNameChanged(it) }
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = state.deviceId,
                        onValueChange = { onDeviceIdChanged(it) },
                        label = { Text(stringResource(R.string.device_id)) },
                        singleLine = true,
                        isError = !state.isDeviceIdValid,
                        textStyle = MaterialTheme.typography.body1,
                        enabled = !isEditMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (!isEditMode) {
                        Text(
                            text = stringResource(R.string.device_id_length_format).format(state.deviceId.length),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                        )
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = state.deviceName,
                    onValueChange = { onDeviceNameChanged(it) },
                    label = { Text(stringResource(R.string.device_name)) },
                    singleLine = true,
                    isError = !state.isDeviceNameValid,
                    textStyle = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                AppSettingsToggleItem(
                    title = stringResource(R.string.notifications),
                    isChecked = state.notificationsOn,
                    onToggleClicked = { onNotificationsOnChanged(it) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                Button(
                    onClick = { onDoneClicked() },
                    enabled = state.isDeviceIdValid && state.isDeviceNameValid && !state.isLoading,
                    modifier = Modifier
                        .width(150.dp)
                        .padding(16.dp)
                ) {
                    Text(stringResource(R.string.done).uppercase())
                }
            }
        }
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        state.error?.let {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                ErrorBoxCancel(
                    message = state.error,
                    onCancel = { confirmError() }
                )
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun UserDeviceImagePager(
    context: Context,
    initialPage: Int? = null,
    onImageNameChanged: (String) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage ?: 0)

    // Observe page state
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onImageNameChanged(getUserDeviceImageNameByPage(context, page))
        }
    }

    HorizontalPager(
        count = 8,
        state = pagerState,
        modifier = Modifier.size(250.dp)
    ) { page ->
        Image(
            alignment = Alignment.Center,
            painter = painterResource(getUserDeviceImageIdByPage(page)),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        )
    }
    HorizontalPagerIndicator(
        pagerState = pagerState,
        activeColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(16.dp),
    )
}

private fun getUserDeviceImageNameByPage(context: Context, page: Int): String = when (page) {
    0 -> context.resources.getResourceEntryName(R.drawable.flower_1)
    1 -> context.resources.getResourceEntryName(R.drawable.flower_2)
    2 -> context.resources.getResourceEntryName(R.drawable.flower_3)
    3 -> context.resources.getResourceEntryName(R.drawable.flower_4)
    4 -> context.resources.getResourceEntryName(R.drawable.flower_5)
    5 -> context.resources.getResourceEntryName(R.drawable.flower_6)
    6 -> context.resources.getResourceEntryName(R.drawable.flower_7)
    7 -> context.resources.getResourceEntryName(R.drawable.flower_8)
    else -> context.resources.getResourceEntryName(R.drawable.flower_2)
}

private fun getUserDeviceImageIdByPage(page: Int): Int = when (page) {
    0 -> R.drawable.flower_1
    1 -> R.drawable.flower_2
    2 -> R.drawable.flower_3
    3 -> R.drawable.flower_4
    4 -> R.drawable.flower_5
    5 -> R.drawable.flower_6
    6 -> R.drawable.flower_7
    7 -> R.drawable.flower_8
    else -> R.drawable.flower_2
}

private fun getPageByUserDeviceImageName(context: Context, page: String): Int = when (page) {
    context.resources.getResourceEntryName(R.drawable.flower_1) -> 0
    context.resources.getResourceEntryName(R.drawable.flower_2) -> 1
    context.resources.getResourceEntryName(R.drawable.flower_3) -> 2
    context.resources.getResourceEntryName(R.drawable.flower_4) -> 3
    context.resources.getResourceEntryName(R.drawable.flower_5) -> 4
    context.resources.getResourceEntryName(R.drawable.flower_6) -> 5
    context.resources.getResourceEntryName(R.drawable.flower_7) -> 6
    context.resources.getResourceEntryName(R.drawable.flower_8) -> 7
    else -> 1
}

@ExperimentalPagerApi
@Preview(
    name = "AddEditUserDeviceScreenContentPreviewDark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AddEditUserDeviceScreenContentPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            AddEditUserDeviceScreenContent(
                state = AddEditUserDeviceState(
                    deviceId = "deviceId",
                    isDeviceIdValid = true,
                    deviceName = "deviceName",
                    isDeviceNameValid = false,
                ),
                PaddingValues(),
                isEditMode = false,
                {}, {}, {}, {}, {}, {}
            )
        }
    }
}
