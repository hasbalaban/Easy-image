package com.example.savewhattsappmedia.view

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.savewhattsappmedia.NavigationDirections
import com.example.savewhattsappmedia.ui.theme.SaveWhattsappMediaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaveWhattsappMediaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MainNavHost()
                }
            }
        }
    }
}

@Composable
fun MainNavHost(){
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavigationDirections.HomeScreen.route ) {


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



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SaveWhattsappMediaTheme {
        MainNavHost()
    }
}