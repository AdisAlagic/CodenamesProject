package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.colors.TextColor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WindowControls(onCloseClick: () -> Unit, onCollapseClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(30.dp).background(Color(0xFF121212))) {
        Box(contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically
            ) {
                var collapseBackground by remember {
                    mutableStateOf(Color.Transparent)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(45.dp)
                        .fillMaxHeight()
                        .background(collapseBackground)
                        .clickable {
                            onCollapseClick()
                        }
                        .pointerMoveFilter(
                            onEnter = {
                                collapseBackground = Color(0xFF3f51b5)
                                return@pointerMoveFilter false
                            },
                            onExit = {
                                collapseBackground = Color.Transparent
                                return@pointerMoveFilter false
                            }
                        )
                ) {
                    Text("—", color = Color(0xFFe3e3e3))
                }

                var xBackground by remember {
                    mutableStateOf(Color.Transparent)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(45.dp)
                        .fillMaxHeight()
                        .background(xBackground)
                        .clickable {
                            onCloseClick()
                        }
                        .pointerMoveFilter(
                            onEnter = {
                                xBackground = Color.Red
                                return@pointerMoveFilter false
                            },
                            onExit = {
                                xBackground = Color.Transparent
                                return@pointerMoveFilter false
                            }
                        )
                ) {
                    Text("×", color = Color(0xFFe3e3e3), fontSize = 30.sp)
                }

            }
        }
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()){
            RText(text = "Codenames!", fontColor = TextColor)
        }
    }
}