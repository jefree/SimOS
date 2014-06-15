package simos;

import util.Queue;

public class Algorithm {
	
	String name;
	
	Queue<Process> processes;
	Planner planner;
	
	public Algorithm() {
		this("FIFO");
	}
	
	public Algorithm(String name) {
		this.name = name;
		processes = new Queue<>();
	}
	
	public void addProcess(Process pr) {
		processes.put(pr);
	}
	
	public Process next(Process current) {
		
		if(current == null) {
			return processes.get();
		} 
		
		return current;
	}
}