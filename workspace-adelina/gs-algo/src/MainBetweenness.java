import static org.junit.Assert.assertEquals;

import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class MainBetweenness {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Graph graph1 = new SingleGraph("Betweenness Centrality Test 2 (b) 1");
		Graph graph2 = new SingleGraph("Betweenness Centrality Test 2 (b) 2");
		BetweennessCentrality bcb = new BetweennessCentrality();

		Node A = graph1.addNode("A");
		Node B = graph1.addNode("B");
		Node C = graph1.addNode("C");
		Node D = graph1.addNode("D");
		Node E = graph1.addNode("E");
		Node F = graph1.addNode("F");

		graph1.addEdge("AB", "A", "B");
		graph1.addEdge("AC", "A", "C");
		graph1.addEdge("AF", "A", "F");
		graph1.addEdge("BC", "B", "C");
		graph1.addEdge("FC", "F", "C");
		graph1.addEdge("CD", "C", "D");
		graph1.addEdge("FE", "F", "E");
		graph1.addEdge("ED", "E", "D");
		graph1.addEdge("BD", "B", "D");

		bcb.setWeight(A, B, 1.0);
		bcb.setWeight(A, C, 1.0);
		bcb.setWeight(A, F, 1.0);
		bcb.setWeight(B, C, 1.0);
		bcb.setWeight(F, C, 1.0);
		bcb.setWeight(C, D, 1.0);
		bcb.setWeight(F, E, 1.0);
		bcb.setWeight(E, D, 1.0);
		bcb.setWeight(B, D, 1.0);

		for (Edge edge : graph1.getEachEdge()) {
			edge.setAttribute("weight", 1);
		}
		for (Edge edge : graph2.getEachEdge()) {
			edge.setAttribute("weight", 1);
		}
		bcb.setUnweighted();
		bcb.init(graph1);
		bcb.compute();

		for (Node node : graph1) {
			System.out.println(((Double) node.getAttribute("Cb")));

			/*
			 * bcb.setWeightAttributeName("weight");
			 * 
			 * bcb.init(graph2); bcb.compute();
			 * 
			 * for(Node node1: graph1) { Node
			 * node2=graph2.getNode(node1.getId());
			 * 
			 * System.out.println(((Double)node2.getAttribute("Cb"))); }
			 * 
			 */

		}
	}
}
