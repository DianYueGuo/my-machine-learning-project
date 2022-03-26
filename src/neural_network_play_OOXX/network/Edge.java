package neural_network_play_OOXX.network;

public class Edge {
	private Node begin;
	private Node end;
	private double weight;
	
	public Edge(Node begin, double weight) {
		this.begin = begin;
		this.weight = weight;
	}
}
