import java.util.Random;


public class test {
	public static void main(String[] args) {
	    final double EPS = 1E-14;
	    Random rnd = new Random();
	    int count = Integer.parseInt(args[0]);
	    for (int k=200; k>=-200; k--) 
	      for (int i=0; i<count; i++) {
		double logp = Math.abs(rnd.nextDouble()) * Math.pow(10, k);
		double logq = Math.abs(rnd.nextDouble());
		double logpplusq = HMMAlgo.logplus(logp, logq);
		double p = Math.exp(logp), 
		  q = Math.exp(logq), 
		  pplusq = Math.exp(logpplusq);
		if (Math.abs(p+q-pplusq) > EPS * pplusq) 
		  System.out.println(p + "+" + q + "-" + pplusq);	
	      }
	  }

}
