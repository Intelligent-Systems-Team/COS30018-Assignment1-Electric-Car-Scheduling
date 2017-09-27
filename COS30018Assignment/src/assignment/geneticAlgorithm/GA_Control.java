package assignment.geneticAlgorithm;

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
	
	public void Setup(LinkedList<CarPreferenceData> list) {
		this.list = list;
	}
}
