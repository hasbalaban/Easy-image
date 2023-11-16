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
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.example.easy_image.utils.NavigationDirections
import com.example.easy_image.R
import com.easyImage.mediapi.model.FavoriteDTO
import com.example.easy_image.ui.theme.SaveWhattsappMediaTheme
import com.example.easy_image.utils.EnterAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel


@AndroidEntryPoint
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

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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
    @Composable
    fun MainNavHost(paddingValues: PaddingValues, navController: NavHostController) {
        val context = LocalContext.current

        val favoriteImages : SnapshotStateList<FavoriteDTO> = remember { mutableStateListOf() }


        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController, startDestination = NavigationDirections.SearchScreen.route ) {


            composable(NavigationDirections.HomeScreen.route) {

                //val viewModel = hiltViewModel<HomeViewModel>()
                val coroutines : CoroutineScope = rememberCoroutineScope()
                val openImageDetail = { imageUrl : String ->
                    navController.navigate(NavigationDirections.DetailScreen.createRoute(imageUrl = imageUrl))
                }

                EnterAnimation(content = {

                HomeScreen(openImageDetail, coroutines = coroutines, favoriteImages = favoriteImages, addOrRemoveFromFavoriteList = {favorite ->
                    if (favoriteImages.any {
                        it.id == favorite.id
                        })
                    {
                        favoriteImages.removeIf{
                            it.id == favorite.id
                        }
                        false
                    }
                    else
                    {
                        favoriteImages.add(favorite)
                        true
                    }
                })
                }
                )


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
                EnterAnimation(content = {
                    FavoriteScreen(navController, openImageDetail, favoriteImages = favoriteImages){
                        favoriteImages.remove(it)
                    }
                })

            }



            composable(NavigationDirections.Video.route,
                arguments = listOf(navArgument("searchMediaText") { type = NavType.StringType })
            ) {
                var searchMediaText = (it.arguments?.getString("searchMediaText"))
                if (searchMediaText?.contains("searchMediaText") == true) {
                    searchMediaText = "car"
                }

                val openVideoDetail = { imageUrl : String ->
                    navController.navigate(NavigationDirections.DetailScreen.createRoute(imageUrl = imageUrl))
                }

                EnterAnimation(content = {
                    VideoScreen(navController = navController,openVideoDetail, searchMediaText)
                })
            }

            composable(NavigationDirections.SearchScreen.route) {
                EnterAnimation(content = {

                    SearchScreen(){
                        navController.navigate(NavigationDirections.Video.createRoute(it))
                    }
                })
            }


            composable(NavigationDirections.DetailScreen.route,
                arguments = listOf(navArgument("imageUrl") {
                    type = NavType.StringType; defaultValue = ""
                },navArgument("hash") {
                    type = androidx.navigation.NavType.StringType; defaultValue = ""
                })) {

                var imageUrl = (it.arguments?.getString("imageUrl")) ?: ""
                val isImageUrl = imageUrl.contains(".jpg") || imageUrl.contains(".jpeg")

                if (it.arguments?.getString("hash").isNullOrEmpty().not()) {
                    imageUrl = imageUrl + "&hash="+ it.arguments?.getString("hash")
                }


                val lifeCycleOwner = LocalLifecycleOwner.current
                val activity = context as Activity

                val popBackStack : () -> Unit= {
                    navController.popBackStack()
                }

                DisposableEffect(LocalLifecycleOwner.current) {

                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_CREATE){
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            return@LifecycleEventObserver
                        }
                        if (event == Lifecycle.Event.ON_STOP){
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            return@LifecycleEventObserver
                        }
                    }
                    lifeCycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifeCycleOwner.lifecycle.removeObserver(observer) }
                }

                EnterAnimation(content = {
                    DetailScreen(imageUrl, isImage = isImageUrl, popBackStack)
                })


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
                    if (currentRoute != NavigationDirections.HomeScreen.route) navController.navigate(
                        NavigationDirections.HomeScreen.route)
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
                    if (currentRoute != NavigationDirections.Favorite.route) navController.navigate(
                        NavigationDirections.Favorite.route)
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
                    if (currentRoute != NavigationDirections.Video.route) navController.navigate(
                        NavigationDirections.Video.route)
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


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        SaveWhattsappMediaTheme {
            val navController = rememberNavController()
            MainNavHost(PaddingValues(), navController)
        }
    }
}





