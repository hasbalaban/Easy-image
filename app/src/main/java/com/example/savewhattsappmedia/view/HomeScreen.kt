package com.example.savewhattsappmedia.view

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.savewhattsappmedia.Hits
import com.example.savewhattsappmedia.ImageResponse
import com.example.savewhattsappmedia.R
import com.example.savewhattsappmedia.TestViewModel
import com.example.savewhattsappmedia.ignoreNull
import com.example.savewhattsappmedia.isPermissionGranted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    openImageDetail: (String) -> Unit,
    coroutines : CoroutineScope,
    viewModel: TestViewModel= androidx.lifecycle.viewmodel.compose.viewModel()
)  {

    LaunchedEffect(Unit) {
        viewModel.getPhotos()
    }

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





    ObserveCounter(
        photos.value,
        shouldGetNewPage,
        state,
        searchedImageText = searchedImageText,
        coroutines = coroutines,
        onSearchedTextChanged =  {
            searchedImageText = it
            if (it.isEmpty()){
                searchedImageText = "sun"
            }
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
    photos: ImageResponse?,
    shouldGetNewPage: Boolean,
    state: LazyGridState,
    searchedImageText: String,
    coroutines: CoroutineScope,
    onSearchedTextChanged: (String) -> Unit,
    openImageDetail : (String) -> Unit,
    scrollToBottomOfPhotos : () -> Unit,
) {
    var shouldCheckBoxVisible by remember { mutableStateOf(false) }
    val selectedImages : SnapshotStateList<Long> = remember { mutableStateListOf() }
    var time = remember { System.currentTimeMillis()}


    SideEffect {
        if (shouldGetNewPage) {
            scrollToBottomOfPhotos.invoke()
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior ()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MainTopAppBar(scrollBehavior, searchedImageText, selectedImages)},
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
                {uuId, isChecked ->

                    if (isChecked){
                        selectedImages.add(uuId)
                        return@MainContent
                    }

                    selectedImages.remove(uuId)
                    return@MainContent
                },
                shouldCheckBoxVisible){

                if (System.currentTimeMillis() > time + 1600){
                    shouldCheckBoxVisible = shouldCheckBoxVisible.not()
                    time = System.currentTimeMillis()
                }
            }
        }
    }

}

@Composable
private fun FloatActionContent(coroutines: CoroutineScope, state: LazyGridState, lastIndex: Int) {

    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(context.isPermissionGranted()) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()) { permissionGranted_ ->


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) Toast.makeText(context, "onActivityResult: Manage External Storage Permissions Granted", Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "Storage Permissions Denied", Toast.LENGTH_SHORT).show()
            } else {

            }

            Toast.makeText(context, "permissionGranted_ $permissionGranted_", Toast.LENGTH_SHORT).show()

            permissionGranted = permissionGranted_[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                permissionGranted = permissionGranted_[Manifest.permission.MANAGE_EXTERNAL_STORAGE] == true
            }

        }


    Column() {
        Image(
            modifier = Modifier
                .background(Color.White)
                .padding(4.dp)
                .clickable {
                    coroutines.launch {
                        var targetPosition =
                            state.firstVisibleItemIndex - (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index.ignoreNull() - state.layoutInfo.visibleItemsInfo.firstOrNull()?.index.ignoreNull()) - 2
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

                    if (permissionGranted.not()) {
                        return@clickable
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                                )
                            )
                            return@clickable
                        }
                        permissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {

                        }

                    }

                    coroutines.launch {

                        var targetPosition =
                            state.layoutInfo.visibleItemsInfo.lastOrNull()?.index.ignoreNull() + (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index.ignoreNull() + state.layoutInfo.visibleItemsInfo.firstOrNull()?.index.ignoreNull()) - 2
                        targetPosition =
                            if (targetPosition > lastIndex) lastIndex else targetPosition
                        state.animateScrollToItem(targetPosition)

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
    selectedImagesList: MutableList<Long>,
    onSelectedImage : (Long, Boolean) -> Unit,
    shouldCheckBoxVisible : Boolean,
    onImageLongClicked : () -> Unit
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



            items(it,
                key = {
                    it.uuId
                }

            ) { item ->

                val isChecked = selectedImagesList.contains(item.uuId)

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
    onSelectedImage: (Long, Boolean) -> Unit,
    isChecked: Boolean,
    context: Context,
    coroutines: CoroutineScope,
    shouldCheckBoxVisible: Boolean,
    onImageLongClicked: () -> Unit
) {
    val imageUrl = item.fullHDURL ?: item.largeImageURL ?: item.imageURL ?: item.previewURL

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {


        val (checkBox) = createRefs()

        AsyncImage(
            modifier = Modifier
                .clickable {

                    if (shouldCheckBoxVisible) {
                      //  Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
                      //  return@clickable
                    }

                    imageUrl?.let {
                        openImageDetail.invoke(it)
                        return@clickable
                    }
                }
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress { change, dragAmount ->
                        coroutines.launch {
                            delay(100)
                            onImageLongClicked.invoke()
                        }
                    }
                }
                .fillMaxWidth()
                .size(120.dp),
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
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
                    val file = File(context.filesDir.path.toString() )

                    try {
                        file.createNewFile()
                    }catch (e: Exception){
                        println(e.localizedMessage)
                    }

                    onSelectedImage.invoke(item.uuId, it)
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
    selectedImages: MutableList<Long>?
) {

    var dropDownMenuExpanded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = searchedImageText,
                textAlign = TextAlign.Center,
                color = Color.Red,
                fontSize = 26.sp,
                fontFamily = FontFamily.SansSerif
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(Color.LightGray),
        scrollBehavior = scrollBehavior,
        actions = {

            if (selectedImages.isNullOrEmpty().not()){
                Button(onClick = {

                }) {
                    Image(painter = painterResource(id = R.drawable.download), contentDescription = "save All images")
                }
            }

            // options icon (vertical dots)
            IconButton(onClick = {
                // show the drop down menu
                dropDownMenuExpanded = true
            }) {
                Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "Options")
            }

            // drop down menu
            DropdownMenu(
                expanded = dropDownMenuExpanded,
                onDismissRequest = {
                    dropDownMenuExpanded = false
                },
                // play around with these values
                // to position the menu properly
                offset = DpOffset(x = 10.dp, y = (-60).dp)
            ) {
                DropdownMenuItem(
                    onClick = {
                        Toast.makeText(context, "Refresh Click", Toast.LENGTH_SHORT).show()
                        dropDownMenuExpanded = false
                    },
                    text = {Text("Refresh")}

                )
                DropdownMenuItem(
                    onClick = {
                        Toast.makeText(context, "Settings Click", Toast.LENGTH_SHORT).show()
                        dropDownMenuExpanded = false
                    },
                    text = {Text("Settings")}

                )
                DropdownMenuItem(
                    onClick = {
                        Toast.makeText(context, "Send Feedback Click", Toast.LENGTH_SHORT).show()
                        dropDownMenuExpanded = false
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
    val enabled = true
    val singleLine = true
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, top = 6.dp, end = 6.dp),
        title = {

            BasicTextField(
                value = searchedImageText,
                onValueChange = onSearchedTextChanged,
                interactionSource = interactionSource,
                enabled = enabled,
                singleLine = singleLine,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextFieldDefaults.OutlinedTextFieldDecorationBox(
                    value = searchedImageText,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = it,
                    singleLine = singleLine,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    // keep vertical paddings but change the horizontal
                    contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                        start = 8.dp, end = 8.dp
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors()
                )
            }


        },
        scrollBehavior = scrollBehavior
    )
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Checks if a volume containing external storage is available to at least read.
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }





}
