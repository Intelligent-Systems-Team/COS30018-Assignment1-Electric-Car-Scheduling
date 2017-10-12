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
	private final float MUTATION_CHANCE = 0.21f;
	private final int MAX_GENERATIONS = 12; //Must be at least 1
	private final float FITNESS_THRESHOLD = 0.95f;
	private final int NUMBER_OF_STATIONS = 4;
	
	
	private LinkedList<CarPreferenceData> listOfCarPrefData;
	private LinkedList<Schedule> population;
	private LinkedList<String> printBuffer = new LinkedList<String>();
	
	private Schedule currentSchedule = null, previousSchedule = null;
	private boolean scheduleReady = false;
	
	private Random random = new Random();
	private Control control;

	private boolean firstAtStartRequested = false;

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
			currentSchedule = GetHighestSchedule(population); //Get the highest fitness member as current schedule
			control.UpdateCurrentSchedule(currentSchedule); //Send it to the control to be displayed
			
			int generations = 1;
			while (generations < MAX_GENERATIONS) { //TODO: Make function for below comment
				// @Debug System.out.println("***********************************");
				// @Debug System.out.println("Debug -- current generation: " + generations);
				// @Debug System.out.println("***********************************");
				
				GeneratePopulation(population); //Use existing list
				currentSchedule = GetHighestSchedule(population); //Get the highest fitness member as current schedule
				control.UpdateCurrentSchedule(currentSchedule); //Send it to the control to be displayed
				// @Debug System.out.println("currentSchedule.TimeFromRequested: " + currentSchedule.TimeFromRequested());
				
				if (currentSchedule.fitness > FITNESS_THRESHOLD /* || more than half have converged on same schedule*/) { break;}
				
				generations++;
			}
			
			for (int i = 0; i < population.size(); i++) {
				// @Debug System.out.println("element " + i + ": " + population.get(i).fitness);
				
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
				if (population.get(i).NumberOfCars() <= 0) {
					population.remove(i);
				}	
			}	
			
			// @Debug System.out.println("1- population.size() = " + population.size());
			
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
				if (s.NumberOfCars() > 0) { 
					newPop.add(s);
					//System.out.println("Debug -- Elite added with fitness of: " + s.fitness);
				}
			} 

			//System.out.println("Debug -- Elites added, creating new schedules");
			
			//Create new schedules
			// @Debug System.out.println("1a - Creating new schedules");
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
					
					// @Debug System.out.println("count:" + count + ", values = " + a + " | " + b);
					
					float r2 = random.nextFloat();
					parents[count] = (r2 < 0.7)?((a.fitness>b.fitness)?a:b):((a.fitness>b.fitness)?b:a); //Tournament Selection
					
					count++;
				}
				
				//System.out.println("-------");
				//System.out.println("parents[0]" + parents[0] + ", parents[1]" + parents[1]);
				//System.out.println("-------");
				
				newPop.add(CreateASchedule(parents[0],parents[1]));
				
			}
			
			// @Debug System.out.println("Population generated for this generation");
			population = newPop;
		}
	}
	
	private Schedule CreateASchedule() { return CreateASchedule(null, null); }
	
	private Schedule CreateASchedule(Schedule parent1, Schedule parent2) {
		Schedule s = new Schedule(NUMBER_OF_STATIONS);
		
		//Schedule with parents
		if (parent1 != null && parent2 != null) {

			//****************
			//'Crossover'
			//****************
			
			// @Debug System.out.println("2a-Crossover schedule with parents");
			
			Schedule schedule = new Schedule(NUMBER_OF_STATIONS);
			
			for (int t = 0; t < NUMBER_OF_STATIONS; t++) {
				StationSlot stationA = parent1.stations.get(t);
				StationSlot stationB = parent2.stations.get(t);
				StationSlot newScheduleStation = schedule.stations.get(t);
				
				//Adds cars from parent 1 to new schedule
				for (int i = 0; i < stationA.registeredCars.size(); i++) {
					newScheduleStation.registeredCars.add(stationA.registeredCars.get(i).Clone());
				}
				
				//Adds cars from parent 2 (if they don't already exist in the schedule, and can fit)
				for (int i = 0; i < stationB.registeredCars.size(); i++) {
					CarSlot car = stationB.registeredCars.get(i);
					boolean canFit = true;
					
				
					for (int c = 0; c < schedule.stations.get(t).registeredCars.size(); c++) {
						CarSlot other = newScheduleStation.registeredCars.get(c);
						if (newScheduleStation.registeredCars.contains(other) || CheckClash(car, car.startTime, other)) {
							canFit = false;
							break;
						}
					}
					
					if (canFit) {
						newScheduleStation.registeredCars.add(car.Clone());
					}
				}
				
				//****************
				//Mutation Next
				//****************
				
				float chance = MUTATION_CHANCE*100;
				
				for (int i = 0; i < newScheduleStation.registeredCars.size(); i++) {
					int r = random.nextInt(100);
					
					if (r<=chance) {
						// @Debug System.out.println("3a- Schedule mutating");
						CarSlot car = newScheduleStation.registeredCars.get(i);
						float moveHours = ((float)(random.nextInt(6)));
						
						if ((moveHours < 0 && (car.startTime+moveHours >= car.startRequested))
								|| (moveHours > 0 && (car.startTime+car.duration+moveHours <= car.finishRequired))) {
							
							boolean spotTaken = true;
							while(spotTaken) {
								
								spotTaken = false;
								for (int t2 = 0; t2 < newScheduleStation.registeredCars.size(); t2++) {
									CarSlot test = newScheduleStation.registeredCars.get(t2);
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
								// @Debug System.out.println("Mutation error");
							}
							
						}
						
						// @Debug System.out.println("3b- Schedule mutated");
					}
				}
				
				// @Debug System.out.println("2b- Schedule crossedover");
				s = schedule; //schedule is the returned schedule
			
			}
			
		//New Schedule
		} else {
			for (int i = 0; i < listOfCarPrefData.size(); i++) {
				CarSlot slot = CarSlotFromData(i);
				
				StationSlot station = s.stations.get(0);
				
				if (firstAtStartRequested == false) 
				{
					firstAtStartRequested = true;

					boolean check = false;
					for (int e = 0; e < station.registeredCars.size(); e++) 
					{
						CarSlot other = station.registeredCars.get(e);
											
						if (CheckClash(slot, slot.startRequested, other)) 
						{
							check = true; 
							break;
						}
					}

					if (!check) // If no clash, register at startRequested
					{
						slot.startTime = slot.startRequested;
						station.registeredCars.add(slot);
					}
					else //If clash, register at random time
					{
						int count = 0;
						while(!TryAddCarToSchedule(s, slot) && count ==10) {
							count++;
						}
					}
				} 
				else
				{
					int chance = random.nextInt(5);
					boolean check = true;
					
					//Chance it'll try the earliest time instead of random time
					if (chance == 1) 
					{
						//Check if clash will occur
						check = false;
						for (int e = 0; e < station.registeredCars.size(); e++) 
						{
							CarSlot other = station.registeredCars.get(e);
												
							if (CheckClash(slot, slot.startRequested, other)) 
							{
								check = true; 
								break;
							}
						}

						if (!check) //If no clash, register at startTime
						{
							slot.startTime = slot.startRequested;
							station.registeredCars.add(slot);
						}
					}
					
					if (check) {
						//Try to add the car to a random location
						int count = 0;
						while(!TryAddCarToSchedule(s, slot) && count ==10) {
							count++;
						}
					}
				}
			}
		}
		
		s.OrderCarsByHours();
		CalculateFitness(s);
		// @Debug System.out.println("1b - Fitness Calculated");
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
		
		float padding = 0; //Minimum space between cars
		
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
		
		return (middleTest>=(start-padding) && middleTest<=(end+padding));
		
	}
	
	/**
	 * Fitness Function
	 * @param p
	 * @return
	 */
	private void CalculateFitness(Schedule p) {
		float max = listOfCarPrefData.size();
		float numberOfCars = p.NumberOfCars();
		float unusedHours = p.UnusedHours();
		float wastedFromRequestedStart = p.TimeFromRequested();
		
		//Fitness function
		//float fit = ((numberOfCars - unusedHours - wastedFromRequestedStart)/max);
		float fit = (numberOfCars - 0.1f*unusedHours - 0.5f*wastedFromRequestedStart)/max;
		
		if (fit > 1) {
			// @Debug System.out.println(max + ", " + numberOfCars + ", " + unusedHours + ", " + wastedFromRequestedStart);
		}
		
		p.fitness = fit;
	}
	
	private boolean TryAddCarToSchedule(Schedule s, CarSlot c) {
		float randomTime = (random.nextFloat() * (c.finishRequired-c.duration-c.startRequested)) + c.startRequested;
		boolean spotTaken = false;
		
		// @Debug System.out.println("c"+c.priority + " randomTime = " + randomTime);
		
		if (randomTime < c.startRequested) {
			randomTime = (random.nextFloat() * (c.finishRequired-c.duration-c.startRequested)) + c.startRequested;
		}
		
		//Time intervals are in 30mins atm
		// @Debug  System.out.println("TryAddCarToSchedule: randomTime = " + randomTime);
		randomTime = SnapToTime(randomTime);
		
		spotTaken = true;
		int count = 0;
		
		//Checks if the car can fit into any of the stations, starting with station 1
		while(spotTaken && count < NUMBER_OF_STATIONS) {
			StationSlot currentStation = s.stations.get(count);
			
			for (int i = 0; i < currentStation.registeredCars.size(); i++) {
				CarSlot other = currentStation.registeredCars.get(i);
									
				if (CheckClash(c, randomTime, other)) {
					spotTaken = true; 
					break;
				}
			}
			
			//If it fits, add it. If it still can't fit it in, leave it
			if (!spotTaken) {
				c.startTime = randomTime;
				currentStation.registeredCars.add(c);
			} else {
				count++;
			}
		}
		return !spotTaken;
	}
	
	private float SnapToTime(float num) {
		int val = (int) Math.floor(60/INTERVAL_SNAP);
		float newNum =Math.round((num*val));
		
		//System.out.println("num = " + num);
		//System.out.println("val = " + val);
		//System.out.println("Math.round((num*val) = " + newNum);
		
		newNum = newNum/val;
		//System.out.println("newNum/val = " + newNum);
		
		return newNum;
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
