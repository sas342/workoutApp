package com.workoutjournal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workoutjournal.dao.SetsJDBCDao;
import com.workoutjournal.model.ExSet;

@Transactional(readOnly=true)
public class SetsJDBCService implements SetsService{

	public List<ExSet> getSetsByExercise(int exId) {
		return setDao.getSets(exId);
	}
		
	public ExSet getSet(int setId) {
		return setDao.getSet(setId);
	}
		
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean updateSet(ExSet set) {
		return setDao.updateSet(set);
	}
	
	@Autowired
	public void setSetDao(SetsJDBCDao setDao) {
		this.setDao = setDao;
	}

	SetsJDBCDao setDao;
}
