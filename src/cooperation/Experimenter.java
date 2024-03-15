package cooperation;

import observer.Observer;
import sim.engine.SimState;
import sim.util.Bag;
import sweep.ParameterSweeper;
import sweep.SimStateSweep;

public class Experimenter extends Observer {

	public Experimenter(String fileName, String folderName, SimStateSweep state, ParameterSweeper sweeper, String precision, String[] headers) {
		super(fileName, folderName, state, sweeper, precision, headers);
	}
	
	public void step(SimState state) {
		super.step(state);
		triggerMutation((Environment) state);
		triggerAttenuationFamiliarity((Environment) state);
		System.out.println("Number of Agents in enviroment: " +((Environment)state).AgentCollection.size());
	}
	
	// call the attenuation familiarity function
	public void triggerAttenuationFamiliarity (Environment state) {
			state.initializeAttenuations();
			//DEBUG PRINT
			for (double[] row : state.FamiliarityArray) {
				System.out.println("Decayed Familiarity: ");
	            for (double num : row) {
	                System.out.print(num + " ");
	            }
	            System.out.println();
			}
		
		
	}
	
	public void triggerMutation(Environment state) {
		state.mutation();
		System.out.println("Mutated Connections Array:");
		for (int[] row : state.ConnectionsArray) {
			for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
		}

	}

}
