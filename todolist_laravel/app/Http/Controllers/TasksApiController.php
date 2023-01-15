<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Task;

class TasksApiController extends Controller
{
	
    public function index()
    {
    	$data = Task::all();
		return response()->json($data);
    }
	
    public function destroy(Task $task)
    {
    	$task->delete();

    	return response()->json(['success' => true]);
    }
	
	public function complete(Request $request, Task $task)
    {
        $task->done = !$task->done;
        $task->save();

        return response()->json(['success' => true]);
    }

    public function store(Request $request)
    {
        return Task::create($request->all());
    }
	
	public function update(Request $request, Task $task)
    {
        if ($request->name == null || $request->description == null) {
            return response()->json(['success' => false]);
        }

        $task->name = $request->name;
        $task->save();

        return response()->json(['success' => true]);
    }

    public function clearCompleted(Request $request) {
        Task::where('done', '=', 1)->delete();
        return response()->json(['success' => true]);
    }

}
