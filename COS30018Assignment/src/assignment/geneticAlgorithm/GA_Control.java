package assignment.geneticAlgorithm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import assignment.agents.AgentInteraction;
import assignment.main.CarPreferenceData;
import assignment.main.Control;
import assignment.ui.DebugMainFrame;
import jade.core.behaviours.Behaviour;

/**
 * 
 * @author Matthew Ward
 *
 */
public class GA_Control implements AgentInteraction {

	// Constants
	private final int INTERVAL_SNAP = 30; // Interval time to snap to (e.g. 30 = 30 minute interval)
	private final int NUMBER_OF_STATIONS = 4;
	private final float FITNESS_THRESHOLD = 10f;
	private float MUTATION_CHANCE = 0.65f;
	
	private int MAX_GENERATIONS = 10; // Must be at least 1
	private int SAMPLE_SIZE = 1000;

	private LinkedList<CarPreferenceData> listOfCarPrefData;
	private LinkedList<Schedule> population;
	private LinkedList<String> printBuffer = new LinkedList<String>();

	private Schedule currentSchedule = null, previousSchedule = null;
	public boolean scheduleReady = false;

	private Random random = new Random();
	private Control control;

	private boolean firstAtStartRequested = false;

	public String Setup(LinkedList<CarPreferenceData> list) {
		this.listOfCarPrefData = list;
		return "Genetic Algorithm Created";
	}

	public void Generate(int idToRemoveOrUpdate) {
		firstAtStartRequested = false;

		// @Debug GetNewConstants
		if (control.debug) {
			String strPopulation = ((DebugMainFrame) control.mainFrame).text_Population.getText();
			String strGenerations = ((DebugMainFrame) control.mainFrame).text_Generations.getText();
			String strMutationChance = ((DebugMainFrame) control.mainFrame).text_Mutation.getText();

			int intPopulation = Integer.parseInt(strPopulation);
			int intGenerations = Integer.parseInt(strGenerations);
			float floatMutationChance = Float.parseFloat(strMutationChance);

			MUTATION_CHANCE = floatMutationChance;
			SAMPLE_SIZE = intPopulation;
			MAX_GENERATIONS = intGenerations;
		}

		if (listOfCarPrefData.size() > 0) {

			System.out.println("--- Genetic Algorithm: Generation 1 ---");

			previousSchedule = currentSchedule;
			scheduleReady = false; // Lets master scheduler know schedule is being calculated

			// Remove the car from the current schedule
			if (idToRemoveOrUpdate > -1 && currentSchedule != null) {
				boolean removed = false;
				for (int s = 0; s < NUMBER_OF_STATIONS; s++) {
					StationSlot station = currentSchedule.stations.get(s);
					for (int c = 0; c < station.registeredCars.size(); c++) {
						int tid = station.registeredCars.get(c).id;

						if (tid == idToRemoveOrUpdate) {
							station.registeredCars.remove(c);
							removed = true;
							break;
						}
					}

					if (removed) {
						break;
					}
				}

			}

			GeneratePopulation(null); // Generate first population
			currentSchedule = GetHighestSchedule(population); // Get the highest fitness member as current schedule
			control.UpdateCurrentSchedule(currentSchedule); // Send it to the control to be displayed

			int generations = 1;
			while (generations < MAX_GENERATIONS) { // TODO: Make function for below comment

				System.out.println("--- Genetic Algorithm: Generation " + (generations + 1) + " ---");

				String calculating = "Calculating";
				for (int ca = 0; ca < generations; ca++) {
					calculating += ".";
				}
				PrintToSystem("Genetic Algorithm: " + calculating);

				GeneratePopulation(population); // Use existing list
				currentSchedule = GetHighestSchedule(population); // Get the highest fitness member as current schedule
				control.UpdateCurrentSchedule(currentSchedule); // Send it to the control to be displayed

				if (currentSchedule.fitness > FITNESS_THRESHOLD
						|| currentSchedule.fitness == 0 /* || more than half have converged on same schedule */) {
					break;
				}

				generations++;
			}

			for (int i = 0; i < population.size(); i++) {
				// @Debug System.out.println("element " + i + ": " + population.get(i).fitness);

				if (i != population.size() - 1) {
					Schedule a = population.get(i);
					Schedule b = population.get(i + 1);

					/*
					 * if (a.fitness == b.fitness && a.fitness != 0 && b.fitness != 1) {
					 * System.out.println("Debug -- Same fitness?"); }
					 */
				}
			}

			PrintToSystem("Genetic Algorithm: Schedule Ready To Use");
			scheduleReady = true; // Schedule ready to be used

		} else {
			PrintToSystem("Genetic Algorithm: Unable to Create List - No Cars");
			currentSchedule = null;
		}
	}

