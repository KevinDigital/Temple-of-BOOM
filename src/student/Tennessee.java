package student;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import game.EscapeState;
import game.ExploreState;
import game.Explorer;
import game.Node;
import game.NodeStatus;

public class Tennessee extends Explorer {

	private ExploreState es;

	/** Get to the orb in as few steps as possible. Once you get there, 
	 * you must return from the function in order to pick
	 * it up. If you continue to move after finding the orb rather 
	 * than returning, it will not count.
	 * If you return from this function while not standing on top of the orb, 
	 * it will count as a failure.
	 * 
	 * There is no limit to how many steps you can take, but you will receive
	 * a score bonus multiplier for finding the orb in fewer steps.
	 * 
	 * At every step, you know only your current tile's ID and the ID of all 
	 * open neighbor tiles, as well as the distance to the orb at each of these tiles
	 * (ignoring walls and obstacles). 
	 * 
	 * In order to get information about the current state, use functions
	 * currentLocation(), neighbors(), and distanceToOrb() in ExploreState.
	 * You know you are standing on the orb when distanceToOrb() is 0.
	 * 
	 * Use function moveTo(long id) in ExploreState to move to a neighboring 
	 * tile by its ID. Doing this will change state to reflect your new position.
	 * 
	 * A suggested first implementation that will always find the orb, but likely won't
	 * receive a large bonus multiplier, is a depth-first search.*/
	@Override public void getOrb(ExploreState state) {
		//TODO : Get the orb
		es = state;
		dfs(new HashSet<Long>());
	}


	/** Depth first search. It takes in an argument of the HashSet of node ID's that have been visited.
	 * 
	 * @param visited
	 */
	public void dfs(HashSet<Long> visited) {
		if (es.distanceToOrb() == 0) {
			return;
		}
		visited.add(es.currentLocation());
		Long currentNode = es.currentLocation();
		long neighborID = 0;
		for (NodeStatus neighbor : es.neighbors()) {
			if(!visited.contains(neighbor.getId())) {
				int shortestNeighbor = Integer.MAX_VALUE;
				for (NodeStatus neighbor2 : es.neighbors()) {
					if (neighbor2.getDistanceToTarget() < shortestNeighbor && !visited.contains(neighbor2.getId())) {
						shortestNeighbor = neighbor2.getDistanceToTarget();
						neighborID = neighbor2.getId();
					}
				}
				es.moveTo(neighborID);
				dfs(visited);
				if (es.distanceToOrb() == 0) {
					return;
				}
				es.moveTo(currentNode);
				dfs(visited);
				if (es.distanceToOrb() == 0) {
					return;
				}
			}
		}
	}

	/** Get out the cavern before the ceiling collapses, trying to collect as much
	 * gold as possible along the way. Your solution must ALWAYS get out before time runs
	 * out, and this should be prioritized above collecting gold.
	 * 
	 * You now have access to the entire underlying graph, which can be accessed through EscapeState.
	 * currentNode() and getExit() will return Node objects of interest, and getNodes()
	 * will return a collection of all nodes on the graph. 
	 * 
	 * Note that the cavern will collapse in the number of steps given by stepsRemaining(),
	 * and for each step this number is decremented by the weight of the edge taken. You can use
	 * stepsRemaining() to get the time still remaining, seizeGold() to pick up any gold
	 * on your current tile (this will fail if no such gold exists), and moveTo() to move
	 * to a destination node adjacent to your current node.
	 * 
	 * You must return from this function while standing at the exit. Failing to do so before time
	 * runs out or returning from the wrong location will be considered a failed run.
	 * 
	 * You will always have enough time to escape using the shortest path from the starting
	 * position to the exit, although this will not collect much gold. For this reason, using 
	 * Dijkstra's to plot the shortest path to the exit is a good starting solution. */
	@Override public void getOut(EscapeState state) {
		//TODO: Escape from the cavern before time runs out

		/* Heap<Node> largestGoldList = new Heap<Node>();
		for (Node node : state.getNodes()) {
			if (node.getTile().getGold() != 0) {
				largestGoldList.add(node, -1* node.getTile().getGold());
			}
		}
		 */

		int nextStep = 0;
		while (2*Paths.pathLength(Paths.dijkstra(state.currentNode(), state.getExit())) < state.stepsRemaining()) {

			if (state.currentNode().getTile().getGold() != 0) {
				state.seizeGold();
			}

			Heap<Node> listOfGold = goldToWeightRatioList(state, state.currentNode());

			if (listOfGold.size() == 0) {
				List<Node> toExit = Paths.dijkstra(state.currentNode(), state.getExit());
				int currentIndex2 = 0;
				while (state.currentNode().getId() != state.getExit().getId()) {
					currentIndex2++;
					state.moveTo(toExit.get(currentIndex2));
					if (state.currentNode().getTile().getGold() != 0) {
						state.seizeGold();
					}
				}
				return;
			}


			Node currentBestGold = listOfGold.poll();
			List<Node> pathToGold = Paths.dijkstra(state.currentNode(), currentBestGold);

			int currentIndex = 1;
			while (currentIndex < pathToGold.size() && Paths.pathLength(Paths.dijkstra(state.currentNode(), state.getExit())) < state.stepsRemaining() + nextStep) {

				if (currentIndex < pathToGold.size() - 1) {
					nextStep = Paths.pathLength(Paths.dijkstra(state.currentNode(), pathToGold.get(currentIndex + 1)));
				}

				if (Paths.pathLength(Paths.dijkstra(state.currentNode(), state.getExit())) >= (state.stepsRemaining()-2*nextStep)) {
					List<Node> toExit = Paths.dijkstra(state.currentNode(), state.getExit());
					int currentIndex2 = 0;
					while (state.currentNode().getId() != state.getExit().getId()) {
						currentIndex2++;
						state.moveTo(toExit.get(currentIndex2));
						if (state.currentNode().getTile().getGold() != 0) {
							state.seizeGold();
						}
					}
					return;
				}


				state.moveTo(pathToGold.get(currentIndex));
				if (state.currentNode().getTile().getGold() != 0) {
					state.seizeGold();
				}
				currentIndex++;
			}
		}

		List<Node> toExit = Paths.dijkstra(state.currentNode(), state.getExit());
		int currentIndex2 = 1;
		while (state.currentNode().getId() != state.getExit().getId()) {
			state.moveTo(toExit.get(currentIndex2));
			if (state.currentNode().getTile().getGold() != 0) {
				state.seizeGold();
			}
			currentIndex2++;
		}
		return;
	}

	/** Return a Heap of Nodes ordered by the largest gold to weight ratio for each tile
	 * 
	 * @param state
	 * @param currentNode
	 * @return
	 */
	public Heap<Node> goldToWeightRatioList (EscapeState state, Node currentNode) {
		Heap<Node> goldToWeightRatioList = new Heap<Node>();


		for (Node goldNode : state.getNodes()) {
			if (goldNode.getTile().getGold() != 0) {
				goldToWeightRatioList.add(goldNode, -1* (goldNode.getTile().getGold())/Paths.pathLength(Paths.dijkstra(state.currentNode(), goldNode)));
			}
		}
		return goldToWeightRatioList;
	}


}
