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
	}
	
	// call the attenuation familiarity function
	public void triggerAttenuationFamiliarity (Environment state) {
			state.initializeAttenuations(state.FamiliarityArray);
			//DEBUG PRINT
			for (double[] row : state.FamiliarityArray) {
	            for (double num : row) {
	            	System.out.println("Decayed Familiarity");
	                System.out.print(num + " ");
	            }
	            System.out.println();
			}
		
		
	}

	public void triggerMutation(Environment state) {
		state.mutation(state.ConnectionsArray);
		for (int[] row : state.ConnectionsArray) {
			for (int num : row) {
            	System.out.println("Mutated Connections Array:");
                System.out.print(num + " ");
            }
            System.out.println();
		}

	}
}
