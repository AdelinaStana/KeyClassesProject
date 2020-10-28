package ranking.propertyValueCalculators.betweenness;

import ranking.propertyValueCalculators.Calculator;
import sysmodel.DSM;
import sysmodel.SparceMatrix;
import ranking.ClassRankingProperties;
import ranking.RankingEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class BetweennessCalc implements Calculator {

	private DSM dsm;
	private SparceMatrix<Integer> graph;
	boolean directed;
	int weightstrategy;

	private Map<Integer, Double> result;
	private boolean weighted;

	private Graph graph1;
	private BetweennessCentrality bcb;

	public BetweennessCalc(DSM dsm, boolean directed, int weightstrategy) {
		this.dsm = dsm;
		this.directed = directed;
		this.weightstrategy = weightstrategy;

		bcb = new BetweennessCentrality();

		graph1 = new SingleGraph("Betweenness Centrality");

		setGraphMatrix();
	}

	private void setGraphMatrix() {
		if (!directed) {
			graph = dsm.getDependencyMatrix().createUndirected(1);
		} else {
			graph = dsm.getDependencyMatrix();
		}

		int N = graph.getNumberOfNodes();
		List<Integer> nodes = graph.getAllNodes();

		for (Integer p : nodes) {
			graph1.addNode("" + p);
		}

		for (Integer p : nodes) {
			for (Integer ip : graph.inboundNeighbors(p)) {
				if (ip > p) {
					//System.out.println("Add Edge " + ip + "-" + p);

					graph1.addEdge("" + ip + "-" + p, "" + ip, "" + p);
				}
			}
		}

		if (weightstrategy == 1) {
			for (Integer p : nodes) {
				Node A = graph1.getNode("" + p);
				for (Integer ip : graph.inboundNeighbors(p)) {
					Node B = graph1.getNode("" + ip);
					bcb.setWeight(A, B, 1.0 / graph.getElement(p, ip));
				}
			}
		}

		if (weightstrategy == 2) {
			double maxElem = -1.0;

			for (Integer p : nodes) {
				for (Integer ip : graph.inboundNeighbors(p)) {
					if (maxElem < graph.getElement(p, ip))
						maxElem = graph.getElement(p, ip);
				}
			}

			for (Integer p : nodes) {
				Node A = graph1.getNode("" + p);
				for (Integer ip : graph.inboundNeighbors(p)) {
					Node B = graph1.getNode("" + ip);
					bcb.setWeight(A, B, (maxElem - graph.getElement(p, ip)));
				}
			}
		}

		/*
		 * for (Edge edge : graph1.getEachEdge()) { edge.setAttribute("weight",
		 * 1); }
		 */

	}

	public Map<Integer, Double> getResult() {
		return new HashMap(result);
	}

	public Map<Integer, Double> computeBetweennes() {

		weighted = false;
		bcb.setUnweighted();
		bcb.init(graph1);
		bcb.compute();

		result = new HashMap<Integer, Double>();

		List<Integer> nodes = graph.getAllNodes();

		for (Integer p : nodes) {
			Node node = graph1.getNode("" + p);
			double value = ((Double) node.getAttribute("Cb"));
			result.put(p, value);
		}

		/*
		 * String STYLE = "node {" + "fill-mode: dyn-plain;" +
		 * "fill-color: blue,yellow;" + "size-mode: dyn-size;" +
		 * "stroke-color: black;" + "stroke-width: 1px;" + "stroke-mode: plain;"
		 * + "}";
		 */

		
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		
		String STYLE = "node {" + "size: 3px;" + "fill-color: #777;" + "text-mode: hidden;" + "z-index: 0;" + "}" +

				"edge {" + "shape: line;" + "fill-color: #222;" + "arrow-size: 3px, 2px;" + "} ";

		graph1.addAttribute("ui.quality");
		graph1.addAttribute("ui.antialias");
		 //graph1.addAttribute("ui.stylesheet", STYLE);

		/*
		 * for (int i = 0; i < graph1.getNodeCount(); i++)
		 * graph1.getNode(i).addAttribute("ui.size",
		 * graph1.getNode(i).getNumber("Cb")*10);
		 */

	//	Viewer viewer = graph1.display(true);
	//	 View view = viewer.getDefaultView();

		//view.getCamera().resizeFrame(800, 600);
		// view.getCamera().setViewCenter(440000, 2503000, 0);
//		 view.getCamera().setViewPercent(0.3);

		 
		return result;
	}

	public Map<Integer, Double> computeWeightedBetweennes() {

		weighted = true;
		// bcb.setUnweighted();
		bcb.setWeightAttributeName("weight");
		bcb.init(graph1);
		bcb.compute();

		result = new HashMap<Integer, Double>();

		List<Integer> nodes = graph.getAllNodes();

		for (Integer p : nodes) {
			Node node = graph1.getNode("" + p);
			double value = ((Double) node.getAttribute("Cb"));

			result.put(p, value);
		}

		return result;
	}

	public List<RankingEntry> getResultAsRankingEntryList() {
		List<RankingEntry> ranking = new ArrayList<>();
		for (Map.Entry<Integer, Double> resultEntry : result.entrySet()) {
			RankingEntry rankingEntry = new RankingEntry();
			rankingEntry.setClassName(dsm.elementAtFull(resultEntry.getKey()).getName());
			rankingEntry.setClassNumber(resultEntry.getKey());
			if (directed) {
				if (weighted)
					rankingEntry.setResultValue(ClassRankingProperties.BTW_DIRECTED_W, resultEntry.getValue());
				else
					rankingEntry.setResultValue(ClassRankingProperties.BTW_DIRECTED, resultEntry.getValue());
			} else {
				if (weighted) {
					if (weightstrategy==1)
					rankingEntry.setResultValue(ClassRankingProperties.BTW_UNDIRECTED_W, resultEntry.getValue());
					else 
						rankingEntry.setResultValue(ClassRankingProperties.BTW_UNDIRECTED_W2, resultEntry.getValue());
				}
					else
					rankingEntry.setResultValue(ClassRankingProperties.BTW_UNDIRECTED, resultEntry.getValue());
			}
			ranking.add(rankingEntry);
		}

		return ranking;
	}

	@Override
	public void addResultToRanking(List<RankingEntry> ranking) {
		if (ranking != null && ranking.size() > 0) {
			for (RankingEntry rankingEntry : ranking) {

				if (directed) {
					if (weighted)
						rankingEntry.setResultValue(ClassRankingProperties.BTW_DIRECTED_W,
								result.get(rankingEntry.getClassNumber()));

					else
						rankingEntry.setResultValue(ClassRankingProperties.BTW_DIRECTED,
								result.get(rankingEntry.getClassNumber()));
				} else {
					if (weighted) {
					if (weightstrategy==1)	
					
						rankingEntry.setResultValue(ClassRankingProperties.BTW_UNDIRECTED_W,
								result.get(rankingEntry.getClassNumber()));
					else 
						rankingEntry.setResultValue(ClassRankingProperties.BTW_UNDIRECTED_W2,
								result.get(rankingEntry.getClassNumber()));
					}					else
						rankingEntry.setResultValue(ClassRankingProperties.BTW_UNDIRECTED,
								result.get(rankingEntry.getClassNumber()));
				}

			}
		}
	}
}
