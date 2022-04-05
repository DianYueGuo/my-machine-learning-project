import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.Network;

public class MainTest {

	public static void main(String args[])
			throws InterruptedException, IOException, MatrixAdditionException, MatrixMultiplicationException {

		FileOutputStream fos = new FileOutputStream("Network.dat");
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		Network network = new Network(Network.RANDOM_INITIALIZAR, Network.SIGMOID_ACTIVATION_FUNCTION,
				new int[] { 2, 10, 100, 100, 100, 10, 1 });
//		System.out.println(network);
		double startTime = System.currentTimeMillis();
		System.out.println(network
				.getOutput(new Matrix<Double>(new Double[][] { { -1.0 }, { 1.0 } }, 2, 1, Matrix.DOUBLE_CALCULATOR)));
		double endTime = System.currentTimeMillis();
		System.out.println("time spent: " + (endTime - startTime) / 1000 + "");

		oos.writeObject(network);

//		FileInputStream fis = new FileInputStream(new File("SerializableObject.dat"));
//		ObjectInputStream ois = new ObjectInputStream(fis);
//		
//		SerializableObject so = (SerializableObject) ois.readObject();
//		System.out.println(so);
	}

}
