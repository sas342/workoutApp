package com.workoutjournal.service;

import com.workoutjournal.model.Paginator;

public class Parameters {

	
	public boolean isAsc() {
		return asc;
	}
	public void setAsc(boolean asc) {
		this.asc = asc;		
	}
	public Paginator getPaginator() {
		return paginator;
	}
	public void setPaginator(Paginator paginator) {
		this.paginator = paginator;
	}
	
	
	public String getOrderColumn() {
		return orderColumn;
	}
	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}


	private String orderColumn = null;
	private boolean asc = true;
	private Paginator paginator;
}
