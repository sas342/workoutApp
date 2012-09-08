package com.workoutjournal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workoutjournal.dao.ExerciseJDBCDao;
import com.workoutjournal.model.ExSet;
import com.workoutjournal.model.Exercise;
import com.workoutjournal.model.ExerciseList;
import com.workoutjournal.model.Exercises;
import com.workoutjournal.model.Paginator;

@Transactional(readOnly=true)
public class ExerciseJDBCService implements ExerciseService{
	
	public List<Exercise> getExercisesByWorkout(int workoutId) {
		List<Exercise> exList = exDao.getWorkoutExercises(workoutId);
		for (Exercise ex : exList) {
			updateExercise(ex);
		}
		
		return exList;
	}
		
	public Exercise getExercise(String userId, int exerciseId) {
		Exercise ex = exDao.getExercise(userId, exerciseId);
		updateExercise(ex);
		return ex;
	}
		
	public ExerciseList getExercises(String userId, String name, Parameters param) {
		Paginator paginator = param.getPaginator();
		ExerciseList list = exDao.getExercises(userId, name, paginator.getStart(), paginator.getSize(), param.isAsc());
		for (Exercise ex : list.getExercises()) {
			updateExercise(ex);
		}
		return list;
	}
		
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean updateExercise(String userId, Exercise ex){
		if (ex.getExercises() != null && ex.getExercises().getId() == null) {
			Exercises name = nameService.addExerciseName(ex.getExercises().getName());
			ex.setExercises(name);
		}		
		Exercise updatedEx = exDao.updateExercise(userId, ex);
		boolean updated = updatedEx.getExerciseId() != null;
		
		for (ExSet set : ex.getSetsList()) {
			set.setExId(updatedEx.getExerciseId());
			updated &= setsService.updateSet(set);
		}
		return updated;
	}
		
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean deleteExercise(String userId, int exerciseId) {
		return exDao.deleteExercise(exerciseId);
	}
	
	private void updateExercise(Exercise ex) {
		ex.setExercises(nameService.getName(ex.getExercises().getId()));
		ex.setSetsList(setsService.getSetsByExercise(ex.getExerciseId()));
	}
	
	@Autowired
	public void setExDao(ExerciseJDBCDao exDao) {
		this.exDao = exDao;
	}
	
	@Autowired
	public void setSetsService(SetsService setsService) {
		this.setsService = setsService;
	}
	
	@Autowired
	public void setNameService(NamesService nameService) {
		this.nameService = nameService;
	}


	ExerciseJDBCDao exDao;
	
	SetsService setsService;
	NamesService nameService;
	
}
