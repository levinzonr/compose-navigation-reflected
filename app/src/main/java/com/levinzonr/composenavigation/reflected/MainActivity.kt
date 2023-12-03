package com.levinzonr.composenavigation.reflected

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.levinzonr.composenavigation.reflected.ui.theme.ReflectedTheme
import com.levinzonr.reflected.core.Destination
import com.levinzonr.reflected.core.destinationDescriptor
import com.levinzonr.reflected.navigation.composable
import com.levinzonr.reflected.navigation.decodeDestination
import com.levinzonr.reflected.navigation.getDestination
import com.levinzonr.reflected.navigation.navigateTo
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

object Home : Destination

data class Test(val a: String, val b: String?, val c: Int = 32) : Destination

fun default(): String = "Test from string"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            ReflectedTheme {
                com.levinzonr.reflected.navigation.NavHost<Home>(
                    startDescriptor = destinationDescriptor(),
                    navController = navController,
                ) {
                    destinationDescriptor<Home>().composable {
                        Text(
                            text = "Home",
                            Modifier.clickable {
                                navController.navigateTo(Test("ah", null, 44)) {
                                }
                            },
                        )
                    }

                    destinationDescriptor<Test>().composable {
                        val dest = it.decodeDestination<Test>()
                        val test = hiltViewModel<HomeViewModel>()
                        Text(text = "$dest")
                    }
                }
            }
        }
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    init {
        val test: Test = savedStateHandle.getDestination()
        println(test)
    }
}
