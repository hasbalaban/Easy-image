package com.example.easy_image.view

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.easy_image.NavigationDirections
import com.example.easy_image.R
import com.example.easy_image.ui.theme.SaveWhattsappMediaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.system.measureTimeMillis


class MainActivity : ComponentActivity() {

    init {
        System.loadLibrary("cpp_code")
    }

    external fun myNativeFunction(a: Int, b: Int): Int

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()
            SaveWhattsappMediaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Scaffold(
                        bottomBar = {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route
                            if (currentRoute != NavigationDirections.DetailScreen.route) {
                                BottomBar(navController)
                            }
                        }
                    ) {it ->

                        MainNavHost(it, navController)
                    }



                }
            }
        }
    }
    @Composable
    fun MainNavHost(paddingValues: PaddingValues, navController: NavHostController) {
        val context = LocalContext.current

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


                val lifeCycleOwner = LocalLifecycleOwner.current
                DisposableEffect(LocalLifecycleOwner.current) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event){
                            Lifecycle.Event.ON_RESUME -> ""
                            else -> ""
                        }
                    }
                    lifeCycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        onDispose { coroutines.cancel()}
                        lifeCycleOwner.lifecycle.removeObserver(observer)
                    }
                }
            }

            composable(NavigationDirections.Favorite.route) {
                val openImageDetail = { imageUrl : String ->
                    navController.navigate(NavigationDirections.DetailScreen.createRoute(imageUrl = imageUrl))
                }

                FavoriteScreen(navController, openImageDetail)
            }

            composable(NavigationDirections.Video.route) {

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
    private fun BottomBar(navController: NavHostController) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomAppBar(windowInsets = WindowInsets.ime) {

            NavigationBarItem(
                selected = currentRoute == NavigationDirections.HomeScreen.route,
                onClick = {
                    if (currentRoute != NavigationDirections.HomeScreen.route) navController.navigate(NavigationDirections.HomeScreen.route)
                },
                icon = {
                       Image(
                           modifier = Modifier.padding(horizontal = 2.dp),
                           colorFilter = ColorFilter.tint(Color.Blue),
                           painter = painterResource(id = R.drawable.ic_home), contentDescription = NavigationDirections.HomeScreen.route)
                },
                label = {
                    Text(text = "Images", color = Color.Black)
                }
            )
            NavigationBarItem(
                selected = currentRoute == NavigationDirections.Favorite.route,
                onClick = {
                    if (currentRoute != NavigationDirections.Favorite.route) navController.navigate(NavigationDirections.Favorite.route)
                          },
                icon = {
                    Image(
                        modifier = Modifier.padding(horizontal = 2.dp),
                        colorFilter = ColorFilter.tint(Color.Blue),
                        painter = painterResource(id = R.drawable.ic_favorite), contentDescription = NavigationDirections.Favorite.route)
                },
                label = {
                    Text(text = "Favorite", color = Color.Black)
                }
            )
            NavigationBarItem(
                selected = currentRoute == NavigationDirections.Video.route,
                onClick = {
                    if (currentRoute != NavigationDirections.Video.route) navController.navigate(NavigationDirections.Video.route)
                          },
                icon = {
                    Image(
                        modifier = Modifier.padding(horizontal = 2.dp),
                        colorFilter = ColorFilter.tint(Color.Blue),
                        painter = painterResource(id = R.drawable.ic_video), contentDescription = NavigationDirections.Video.route)
                },
                label = {
                    Text(text = "Video", color = Color.Black)
                }
            )

        }
    }

    // test cpp and kotlin
    fun yNativeFunction(a: Int, b: Int): Int {
        for (i in 0..100000) {
            if (i == 999) {
                return i
            }
        }
        return 0
    }
    private fun testcpp(){
        myNativeFunction(1,2)
        val time = measureTimeMillis {
            for (i in 0..1){
                myNativeFunction(1,1)
            }
        }
        println(time)
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        SaveWhattsappMediaTheme {
            val navController = rememberNavController()
            MainNavHost(PaddingValues(), navController)
        }
    }
}





