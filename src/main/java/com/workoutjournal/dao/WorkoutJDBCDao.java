package com.workoutjournal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.workoutjournal.model.Workout;
import com.workoutjournal.model.WorkoutList;

public class WorkoutJDBCDao {

	public Workout getWorkout(String userId, int workoutId) {
		return template.queryForObject("select * from workout where user_id=? and workout_id=?", new WorkoutRowWrapper(), userId, workoutId);
	}
	
	public WorkoutList getWorkouts(String userId, String name, int start, int size, String orderBy, boolean asc) {
		String sql = "select a.* from workout a, exercise b, exercises c where a.user_id=? and b.workout_id=a.workout_id and b.ex_name=c.id and (a.name like ? or c.name like ?) order by "+getOrderBy(orderBy, asc);
		List<Workout> list =  template.query(sql, new WorkoutRowWrapper(), userId, "%"+name+"%", "%"+name+"%");
		WorkoutList wlist = new WorkoutList();
		wlist.setWorkouts(generateSubList(list, start, size));
		wlist.setSize(wlist.getWorkouts().size());
		wlist.setStart(start);
		wlist.setTotalCount(list.size());
		return wlist;
	}
	
	public WorkoutList getWorkouts(String userId, int start, int size, String orderBy, boolean asc) {
		String sql = "select * from workout where user_id=? order by "  +getOrderBy(orderBy, asc);
		List<Workout> list = template.query(sql, new WorkoutRowWrapper(), userId);
		WorkoutList wlist = new WorkoutList();
		wlist.setWorkouts(generateSubList(list, start, size));
		wlist.setSize(wlist.getWorkouts().size());
		wlist.setStart(start);
		wlist.setTotalCount(list.size());
		return wlist;
	}
	
	@SuppressWarnings(value = { "unchecked" })
	private List<Workout> generateSubList(List<Workout> list, int start, int size) {
		int listSize = list.size();
		if (start > listSize) {
			return Collections.EMPTY_LIST;
		}
		else if ((start + size) > listSize) {
			return list.subList(start, listSize);
		}
		return list.subList(start, start+size);
	}
	
	public Workout updateWorkouts(String userId, Workout workout) {
		String sql = null;
		int affected = 0;
		if (workout.getWorkoutId() != null) {
			sql = "update workout set name=?, date=?, time=?, notes=? where workout_id=? and user_id=?";
			affected = template.update(sql, workout.getName(), workout.getDate(), workout.getTime(), workout.getNotes(), workout.getWorkoutId(), userId);
		}
		else {
			sql = "insert into workout (name, date, time, notes, user_id) values (?,?,?,?,?) ";
			affected = template.update(sql, workout.getName(), workout.getDate(), workout.getTime(), workout.getNotes(), userId);
		}
		
		logger.debug(sql);
		logger.debug("returned value of "+affected);
		
		//return updated workout
		if (affected > 0) {
			if (workout.getWorkoutId() == null) {
				return getLastInsertedWorkout(userId);
			}
			else {
				return getWorkout(userId, workout.getWorkoutId());
			}
		}
		
		return workout;
		
	}
	
	private Workout getLastInsertedWorkout(String userId) {
		String sql = "select * from workout where user_id = ? order by workout_id desc limit 1";
		Workout w = template.queryForObject(sql,  new WorkoutRowWrapper(), userId);
		return w;
	}
	
	public boolean deleteWorkout(int workoutId) {
		String sql = "delete from workout where workout_id=?";
		int affected = template.update(sql, workoutId);
		return affected > 0;
	}
	
	private String getOrderBy(String orderBy, boolean asc) {
		return orderBy + ((asc) ? " asc" : " desc");
	}
	
	public int getNumberOfExercise(int workoutId) {		
		String sql = "Select count(*) from exercise e where e.workout_id=?";
		int count = template.queryForInt(sql,  workoutId);
		return count;
		
	}
	
	public List<Workout> getSimilarWorkouts(String userId, int workoutId) {
		List<Workout> workouts = new java.util.ArrayList<Workout>();
		
		int size = getNumberOfExercise(workoutId);
		String sql = 
			"select distinct(a.workout_id) as wId from (select * from exercise where workout_id in "+
			"	(select workout_id from exercise group by workout_id having count(workout_id)="+size+")) a "+
			"	right join "+
			"	(select * from exercise where workout_id="+workoutId+") b "+
			"	on a.ex_name=b.ex_name "+
			"group by a.workout_id having count(a.workout_id)="+size;
		
		List<Map<String,Object>> list = template.queryForList(sql);
		for (Map<String,Object> map : list) {
			Integer wId = (Integer) map.get("wId");
			workouts.add(getWorkout(userId, wId));
		}
		return workouts;
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		template = new SimpleJdbcTemplate(dataSource);
	}
	
	
	public class WorkoutRowWrapper implements RowMapper<Workout> {

		public Workout mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (rs != null) {
				Workout workout = new Workout();
				workout.setDate(rs.getDate("date"));
				workout.setWorkoutId(rs.getInt("workout_id"));
				workout.setName(rs.getString("name"));
				java.sql.Time time = rs.getTime("time");
				if (time != null) {
					workout.setTime(time.toString());
				}
				
				workout.setNotes(rs.getString("notes"));
				return workout;
			}
			return null;
		}
		
	}
	SimpleJdbcTemplate template;
	
	private static Logger logger = LoggerFactory.getLogger(WorkoutJDBCDao.class);
}
