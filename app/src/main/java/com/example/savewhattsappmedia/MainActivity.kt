package com.example.savewhattsappmedia

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.savewhattsappmedia.ui.theme.SaveWhattsappMediaTheme



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
                    val viewModel: TestViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    Column() {
                        val context = LocalContext.current
                        val backHandlingEnabled by remember { mutableStateOf(false) }

                        BackHandler(backHandlingEnabled) {
                            Toast.makeText(context, "ww" , Toast.LENGTH_LONG).show()
                        }
                        LaunchedEffect(Unit){
                            viewModel.getPhotos(
                                query = "sun",
                                page = 1
                            )
                        }

                        var page by remember { mutableStateOf(1) }
                        val photos = viewModel.photos.observeAsState()
                        ObserveCounter(photos.value, page = page){
                            page++
                                viewModel.getPhotos(
                                    query = "sun",
                                    page = page
                                )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ObserveCounter(photos: ImageResponse?, page : Int,  viewModel: TestViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), scrollToBottomOfPhotos : () -> Unit) {
    Column() {
        photos?.hits?.let {

            val listState = rememberLazyGridState()
            LaunchedEffect(listState.canScrollForward){
                if (listState.canScrollForward.not()){
                    scrollToBottomOfPhotos.invoke()
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                userScrollEnabled = true,
                state = listState
            ) {
                items(it,
                    key = {
                        it.id
                    }
                ){item->
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(120.dp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.largeImageURL)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
    }


}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SaveWhattsappMediaTheme {
        Greeting("Android")
    }
}