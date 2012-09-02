package com.workoutjournal.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.workoutjournal.service.Parameters;
import com.workoutjournal.service.WorkoutService;
import com.workoutjournal.model.WorkoutList;
import com.workoutjournal.model.Workout;

@Controller
@RequestMapping("workouts")
public class WorkoutController extends BaseController{

		
	@RequestMapping(method=RequestMethod.GET)	
	public ModelMap getWorkouts(@RequestParam(value=OFFSET, required=false) Integer start, 
			@RequestParam(value=LIMIT, required=false) Integer size, 
			@RequestParam(value=SORTFIELD, required=false) String orderBy,
			@RequestParam(value=SORTDIR, required=false) String dir, ModelMap map) {
				
		Parameters params = new Parameters();
		params.setPaginator(createPaginator(start, size));
		params.setAsc(createOrderBy(dir));
		params.setOrderColumn(orderBy);
		WorkoutList list = service.getWorkouts(userId, params);
		map.put("size", list.getSize());
		map.put("start", list.getStart());
		map.put("totalCount", list.getTotalCount());
		map.put("workouts", list.getWorkouts());
		return map;
		
	}
	
	@RequestMapping(value="/search/{searchStr}", method=RequestMethod.GET)
	public ModelMap getWorkoutsByName(@RequestParam(value=OFFSET, required=false) Integer start, 
			@RequestParam(value=LIMIT, required=false) Integer size, 
			@RequestParam(value=SORTFIELD, required=false) String orderBy,
			@RequestParam(value=SORTDIR, required=false) String dir,
			@PathVariable String searchStr, ModelMap map) {
		
		Parameters params = new Parameters();
		params.setPaginator(createPaginator(start, size));
		params.setAsc(createOrderBy(dir));
		params.setOrderColumn(orderBy);
		
		WorkoutList list = service.getWorkouts(userId, searchStr, params);
		map.put("size", list.getSize());
		map.put("start", list.getStart());
		map.put("totalCount", list.getTotalCount());
		map.put("workouts", list.getWorkouts());
		
		return map;
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ModelMap getWorkout(@PathVariable int id, ModelMap map) {
		
		map.put("workout", service.getWorkout(userId, id));
		return map;
	}

	@RequestMapping(value="/similar/{id}", method=RequestMethod.GET)
	public ModelMap getSimilarWorkouts(@PathVariable int id, ModelMap map) {

		map.put("workouts", service.getSimilarWorkouts(userId, id));
		return map;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public boolean saveWorkout(@RequestBody Workout workout) {
		System.out.println("savedWorkout called with "+workout+" for user "+userId);
		logger.info("savedWorkout called with "+workout+" for user "+userId);
		return service.updateWorkout(userId, workout);
	}
	
	@Autowired
	public void setWorkoutService(WorkoutService service) {
		this.service = service;
	}
	
	final Logger logger = LoggerFactory.getLogger(WorkoutController.class);
	
	WorkoutService service;
}
