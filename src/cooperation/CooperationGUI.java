package cooperation;

import java.awt.Color;
import sweep.GUIStateSweep;
import sweep.SimStateSweep;
import spaces.Spaces;

public class CooperationGUI extends GUIStateSweep {

	public CooperationGUI(SimStateSweep state, int gridWidth, int gridHeight, Color backdrop, Color agentDefaultColor, boolean agentPortrayal) {
		super(state, gridWidth, gridHeight, backdrop, agentDefaultColor, agentPortrayal);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		CooperationGUI.initialize(Environment.class, Experimenter.class, CooperationGUI.class, 400, 400, Color.WHITE, Color.BLUE, true, Spaces.SPARSE);
	}

}
