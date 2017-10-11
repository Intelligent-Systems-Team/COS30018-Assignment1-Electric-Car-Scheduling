package assignment.geneticAlgorithm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import assignment.agents.AgentInteraction;
import assignment.main.CarPreferenceData;
import assignment.main.Control;
import jade.core.behaviours.Behaviour;

public class GA_Control implements AgentInteraction{

	//***********************
	//***********************
	//***********************
	//***********************
	//***********************
	//NEEDS CONFIGURATION FILE??
	//***********************
	//***********************
	//***********************
	//***********************
	//***********************
	
	//Constants
	private final int INTERVAL_SNAP = 30; //Interval time to snap to (e.g. 30 = 30 minute interval)
	private final int SAMPLE_SIZE = 1000;
	private final int NUM_ELITES = 2;
	private final float MUTATION_CHANCE = 0.1f;
	private final int MAX_GENERATIONS = 10; //Must be at least 1
	private final float FITNESS_THRESHOLD = 0.5f;
	
	private LinkedList<CarPreferenceData> listOfCarPrefData;
	private LinkedList<Schedule> population;
	private LinkedList<String> printBuffer = new LinkedList<String>();
	
	private Schedule currentSchedule = null, previousSchedule = null;
	private boolean scheduleReady = false;
	
	private Random random = new Random();
	private Control control;
	
	public String Setup(LinkedList<CarPreferenceData> list) {
		this.listOfCarPrefData = list;
		//random.randomize()??
		return "Genetic Algorithm Created";
	}
	
	public void Generate() {
		
		PrintToSystem("Genetic Algorithm: Generate Called");
		
		if (listOfCarPrefData.size() > 0) {
			previousSchedule = currentSchedule;
			scheduleReady = false; //Lets master scheduler know schedule is being calcualted
			
			GeneratePopulation(null); //Generate first population
			currentSchedule = population.getFirst(); //Get the highest fitness member as current schedule
			control.UpdateCurrentSchedule(currentSchedule); //Send it to the control to be displayed
			
			int generations = 1;
			while (generations < MAX_GENERATIONS) { //TODO: Make function for below comment
				System.out.println("***********************************");
				System.out.println("Debug -- current generation: " + generations);
				System.out.println("***********************************");
				if (population.getFirst().fitness > FITNESS_THRESHOLD /* || more than half have converged on same schedule*/) { break;}
				
				GeneratePopulation(population); //Use existing list
				currentSchedule = GetHighestSchedule(population); //Get the highest fitness member as current schedule
				control.UpdateCurrentSchedule(currentSchedule); //Send it to the control to be displayed
				
				generations++;
			}
			
			for (int i = 0; i < population.size(); i++) {
				System.out.println("element " + i + ": " + population.get(i).fitness);
				
				if (i != population.size()-1) {
					Schedule a = population.get(i);
					Schedule b = population.get(i+1);
					
					/*
					if (a.fitness == b.fitness && a.fitness != 0 && b.fitness != 1) {
						System.out.println("Debug -- Same fitness?");
					}
					*/
				}
			}
			
			PrintToSystem("Genetic Algorithm: Schedule Ready To Use");
			scheduleReady = true; //Schedule ready to be used
		
			
		} else {
			PrintToSystem("Genetic Algorithm: Unable to Create List - No Cars");
			currentSchedule = null;	
		}
	}
	
	private Schedule GetHighestSchedule(LinkedList<Schedule> p) {
		
		if (p.size() > 0) {
			Schedule highest = p.getFirst();
			float fit = highest.fitness;
			
			for (int i = 1; i < p.size(); i++) {
				Schedule test = p.get(i);
				
				if (test.fitness > fit) {
					highest = test;
					fit = highest.fitness;
				}
			}
			
			return highest;
			
		} else {
			return null;
		}
	}

	/**
	 * Restores schedule to what is was before the last Generate() was called
	 */
	public void RestoreSchedule() {
		currentSchedule = previousSchedule;
	}
	
	public Schedule GetCurrentSchedule() { 
		return scheduleReady?currentSchedule:null; //If the current schedule isn't finished, send a null schedule
	}
	
