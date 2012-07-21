package com.workoutjournal.service;

import java.util.List;

import com.workoutjournal.model.Exercise;
import com.workoutjournal.model.ExerciseList;

public interface ExerciseService {

	public List<Exercise> getExercisesByWorkout(int workoutId);
	public Exercise getExercise(String userId, int exerciseId);
	public ExerciseList getExercises(String userId, String name, Parameters param);
	public boolean updateExercise(String userId, Exercise ex);
	public boolean deleteExercise(String userId, int exerciseId);
}
