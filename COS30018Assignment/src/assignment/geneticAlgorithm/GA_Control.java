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
	//NEEDS CONFIGURATION FILE
	//***********************
	//***********************
	//***********************
	//***********************
	//***********************
	
	//Constants
	private final int SAMPLE_SIZE = 100;
	private final float CROSSOVER_CHANCE = 0.8f;
	private final float MUTATION_CHANCE = 0.05f;
	
	private LinkedList<CarPreferenceData> list;
	private LinkedList<Schedule> population;
	
	public void Setup(LinkedList<CarPreferenceData> list) {
		this.list = list;
	}
	
	public Schedule Generate() {
		Schedule schedule = new Schedule();
		
		return schedule;
	}
	
	private void GeneratePopulation(LinkedList<Schedule> previous) {
		if (previous == null) {
			population = new LinkedList<Schedule>();
			for(int i = 0; i<SAMPLE_SIZE; i++) {
				population.add((CreateASchedule()));
			}
			
		} else {
			Collections.sort(population);
		}
	}
	
	private Schedule CreateASchedule() {
		Schedule s = new Schedule();
		
		//Build/Randomize schedule
		
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