	/**
	 * Restores schedule to what is was before the last Generate() was called. (Can
	 * be used if it finds it can't fit )
	 */
	public void RestoreSchedule(boolean checkIfBetter) {
		if (checkIfBetter) {
			currentSchedule = (currentSchedule.fitness <= previousSchedule.fitness) ? previousSchedule
					: currentSchedule;
		} else {
			currentSchedule = previousSchedule;
		}
	}

	public Schedule GetCurrentSchedule() {
		return scheduleReady ? currentSchedule : null; // If the current schedule isn't finished, send a null schedule
	}

	private void GeneratePopulation(LinkedList<Schedule> previous) {
		// Original generation

		if (previous == null) {
			population = new LinkedList<Schedule>();

			int size = SAMPLE_SIZE;

			// Create population
			if (currentSchedule == null) {
				for (int i = 0; i < size; i++) {
					population.add((CreateASchedule()));
				}
			} else {
				for (int i = 0; i < size; i++) {
					Schedule clone = currentSchedule.Clone();
					clone = MutationFunction(clone, 1);
					AddNewestCar(clone);
					ChooseFitnessFunction(clone);
					population.add(clone);
				}
			}

		} else {
			// Cross-over & Mutation (all following generations)

			float t = 0;
			for (int i = 0; i < population.size(); i++) {
				t += population.get(i).fitness;
			}
			float average = t / population.size();

			// Get rid of low fitness members below the average
			t = 0;
			for (int i = 0; i < population.size(); i++) {
				Schedule s = population.get(i);
				if (s.fitness < average) {
					population.remove(s);
					t++;
				}
				if (t > SAMPLE_SIZE / 2) {
					break;
				}
			}

			// Get rid of schedules with no cars
			for (int i = population.size() - 1; i >= 0; i--) {
				if (population.get(i).NumberOfCars() <= 0) {
					population.remove(i);
				}
			}

			LinkedList<Schedule> newPop = new LinkedList<Schedule>();

			// Elites
			Schedule highest = GetHighestSchedule(population);
			AddNewestCar(highest);
			Schedule secondHighest = GetSecondHighestSchedule(population, highest);
			AddNewestCar(secondHighest);

			// Adds the elites to the new population
			newPop.add(highest);
			newPop.add(secondHighest);

			// Now Create new schedules
			while (newPop.size() < SAMPLE_SIZE) {
				Schedule[] parents = new Schedule[2];
				int count = 0;

				while (count < 2) {
					Collections.shuffle(population);
					int r = random.nextInt(population.size());
					Schedule a = population.get(r);

					Schedule b = a;
					while (b == a) {
						r = random.nextInt(population.size());
						b = population.get(r);
					}

					// @Debug System.out.println("count:" + count + ", values = " + a + " | " + b);

					float r2 = random.nextFloat();
					parents[count] = (r2 < 0.7) ? ((a.fitness > b.fitness) ? a : b) : ((a.fitness > b.fitness) ? b : a);
					// Tournament Selection

					count++;
				}

				newPop.add(CreateASchedule(parents[0], parents[1]));

			}

			// @Debug System.out.println("Population generated for this generation");
			population = newPop;
		}
	}

	// Creating new schedules
	// ************************************************************************
	private Schedule CreateASchedule() {
		return CreateASchedule(null, null);
	}

