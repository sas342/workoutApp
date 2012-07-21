package com.workoutjournal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.workoutjournal.model.Exercise;
import com.workoutjournal.model.ExerciseList;
import com.workoutjournal.model.Exercises;

public class ExerciseJDBCDao {

	public Exercise getExercise(String userId, int exerciseId) {
		String sql = "select * from exercise e join workout w on e.workout_id=w.workout_id "+
			"join users u on u.user_id=w.user_id where u.user_id=? and e.exercise_id=?";
		
		return template.queryForObject(sql, new ExerciseRowWrapper(), userId, exerciseId);
		
	}
	
	public ExerciseList getExercises(String userId, String name, int start, int size, boolean asc) {
		String sql = "select * from exercise e join workout w on e.workout_id=w.workout_id "+
		"join users u on u.user_id=w.user_id join exercises es on e.ex_name=es.id where u.user_id=? and es.name like ? order by es.name " + getOrderBy(asc);
	
		List<Exercise> list =  template.query(sql, new ExerciseRowWrapper(), userId, "%"+name+"%");
		ExerciseList elist = new ExerciseList();
		elist.setTotalCount(list.size());
		elist.setStart(start);
		elist.setExercises(generateSubList(list, start, size));
		elist.setSize(elist.getExercises().size());
		return elist;
	}
	
	
	public List<Exercise> getWorkoutExercises(int workoutId) {
		String sql = "select * from exercise e join workout w on e.workout_id=w.workout_id and "+
			"w.workout_id=?";
		
		return template.query(sql, new ExerciseRowWrapper(), workoutId);
	}
	
	public Exercise updateExercise(String userId, Exercise ex) {
		String sql = null;
		int affected = 0;
		if (ex.getExerciseId() != null) {
			sql = "update exercise set ex_name = ?";
			affected = template.update(sql, ex.getExercises().getId());
		}
		else {
			sql = "insert into exercise (workout_id, ex_num, ex_name) values (?,?,?)";
			affected = template.update(sql, ex.getWorkoutId(), ex.getExNum(), ex.getExercises().getId());
		}
		
		if (affected > 0) {
			if (ex.getExerciseId() == null) {
				return getLastInsertedExercise(userId);
			}
			else {
				return getExercise(userId, ex.getExerciseId());
			}
		}
		
		return ex;
	}
	
	private Exercise getLastInsertedExercise(String userId) {
		String sql = "select * from exercise e join workout w on e.workout_id=w.workout_id "+
		"join users u on u.user_id=w.user_id where w.user_id = ? order by exercise_id desc limit 1";
		
		return template.queryForObject(sql, new ExerciseRowWrapper(), userId);
	}
	
	public boolean deleteExercise(int exerciseId) {
		String sql = "delete from exercise where exercise_id = ?";
		int affected = template.update(sql, exerciseId);
		
		return affected > 0;
	}
	
	private String getOrderBy(boolean asc) {
		return (asc) ? "asc" : "desc";
	}
	
	@SuppressWarnings(value = { "unchecked" })
	private List<Exercise> generateSubList(List<Exercise> list, int start, int size) {
		int listSize = list.size();
		if (start > listSize) {
			return Collections.EMPTY_LIST;
		}
		else if ((start + size) > listSize) {
			return list.subList(start, listSize);
		}
		return list.subList(start, start+size);
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		template = new SimpleJdbcTemplate(dataSource);
	}
	
	public class ExerciseRowWrapper implements RowMapper<Exercise> {

		@Override
		public Exercise mapRow(ResultSet rs, int arg1) throws SQLException {
			Exercise ex = new Exercise();
			ex.setExerciseId(rs.getInt("exercise_id"));
			ex.setExNum(rs.getInt("ex_num"));
			ex.setWorkoutId(rs.getInt("workout_id"));
			Exercises name = new Exercises();
			name.setId(rs.getInt("ex_name"));
			ex.setExercises(name);
			return ex;
		}
		
	}
	
	SimpleJdbcTemplate template;
}