	private void GeneratePopulation(LinkedList<Schedule> previous) {
			//Original generation
		if (previous == null) {
			population = new LinkedList<Schedule>();
			
			int size = SAMPLE_SIZE;
			if (previousSchedule != null) {
				size -= 1;
				CalculateFitness(previousSchedule);
				population.add(previousSchedule);
			}
			
			//Create population
			for(int i = 0; i<size; i++) {
				population.add((CreateASchedule()));
			}
		
			//Cross-over & Mutation (all following generations)
		} else {
			//System.out.println("0- population.size() = " + population.size());
			
			
			float t = 0;
			for (int i = 0; i < population.size(); i++) {
				t += population.get(i).fitness;
			}
			float average = t/population.size();
			
			//Get rid of low fitness members below the average
			t = 0;
			for (int i = 0; i < population.size(); i++) {
				Schedule s = population.get(i);
				if (s.fitness < average) {
					population.remove(s);
					t++;
				}
				if (t > SAMPLE_SIZE/2) {break;}
			}
			
			//Get rid of schedules with no cars
			for (int i = population.size()-1; i >= 0; i--) {
				if (population.get(i).registeredCars.size() <= 0) {
					population.remove(i);
				}	
			}	
			
			System.out.println("1- population.size() = " + population.size());
			
			for (int i = 0; i < population.size(); i++) {
				Schedule secondChance = population.get(i);
				
				for (int c = 0; c < listOfCarPrefData.size(); c++) {
					CarSlot test = CarSlotFromData(c);
					
					if (secondChance.CarExist(test.name) == false) {
						TryAddCarToSchedule(secondChance, test); //Adds any new cars if they can fit 
					}
				}
			}
			
			LinkedList<Schedule> newPop = new LinkedList<Schedule>();
			//Add elites to new population
			for (int i = 0; i < NUM_ELITES; i++) {
				if (i >= population.size()) {break;}
				
				Schedule s = population.get(i);
				if (s.registeredCars.size() > 0) { 
					newPop.add(s);
					//System.out.println("Debug -- Elite added with fitness of: " + s.fitness);
				}
			} 

			//System.out.println("Debug -- Elites added, creating new schedules");
			
			//Create new schedules
			System.out.println("1a - Creating new schedules");
			while (newPop.size() < SAMPLE_SIZE) {
				Schedule[] parents = new Schedule[2];
				int count = 0;
				
				while (count < 2) {
					Collections.shuffle(population);
					int r = random.nextInt(population.size());
					Schedule a = population.get(r);
					
					Schedule b = a;
					while (b==a) {
						r = random.nextInt(population.size());
						b = population.get(r);
					}
					
					System.out.println("count:" + count + ", values = " + a + " | " + b);
					
					float r2 = random.nextFloat();
					parents[count] = (r2 < 0.7)?((a.fitness>b.fitness)?a:b):((a.fitness>b.fitness)?b:a); //Tournament Selection
					
					count++;
				}
				
				//System.out.println("-------");
				//System.out.println("parents[0]" + parents[0] + ", parents[1]" + parents[1]);
				//System.out.println("-------");
				
				newPop.add(CreateASchedule(parents[0],parents[1]));
				
			}
			
			System.out.println("Population generated for this generation");
			population = newPop;
		}
	}
	
	private Schedule CreateASchedule() { return CreateASchedule(null, null); }
	
	private Schedule CreateASchedule(Schedule parent1, Schedule parent2) {
		Schedule s = new Schedule();
		
		//Schedule with parents
		if (parent1 != null && parent2 != null) {

			//****************
			//'Crossover'
			//****************
			
			System.out.println("2a-Crossover schedule with parents");
			
			Schedule schedule = new Schedule();
			
			//Adds cars from parent 1 to new schedule
			for (int i = 0; i < parent1.registeredCars.size(); i++) {
				schedule.registeredCars.add(parent1.registeredCars.get(i));
			}
			
			//Adds cars from parent 2 (if they don't already exist in the schedule, and can fit)
			for (int i = 0; i < parent2.registeredCars.size(); i++) {
				CarSlot car = parent2.registeredCars.get(i);
				boolean canFit = true;
				
				for (int c = 0; c < schedule.registeredCars.size(); c++) {
					CarSlot other = schedule.registeredCars.get(c);
					if (schedule.registeredCars.contains(other) || CheckClash(car, car.startTime, other)) {
						carFit = false;
						break;
					}
				}
				
				if (canFit) {
					schedule.registeredCars.add(car);
				}
			}
			
			//****************
			//Mutation Next
			//****************
			
			float chance = MUTATION_CHANCE*100;
			
			for (int i = 0; i < a.registeredCars.size(); i++) {
				int r = random.nextInt(100);
				
				if (r<=chance) {
					System.out.println("3a- Schedule mutating");
					CarSlot car = schedule.registeredCars.get(i);
					float moveHours = ((float)(random.nextInt(6)));
					
					if ((moveHours < 0 && (car.startTime+moveHours >= car.startRequested))
							|| (moveHours > 0 && (car.startTime+car.duration+moveHours <= car.finishRequired))) {
						
						/*
						System.out.println("car.startTime = " + car.startTime + "moveHours = " + moveHours);
						if ((moveHours < 0 && (car.startTime+moveHours >= car.startRequested))){
							System.out.println("moveHours < 0");
							System.out.println(car.startTime+moveHours + ">=" + car.startRequested);
						}
						*/
						
						boolean spotTaken = true;
						while(spotTaken) {
							
							spotTaken = false;
							for (int t = 0; t < schedule.registeredCars.size(); t++) {
								CarSlot test = schedule.registeredCars.get(t);
								if (car!=test && CheckClash(car, car.startTime+moveHours, test)) {
									spotTaken = true;
									break;
								}
							}
							
							if (spotTaken) {moveHours *= 0.5;} //Half move hours if jump was too big
							if (Math.abs(moveHours) <0.1) {break;} //Break when moveHours gets too close to 0
						}
						
						if (!spotTaken) {
							car.startTime = SnapToTime(car.startTime+moveHours); //Mutate start time
						}
						
						
						if (car.startTime < car.startRequested) {
							System.out.println("Mutation error");
						}
						
					}
					
					System.out.println("3b- Schedule mutated");
				}
			}
			
			System.out.println("2b- Schedule crossedover");
			s = schedule; //schedule is the returned schedule
			
			
		//*************
		//New Schedule
		//*************
		} else {
			for (int i = 0; i < listOfCarPrefData.size(); i++) {
				CarSlot slot = CarSlotFromData(i);
				
				if (listOfCarPrefData.size() == 1) {
					
					slot.startTime = slot.startRequested;
					s.registeredCars.add(slot);
					
				} else if (listOfCarPrefData.size() > 1) {
					
					int chance = random.nextInt(5);
					boolean check = true;
					
					//Chance it'll try the earliest time instead of random time
					if (chance == 1) {
						check = false;
						for (int e = 0; e < s.registeredCars.size(); e++) {
							CarSlot other = s.registeredCars.get(e);
												
							if (CheckClash(slot, slot.startRequested, other)) {
								check = true; 
								break;
							}
						}
						if (!check) {
							slot.startTime = slot.startRequested;
							s.registeredCars.add(slot);
						}
					}
					
					if (check) {
						//Try to add the car to a random location
						TryAddCarToSchedule(s, slot);
					}
				}
			}
		}
		
		s.OrderCarsByHours();
		//System.out.println("1a - Calculating Fitness");
		CalculateFitness(s);
		//System.out.println("1b - Fitness Calculated");
		return s;
	}
	
