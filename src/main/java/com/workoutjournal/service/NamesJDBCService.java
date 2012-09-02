package com.workoutjournal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workoutjournal.dao.ExercisesJDBCDao;
import com.workoutjournal.model.Exercises;

@Transactional(readOnly=true)
public class NamesJDBCService implements NamesService{
	
	public Exercises getName(int id) {
		return dao.getExercises(id);
	}
		
	public Exercises getByName(String name) {
		return dao.getExercisesByName(name);
	}
		
	public List<Exercises> getNames(String name) {
		return dao.getExercises(name);
	}
		
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Exercises addExerciseName(String name) {
		return dao.addExerciseName(name);
	}
	
	@Autowired
	public void setDao(ExercisesJDBCDao dao) {
		this.dao = dao;
	}
	
	ExercisesJDBCDao dao;
}
