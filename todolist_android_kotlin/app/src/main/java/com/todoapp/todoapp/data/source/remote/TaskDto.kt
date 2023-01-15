package com.todoapp.todoapp.data.source.remote

data class TaskDto(
    val id: Int,
    val name: String,
    val description: String,
    val done: Boolean
)

data class SuccessDto(
    val success: Boolean
)

data class TaskDtoRequest(
    val name: String,
    val description: String,
)


