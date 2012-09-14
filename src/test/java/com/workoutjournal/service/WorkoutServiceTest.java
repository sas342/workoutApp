package com.workoutjournal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.workoutjournal.model.ExSet;
import com.workoutjournal.model.Exercise;
import com.workoutjournal.model.Exercises;
import com.workoutjournal.model.Paginator;
import com.workoutjournal.model.Workout;
import com.workoutjournal.model.WorkoutList;



@ContextConfiguration(locations={"/com/workoutjournal/testContext.xml"})
@TransactionConfiguration(defaultRollback=true)
public class WorkoutServiceTest extends AbstractTransactionalTestNGSpringContextTests {
	
	@Test
	public void testGetWorkout(){
		Workout w = service.getWorkout("sas342", 30);
		Assert.assertNotNull(w);
	}
	
	@Test
	public void testGetWorkouts() {
		Parameters param = new Parameters();
		param.setPaginator(new Paginator(15, 1000));
		WorkoutList workouts = service.getWorkouts("sas342", param);
		Assert.assertNotNull(workouts);
		List<Workout> list = workouts.getWorkouts();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0 && list.size() <=1000);
	}
	
	@Test
	public void testGetWorkoutsWithName() {
		Parameters param = new Parameters();
		param.setPaginator(new Paginator(15, 20));
		WorkoutList workouts = service.getWorkouts("sas342", "WOD", param);
		Assert.assertNotNull(workouts);
		List<Workout> list = workouts.getWorkouts();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0 && list.size() <=20);
		for (Workout w : list) {
			Assert.assertTrue(w.getName().contains("WOD"));
			Assert.assertNotNull(w.getExerciseList());
		}
		
	}
	
	@Test
	@Rollback(true)
	public void testInsert() {
		Workout workout = new Workout();
		workout.setName("TestWorkout");
		workout.setDate(java.util.Calendar.getInstance().getTime());
		workout.setNotes("Test Notes");
		workout.setTime("1000");
		
		List<Exercise> el = new ArrayList<Exercise>();
		Exercise e = new Exercise();
		e.setExercises(new Exercises("My Exercise"));
		e.setExNum(1);
		List<ExSet> list = new ArrayList<ExSet>();
		for (int i=1; i< 3; i++) {
			ExSet set = new ExSet();
			set.setNotes("Note"+i);
			set.setNumber(i);
			set.setReps(i*4);
			set.setWeight(i*5);
			list.add(set);
		}
		e.setSetsList(list);
		el.add(e);
		workout.setExerciseList(el);
		
		boolean updated = service.updateWorkout("sas342", workout);
		Assert.assertTrue(updated);
		Parameters param = new Parameters();
		param.setPaginator(new Paginator(0, 10));
		WorkoutList wlist = service.getWorkouts("sas342", "TestWorkout", param);
		
		Assert.assertNotNull(wlist);
		Assert.assertTrue(wlist.getWorkouts().size() > 0);
		
	}
	
	@Test
	@Rollback(true)
	public void testUpdate() {
		Workout w = service.getWorkout("sas342", 40);
		Assert.assertNotNull(w);
		
		w.setNotes("New Notes");
		for (Exercise e : w.getExerciseList()) {
			Exercises ex = new Exercises("My Ex"+e.getExerciseId());
			e.setExercises(ex);
			for (ExSet set : e.getSetsList()) {
				set.setReps(1000);
			}
		}
		
		boolean updated = service.updateWorkout("sas342", w);
		Assert.assertTrue(updated);
		
		w = service.getWorkout("sas342", 40);
		Assert.assertEquals("New Notes", w.getNotes());
	}
	
	@Autowired
	public void setService(WorkoutService service) {
		this.service = service;
	}

	WorkoutService service;
}
