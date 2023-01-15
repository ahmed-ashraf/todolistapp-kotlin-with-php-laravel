<?php

use Illuminate\Http\Request;

use App\Http\Controllers\TasksApiController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::get('/tasks', [TasksApiController::class, 'index']);

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});


/**
 * Delete task.
 */
Route::delete('/tasks/{task}', [TasksApiController::class, 'destroy']);

/**
 * Add task.
 */
Route::post('/tasks', [TasksApiController::class, 'store']);

/**
 * Complete task status.
 */
Route::patch('/tasks/toggle/{task}', [TasksApiController::class, 'complete']);

/**
 * Update task status.
 */
Route::patch('/tasks/{task}', [TasksApiController::class, 'update']);

/**
 * Clear all tasks.
 */
Route::delete('/clearCompleted', [TasksApiController::class, 'clearCompleted']);