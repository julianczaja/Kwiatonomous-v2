package com.corrot.kwiatonomousapp.presentation.add_user_device

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect

@ExperimentalPagerApi
@Composable
fun AddUserDeviceScreen(
    navController: NavController,
    viewModel: AddUserDeviceViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                AddUserDeviceViewModel.Event.NAVIGATE_UP -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(45.dp),
                backgroundColor = MaterialTheme.colors.primary,
                title = { Text(text = stringResource(R.string.add_device)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "")
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                item {
                    UserDeviceImagePager(onImageIdChanged = { viewModel.onDeviceImageIdChanged(it) })
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.deviceId,
                            onValueChange = {
                                viewModel.onDeviceIdChanged(it)
                            },
                            label = { Text(stringResource(R.string.device_id)) },
                            singleLine = true,
                            isError = !state.isDeviceIdValid,
                            textStyle = MaterialTheme.typography.body1,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = stringResource(R.string.device_id_length_format).format(state.deviceId.length),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.body1.copy(fontSize = 11.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = state.deviceName,
                        onValueChange = { viewModel.onDeviceNameChanged(it) },
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
                    Button(
                        onClick = { viewModel.onDoneClicked() },
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
                        onCancel = { viewModel.confirmError() }
                    )
                }
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun UserDeviceImagePager(onImageIdChanged: (Int) -> Unit) {
    val pagerState = rememberPagerState()

    // Observe page state
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onImageIdChanged(getUserDeviceImageIdByNumber(page))
        }
    }

    HorizontalPager(
        count = 8,
        state = pagerState,
        modifier = Modifier.size(250.dp)
    ) { page ->
        Image(
            alignment = Alignment.Center,
            painter = painterResource(getUserDeviceImageIdByNumber(page)),
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

private fun getUserDeviceImageIdByNumber(number: Int): Int = when (number) {
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