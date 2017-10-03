package assignment.geneticAlgorithm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import assignment.main.CarPreferenceData;

public class GA_Control {

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
	private final float CROSSOVER_AMOUNT = 0.8f;
	private final float MUTATION_CHANCE = 0.05f;
	
	private LinkedList<CarPreferenceData> list;
	private LinkedList<Schedule> population;
	
	private Schedule currentSchedule = null, previousSchedule = null;
	private boolean scheduleReady = false;
	
	private Random random = new Random();
	
	public String Setup(LinkedList<CarPreferenceData> list) {
		this.list = list;
		//random.randomize()??
		return "Genetic Algorithm Created";
	}
	
	public void Generate() {
		previousSchedule = currentSchedule;
		
		GeneratePopulation(null);
		//Calculate
		
		//if (one type of schedule has majority (50%+)) then set it as schedule

		//currentSchedule = population.get(??);
		//scheduleReady = true;
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
			
		} else {
			//Sort population, get rid of low fitness members
			Collections.sort(population);
			for (int i = population.size(); i > Math.floor((population.size()/2)); i++) {
				population.remove(i);
			}
		}
	}
	
	private Schedule CreateASchedule() { return CreateASchedule(null, null); }
	
	private Schedule CreateASchedule(Schedule parent1, Schedule parent2) {
		Schedule s = new Schedule();
		
		if (parent1 == null || parent2 == null) {
			// Crossover and mutation
		} else {
			for (int i = 0; i < list.size(); i++) {
				CarPreferenceData car = list.get(i);
				
				CarSlot slot = new CarSlot();
				slot.name = car.agentName;
				slot.priority = i+1;
				slot.duration = 1; //TODO: Change this depending on car, charge left, etc
				slot.startRequested = car.startTime;
				slot.finishRequired = car.finishTime;
				
				boolean spotTaken = false;
				for (int c = 0; c < s.registeredCars.size(); c++) {
					CarSlot other = s.registeredCars.get(c);
										
					if (CheckClash(slot, slot.startRequested, other)) {spotTaken = true; break;}
				}
				
				
				//If it can't fit at requested start, randomize the location
				if (spotTaken) {
				
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
	private float CalculateFitness(Schedule p) {
		int max = list.size();
		int numberOfCars = p.registeredCars.size();
		int unusedHours = p.UnusedHours();
		int wastedFromRequestedStart = p.TimeFromRequested();
		
		//Fitness function
		return (numberOfCars - unusedHours - wastedFromRequestedStart)/max;
	}
}
