package com.example.easy_image.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen () {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchText,
            onValueChange = {
                searchText = it
            },
            label = {
                Text(text = "search image")
            },
            placeholder = {
                Text(text = "search image")
            }
        )

    }
}

@Preview
@Composable
private fun PreviewSearchScreen(){
    SearchScreen()
}