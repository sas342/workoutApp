package com.workoutjournal.model;

public class Paginator {

	private int start = 0;
	private int size = 10;
	
	public Paginator(){}
	public Paginator(int start, int size) {
		this.start = start;
		this.size = size;
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	
}
