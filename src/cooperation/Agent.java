package cooperation;

import ec.util.MersenneTwisterFast;
import java.lang.Math;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

public class Agent implements Steppable {
	int x;
	int y;
	int id;
	double sociability;
	public int type;
	Bag connections = new Bag();
	public double curr_payoff;
	public double attenuationRate;

	public Agent(int x, int y, int id, int type, double sociability) {
		super();
		this.x = x;
		this.y = y;
		this.id = id;
		this.type = type;
		this.sociability = sociability;
		this.curr_payoff = 0;
	}

	public Agent meet(Environment state) {
		double[] chance_array = new double[state.NumAgents];
		int randomnum = state.random.nextInt(99);
		
		double lower = 0;
		double higher = 0;
		
		double fSum = state.calculateFamiliarity(this);
		for (Object a: state.AgentCollection ) {
			Agent b = (Agent) a;
			chance_array[b.id] = ((state.FamiliarityArray[this.id][b.id] / fSum) * 100.0);
		}
		
		 System.out.print("chance array for: " + this.id);
		for (double num : chance_array) {
            System.out.print(num + " ");
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
	
	public double ppf(Agent a, Agent[] arr, Environment state) {
		int wit = 0;
		int bit = 0;
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] != null && a.type == arr[i].type) {
				wit ++;
			}
			else {
				bit++;
			}
		}
		double payoff = ((Math.pow((Math.pow(wit + 1, state.alpha)), Math.pow(bit + 1, state.beta)) * Math.pow(bit + 1, state.beta)) - (wit * state.WithinCost) - (bit * state.BetweenCost));
		return payoff;
	}
	
	public int calc_payoff(Agent pp, Environment state) { //O(n^2)?
		Agent[] n_array = new Agent[this.connections.size() + 1];
		this.connections.copyIntoArray(0, n_array, 0, this.connections.size());
		
		n_array[this.connections.size()] = pp;
		if (n_array.length < state.maxConnections) {
			this.curr_payoff = ppf(this, n_array, state);
			this.connections = new Bag(n_array);
			return -1;
		}
		else {
			int to_kick = -1;
			double bestpp = this.curr_payoff;
			Object[] best_array = this.connections.toArray(); //set the best array to the current connections
			Agent[] potential_array = new Agent[state.maxConnections]; //create the new array up here to save on memory
			
			for(int i = 0; i<n_array.length;i++) { //iterate through each potential agent to pop, i
				int potential_index = 0; //track index within potential_array
				for(int j=0; j < n_array.length; j++) { //iterate through the n_array to add to potential
					if(j != i) { //if the index j is not the excluded index i
						potential_array[potential_index] = n_array[j]; //set potential_array[potential_index] to the correspond n_array
						potential_index ++; //inc the pot_index
					}
				}
				double newpp = ppf(this, potential_array, state);
				if( newpp > bestpp) {
					bestpp = newpp;
					best_array = potential_array;
					to_kick = i;
				}
			}
			
			this.curr_payoff = bestpp;
			this.connections = new Bag(best_array);
			
			if(to_kick >= 0) {
				return n_array[to_kick].id; //returning the id of agent to kick
			}
			else {
				return -1;
			}
			
			//TODO iterate
		}
		
	}
	
	public void move(Environment state) {
		int xsum = x;
		int ysum = y;
		if(this.connections != null) {
			for (Object a: this.connections) {
				Agent b = (Agent) a;
				if(b != null) {
					xsum = xsum + b.x;
					ysum = ysum + b.y;
				}
			}
			int new_x = (int) (xsum/(connections.size() + 1));
			int new_y = (int) (ysum/(connections.size() + 1));
			state.placeAgent(this, new_x, new_y);
		}	
	}

	@Override
	public void step(SimState state) {
		Environment e = (Environment) state;
		Agent pp = meet(e);
		System.out.println("Agent x meeting Agent y");
		System.out.println(this.id + " " + pp.id);
		e.reinforce(this.id, pp.id); //Increase familiarity for agent i and j upon meeting
		int kickid = calc_payoff(pp, e);
		System.out.println(kickid);
		move(e);
		e.update_connections(this.id, kickid, pp.id);
	}
	
}
