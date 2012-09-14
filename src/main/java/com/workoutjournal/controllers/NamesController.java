package com.workoutjournal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import com.workoutjournal.service.NamesService;
import com.workoutjournal.model.Exercises;

@Controller
@RequestMapping("names")
public class NamesController extends BaseController{
	
	@RequestMapping(value="/{searchStr}", method=RequestMethod.GET)
	public ModelMap getNames(@PathVariable String searchStr, ModelMap model) {
		List<Exercises> list = service.getNames(searchStr);
		model.put("exerciseNames", service.getNames(searchStr));
		model.put("totalCount", list.size());
		return model;		
	}
	
	@Autowired
	public void setService(NamesService service) {
		this.service = service;
	}
	private NamesService service;

}
