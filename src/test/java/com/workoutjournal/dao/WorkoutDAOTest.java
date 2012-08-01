package com.workoutjournal.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.workoutjournal.model.Workout;
import com.workoutjournal.model.WorkoutList;

@ContextConfiguration(locations={"/com/workoutjournal/testContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class WorkoutDAOTest extends AbstractTransactionalTestNGSpringContextTests {

	@Rollback(true)
	@Test	
	public void testInsert() {
		Workout workout = new Workout();
		workout.setName("TestWorkout");
		workout.setDate(java.util.Calendar.getInstance().getTime());
		workout.setNotes("Test Notes");
		workout.setTime("1000");
				
		Workout inserted = dao.updateWorkouts("sas342", workout);
		Assert.assertNotNull(inserted);
		Assert.assertNotNull(inserted.getWorkoutId());
		
	}
	
	@Test
	public void test1() {
		Workout workout = dao.getWorkout("sas342", 10);
		Assert.assertNotNull(workout);
		
	}
	
	@Rollback(true)
	@Test
	public void test2() {
		Workout workout = new Workout();
		workout.setName("TestWorkout");
		workout.setDate(java.util.Calendar.getInstance().getTime());
		workout.setNotes("Test Notes");
		workout.setTime("1000");				
		dao.updateWorkouts("sas342", workout);
		
		WorkoutList workouts = dao.getWorkouts("sas342","TestWorkout", 0, 10, "name",true);
		
		Assert.assertNotNull(workouts);
		Assert.assertTrue(workouts.getWorkouts().size() > 0);
				
		workout = workouts.getWorkouts().get(0);
		Assert.assertEquals(workout.getName(), "TestWorkout");
		Assert.assertNotNull(workout.getDate());
		Assert.assertEquals(workout.getTime(), "00:10:00");
		Assert.assertEquals(workout.getNotes(), "Test Notes");
	}
	
	@Test
	public void testGetCount() {
		int count = dao.getNumberOfExercise(30);
		Assert.assertTrue(count > 0);
	}
	
	@Test
	public void testMatch() throws Exception{
		
		List<Workout> list = dao.getSimilarWorkouts("sas342", 32);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
	}
	
	@Autowired
	public void setWorkoutDao(WorkoutJDBCDao dao) {
		this.dao = dao;
	}
	
	WorkoutJDBCDao dao;
}
