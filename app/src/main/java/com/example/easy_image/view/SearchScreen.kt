package com.example.easy_image.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen (searchMediaText : (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val keyboardActions by remember {
        mutableStateOf(KeyboardActions(
            onGo = {

                keyboardController?.hide()
                searchMediaText(searchText)
            }
        ))
    }



    Column(modifier = Modifier.fillMaxSize()) {

        TextField(
            modifier = Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
            ,
            value = searchText,
            onValueChange = {
                searchText = it
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = {
                Text(text = "search image")
            },
            placeholder = {
                Text(text = "search image")
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),

            keyboardActions = keyboardActions
        )

    }
}

@Preview
@Composable
private fun PreviewSearchScreen(){
    SearchScreen(){

    }
}