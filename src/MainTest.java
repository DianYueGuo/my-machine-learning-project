import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import neural_network.Network;

public class MainTest {

	public static void main(String args[]) throws InterruptedException, IOException {
		
		FileOutputStream fos = new FileOutputStream("Network.dat");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		Network network = new Network(Network.RANDOM_INITIALIZAR, Network.SIGMOID_ACTIVATION_FUNCTION, new int[] {1, 2, 1});
		System.out.println(network);
		
//		oos.writeObject(network);
		
//		FileInputStream fis = new FileInputStream(new File("SerializableObject.dat"));
//		ObjectInputStream ois = new ObjectInputStream(fis);
//		
//		SerializableObject so = (SerializableObject) ois.readObject();
//		System.out.println(so);
	}

}
