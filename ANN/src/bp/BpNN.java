package bp;

import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class BpNN {
	Neuron[] inputNeuron;
	Neuron[] hiddenNeuron;
	Neuron[] outputNeuron;
	double learnRate;
	double threshold;
	
	private final int inputCount;
	private final int hiddenCount;
	private final int outputCount;
	
	public BpNN(int inputCount, int hiddenCount, int outputCount) {
		this.inputCount = inputCount;
		this.hiddenCount = hiddenCount;
		this.outputCount = outputCount;
		build();
	}
	
	public void reBuild(){
		build();
	}
	
	public void build(){
		inputNeuron = new Neuron[inputCount+1];
		hiddenNeuron = new Neuron[hiddenCount];
		outputNeuron = new Neuron[outputCount];
        initNeurons(inputNeuron);
        initNeurons(hiddenNeuron);
        initNeurons(outputNeuron);
        makeLink(inputNeuron, hiddenNeuron);
        makeLink(hiddenNeuron, outputNeuron);
	}
	
	public void initNeurons(Neuron[] neurons){
		for(int i=0; i < neurons.length; i++){
			if(neurons[i] == null){
				neurons[i] = new Neuron();
			}
		}
	}
	
	public void makeLink(Neuron[] backNeurons, Neuron[] forwardNeurons){
		for(int backLen=0; backLen<backNeurons.length; backLen++){
			for(int forwardLen=0; forwardLen<forwardNeurons.length; forwardLen++){
				//Link link = new Link();
				Link link2 = backNeurons[backLen].getBackLink(forwardLen);
				if(link2 == null){
					link2 = new Link();
					backNeurons[backLen].setForwardLink(forwardLen, link2);
					//forwardNeurons[forwardLen].setBackLink(forwardLen, link);
				}
				link2.setForwardNeuron(forwardNeurons[forwardLen]);
			}
		}
	}
	
    private double sigmoid(double x) {
        return Math.tanh(x);
    }
    
    private double sigmoidDerivative(double y) {
        return 1.0 - sigmoid(y) * y;
    }
    
    public boolean train(double[][] inputs, double[][] targets){
    	return train(inputs, targets, 0.3, 0.05, 1000);
    }
    
    public boolean train(double[][] inputs, double[][] targets, double learnRate, double threshold, int maxlearn){
    	if(inputs.length != targets.length){
    		System.out.println("input&target don't match");
    		System.exit(0);
    	}
    	this.learnRate = learnRate;
    	this.threshold = threshold;
    	boolean complete = false;
    	int count = 0;
    	double error = 0.0;
    	while(!complete){
    		count++;
    		error = 0;
    		for(int i=0; i<inputs.length; i++){
    			error += learn(inputs[i], targets[i]);
    			if(error < this.threshold){
    				complete = true;
    				break;
    			}
    		}
    		if(count > maxlearn){
    			System.out.println("train fail; e = "+error);
    			return false;
    		}
    	}
    	System.out.println("train success; e = "+error);
    	return true;
    }
    
    public double learn(double[] input, double[] target){
    	makeInput(input);
    	activation();
    	return evolutionComputing(target);
    }
    
    public void makeInput(double[] input){
    	for(int i=0; i<inputNeuron.length; i++){
    		if(i >= input.length){
    			inputNeuron[i].data = 1.0;
    		}else{
    			inputNeuron[i].data = input[i];
    		}
    	}
    }
    
    public void activation(){
        transmitComputing(hiddenNeuron);
        transmitComputing(outputNeuron);
    }
    
    public double[] thinking(double[] inputs){
    	makeInput(inputs);
    	activation();
    	return makeOutput();
    }
    
    public void transmitComputing(Neuron[] neurons){
    	for(Neuron neuron : neurons){
    		double sum = 0.0;
    		Set<Entry<Integer, Link>> linkset = neuron.backLink.entrySet();
    		for(Entry<Integer, Link> entry : linkset) {
    			Link link = entry.getValue();
    			sum += link.backNeuron.data * link.weight;
    		}
    		neuron.data = sigmoid(sum);
    	}
    }
    
    public double evolutionComputing(double[] target){
    	double[] output_delta = new double[outputNeuron.length];
    	double totalError = 0.0;
    	for(int i=0; i<output_delta.length; i++){
    		output_delta[i] = (target[i] - outputNeuron[i].data)
    				* sigmoid2(target[i]);
    	}
    	
    	double[] hidden_delta = new double[hiddenNeuron.length];
    	for(int i=0; i<hiddenNeuron.length; i++){
    		double error = 0.0;
    		Set<Entry<Integer, Link>> linkSet = hiddenNeuron[i].forwardLink.entrySet();
    		for(Entry<Integer, Link> entry : linkSet){
    			error += output_delta[entry.getKey()] * entry.getValue().weight;
    		}
    		hidden_delta[i] = sigmoid2(hiddenNeuron[i].data) * error;
    	}
    	
    	for(int i=0; i<hiddenNeuron.length; i++){
    		Set<Entry<Integer, Link>> linkSet = hiddenNeuron[i].forwardLink.entrySet();
    		for(Entry<Integer, Link> entry : linkSet){		
    			entry.getValue().plusWeight(output_delta[entry.getKey()]
    					* hiddenNeuron[entry.getKey()].data * learnRate);
    		}
    	}
    	
    	for(int i=0; i<inputNeuron.length; i++){
    		Set<Entry<Integer, Link>> linkSet = inputNeuron[i].forwardLink.entrySet();
    		for(Entry<Integer, Link> entry : linkSet){
    			entry.getValue().plusWeight(hidden_delta[entry.getKey()]
    					* inputNeuron[i].data * learnRate);
    		}
    	}
    	
    	for(int i=0; i<outputNeuron.length; i++){
    		double tmperror = outputNeuron[i].data - target[i];
    		totalError += tmperror * tmperror;
    	}
    	
    	return totalError * 0.5;    	
    }
    
    public double[] makeOutput(){
    	double[] output = new double[outputNeuron.length];
    	for(int i=0; i<outputNeuron.length; i++){
    		output[i] = outputNeuron[i].data;
    	}
    	return output;
    }
    
	private double sigmoid2(double val) {
		return 1d / (1d + Math.exp(-val));
	}

}
