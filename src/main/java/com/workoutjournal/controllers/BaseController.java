package com.workoutjournal.controllers;

import org.springframework.web.servlet.ModelAndView;

import com.workoutjournal.model.Paginator;

public abstract class BaseController {

	public void setView(ModelAndView mv) {
		//mv.setViewName(viewName)
	}
	
	public Paginator createPaginator(Integer start, Integer size) {
		start = (start == null) ? 0 : start;
		
		if (size == null || size == 0) {
			size = 10;
		}
		
		Paginator p = new Paginator(start, size);
		return p;
	}
	
	public boolean createOrderBy(String dir) {		
		if (dir != null && dir.equalsIgnoreCase("DESC")) {
			return false;
		}
		return true;
	}
	
	protected static final String userId = "sas342";
	protected static final String OFFSET = "offset";
	protected static final String LIMIT = "limit";
	protected static final String SORTFIELD = "sortField";
	protected static final String SORTDIR = "sortDir";
	
}
