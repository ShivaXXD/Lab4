package com.example.lab3

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val progress: Float = 0f
)