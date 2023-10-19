package com.example.easy_image.view

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.easy_image.NavigationDirections
import com.example.easy_image.ui.theme.SaveWhattsappMediaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaveWhattsappMediaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Scaffold(
                        bottomBar = {
                            BottomBar()
                        }
                    ) {it ->

                        MainNavHost(it)
                    }

                }
            }
        }
    }
    @Composable
    fun MainNavHost(paddingValues: PaddingValues) {
        val context = LocalContext.current
        val navController = rememberNavController()

        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController, startDestination = NavigationDirections.HomeScreen.route ) {


            composable(NavigationDirections.HomeScreen.route) {
                //val viewModel = hiltViewModel<TestViewModel>()
                val coroutines : CoroutineScope = rememberCoroutineScope()
                val openImageDetail = { imageUrl : String ->
                    navController.navigate(NavigationDirections.DetailScreen.createRoute(imageUrl = imageUrl))
                }

                HomeScreen(openImageDetail, coroutines = coroutines)

                DisposableEffect(LocalLifecycleOwner.current) {
                    onDispose { coroutines.cancel()}
                }
            }

            composable(NavigationDirections.SearchScreen.route) {

            }


            composable(NavigationDirections.DetailScreen.route, arguments = listOf(navArgument("imageUrl") {
                type = NavType.StringType; defaultValue = ""
            })) {
                val imageUrl = it.arguments?.getString("imageUrl") ?: ""
                val lifeCycleOwner = LocalLifecycleOwner.current
                val activity = context as Activity

                val popBackStack : () -> Unit= {
                    navController.popBackStack()
                }

                DisposableEffect(LocalLifecycleOwner.current) {
                    val observer = LifecycleEventObserver { _, event ->
                        activity.requestedOrientation = when (event){
                            Lifecycle.Event.ON_RESUME ->  {
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            }
                            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }
                    lifeCycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifeCycleOwner.lifecycle.removeObserver(observer) }
                }

                DetailScreen(imageUrl, popBackStack)

            }
        }
    }

    @Composable
    private fun BottomBar(){
        val context = LocalContext.current
        Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show()

        BottomAppBar(contentPadding = PaddingValues(bottom = 16.dp)) {
            NavigationBarItem(
                selected = false,
                onClick = {

                },
                icon = {

                },
                label = {
                    Text(text = "Images", color = Color.Black)
                }
            )
            NavigationBarItem(
                selected = false,
                onClick = {

                },
                icon = {

                },
                label = {
                    Text(text = "Favorite", color = Color.Black)
                }
            )
            NavigationBarItem(
                selected = false,
                onClick = {

                },
                icon = {

                },
                label = {
                    Text(text = "Video", color = Color.Black)
                }
            )

        }
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        SaveWhattsappMediaTheme {
            MainNavHost(PaddingValues())
        }
    }
}





