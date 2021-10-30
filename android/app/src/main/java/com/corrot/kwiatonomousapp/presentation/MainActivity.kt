package com.corrot.kwiatonomousapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.corrot.kwiatonomousapp.presentation.dasboard.DashboardScreen
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KwiatonomousAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    DashboardScreen()
                }
            }
        }
    }
}
