package simos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class OS {

	List<Processor> processors;
	HashMap<String, Resource> resources;
	
	List<OSEventListener> listeners;
	
	public OS() {
		
		processors = new ArrayList<>();
		resources = new HashMap<>();
		listeners = new LinkedList<>();
	}
	
	public void execute() {
		
		for (Processor p : processors) {
			p.plan();
		}
		
		for (Processor p : processors) {
			p.execute();
		} 
	}
	
	public boolean tryRun(Process pr) {
		
		boolean canRun = canRun(pr);
		
		if(canRun) {			
			run(pr);
		}
		
		return canRun;
	}
	
	protected boolean hasPriority(Process pr, Process other) {
		return false;
	}
	
	private boolean canRun(Process pr) {
		
		boolean canRun = true;
		
		for (Processor p : processors) {
			
			if (p.current == pr || p.current == null) {
				continue;
			}
			
			if (haveCommonRes(pr, p.current)) {
				
				if (!hasPriority(pr, p.current)) {	
					canRun = false;
					break;
				}
			}
		}
		
		for (String res : pr.resources) {
			if (!resources.containsKey(res)){
				canRun = false;
				break;
			}
		}
		
		return canRun;
	}
	
	private void run(Process pr) {
		
		for (Processor p : processors) {
			
			if (p.current == pr || p.current == null) {
				continue;
			}
			
			if (haveCommonRes(pr, p.current)) {
				
				freeResources(p.current);
				
				p.current.block();
				p.current = null;
				
				p.plan();
			}
		}
		
		activeResources(pr);
		
	}
	
	public boolean verifyResources(Process pr) {
		
		boolean result = true;
		
		for (String res : pr.resources) {
			if (!resources.containsKey(res)) {
				result = false;
				break;
			} else if (!resources.get(res).isFree()) {
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	private void activeResources(Process pr) {
		
		for (String res : pr.resources) {
			Resource r = resources.get(res);
			
			if (r != null) {
				r.active(pr);
			}
		}
	}
	
	private void freeResources(Process pr) {
		
		for (String res : pr.resources) {
			Resource r = resources.get(res);
			
			if (r != null) {
				r.free();
			}
		}
		
	}
	
	private boolean haveCommonRes(Process pr, Process other) {
		
		boolean common = false;
		
		for (String res : pr.resources) {
			if (other.resources.contains(res)) {
				common = true;
				break;
			}
		}
		
		return common;
	}
	
	public void addResource(String name) {
		
		Resource res = new Resource(name);
		res.os = this;
		
		resources.put(name, res);
		
		for (OSEventListener listener : listeners) {
			listener.onAddResource(res.name);
		}
	}
	
	public void addProcessor(Processor p) {
		processors.add(p);
	}
	
	public void addListener(OSEventListener listener) {
		listeners.add(listener);
	}
	
	public interface OSEventListener {
		
		void onAddProcess(String pr, String p, String alg);
		void onSuspendProcess(String pr, String p);
		void onBlockProcess(String pr, String p);
		void onEndProcess(String pr);
		
		void onRunProcess(String pr, String p);
		
		void onAddResource(String r);
		void onActiveResource(String r, String pr);
		void onFreeResource(String r);
		void onBlockResource(String r);
		
	}
}