	private Schedule CreateASchedule(Schedule parent1, Schedule parent2) {
		Schedule s = new Schedule(NUMBER_OF_STATIONS);

		// Schedule with parents
		if (parent1 != null && parent2 != null) {

			Schedule schedule = CrossoverFunction2(parent1, parent2); // Crossover
			schedule = MutationFunction(schedule, MUTATION_CHANCE); // Mutation

			s = schedule; // schedule is the returned schedule

			// New Schedule
		} else {
			for (int i = 0; i < listOfCarPrefData.size(); i++) {
				CarSlot slot = CarSlotFromData(i);

				if (firstAtStartRequested == false) {

					boolean clash = false;

					for (int s1 = 0; s1 < NUMBER_OF_STATIONS; s1++) {
						StationSlot station = s.stations.get(s1);

						for (int e = 0; e < station.registeredCars.size(); e++) {
							CarSlot other = station.registeredCars.get(e);

							if (CheckClash(slot, slot.startRequested, other)) {
								clash = true;
								break;
							}
						}

						if (!clash) // If no clash, register at startRequested
						{
							slot.startTime = slot.startRequested;
							station.registeredCars.add(slot);
							break;
						}
					}

					if (clash) // If clash, register at random time
					{
						int count = 0;
						while (!TryAddCarToSchedule(s, slot) && count == 10) {
							count++;
						}
					}

				} else {
					// Try to add the car to a random location
					// @Debug System.out.println("--DEBUG-- Trying car in random location");
					int count = 0;
					while (!TryAddCarToSchedule(s, slot) && count < 10) {
						count++;
						// @Debug System.out.println("count: " + count);
					}
				}
			}
			firstAtStartRequested = true;
		}

		s.OrderCarsByHours();

		ChooseFitnessFunction(s);

		return s; // Returns the new schedule
	}

	// ************************************************************************

	private void ChooseFitnessFunction(Schedule s) {
		// Debug -> Choose which fitness function to use
		if (control.debug == true) {
			int verINDEX = ((DebugMainFrame) control.mainFrame).fitnessCB.getSelectedIndex();
			switch (verINDEX) {
			case 0:
				CalculateFitness(s);
				break;
			case 1:
				CalculateFitnessV2(s);
				break;
			case 2:
				CalculateFitnessV3(s);
				break;
			case 3:
				CalculateFitnessV4(s);
				break;
			case 4:
				CalculateFitnessV5(s);
				break;
			case 5:
				CalculateFitnessV6(s);
				break;
			default:
				CalculateFitnessV3(s);
			}
		} else {

			CalculateFitnessV3(s); // Our chosen fitness function

		}

	}

	// Checks if a car lies within the duration of another car
	private boolean CheckClash(CarSlot newCar, float newCarRequest, CarSlot other) {

		if (newCarRequest == other.startTime) {
			return true;
		}
		float padding = 0; // Minimum space between cars

		float start, end, middleTest;
		if (other.startTime >= newCarRequest) {
			start = newCarRequest;
			end = newCarRequest + newCar.duration;
			middleTest = other.startTime;
		} else {
			start = other.startTime;
			end = other.startTime + other.duration;
			middleTest = newCarRequest;
		}
		return (middleTest < end);
		// return (middleTest > (start - padding) && middleTest < (end + padding));

	}

