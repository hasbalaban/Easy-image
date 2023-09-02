package com.example.savewhattsappmedia.view

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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

                    Scaffold() {it ->
                        it.let {}

                        MainNavHost()
                    }
                }
            }
        }
    }
}

@Composable
fun MainNavHost(){

    val context = LocalContext.current

    val navController = rememberNavController()
    var backHandlingEnabled by remember { mutableStateOf(false) }

    BackHandler(backHandlingEnabled) {
        if (navController.currentDestination?.route == NavigationDirections.DetailScreen.route){
            backHandlingEnabled = false
            val activity = context as Activity
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            navController.popBackStack()
        }
        else navController.popBackStack()
    }

    NavHost(navController = navController, startDestination = NavigationDirections.HomeScreen.route ) {


        composable(NavigationDirections.HomeScreen.route) {
            //val viewModel = hiltViewModel<TestViewModel>()
            val openImageDetail = { imageUrl : String ->
                navController.navigate(NavigationDirections.DetailScreen.createRoute(imageUrl = imageUrl))
            }

            HomeScreen(openImageDetail)
        }

        composable(NavigationDirections.SearchScreen.route) {

        }

        val detailScreenArguments = listOf(
            navArgument("imageUrl") {
                type = NavType.StringType; defaultValue = ""
            }
        )

        composable(NavigationDirections.DetailScreen.route, arguments = detailScreenArguments) {
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