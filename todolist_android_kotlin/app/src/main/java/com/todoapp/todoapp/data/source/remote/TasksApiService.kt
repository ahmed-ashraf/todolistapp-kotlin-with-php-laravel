package com.todoapp.todoapp.data.source.remote

import com.todoapp.todoapp.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


private const val BASE_URL = com.todoapp.todoapp.Constants.BASE_URL

//private val moshi = Moshi.Builder()
//    .add(KotlinJsonAdapterFactory())
//    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    //.addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TasksApiService {
    @GET("tasks")
    suspend fun getTasks(
    ): List<TaskDto>


    @PATCH("tasks/toggle/{id}")
    suspend fun toggleTask(
        @Path("id") id: Int
    ): SuccessDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): SuccessDto

    @DELETE("clearCompleted")
    suspend fun deleteAll(): SuccessDto

    @POST("tasks")
    suspend fun addTask(@Body task: TaskDtoRequest): TaskDto

    @PATCH("tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: TaskDtoRequest): TaskDto

}

object TasksApi {
    val retrofitService: TasksApiService by lazy { retrofit.create(TasksApiService::class.java) }
}
