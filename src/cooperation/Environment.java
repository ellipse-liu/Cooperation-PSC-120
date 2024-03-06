package cooperation;

import sim.util.Bag;
import spaces.Spaces;
import sweep.SimStateSweep;
import sim.util.distribution.Normal;

public class Environment extends SimStateSweep {
	
	//Experiment Parameters
	double MutationRate = 0.1; //chance of a bond mutating
	int NumAgents = 50; //number of agents in the simulation
	double TypeOneProportion = 0.5; //proportion of Type One Agents in the simulation
	int MinDistance = 5; // minimum spawning distance
	int WithinFamiliarity = 50; // THESE ARE MAXIMUMS WE NEED TO GENERATE INITIAL WF BF ACCORDING TO NORMAL DISTRIBUTION
	int BetweenFamiliarity = 10; //THESE ARE MAXIMUMS
	
	//update according to paper, scalar around 0, 1, so assigned scalars in fam array
	double iwfmean = 0;
	double iwfstd = 1;
	double ibfmean = 0;
	double ibfstd = 1;
	
	int maxConnections = 3; //num connections the agent can have
	double BetweenCost = 0.2;
	double WithinCost = 0.1;
	double beta = 0.1;
	double alpha = 0.4;
	double famDecay = 0.1;
	
	double sociabilityMean = 0.3;
	double sociabilityStd = 0.05;
	
	//Space Parameters
	int GridWidth = 50;
	int GridHeight = 50;
	
	// Runtime variables
	public Bag AgentCollection = new Bag();
	double[][] FamiliarityArray;
	int[][] ConnectionsArray = new int[NumAgents][NumAgents];
	
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
		
		Normal norm = new Normal(sociabilityMean, sociabilityStd, random); //normal distribution generator for sociability
		
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
			
			double sociability = norm.nextDouble();
			
			Agent a = new Agent(random_x, random_y, i, type, sociability);
			System.out.println("Made agent: " + i);
			AgentCollection.add(a);
			schedule.scheduleRepeating(a);
			sparseSpace.setObjectLocation(a, random_x, random_y);
			
		}
	}
	
	public double[][] initializeFamiliarity(double[][]FamiliarityArray) {
		
		Normal wfnorm = new Normal(iwfmean, iwfstd, random);
		Normal bfnorm = new Normal(ibfmean, ibfstd, random);
		
		for(Object a : AgentCollection) {
			Agent b = (Agent) a;
			for (int i = 0; i < NumAgents; i++) {
				if (b.id == i) {
					continue;
				}
				if (b.type == ((Agent)AgentCollection.get(i)).type) {
					FamiliarityArray[b.id][i] = (0.2*wfnorm.nextDouble()) + 1.0;
				}
				else {
					FamiliarityArray[b.id][i] = (0.2*bfnorm.nextDouble() + 1.0);
				}
				
			}
		}
		
		//DEBUG PRINT
		for (double[] row : FamiliarityArray) {
            for (double num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
		}
		return (FamiliarityArray);
	}
	
	public double calculateFamiliarity(Agent a) {
		double SumFamiliarity = 0;
		for(int i = 0; i < NumAgents; i++) {
			if(a.id == i) {
				continue;
			}
			SumFamiliarity = SumFamiliarity + FamiliarityArray[a.id][i];
		}
		return SumFamiliarity;
	}
	
	public void placeAgent(Agent a, int x, int y) {
		this.sparseSpace.setObjectLocation(a, x, y);
	}
	
	public void update_connections(int a_id, int b_id, int pp_id) {
		ConnectionsArray[a_id][pp_id] = 1;
		if (b_id >= 0) {
			ConnectionsArray[a_id][b_id] = 0;
		}
	}
	
	public void start() {
		super.start();
		spaces = Spaces.SPARSE;
		this.make2DSpace(spaces, GridWidth, GridHeight);
		System.out.println("Made space");
		FamiliarityArray = new double[NumAgents][NumAgents];
		System.out.println("Made familiarity array");
		makeAgents();
		System.out.println("Made agents");
		FamiliarityArray = initializeFamiliarity(FamiliarityArray);
		System.out.println("Preset familiarity");
	}

	public double getMutationRate() {
		return MutationRate;
	}

	public void setMutationRate(double mutationRate) {
		MutationRate = mutationRate;
	}

	public int getNumAgents() {
		return NumAgents;
	}

	public void setNumAgents(int numAgents) {
		NumAgents = numAgents;
	}

	public double getTypeOneProportion() {
		return TypeOneProportion;
	}

	public void setTypeOneProportion(double typeOneProportion) {
		TypeOneProportion = typeOneProportion;
	}

	public int getMinDistance() {
		return MinDistance;
	}

	public void setMinDistance(int minDistance) {
		MinDistance = minDistance;
	}

	public int getWithinFamiliarity() {
		return WithinFamiliarity;
	}

	public void setWithinFamiliarity(int withinFamiliarity) {
		WithinFamiliarity = withinFamiliarity;
	}

	public int getBetweenFamiliarity() {
		return BetweenFamiliarity;
	}

	public void setBetweenFamiliarity(int betweenFamiliarity) {
		BetweenFamiliarity = betweenFamiliarity;
	}

	public int getGridWidth() {
		return GridWidth;
	}

	public void setGridWidth(int gridWidth) {
		GridWidth = gridWidth;
	}

	public int getGridHeight() {
		return GridHeight;
	}

	public void setGridHeight(int gridHeight) {
		GridHeight = gridHeight;
	}

}
