package com.example.savewhattsappmedia.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.savewhattsappmedia.Hits
import com.example.savewhattsappmedia.ImageResponse
import com.example.savewhattsappmedia.R
import com.example.savewhattsappmedia.SaveImageToCacheAndShare
import com.example.savewhattsappmedia.TestViewModel
import com.example.savewhattsappmedia.ignoreNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    openImageDetail: (String) -> Unit,
    coroutines : CoroutineScope,
    viewModel: TestViewModel= androidx.lifecycle.viewmodel.compose.viewModel()
)  {

    val photos = viewModel.photos.observeAsState()
    val state = rememberLazyGridState()
    val shouldGetNewPage by remember { derivedStateOf { (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= ((photos.value?.hits?.size ?: 1) * 0.8) } }
    var searchedImageText by remember { mutableStateOf("sun") }


    val keyboardController = LocalSoftwareKeyboardController.current
    if (state.interactionSource.collectIsDraggedAsState().value){
        LaunchedEffect(Unit) {
            keyboardController?.hide()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getPhotos()
    }

    ObserveCounter(
        shouldGetNewPage,
        state,
        photos.value,
        searchedImageText = searchedImageText,
        coroutines = coroutines,
        onSearchedTextChanged =  {
            searchedImageText = it
            coroutines.launch {
                delay(300L)
                viewModel.getPhotos(searchedImageText, shouldClearPhotos = true)
            }
        } ,
        openImageDetail){
        viewModel.getPhotos(searchedImageText)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObserveCounter(
    shouldGetNewPage : Boolean,
    state : LazyGridState,
    photos: ImageResponse?,
    searchedImageText: String,
    coroutines: CoroutineScope,
    onSearchedTextChanged: (String) -> Unit,
    openImageDetail : (String) -> Unit,
    scrollToBottomOfPhotos : () -> Unit,
) {

    var shouldCheckBoxVisible by remember { mutableStateOf(false) }
    val selectedImages : SnapshotStateList<Pair<Long, Bitmap>> = remember { mutableStateListOf() }
    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    SideEffect {
        if (shouldGetNewPage) {
            scrollToBottomOfPhotos.invoke()
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior ()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MainTopAppBar(scrollBehavior, searchedImageText, selectedImages, dropDownMenuExpanded){
            dropDownMenuExpanded = it
        } },
        bottomBar = {
            MainBottomBar(scrollBehavior, searchedImageText) { newText: String ->
                onSearchedTextChanged.invoke(newText)
            }
        },
        floatingActionButton = {
            photos?.hits?.size?.let {
                FloatActionContent(coroutines, state, it)
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            MainContent(photos, state, openImageDetail, coroutines, selectedImages,
                {uuId, bitmap, isChecked ->

                    if (isChecked){
                        selectedImages.add(Pair(first = uuId, second = bitmap))
                        return@MainContent
                    }

                    selectedImages.firstOrNull{
                        it.first == uuId
                    }?.let {
                        selectedImages.remove(it)
                    }

                    return@MainContent
                },
                shouldCheckBoxVisible){
                    shouldCheckBoxVisible = shouldCheckBoxVisible.not()
                    if (shouldCheckBoxVisible) {
                        selectedImages.clear()
                    }
            }
        }
    }

}

@Composable
private fun FloatActionContent(coroutines: CoroutineScope, state: LazyGridState, lastIndex: Int) {
    Column() {
        Image(
            modifier = Modifier
                .background(Color.White)
                .padding(4.dp)
                .clickable {
                    coroutines.launch {
                        var targetPosition =
                            (state.layoutInfo.visibleItemsInfo.firstOrNull()?.index.ignoreNull()) - (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index.ignoreNull() - state.layoutInfo.visibleItemsInfo.firstOrNull()?.index.ignoreNull() - 8 )
                        targetPosition = if (targetPosition <= 0) 0 else targetPosition
                        state.animateScrollToItem(targetPosition)
                    }
                },
            colorFilter = ColorFilter.tint(Color.Black),
            painter = painterResource(id = R.drawable.arrow_upward), contentDescription = "go to up"
        )
        Image(
            modifier = Modifier
                .padding(top = 6.dp)
                .clickable {
                    coroutines.launch {
                        var targetPosition =
                            state.layoutInfo.visibleItemsInfo.firstOrNull()?.index.ignoreNull() + (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index.ignoreNull() - state.layoutInfo.visibleItemsInfo.firstOrNull()?.index.ignoreNull()) - 2
                        targetPosition =
                            if (targetPosition > lastIndex) lastIndex else targetPosition
                        state.animateScrollToItem(targetPosition)

                    }
                }
                .background(Color.White)
                .padding(4.dp),
            colorFilter = ColorFilter.tint(Color.Black),
            painter = painterResource(id = R.drawable.arrow_downward),
            contentDescription = "go to bottom"
        )
    }
}

@Composable
private fun MainContent(
    photos: ImageResponse?,
    state: LazyGridState,
    openImageDetail: (String) -> Unit,
    coroutines: CoroutineScope,
    selectedImagesList: SnapshotStateList<Pair<Long, Bitmap>>,
    onSelectedImage: (Long, Bitmap, Boolean) -> Unit,
    shouldCheckBoxVisible: Boolean,
    onImageLongClicked: () -> Unit
) {

    photos?.hits?.let {
        val context = LocalContext.current

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
            userScrollEnabled = true,
            state = state
        ) {

            items(
                it,
                key = { it.uuId }
            ) { item ->

                val isChecked = selectedImagesList.any {
                    it.first == item.uuId
                }
                ImageItem(item, openImageDetail, onSelectedImage, isChecked = isChecked, context, coroutines,
                shouldCheckBoxVisible = shouldCheckBoxVisible){
                    onImageLongClicked.invoke()
                }
            }
        }
    }
}


@Composable
private fun ImageItem(
    item: Hits,
    openImageDetail: (String) -> Unit,
    onSelectedImage: (Long, Bitmap, Boolean) -> Unit,
    isChecked: Boolean,
    context: Context,
    coroutines: CoroutineScope,
    shouldCheckBoxVisible: Boolean,
    onImageLongClicked: () -> Unit
) {
    var isZoomed by remember { mutableStateOf(false) }
    val imageUrl = item.imageURL ?: item.largeImageURL ?: item.fullHDURL ?: item.previewURL
    var scale by remember { mutableStateOf(1f) }

    val createBitmap : (Long, Boolean, ImageLoader, ImageRequest)->  Unit = {id, status, imageLoader, imageReguest ->
            coroutines.launch {
                val result = (imageLoader.execute(imageReguest) as SuccessResult).drawable
                 (result as BitmapDrawable).bitmap?.let { it1 ->
                     onSelectedImage.invoke(item.uuId, it1, status)
                 }
            }
    }

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val loader = ImageLoader(LocalContext.current)
        val imageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .crossfade(true)
            .build()

        val (checkBox) = createRefs()
        AsyncImage(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            coroutines.launch {
                                delay(100)
                                onImageLongClicked.invoke()
                            }
                        },
                        onTap = {
                            val url = item.fullHDURL ?: item.largeImageURL ?: item.imageURL ?: item.previewURL

                            url?.let {
                                openImageDetail.invoke(it)
                            }
                        },

                        onDoubleTap = {
                            if (isZoomed) {
                                scale = 1f
                                isZoomed = false
                                return@detectTapGestures
                            }
                            scale = 1.1f
                            isZoomed = true
                        }
                    )
                }
                .fillMaxWidth()
                .size(120.dp),
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

        if (shouldCheckBoxVisible){
            Checkbox(
                checked = isChecked,
                enabled = true,
                modifier = Modifier
                    .padding(6.dp)
                    .constrainAs(checkBox) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                onCheckedChange ={
                    /*
                    val file = File(context.filesDir.path.toString() )

                    try {
                        file.createNewFile()
                    }catch (e: Exception){
                        println(e.localizedMessage)
                    }
                     */
                    createBitmap(item.uuId, it, loader, imageRequest)

                },
                colors = CheckboxDefaults.colors(checkedColor = Color.Blue, uncheckedColor = Color.White)

                )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    searchedImageText: String,
    selectedImages: SnapshotStateList<Pair<Long, Bitmap>>,
    dropDownMenuExpanded : Boolean,
    onStateChanged : (Boolean) -> Unit
) {

    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "$searchedImageText images",
                textAlign = TextAlign.Start,
                color = Color.Red,
                fontSize = 26.sp,
                fontFamily = FontFamily.SansSerif
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(Color.LightGray),
        scrollBehavior = scrollBehavior,
        actions = {

            if (selectedImages.isEmpty().not()){
                Button(onClick = {
                    selectedImages.map {
                        it.second
                    }.let {
                        val first = it.first().asImageBitmap()
                        val second = it.getOrNull(1)?.asImageBitmap()
                        second?.let {
                            SaveImageToCacheAndShare().saveImageToCache(first, it, context =  context)
                        }
                    }



                }) {
                    Image(painter = painterResource(id = R.drawable.share_icon), contentDescription = "save All images")
                }
            }

            // options icon (vertical dots)
            IconButton(onClick = {
                // show the drop down menu
                onStateChanged.invoke(true)
            }) {
                Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "Options")
            }

            // drop down menu
            DropdownMenu(
                expanded = dropDownMenuExpanded,
                onDismissRequest = {
                    onStateChanged.invoke(false)
                },
                offset = DpOffset(x = 10.dp, y = (-60).dp)
            ) {
                DropdownMenuItem(
                    onClick = {
                        Toast.makeText(context, "Refresh Click", Toast.LENGTH_SHORT).show()
                        onStateChanged.invoke(false)
                    },
                    text = {Text("Refresh")}

                )
                DropdownMenuItem(
                    onClick = {
                        Toast.makeText(context, "Settings Click", Toast.LENGTH_SHORT).show()
                        onStateChanged.invoke(false)
                    },
                    text = {Text("Settings")}

                )
                DropdownMenuItem(
                    onClick = {
                        Toast.makeText(context, "Send Feedback Click", Toast.LENGTH_SHORT).show()
                        onStateChanged.invoke(false)
                    },
                    text = {Text("Send Feedback")}
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainBottomBar(
    scrollBehavior: TopAppBarScrollBehavior,
    searchedImageText: String,
    onSearchedTextChanged: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val singleLine = true
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, top = 6.dp, end = 6.dp),
        title = {

            OutlinedTextField(
                value = searchedImageText,
                onValueChange = onSearchedTextChanged,
                interactionSource = interactionSource,
                singleLine = singleLine,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20f),
                placeholder = ({ Text(text = "search image") })
            )

        },
        scrollBehavior = scrollBehavior
    )
}
