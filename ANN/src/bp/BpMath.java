package bp;

import java.util.Random;

public class BpMath {
    static Random random = new Random();
    {
        random.setSeed(System.nanoTime());
    }
	
	public static double rand(int min, int max){
		return (max - min) * random.nextDouble() + min;
	}

}