	private Schedule MutationFunction(Schedule schedule, float chance) {

		chance *= 100;

		for (int t = 0; t < NUMBER_OF_STATIONS; t++) {
			StationSlot newScheduleStation = schedule.stations.get(t);

			for (int i = 0; i < newScheduleStation.registeredCars.size(); i++) {
				int r = random.nextInt(100);

				// Mutate??
				if (r <= chance) {
					
					CarSlot car = newScheduleStation.registeredCars.get(i);
					
					r = random.nextInt(5); //Chance of station hoping instead of mutating hours
					if (r != 1) {
						
						//Mutate hours
						float moveHours = ((float) (random.nextInt(6)));

						if ((moveHours < 0 && (car.startTime + moveHours >= car.startRequested)) || (moveHours > 0
								&& (car.startTime + car.duration + moveHours <= car.finishRequired))) {

							boolean spotTaken = true;
							while (spotTaken) {

								spotTaken = false;
								for (int test = 0; test < newScheduleStation.registeredCars.size(); test++) {
									CarSlot testCar = newScheduleStation.registeredCars.get(test);
									if (car != testCar && CheckClash(car, car.startTime + moveHours, testCar)) {
										spotTaken = true;
										break;
									}
								}

								if (spotTaken) {
									moveHours *= 0.5;
								} // Half move hours if jump was too big
								if (Math.abs(moveHours) < 0.1) {
									break;
								} // Break when moveHours gets too close to 0
							}

							if (!spotTaken) {
								car.startTime = SnapToTime(car.startTime + moveHours); // Mutate start time
								if (car.startTime < car.startRequested) {
									car.startTime += (INTERVAL_SNAP / 60);
								}
							}

						}

					} else {
						
						//Mutate to different station
						for (int t2 = 0; t2 < NUMBER_OF_STATIONS; t2++) {
							if (t2 != t) {
								StationSlot stationMutatingTo = schedule.stations.get(t2);
							
								boolean spotTaken = false;
								for (int test = 0; test < stationMutatingTo.registeredCars.size(); test++) {
									CarSlot testCar = stationMutatingTo.registeredCars.get(test);
									if (car != testCar && CheckClash(car, car.startTime, testCar)) {
										spotTaken = true;
										break;
									}
								}

								if (!spotTaken) {
									stationMutatingTo.registeredCars.add(car);
									newScheduleStation.registeredCars.remove(car);
									break;
								}
							}
						}
					}
				} else {

					// Small chance it could try jumping to the start requested now
					r = random.nextInt(100);
					if (r <= chance / 4) {

						CarSlot car = newScheduleStation.registeredCars.get(i);
						boolean spotTaken = false;

						for (int t2 = 0; t2 < newScheduleStation.registeredCars.size(); t2++) {
							CarSlot test = newScheduleStation.registeredCars.get(t2);
							if (car != test && CheckClash(car, car.startRequested, test)) {
								spotTaken = true;
								break;
							}
						}

						if (!spotTaken) {
							car.startTime = SnapToTime(car.startRequested); // Mutate start time
							if (car.startTime < car.startRequested) {
								car.startTime += (INTERVAL_SNAP / 60);
							}
						}
					}
				}
			}

		}

		return schedule;
	}

