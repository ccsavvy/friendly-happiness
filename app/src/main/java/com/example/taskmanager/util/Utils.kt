package com.example.taskmanager.util

val <T> T.exhaustive: T
    get() = this        //Extension property that can use on any type, turns a statement into an expression