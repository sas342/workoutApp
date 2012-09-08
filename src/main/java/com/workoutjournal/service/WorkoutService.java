package com.workoutjournal.service;

import java.util.List;

import com.workoutjournal.model.Workout;
import com.workoutjournal.model.WorkoutList;

public interface WorkoutService {

	public WorkoutList getWorkouts(String userId, Parameters parameters);
	public WorkoutList getWorkouts(String userId, String name, Parameters parameters);
	public Workout getWorkout(String userId, int workoutId);
	public boolean updateWorkout(String userId, Workout workout);
	public boolean deleteWorkout(String userId, int workoutId);
	public List<Workout> getSimilarWorkouts(String userId, int workoutId);
}
