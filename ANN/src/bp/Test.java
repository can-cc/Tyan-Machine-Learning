package bp;

public class Test {
	public static void main(String[] args) {
		BpNN bp = new BpNN(2, 4, 1);
		
		double[][] inputs = new double[][] {
				new double[] {-1, -1},
				new double[] {1, 1},
				new double[] {-1, 1},
				new double[] {1, -1}
				};
		double[][] targets = new double[][] {
				new double[] {-1},
				new double[] {-1},
				new double[] {1},
				new double[] {1},
		};
		
		while(!bp.train(inputs, targets)){
			bp.reBuild();
		}
		
		double[] result = bp.thinking(new double[] {-1, -1});
		for(double s : result){
			System.out.println("thinking: "+ s);
		}
		
	}
}