	private Schedule CrossoverFunction2(Schedule parent1, Schedule parent2) {

		Schedule schedule = new Schedule(NUMBER_OF_STATIONS);

		for (int t = 0; t < NUMBER_OF_STATIONS; t++) {
			StationSlot stationA = parent1.stations.get(t);
			StationSlot stationB = parent2.stations.get(t);
			StationSlot newScheduleStation = schedule.stations.get(t);

			// Adds cars from parent 1 to new schedule
			for (int i = 0; i < stationA.registeredCars.size(); i++) {
				newScheduleStation.registeredCars.add(stationA.registeredCars.get(i).Clone());
			}

			// Adds cars from parent 2 (if they don't already exist in the schedule, and can
			// fit)
			for (int i = 0; i < stationB.registeredCars.size(); i++) {
				CarSlot carToAdd = stationB.registeredCars.get(i);

				boolean canFit = true;

				for (int c = 0; c < schedule.stations.get(t).registeredCars.size(); c++) {
					CarSlot other = newScheduleStation.registeredCars.get(c);

					// Does the new car clash with existing car?
					if (CheckClash(carToAdd, carToAdd.startTime, other)) {
						canFit = false;
						break;
					}

					// Does car already exist in the new schedule?
					for (int test = 0; test < newScheduleStation.registeredCars.size(); test++) {
						if (newScheduleStation.registeredCars.get(test).id == other.id) {
							canFit = false;
							break;
						}
					}

					// Does car already exist in a different station in new schedule?
					for (int t2 = 0; t2 < NUMBER_OF_STATIONS; t2++) {
						StationSlot testStation = schedule.stations.get(t2); // Test other stations

						for (int test = 0; test < testStation.registeredCars.size(); test++) {
							if (testStation.registeredCars.get(test).id == other.id) {
								canFit = false;
								break;
							}
						}
					}

					if (canFit) {
						newScheduleStation.registeredCars.add(carToAdd.Clone());
					}
				}
			}

		}

		// Now, if any cars weren't added from station 2, it attempts to add them now
		boolean noLostCars = true;

		for (int t = 0; t < NUMBER_OF_STATIONS; t++) {
			StationSlot testParent1 = parent1.stations.get(t);
			StationSlot testParent2 = parent2.stations.get(t);

			for (int i = 0; i < testParent1.registeredCars.size(); i++) {
				CarSlot carToTest = testParent1.registeredCars.get(i);

				if (!schedule.CarExist(carToTest.id)) {
					boolean success = false;
					int attempts = 0;

					while (success == false && attempts++ < 12) {
						success = TryAddCarToSchedule(schedule, carToTest.Clone());
					}

					if (!success) {
						noLostCars = true;
						break;
					}

				}
			}

			if (noLostCars == false) {
				break;
			}

			for (int i = 0; i < testParent2.registeredCars.size(); i++) {
				CarSlot carToTest = testParent2.registeredCars.get(i);

				if (!schedule.CarExist(carToTest.id)) {
					boolean success = false;
					int attempts = 0;

					while (success == false && attempts++ < 12) {
						success = TryAddCarToSchedule(schedule, carToTest.Clone());
					}

					if (!success) {
						noLostCars = true;
						break;
					}

				}
			}
		}

		if (noLostCars == false) {
			schedule = (parent1.fitness >= parent2.fitness) ? parent1.Clone() : parent2.Clone();
		}

		return schedule;

	}

	private Schedule CrossoverFunction1(Schedule parent1, Schedule parent2) {
		Schedule schedule = new Schedule(NUMBER_OF_STATIONS);

		for (int t = 0; t < NUMBER_OF_STATIONS; t++) {
			StationSlot stationA = parent1.stations.get(t);
			StationSlot stationB = parent2.stations.get(t);
			StationSlot newScheduleStation = schedule.stations.get(t);

			// Adds cars from parent 1 to new schedule
			for (int i = 0; i < stationA.registeredCars.size(); i++) {
				newScheduleStation.registeredCars.add(stationA.registeredCars.get(i).Clone());
			}

			// Adds cars from parent 2 (if they don't already exist in the schedule, and can
			// fit)
			for (int i = 0; i < stationB.registeredCars.size(); i++) {
				CarSlot carToAdd = stationB.registeredCars.get(i);

				boolean canFit = true;

				for (int c = 0; c < schedule.stations.get(t).registeredCars.size(); c++) {
					CarSlot other = newScheduleStation.registeredCars.get(c);

					// Does the new car clash with existing car?
					if (CheckClash(carToAdd, carToAdd.startTime, other)) {
						canFit = false;
						break;
					}

					// Does car already exist in the new schedule?
					for (int test = 0; test < newScheduleStation.registeredCars.size(); test++) {
						if (newScheduleStation.registeredCars.get(test).id == other.id) {
							canFit = false;
							break;
						}
					}

					// Does car already exist in a different station in new schedule?
					for (int t2 = 0; t2 < NUMBER_OF_STATIONS; t2++) {
						StationSlot testStation = schedule.stations.get(t2); // Test other stations

						for (int test = 0; test < testStation.registeredCars.size(); test++) {
							if (testStation.registeredCars.get(test).id == other.id) {
								canFit = false;
								break;
							}
						}
					}

					if (canFit) {
						newScheduleStation.registeredCars.add(carToAdd.Clone());
					}
				}
			}
		}

		return schedule;
	}

