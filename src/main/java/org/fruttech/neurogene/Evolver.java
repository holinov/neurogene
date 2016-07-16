package org.fruttech.neurogene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Evolver {

    private static final double MUTATION_CHANCE = 0.10;
    private final List<Integer> layersConfig;
    private Random rnd = new Random(System.currentTimeMillis());
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    public Evolver(List<Integer> layersConfig) {
        this.layersConfig = layersConfig;
    }

    public NeuroNet evolveForData(List<DataLine> data, int populationSize, int maxEpoches) {
        return evolveForData(data, populationSize, maxEpoches, null);
    }

    private boolean continueEvolution(Integer maxEpoches, Double targetError, int epoch, double error) {
        final boolean targetReached = targetError != null && error < targetError;
        final boolean maxEpochReached = maxEpoches != null && epoch < maxEpoches;
        return !(targetReached || maxEpochReached);
    }

    public NeuroNet evolveForData(List<DataLine> data, int populationSize, Integer maxEpoches, Double targetError) {
        List<NeuroNet> population = generateFirstPopulation(populationSize);

        //for (int i = 0; i < maxEpoches; i++) {
        double error = Double.MAX_VALUE;
        int epoch = 0;
        while (continueEvolution(maxEpoches, targetError, epoch, error)) {
            final CountDownLatch countDownLatch = new CountDownLatch(populationSize);

            final List<NeuroNet> pop = new ArrayList<>(population);
            executorService.execute(() -> {
                for (NeuroNet net : pop) {
                    double sum = 0;
                    for (DataLine line : data) {
                        final List<Double> netResults = net.process(line.input);
                        for (int netRI = 0; netRI < netResults.size(); netRI++) {
                            final double netR = netResults.get(netRI);
                            final double dataR = line.output.get(netRI);
                            final double delta = netR - dataR;
                            sum += delta * delta;
                        }
                    }
                    final double nerror = Math.sqrt(sum) / data.size();
                    net.setError(nerror);
                    countDownLatch.countDown();
                }
            });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            population.sort(Comparator.comparingDouble(NeuroNet::getError));
            final List<NeuroNet> sortedPopulation = new ArrayList<>(population);

            final NeuroNet winner1 = population.get(0);
            final NeuroNet winner2 = population.get(1);
            NeuroNet child = breed(winner1, winner2);
            population.remove(population.size() - 1);

            for (int i = 2; i < 10; i++) {
                NeuroNet neuroNet = population.get(i);
                final NeuroNet c = breed(neuroNet, winner1);
                population.set(i, c);
            }
            for (int i = 22, populationSize1 = population.size(); i < populationSize1; i++) {
                NeuroNet neuroNet = population.get(i);
                final NeuroNet mutant = breed(neuroNet, neuroNet);
                population.set(i, mutant);
            }

            population.add(child);


            error = winner1.getError();
            if (epoch % 100 == 0) {
                final double avgEpochError = sortedPopulation.stream().collect(Collectors.averagingDouble(NeuroNet::getError));
                System.out.printf("Epoch [%5d] winner error: %.8f avg epoch error: %.8f%n",
                        epoch, winner1.getError(), avgEpochError);
            }
            epoch++;
        }

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("Winner: " + gson.toJson(population.get(0).getStorableData()));
        return population.get(0);
    }

    private NeuroNet breed(NeuroNet parent1, NeuroNet parent2) {
        final NeuroNet child = new NeuroNet(layersConfig);
        final List<Double> parent1WeightGenes = parent1.getWeightGenes();
        final List<Double> parent2WeightGenes = parent2.getWeightGenes();
        final List<Double> childWeightGenes = new ArrayList<>(parent1WeightGenes.size());

        for (int i = 0; i < parent1WeightGenes.size(); i++) {
            double resGene = rnd.nextBoolean() ? parent1WeightGenes.get(i) : parent2WeightGenes.get(i);
            if (rnd.nextDouble() < MUTATION_CHANCE) {
                //if (rnd.nextBoolean()) {
                //    resGene = resGene / 2 * (rnd.nextBoolean() ? 1 : -1);
                //} else {
                resGene = rnd.nextDouble();
                //}
            }
            childWeightGenes.add(resGene);
        }

        child.setWeightGenes(childWeightGenes);

        return child;
    }

    private List<NeuroNet> generateFirstPopulation(int populationSize) {
        final List<NeuroNet> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            final NeuroNet neuroNet = new NeuroNet(layersConfig);
            population.add(neuroNet);
        }
        return population;
    }
}