	private CarSlot CarSlotFromData(int i) {
		CarPreferenceData data = listOfCarPrefData.get(i);
		CarSlot slot = new CarSlot();
		
		slot.name = data.agentName;
		slot.priority = i+1;
		slot.duration = data.durationRequested; //TODO: Change this depending on car, charge left, etc
		slot.startRequested = data.startTime;
		slot.finishRequired = data.finishTime;
		
		return slot;
	}
	
	//Checks if a car lies within the duration of another car
	private boolean CheckClash(CarSlot n, float request, CarSlot other) {
		// TODO CheckClash Needs Work
		float start, end, middleTest;
		if (other.startTime >= request) {
			start = request;
			end = request + n.duration;
			middleTest = other.startTime;
		} else {
			start = other.startTime;
			end = other.startTime + other.duration;
			middleTest = request;
		}
		
		return (middleTest>=(start-0.1) && middleTest<=(end+0.1));
		
	}
	
	/**
	 * Fitness Function
	 * @param p
	 * @return
	 */
	private void CalculateFitness(Schedule p) {
		float max = listOfCarPrefData.size();
		float numberOfCars = p.registeredCars.size();
		float unusedHours = p.UnusedHours();
		float wastedFromRequestedStart = p.TimeFromRequested();
		
		//Fitness function
		float fit = (numberOfCars - unusedHours - 0.1f*wastedFromRequestedStart)/max;
		
		if (fit > 1) {
			System.out.println(max + ", " + numberOfCars + ", " + unusedHours + ", " + wastedFromRequestedStart);
		}
		
		p.fitness = fit;
	}
	
	private void TryAddCarToSchedule(Schedule s, CarSlot c) {
		float randomTime = (random.nextFloat() * (c.finishRequired-c.duration-c.startRequested)) + c.startRequested;
		boolean spotTaken = false;
		
		//System.out.println("c"+c.priority + " randomTime = " + randomTime);
		
		if (randomTime < c.startRequested) {
			randomTime = (random.nextFloat() * (c.finishRequired-c.duration-c.startRequested)) + c.startRequested;
		}
		
		//Time intervals are in 30mins atm
		randomTime = SnapToTime(randomTime);
		
		for (int i = 0; i < s.registeredCars.size(); i++) {
			CarSlot other = s.registeredCars.get(i);
								
			if (CheckClash(c, randomTime, other)) {
				spotTaken = true; 
				break;
			}
		}
		
		//If it fits, add it. If it still can't fit it in, leave it
		if (!spotTaken) {
			c.startTime = randomTime;
			s.registeredCars.add(c);
		}
	}
	
	private float SnapToTime(float num) {
		int val = (int) Math.floor(60/INTERVAL_SNAP);
		return Math.round((num*(val)))/(val);
	}
	
	@Override
	public void RegisterControl(Control c) {
		control = c;
		PrintToSystem("");
	}

	@Override
	public void AddBehaviour(Behaviour b) {}

	@Override
	public void PrintToSystem(String s) {
		System.out.println(s);
		if (control == null) {
			if (s!="") { printBuffer.add(s); }
		} else {
			//Adds any buffered messages first
			for (int count = 0; count < printBuffer.size(); count++) {
				control.AddLastMessage(printBuffer.get(count));
			}
			printBuffer.clear();
			
			//Adds latest message
			if (s!="") { control.AddLastMessage(s); }
		}
	}

	@Override
	public String AgentName() {
		return "Genetic Algorithm";
	}
}
