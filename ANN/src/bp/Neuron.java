package bp;

import java.util.HashMap;

public class Neuron {
	HashMap<Integer, Link> backLink = new HashMap<Integer, Link>();
	HashMap<Integer, Link> forwardLink = new HashMap<Integer,Link>();
	public double data = 0.0;
	
	public Link getBackLink(int key) {
		return backLink.get(key);
	}
	
	public Link getForwardLink(int key) {
		return forwardLink.get(key);
	}
	
	public boolean ContainBackLink(Link link){
		return backLink.containsValue(link);
	}
	
	public boolean ContainForwardLink(Link link){
		return forwardLink.containsValue(link);
	}
	
	public Link setBackLink(int key, Link link){
		if(link.forwardNeuron != this){
			link.setForwardNeuron(this);
		}
		return backLink.put(key, link);
	}
	
	public Link setForwardLink(int key, Link link){
		if(link.backNeuron != this){
			link.setBackNeuron(this);
		}
		return forwardLink.put(key, link);
	}
}
