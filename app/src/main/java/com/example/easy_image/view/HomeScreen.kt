package com.example.easy_image.view

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.size.Size
import com.example.easy_image.R
import com.example.easy_image.utils.SaveImageToCacheAndShare
import com.example.easy_image.viewmodel.HomeViewModel
import com.example.easy_image.model.FavoriteDTO
import com.example.easy_image.model.ImageDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    openImageDetail: (String) -> Unit,
    coroutines: CoroutineScope,
    favoriteImages: SnapshotStateList<FavoriteDTO>,
    addOrRemoveFromFavoriteList: (FavoriteDTO) -> Boolean,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    val photos = viewModel.photos.observeAsState()
    val state = rememberLazyGridState()
    val shouldGetNewPage by remember { derivedStateOf { (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= ((photos.value?.size ?: 1) * 0.8) } }
    var searchedImageText by remember { mutableStateOf("planet") }
    var gridCellCount by remember { mutableStateOf(1) }

    var shouldCheckBoxVisible by remember { mutableStateOf(false) }

    val selectedImagesToShare: SnapshotStateList<Pair<Int, Bitmap>> = remember { mutableStateListOf() }

    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { MutableInteractionSource() }

    if (state.interactionSource.collectIsDraggedAsState().value) {
        LaunchedEffect(Unit) {
            keyboardController?.hide()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getPhotos()
    }

    SideEffect {
        if (shouldGetNewPage) {
            viewModel.getPhotos(searchedImageText)
        }
    }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(
            modifier = Modifier.padding(top = 6.dp),
            title = {

            OutlinedTextField(
                value = searchedImageText,
                onValueChange = { newText: String ->
                    coroutines.launch {
                        delay(300L)
                        viewModel.getPhotos(searchedImageText, shouldClearPhotos = true)
                    }
                    searchedImageText = newText
                },
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20f),
                placeholder = ({ Text(text = "search image") })
            )
        },
            colors = TopAppBarDefaults.smallTopAppBarColors(),
            scrollBehavior = scrollBehavior,
            actions = {
                DropDownMenu(
                    dropDownMenuExpanded = dropDownMenuExpanded,
                    gridCellCountChanged = {
                        gridCellCount = it
                    },
                    onStateChanged = {
                        dropDownMenuExpanded = false
                    },
                )

                if (selectedImagesToShare.isEmpty().not()) {
                    Button(
                        modifier = Modifier.padding(start = 12.dp),
                        onClick = {

                        selectedImagesToShare.map { it.second }.let {
                            val first = it.first().asImageBitmap()
                            val second = it.getOrNull(1)?.asImageBitmap()
                            second?.let {
                                SaveImageToCacheAndShare().saveImageToCache(first, it, context = context)
                            }
                        }

                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.share_icon),
                            contentDescription = "save All images"
                        )
                    }
                }

                IconButton(onClick = { dropDownMenuExpanded = true }) {
                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "Options")
                }
            })
    }) {paddingValues ->
        photos.value?.let {
        Column(modifier = Modifier.padding(paddingValues)) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridCellCount),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                    userScrollEnabled = true, state = state
                ) {

                    items(
                        items = it,
                        key = {
                            it.id
                        }
                    ) { item ->

                        val isChecked = selectedImagesToShare.any { it.first == item.id }
                        val imageUrl by remember { mutableStateOf(item.imageURL ?: item.largeImageURL ?: item.fullHDURL ?: item.previewURL) }



                        ImageItem(
                            gridCellCount, item, openImageDetail,
                            {
                                shouldCheckBoxVisible = shouldCheckBoxVisible.not()
                                if (shouldCheckBoxVisible) selectedImagesToShare.clear()
                            },
                            isChecked = isChecked, coroutines = coroutines,
                            shouldCheckBoxVisible = shouldCheckBoxVisible,
                            imageUrl = imageUrl ?: "",
                            favoriteImages = favoriteImages,
                            onClickFavoriteButton = { favorite ->
                                addOrRemoveFromFavoriteList.invoke(favorite)
                            }
                        ) { id, bitmap, checked ->

                            if (checked) selectedImagesToShare.add(Pair(first = id, second = bitmap))
                            else selectedImagesToShare.firstOrNull { it.first == id }?.let { selectedImagesToShare.remove(it) }
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun DropDownMenu(
    dropDownMenuExpanded: Boolean,
    gridCellCountChanged: (Int) -> Unit,
    onStateChanged: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = dropDownMenuExpanded, onDismissRequest = {
            onStateChanged.invoke(false)
        }, offset = DpOffset(x = 10.dp, y = (-60).dp)
    ) {
        ClickDropDownMenuItem(1){
            gridCellCountChanged.invoke(1)
            onStateChanged.invoke(false)
        }
        ClickDropDownMenuItem(2){
            gridCellCountChanged.invoke(2)
            onStateChanged.invoke(false)
        }
        ClickDropDownMenuItem(3){
            gridCellCountChanged.invoke(3)
            onStateChanged.invoke(false)
        }
        ClickDropDownMenuItem(5){
            gridCellCountChanged.invoke(5)
            onStateChanged.invoke(false)
        }
    }

}

@Composable
private fun ClickDropDownMenuItem(cellCount: Int, onClickMenuItem: () -> Unit) {
    val context = LocalContext.current
    DropdownMenuItem(onClick = {
        Toast.makeText(context, "gridCellCount set $cellCount", Toast.LENGTH_SHORT).show()
        onClickMenuItem.invoke()
    }, text = { Text("gridCellCount set $cellCount") })

}

@Composable
private fun ImageItem(
    gridCellCount: Int,
    item: ImageDTO,
    openImageDetail: (String) -> Unit,
    onImageLongClicked: () -> Unit,
    isChecked: Boolean,
    coroutines: CoroutineScope,
    shouldCheckBoxVisible: Boolean,
    imageUrl: String,
    favoriteImages: SnapshotStateList<FavoriteDTO>,
    onClickFavoriteButton: (FavoriteDTO) -> Boolean,
    onSelectedImage: (Int, Bitmap, Boolean) -> Unit,
) {
    var isZoomed by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }
    
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    var isFavorite by remember { mutableStateOf(favoriteImages.any {
        it.id == item.id
    }) }

    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)) {
        val (checkBox) = createRefs()
        val painter1 = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .listener { _, result ->
                    bitmap = (result.drawable  as BitmapDrawable).bitmap
                }
                .size(Size.ORIGINAL)
                .build()
        )

        val context = LocalContext.current


        val eventListener = object : EventListener{
            override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                super.onSuccess(request, result)
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .listener { _, _ ->
                        bitmap = (result.drawable  as BitmapDrawable).bitmap
                    }
                    .size(Size.ORIGINAL)
                    .build()
            }
        }

        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.05)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.04)
                    .build()
            }
            .eventListener(listener = eventListener)
            .build()
        val deliveredPainter by remember { derivedStateOf {painter1} }

        Image(
            painter = deliveredPainter,
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale, scaleY = scale
                )
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = {
                        coroutines.launch {
                            delay(10)
                            onImageLongClicked.invoke()
                        }
                    }, onTap = {
                        val url = item.fullHDURL ?: item.largeImageURL ?: item.imageURL
                        ?: item.previewURL
                        url?.let { openImageDetail.invoke(it) }
                    },
                        onDoubleTap = {
                            if (isZoomed) {
                                scale = 1f
                                isZoomed = false
                                return@detectTapGestures
                            }
                            scale = ((1f * (1.0 + (gridCellCount * 0.1))).toFloat())
                            isZoomed = true
                        })
                }
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.FillBounds
        )

        Image(modifier = Modifier
            .clickable {
                isFavorite = onClickFavoriteButton.invoke(FavoriteDTO(item.id, imageUrl))
            }
            .padding(6.dp),
            painter = painterResource(id = R.drawable.ic_favorite),
            contentDescription = "select favorite",
            colorFilter = ColorFilter.tint(
                if (isFavorite) Color.Yellow
                else Color.White
            )
        )


        if (shouldCheckBoxVisible) {
            Checkbox(
                checked = isChecked, enabled = true,
                modifier = Modifier
                    .padding(6.dp)
                    .constrainAs(checkBox) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                onCheckedChange = {
                    coroutines.launch {
                        bitmap?.let { it1 -> onSelectedImage.invoke(item.id, it1, it) }
                    }
                },
                colors = CheckboxDefaults.colors(checkedColor = Color.Blue, uncheckedColor = Color.White)
            )
        }
    }
}