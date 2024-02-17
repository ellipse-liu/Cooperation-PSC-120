package cooperation;

import sim.util.Bag;
import spaces.Spaces;
import sweep.SimStateSweep;

public class Environment extends SimStateSweep {
	
	//Experiment Parameters
	double MutationRate = 0.1; //chance of a bond mutating
	int NumAgents = 50; //number of agents in the simulation
	double TypeOneProportion = 0.5; //proportion of Type One Agents in the simulation
	int MinDistance = 5; // minimum spawning distance
	int WithinFamiliarity = 50;
	int BetweenFamiliarity = 10;
	
	//Space Parameters
	int GridWidth = 500;
	int GridHeight = 500;
	
	// Runtime variables
	public Bag AgentCollection = new Bag();
	int[][] FamiliarityArray;
	
	public Environment(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}

	public Environment(long seed, Class observer) {
		super(seed, observer);
		// TODO Auto-generated constructor stub
	}

	public Environment(long seed, Class observer, String runTimeFileName) {
		super(seed, observer, runTimeFileName);
		// TODO Auto-generated constructor stub
	}
	
	public void makeAgents() {
		for (int i = 0; i < NumAgents; i++) {
			
			int random_x = random.nextInt(GridWidth);
			int random_y = random.nextInt(GridHeight);
			
			Bag b = sparseSpace.getMooreNeighbors(random_x, random_y, MinDistance, sparseSpace.BOUNDED, false);
			
			while(!b.isEmpty()) {
				random_x = random.nextInt(GridWidth);
				random_y = random.nextInt(GridHeight);
				b = sparseSpace.getMooreNeighbors(random_x, random_y, MinDistance, sparseSpace.BOUNDED, false);
			}
			
			int type = random.nextBoolean(TypeOneProportion) ? 1:2;
			
			Agent a = new Agent(random_x, random_y, i, type);
			System.out.println("Made agent: " + i);
			sparseSpace.setObjectLocation(a, random_x, random_y);
			AgentCollection.add(a);
			schedule.scheduleRepeating(a);
		}
	}
	
	public void initializeFamiliarity() {
		for(Object a : AgentCollection) {
			Agent b = (Agent) a;
			for (int i = 0; i < NumAgents; i++) {
				if (b.id == i + 1) {
					continue;
				}
				if (b.type == ((Agent)AgentCollection.get(i)).type) {
					FamiliarityArray[b.id][i] = WithinFamiliarity;
				}
				else {
					FamiliarityArray[b.id][i] = BetweenFamiliarity;
				}
				
			}
		}
	}
	
	public void calculateFamiliarity(Agent a) {
		int SumFamiliarity = 0;
		for(int i = 0; i < NumAgents; i++) {
			if(a.id == i) {
				continue;
			}
			SumFamiliarity = SumFamiliarity + FamiliarityArray[a.id][i];
		}
	}
	
	public void start() {
		super.start();
		spaces = Spaces.SPARSE;
		make2DSpace(spaces, GridWidth, GridHeight);
		System.out.println("Made space");
		FamiliarityArray = new int[NumAgents][NumAgents];
		System.out.println("Made familiarity array");
		makeAgents();
		System.out.println("Made agents");
		initializeFamiliarity();
		System.out.println("Preset familiarity");
	}

}
