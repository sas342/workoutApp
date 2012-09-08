package com.workoutjournal.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workoutjournal.dao.WorkoutJDBCDao;
import com.workoutjournal.model.Exercise;
import com.workoutjournal.model.Paginator;
import com.workoutjournal.model.Workout;
import com.workoutjournal.model.WorkoutList;

@Transactional(readOnly=true)
public class WorkoutJDBCService implements WorkoutService{
	
	public Workout getWorkout(String userId, int workoutId) {
		Workout workout = workoutDao.getWorkout(userId, workoutId);
		return updateWorkoutDetails(workout);
	}
	
	public WorkoutList getWorkouts(String userId, Parameters parameters) {
		Paginator paginator = parameters.getPaginator();
		parameters.setOrderColumn(getColumn(parameters.getOrderColumn()));
		WorkoutList wlist = workoutDao.getWorkouts(userId, paginator.getStart(), paginator.getSize(), parameters.getOrderColumn(), parameters.isAsc());
		List<Workout> list = wlist.getWorkouts();
		for (Workout w : list) {
			updateWorkoutDetails(w);
		}
		return wlist;
	}
		
	public WorkoutList getWorkouts(String userId, String name, Parameters parameters) {
		Paginator paginator = parameters.getPaginator();
		parameters.setOrderColumn(getColumn(parameters.getOrderColumn()));
		WorkoutList wlist = workoutDao.getWorkouts(userId, name, paginator.getStart(), paginator.getSize(), parameters.getOrderColumn(), parameters.isAsc());		
		for (Workout w : wlist.getWorkouts()) {
			updateWorkoutDetails(w);
		}
		return wlist;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)	
	public boolean updateWorkout(String userId, Workout workout) {
		Workout updatedWorkout = workoutDao.updateWorkouts(userId, workout);
		boolean updated = updatedWorkout.getWorkoutId() != null;
		System.out.println("workout updated "+updated);
		logger.info("workout updated "+updated);
		//need newly inserted workout id for each exercise
		for (Exercise ex : workout.getExerciseList()) {
			ex.setWorkoutId(updatedWorkout.getWorkoutId());
			updated &= exService.updateExercise(userId, ex);
		}
		logger.info("all exercises updated: "+updated);
		return updated;
		
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)	
	public boolean deleteWorkout(String userId, int workoutId) {
		return workoutDao.deleteWorkout(workoutId);
	}
	
	@Transactional()	
	public List<Workout> getSimilarWorkouts(String userId, int workoutId) {
		List<Workout> list = workoutDao.getSimilarWorkouts(userId, workoutId);
		for (Workout w : list) {
			updateWorkoutDetails(w);
		}
		return list;
	}
	
	private Workout updateWorkoutDetails(Workout w) {
		w.setExerciseList(exService.getExercisesByWorkout(w.getWorkoutId()));
		return w;
	}
	
	@Autowired
	public void setWorkoutDao(WorkoutJDBCDao workoutDao) {
		this.workoutDao = workoutDao;
	}

	@Autowired
	public void setExService(ExerciseService exService) {
		this.exService = exService;
	}


	WorkoutJDBCDao workoutDao;
	ExerciseService exService;
	

	private String getColumn(String name) {
		if (name != null) {
			name = name.toLowerCase();
			if (name.equals("name") || name.equals("date") || name.equals("time")) {
				return name;
			}
		}
		return "name";
	}
	
	final Logger logger = LoggerFactory.getLogger(WorkoutService.class);
}
