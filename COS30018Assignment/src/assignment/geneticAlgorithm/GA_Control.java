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
	private final int SAMPLE_SIZE = 100;
	private final int CROSSOVER_MINIMUM = 1;
	private final float MUTATION_CHANCE = 0.05f;
	private final int MAX_GENERATIONS = 1; //Must be at least 1
	private final float FITNESS_THRESHOLD = 100;
	
	private LinkedList<CarPreferenceData> list;
	private LinkedList<Schedule> population;
	private LinkedList<String> printBuffer = new LinkedList<String>();
	
	private Schedule currentSchedule = null, previousSchedule = null;
	private boolean scheduleReady = false;
	
	private Random random = new Random();
	private Control control;
	
	public String Setup(LinkedList<CarPreferenceData> list) {
		this.list = list;
		//random.randomize()??
		return "Genetic Algorithm Created";
	}
	
	public void Generate() {
		
		PrintToSystem("Genetic Algorithm: Generate Called");
		
		if (list.size() > 0) {
			previousSchedule = currentSchedule;
			scheduleReady = false; //Lets master scheduler know schedule is being calcualted
			
			GeneratePopulation(null); //Generate first population
			currentSchedule = population.getFirst(); //Get the highest fitness member as current schedule
			currentSchedule.OrderCarsByHours();
			control.UpdateCurrentSchedule(currentSchedule); //Send it to the control to be displayed
			
			int generations = 1;
			while (generations < MAX_GENERATIONS) { //TODO: Make function for below comment
				if (population.getFirst().fitness > FITNESS_THRESHOLD /* || more than half have converged on same schedule*/) { break;}
				
				GeneratePopulation(population); //Use existing list
				currentSchedule = population.getFirst(); //Get the highest fitness member as current schedule
				currentSchedule.OrderCarsByHours();
				control.UpdateCurrentSchedule(currentSchedule); //Send it to the control to be displayed
			}
			
			PrintToSystem("Genetic Algorithm: Schedule Ready To Use");
			scheduleReady = true; //Schedule ready to be used
			
		} else {
			PrintToSystem("Genetic Algorithm: Unable to Create List - No Cars");
			currentSchedule = null;	
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
		if (previous == null) {
			population = new LinkedList<Schedule>();
			
			//Create population
			for(int i = 0; i<SAMPLE_SIZE; i++) {
				population.add((CreateASchedule()));
			}
			
			//Sort population
			Collections.sort(population);
			
		} else {
			
			//Get rid of low fitness members
			for (int i = population.size(); i > Math.floor((population.size()/2)); i++) {
				population.remove(i);
			}

			//Sort populationSort population
			Collections.sort(population);
		}
	}
	
	private Schedule CreateASchedule() { return CreateASchedule(null, null); }
	
	private Schedule CreateASchedule(Schedule parent1, Schedule parent2) {
		Schedule s = new Schedule();
		
		if (parent1 != null || parent2 != null) {
			// Crossover and mutation
		} else {
			for (int i = 0; i < list.size(); i++) {
				CarPreferenceData car = list.get(i);
				
				CarSlot slot = new CarSlot();
				slot.name = car.agentName;
				slot.priority = i+1;
				slot.duration = car.durationRequested; //TODO: Change this depending on car, charge left, etc
				slot.startRequested = car.startTime;
				slot.finishRequired = car.finishTime;
				
				boolean spotTaken = false;
				if (list.size() == 1) {
					for (int c = 0; c < s.registeredCars.size(); c++) {
						CarSlot other = s.registeredCars.get(c);
											
						if (CheckClash(slot, slot.startRequested, other)) {spotTaken = true; break;}
					}
				}
				
				
				//If it can't fit at requested start, randomize the location
				if (spotTaken || list.size() > 1) {
				
					spotTaken = false;
					float randomTime = (random.nextFloat() * (slot.finishRequired-slot.duration-slot.startRequested)) + slot.startRequested;
					for (int c = 0; c < s.registeredCars.size(); c++) {
						CarSlot other = s.registeredCars.get(c);
											
						if (CheckClash(slot, slot.startRequested, other)) {spotTaken = true; break;}
					}
					
					//If it fits, add it. If it still can't fit it in, leave it
					if (!spotTaken) {
						slot.startTime = randomTime;
						s.registeredCars.add(slot);
					}
					
				//If it can fit it at the start it requested, then put it there	
				} else {
					slot.startTime = slot.startRequested;
					s.registeredCars.add(slot);
				}
				
			}
		}
		CalculateFitness(s);
		return s;
	}
	
	private boolean CheckClash(CarSlot n, float nStart, CarSlot other) {
		
		float start, duration, otherStart;
		if (other.startTime >= n.startRequested) {
			start = nStart;
			duration = n.duration;
			otherStart = other.startTime;
		} else {
			start = other.startTime;
			duration = other.duration;
			otherStart = nStart;
		}
		
		return (otherStart>=start && otherStart<=start+duration);
		
	}
	
	/**
	 * Fitness Function
	 * @param p
	 * @return
	 */
	private void CalculateFitness(Schedule p) {
		int max = list.size();
		int numberOfCars = p.registeredCars.size();
		int unusedHours = p.UnusedHours();
		int wastedFromRequestedStart = p.TimeFromRequested();
		
		//Fitness function
		p.fitness = (numberOfCars - unusedHours - wastedFromRequestedStart)/max;
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