	// Calculating Fitness
	// *************************************************************************
	/**
	 * @author Matthew Ward
	 */
	private void CalculateFitness(Schedule p) {
		float max = listOfCarPrefData.size();
		float numberOfCars = p.NumberOfCars();
		float totalTimeGap = p.TimeGap();
		float wastedFromRequestedStart = p.TimeFromRequested();

		// Fitness function
		float fit = (numberOfCars - 0.1f * totalTimeGap - 0.5f * wastedFromRequestedStart) / max;

		p.fitness = fit;
	}

	/**
	 * @author Jacques Van Niekerk
	 */
	private void CalculateFitnessV2(Schedule p) {
		float TotalunusedHours = p.TotalUnusedHours();
		float PriorityScore = p.PriorityScore();

		float fit = (float) (1 / TotalunusedHours) - PriorityScore;
		p.fitness = fit;
	}

	/**
	 * @author Jacques Van Niekerk
	 */
	private void CalculateFitnessV4(Schedule p) {
		float TotalAlloctedTime = p.TotalAlloctedTime();
		float totalRequestedTime = TotalRequestedTime();
		float PriorityFactor;
		float PriorityScore = p.PriorityScore();
		float BestPosibilePriorityScore = BestPosibilePriorityScore(p.NumberOfCars());
		float fit = TotalAlloctedTime / totalRequestedTime;
		PriorityFactor = (float) Math.pow(fit - 1, 2);
		fit = fit - (PriorityFactor * (PriorityScore / BestPosibilePriorityScore));
		p.fitness = fit;
	}

	/**
	 * @author Matthew Ward
	 */
	private void CalculateFitnessV3(Schedule p) {
		float PriorityScore = p.Prioritypoints();
		float AmountDown = p.TimeFromRequested();
		float TimeGap = p.TimeGap();
		float fit = 0;
		fit = PriorityScore * 2 - TimeGap * 0.5f - AmountDown * 0.5f;
		p.fitness = fit;
	}

	/**
	 * @author Jacques Van Niekerk
	 */
	private void CalculateFitnessV5(Schedule p) {
		float TotalAlloctedTime = p.TotalAlloctedTime();
		float totalRequestedTime = TotalRequestedTime();
		float PriorityScore = p.PriorityScore();

		float fit = TotalAlloctedTime / totalRequestedTime - (1 / PriorityScore);
		p.fitness = fit;
	}
	
	/**
	 * @author Jacques Van Niekerk, Brendan Pert
	 */
	private void CalculateFitnessV6(Schedule p) {
		float TotalAlloctedTime = p.TotalAlloctedTime();
		float totalRequestedTime = TotalRequestedTime();
		float TimeGap = p.TimeGap();

		float fit = (TotalAlloctedTime / totalRequestedTime) - (0.1f * TimeGap);
		p.fitness = fit;
	}

	private float BestPosibilePriorityScore(float numOfCars) {
		float total = 0;
		float count = 1;
		for (int i = 0; i < numOfCars; i++) {
			total += count;
			count++;
		}
		return total;
	}

	// Used by calculate fitness
	public float TotalRequestedTime() {
		float total = 0;
		for (int request = 0; request < listOfCarPrefData.size(); request++) {
			total += listOfCarPrefData.get(request).durationRequested;
		}
		return total;
	}

	// *************************************************************************

	// Sees if the schedule can fit the newest cars
	private void AddNewestCar(Schedule s) {
		CarSlot n = CarSlotFromData(listOfCarPrefData.size() - 1);

		if (s.CarExist(n.id) == false) {

			int attempt = 0;
			boolean added = false;
			while (attempt++ < 12 && !added) {
				added = TryAddCarToSchedule(s, n);
			}
		}
	}

