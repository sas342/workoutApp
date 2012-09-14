package com.workoutjournal.service;

import java.util.List;

import com.workoutjournal.model.ExSet;

public interface SetsService {

	public ExSet getSet(int setId);
	public List<ExSet> getSetsByExercise(int exId);
	public boolean updateSet(ExSet set);
}
