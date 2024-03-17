package cooperation;

import sim.util.Bag;
import spaces.Spaces;
import sweep.SimStateSweep;
import sim.util.distribution.Normal;
import java.lang.Math;
import java.util.Random;

public class Environment extends SimStateSweep {
	
	//Experiment Parameters
	double MutationRate = 0.1; //chance of a bond mutating
	int NumAgents = 6; //number of agents in the simulation
	double TypeOneProportion = 0.5; //proportion of Type One Agents in the simulation
	int MinDistance = 5; // minimum spawning distance
	int WithinFamiliarity = 50; // scalar
	int BetweenFamiliarity = 10; // scalar
	
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
	double attenuationRate = 0.01;
	
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
			Random r = new Random();
			double cultureValue = 100 + (100 - 0) * r.nextDouble();
			double sociability = norm.nextDouble();
			
			Agent a = new Agent(random_x, random_y, i, type, cultureValue, sociability);
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
					FamiliarityArray[b.id][i] = ((0.2*wfnorm.nextDouble()) + 1.0) * WithinFamiliarity;
				}
				else {
					FamiliarityArray[b.id][i] = ((0.2*bfnorm.nextDouble() + 1.0)) * BetweenFamiliarity;
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
	
	//need to do the experimenter to step through this function because there is no step function in environment.
	// subtracts attenuationRate from familiarity array
	public void initializeAttenuations() {
		for(Object a : AgentCollection) {
			Agent b = (Agent) a;
			for (int i = 0; i < NumAgents; i++) {
				// attenuation is capped at 0
				if (FamiliarityArray[b.id][i] == 0) {
					continue;
				}
				// if attenuation is less than 0.01 make it 0
				else if (FamiliarityArray[b.id][i] < 0.01) {
					FamiliarityArray[b.id][i] = 0;
				} else {
					FamiliarityArray [b.id][i] -= attenuationRate;
				}
			}
		}

	}
    public void mutation(){
        for (int i = 0; i < NumAgents; i++) {
            for (int j = 0; j < NumAgents; j++) {
                if (random.nextBoolean(MutationRate)) {
                	if(i!=j) {
	                    if (ConnectionsArray[i][j] == 0) {
	                        ConnectionsArray[i][j] = 1;
	                        Agent agentA = (Agent) AgentCollection.get(i);
	                        Agent agentB = (Agent) AgentCollection.get(j);
	                        agentA.addConnection(agentB, this);
	                        //agentA.connections.add(agentB);
	                        agentA.move(this);
	                        System.out.println("Mutated added at " + i + "," + j);
	                    } else {
	                        ConnectionsArray[i][j] = 0;
	                        Agent agentA = (Agent) AgentCollection.get(i);
	                        Agent agentB = (Agent) AgentCollection.get(j);
	                        //agentA.removeConnection(agentB, this);
	                        agentA.connections.remove(agentB);
	                        agentA.move(this);
	                        System.out.println("Mutated removed at " + i + "," + j);
	                    }
                	}
                }
            }
        }
       
    }
	
	// add experimenter to the start
	public void start() {
		super.start();
		spaces = Spaces.SPARSE;
		this.makeSpace(GridWidth, GridHeight);
		System.out.println("Made space");
		FamiliarityArray = new double[NumAgents][NumAgents];
		System.out.println("Made familiarity array");
		makeAgents();
		// initialize the experimenter by calling initialize in the parent class
		if(observer != null) {
			observer.initialize(sparseSpace, spaces);
		}
		System.out.println("Made agents");
		FamiliarityArray = initializeFamiliarity(FamiliarityArray);
		System.out.println("Preset familiarity");
	}
	
	//Increase familiarity for agent i and j upon meeting
	public void reinforce(int agentIId, int agentJId) {
	    double currentFamiliarityIJ = FamiliarityArray[agentIId][agentJId];
	    double currentFamiliarityJI = FamiliarityArray[agentJId][agentIId];

	    FamiliarityArray[agentIId][agentJId] = Math.min((1.04 * currentFamiliarityIJ), 100); //Set a cap on familiarity
	    FamiliarityArray[agentJId][agentIId] = Math.min((1.04 * currentFamiliarityJI), 100);
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
