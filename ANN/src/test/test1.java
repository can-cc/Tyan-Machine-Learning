package test;

public class test1 {
	class Neuron{
		int data;
	}
	
	class Link{
		int data;
	}
	Neuron[] ntest;
	public void cons(){
		ntest = new Neuron[3];
		ntest[0] = new Neuron();
		ntest[0].data = 3;
		ntest = new Neuron[3];
		System.out.println(ntest[0].data);
		
	}
	
	public static void main(String[] args) {
		test1 t1= new test1();
		t1.cons();
		
	}

}
