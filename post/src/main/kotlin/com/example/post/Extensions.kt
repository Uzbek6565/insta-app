package com.example.post

fun Boolean.runIfTrue(func: () -> Unit) {
    if (this) func()
}

fun Boolean.runIfFalse(func: () -> Unit) {
    if (!this) func()
}