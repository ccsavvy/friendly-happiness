package com.example.taskmanager.model

import java.util.Date

class Task {
    private lateinit var name :String
    private lateinit var description :String

    private val complete: Boolean = false

    private lateinit var dateCreated : Date
    private lateinit var dateCompleted : Date

}