	private boolean TryAddCarToSchedule(Schedule s, CarSlot c) {
		float randomTime = (random.nextFloat() * (c.finishRequired - c.duration - c.startRequested)) + c.startRequested;
		boolean spotTaken = false;

		if (randomTime < c.startRequested) {
			randomTime = (random.nextFloat() * (c.finishRequired - c.duration - c.startRequested)) + c.startRequested;
		}

		randomTime = SnapToTime(randomTime);

		spotTaken = true;
		int count = 0;

		// Checks if the car can fit into any of the stations, starting with station 1
		while (spotTaken && count < NUMBER_OF_STATIONS) {
			spotTaken = false;
			StationSlot currentStation = s.stations.get(count);

			for (int i = 0; i < currentStation.registeredCars.size(); i++) {
				CarSlot other = currentStation.registeredCars.get(i);

				if (CheckClash(c, randomTime, other)) {
					spotTaken = true;
					break;
				}
			}

			// If it fits, add it. If it still can't fit it in, leave it
			if (!spotTaken) {
				c.startTime = randomTime;
				currentStation.registeredCars.add(c);
				// @Debug System.out.println("Car fitted into station" + count + ": " + c);
			} else {
				// @Debug System.out.println("Car COULD NOT FIT INTO station" + count + ": " +
				// c);
				count++;
			}
		}
		return !spotTaken;
	}

	/**
	 * Snaps time to the interval defined (e.g. 30 minute intervals = 0.5). Number
	 * passed in is number that is snapped to time
	 * 
	 * @param num
	 * @return
	 */
	private float SnapToTime(float num) {
		int val = (int) Math.floor(60 / INTERVAL_SNAP);
		float newNum = Math.round((num * val));

		newNum = newNum / val;
		return newNum;
	}

	public void RemoveCarFromAllSchedules(int id) {
		listOfCarPrefData.remove();
		for (int i = 0; i < population.size(); i++) {
			Schedule s = population.get(i);

			boolean found = false;
			for (int st = 0; st < s.stations.size(); st++) {
				StationSlot station = s.stations.get(st);
				for (int c = 0; c < station.registeredCars.size(); c++) {
					CarSlot car = station.registeredCars.get(c);

					if (car.id == id) {
						station.registeredCars.remove(car);
						found = true;
						break;
					}
				}

				if (found) {
					break;
				}
			}
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

	private Schedule GetSecondHighestSchedule(LinkedList<Schedule> p, Schedule firstHighest) {

		if (p.size() > 1) {
			Schedule highest2 = p.getFirst();
			highest2 = (highest2 != firstHighest) ? highest2 : p.get(1);
			float fit = highest2.fitness;

			for (int i = 1; i < p.size(); i++) {
				Schedule test = p.get(i);

				if (test.fitness > fit && test != firstHighest) {
					highest2 = test;
					fit = highest2.fitness;
				}
			}

			return highest2;

		} else {
			return null;
		}
	}

	/**
	 * Converts car preference data at index i, into a new car slot
	 * 
	 * @param i
	 * @return
	 */
	private CarSlot CarSlotFromData(int i) {
		CarPreferenceData data = listOfCarPrefData.get(i);
		CarSlot slot = new CarSlot();

		slot.id = data.id;
		slot.type = data.carType;
		slot.name = data.agentName;
		slot.priority = i + 1;
		slot.duration = data.durationRequested; // TODO: Change this depending on car, charge left, etc
		slot.startRequested = data.startTime;
		slot.finishRequired = data.finishTime;

		return slot;
	}

	// **************************
	// Agent Interaction commands
	// **************************

	@Override
	public void RegisterControl(Control c) {
		control = c;
		PrintToSystem("");
	}

	@Override
	public void AddBehaviour(Behaviour b) {
	}

	@Override
	public void PrintToSystem(String s) {
		System.out.println(s);
		if (control == null) {
			if (s != "") {
				printBuffer.add(s);
			}
		} else {
			// Adds any buffered messages first
			for (int count = 0; count < printBuffer.size(); count++) {
				control.AddLastMessage(printBuffer.get(count));
			}
			printBuffer.clear();

			// Adds latest message
			if (s != "") {
				control.AddLastMessage(s);
			}
		}
	}

	@Override
	public String AgentName() {
		return "Genetic Algorithm";
	}
}
