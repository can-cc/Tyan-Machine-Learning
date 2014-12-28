package bp;

public class Link {
	Neuron backNeuron;
	Neuron forwardNeuron;
	double weight;
	
	public Link() {
		weight = BpMath.rand(-1 ,1);
	}
	
	public void setBackNeuron(Neuron backNeuron) {
		if(!backNeuron.ContainForwardLink(this)){
			this.backNeuron = backNeuron;
			 backNeuron.setForwardLink(backNeuron.forwardLink.size(), this);
		}
	}
	
	public void setForwardNeuron(Neuron forwardNeuron) {
		if(!forwardNeuron.ContainBackLink(this)){
			this.forwardNeuron = forwardNeuron;
			forwardNeuron.setBackLink(forwardNeuron.backLink.size(), this);
		}
	}
	
	public void updateWeight(double weight){
		this.weight = weight;
	}
	
	public void plusWeight(double increase){
		this.weight += increase;
	}
}
