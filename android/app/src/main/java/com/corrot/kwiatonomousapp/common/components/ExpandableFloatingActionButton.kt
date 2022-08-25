package com.corrot.kwiatonomousapp.common.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import kotlinx.coroutines.launch

private const val SMALL_BUTTON_RADIUS_DP = 42
private const val SMALL_BUTTON_IMAGE_SIZE_DP = 21
private const val SMALL_BUTTON_STROKE_DP = 2

data class ExpandableFloatingActionButtonItem(
    val strokeColor: Color,
    val fillColor: Color,
    @DrawableRes val imageId: Int,
    val onItemClick: () -> Unit,
)

@Composable
fun ExpandableFloatingActionButton(
    isVisible: Boolean,
    items: List<ExpandableFloatingActionButtonItem>,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val alphaAnimatable = remember { Animatable(0f) }
    val positionAnimatable = remember { Animatable(0f) }
    val rotationAnimatable = remember { Animatable(0f) }

    LaunchedEffect(isExpanded) {
        launch {
            positionAnimatable.animateTo(
                targetValue = if (isExpanded) 1f else 0f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alphaAnimatable.animateTo(
                targetValue = if (isExpanded) 1f else 0f,
                animationSpec = tween(250, 100, LinearEasing)
            )
        }
        launch {
            rotationAnimatable.animateTo(
                targetValue = if (isExpanded) -45f else 0f,
                animationSpec = tween(150, easing = FastOutSlowInEasing)
            )
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            if (positionAnimatable.value > 0) {
                items.forEachIndexed { index, item ->
                    val padding = (72.dp + (56.dp * index)) * positionAnimatable.value
                    SmallFloatingActionButton(
                        alpha = alphaAnimatable.value,
                        item = item,
                        onItemClick = { isExpanded = false },
                        modifier = Modifier.padding(bottom = padding)
                    )
                }
            }
            FloatingActionButton(
                onClick = { isExpanded = !isExpanded },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, "",
                    modifier = Modifier.rotate(rotationAnimatable.value)
                )
            }
        }
    }
}

@Composable
fun SmallFloatingActionButton(
    alpha: Float,
    item: ExpandableFloatingActionButtonItem,
    onItemClick: () -> Unit,
    modifier: Modifier,
) {
    val imageSizePx = with(LocalDensity.current) { SMALL_BUTTON_IMAGE_SIZE_DP.dp.toPx() }
    val strokeSizePx = with(LocalDensity.current) { SMALL_BUTTON_STROKE_DP.dp.toPx() }
    val image = ImageBitmap.imageResource(item.imageId)
        .asAndroidBitmap()
        .scale(imageSizePx.toInt(), imageSizePx.toInt())
        .asImageBitmap()

    Canvas(
        modifier = modifier
            .size(if (alpha > 0f) SMALL_BUTTON_RADIUS_DP.dp else 0.dp)
            .graphicsLayer(
                shadowElevation = 16f,
                alpha = alpha,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = {
                    onItemClick.invoke()
                    item.onItemClick.invoke()
                }
            )
    ) {
        drawCircle(
            color = item.fillColor,
        )
        drawImage(
            image = image,
            topLeft = Offset(
                x = center.x - (imageSizePx / 2f),
                y = center.y - (imageSizePx / 2f)
            )
        )
        drawCircle(
            color = item.strokeColor,
            style = Stroke(width = strokeSizePx)
        )
    }
}