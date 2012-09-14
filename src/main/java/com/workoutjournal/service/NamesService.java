package com.workoutjournal.service;

import java.util.List;

import com.workoutjournal.model.Exercises;

public interface NamesService {

	public Exercises getName(int id);
	public Exercises getByName(String name);
	public Exercises addExerciseName(String name);
	public List<Exercises> getNames(String name);
}
