package assignment.geneticAlgorithm;

import java.util.Collections;
import java.util.LinkedList;

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
	
	public String Setup(LinkedList<CarPreferenceData> list) {
		this.list = list;
		return "Genetic Algorithm Created";
	}
	
	public void Generate() {
		previousSchedule = currentSchedule;
		//Calculate
		
		//if (one type of schedule has majority (50%+)) then set it as schedule

		//currentSchedule = population.get(??);
		//scheduleReady = true;
	}
	
	/**
	 * Restores schedule to what is was before the last 'generate' was called
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
			// Build/Randomize schedule
		}
		
		return s;
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
