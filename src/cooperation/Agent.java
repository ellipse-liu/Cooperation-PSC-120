package cooperation;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

public class Agent implements Steppable {
	int x;
	int y;
	int id;
	float sociability;
	public int type;
	Bag connections;
	public int curr_payoff;

	public Agent(int x, int y, int id, int type, float sociability) {
		super();
		this.x = x;
		this.y = y;
		this.id = id;
		this.type = type;
		this.sociability = sociability;
		this.curr_payoff = 0;
	}

	public Agent meet(Environment state) {
		int[] chance_array = new int[state.NumAgents];
		int randomnum = state.random.nextInt(99);
		
		int lower = 0;
		int higher = 0;
		
		int fSum = state.calculateFamiliarity(this);
		for (Object a: state.AgentCollection ) {
			Agent b = (Agent) a;
			chance_array[b.id] = (int) ((state.FamiliarityArray[this.id][b.id] / fSum) * 100);
		}
		
		for(int i = 0; i < state.NumAgents; i++) {
			lower = higher;
			higher = higher + chance_array[i];
			if(randomnum >= lower && randomnum < higher) {
				return ((Agent)state.AgentCollection.get(i));
			}
		}
		return this;
	}
	
	public int calc_payoff(Agent pp) {
		
	}
	
	public void move(Environment state) {
		int xsum = x;
		int ysum = y;
		for (Object a: connections) {
			Agent b = (Agent) a;
			xsum = xsum + b.x;
			ysum = ysum + b.y;
		}
		int new_x = (int) (xsum/(connections.size() + 1));
		int new_y = (int) (ysum/(connections.size() + 1));
		state.placeAgent(this, new_x, new_y);
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		
	}
	
}
