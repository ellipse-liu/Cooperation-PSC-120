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
	public int type;
	Bag connections;

	public Agent(int x, int y, int id, int type) {
		super();
		this.x = x;
		this.y = y;
		this.id = id;
		this.type = type;
	}

	public void meet(Environment state) {
		for (Object a: state.AgentCollection ) {
			
		}
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		
	}
	
}
