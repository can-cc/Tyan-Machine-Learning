package hebbRule;

public class Hebb {
	private int[][] inputData;
	private int[] weights;
	private int[] targets;
	private int bias;
	private int output;

	public Hebb() {
		defineWeights();
		defineBias();
		defineInputData();
		defineTargets();
		printData();
		printBobotAwal();
		modificationWeight();
	}

	private void printBobotAwal() {
		System.out.println("Bobot awal dan bias : " + weights[0] + " "
				+ weights[1] + " " + bias);
	}

	public void recognize(int x1, int x2) {
		int net = 0;
		net = x1 * weights[0] + x2 * weights[1] + bias;
		System.out.println("input " + x1 + " dan " + x2 + " hasilnya "
				+ fungsiAktifasi(net));
	}

	private int fungsiAktifasi(int net) {
		if (net >= 1)
			return 1;
		else
			return -1;
	}

	private void printData() {
		System.out.print("Data inputan : \n");
		System.out.print(" x1 x2 t \n");
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				System.out.print(" " + inputData[i][j]);
			}
			System.out.print(" " + targets[i] + "\n");
		}
		System.out.println();
	}

	private void modificationWeight() {
		System.out.println("Hitung bobot : x1 x2 bias ");
		for (int i = 0; i < 4; i++) {
			System.out.print("Bobot baru : ");
			for (int j = 0; j < 2; j++) {
				weights[j] = weights[j] + targets[i] * inputData[i][j];
				System.out.print(" " + weights[j]);
			}
			bias = bias + targets[i];
			System.out.print(" " + bias + "\n");
		}
	}

	private void defineWeights() {
		weights = new int[2];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}
	}

	private void defineBias() {
		bias = 0;
	}

	private void defineTargets() {
		targets = new int[4];
		targets[0] = 1;
		targets[1] = 1;
		targets[2] = 1;
		targets[3] = -1;
	}

	private void defineInputData() {
		inputData = new int[4][2];
		inputData[0][0] = 1;
		inputData[0][1] = 1;
		inputData[1][0] = 1;
		inputData[1][1] = 0;
		inputData[2][0] = 0;
		inputData[2][1] = 1;
		inputData[3][0] = 0;
		inputData[3][1] = 0;
	}
}
