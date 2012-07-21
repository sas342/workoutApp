package com.workoutjournal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.workoutjournal.service.ExerciseService;
import com.workoutjournal.service.Parameters;

@Controller
@RequestMapping(value="exercises")
public class ExerciseController extends BaseController{

	@RequestMapping(value="/search/{searchStr}", method = RequestMethod.GET)
	public ModelMap getExercises(@RequestParam(value=OFFSET, required=false) Integer start, 
			@RequestParam(value=LIMIT, required=false) Integer size, 
			@RequestParam(value=SORTFIELD, required=false) String orderBy,
			@RequestParam(value=SORTDIR, required=false) String dir,
			@PathVariable String searchStr, ModelMap model) {
		
		Parameters param = new Parameters();
		param.setPaginator(createPaginator(start, size));
		param.setAsc(createOrderBy(dir));
		model.put("exercises", service.getExercises(userId, searchStr, param));
		return model;
	}
	
	@RequestMapping(value="/workout/{id}", method = RequestMethod.GET)
	public ModelMap getExercisesByWorkout(@PathVariable int workoutId, ModelMap model) {
		
		model.put("exercises", service.getExercisesByWorkout(workoutId));
		return model;
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ModelMap getExercises(@PathVariable int id, ModelMap model) {
		
		model.put("exercises", service.getExercise(userId, id));
		return model;
	}
	
	
	@Autowired
	public void setService(ExerciseService service) {
		this.service = service;
	}
	
	private ExerciseService service;
}
