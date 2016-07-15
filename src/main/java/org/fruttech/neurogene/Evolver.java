package org.fruttech.neurogene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Evolver {

    private static final float MUTATION_CHANCE = 0.3f;
    private final List<Integer> layersConfig;
    private Random rnd = new Random() {
        @Override public synchronized float nextFloat() {
            return super.nextFloat();
        }

        @Override synchronized public int nextInt() {
            return super.nextInt();
        }
    };

    public Evolver(List<Integer> layersConfig) {
        this.layersConfig = layersConfig;
    }

    public NeuroNet evolveForData(List<DataLine> data, int populationSize, int maxEpoches) {
        return evolveForData(data, populationSize, maxEpoches, null);
    }

    private boolean contunueEvolution(Integer maxEpoches, Double targetError, int epoch, double error) {
        return (maxEpoches != null && epoch < maxEpoches) || (targetError != null && error > targetError);
    }

    public NeuroNet evolveForData(List<DataLine> data, int populationSize, Integer maxEpoches, Double targetError) {
        List<NeuroNet> population = generateFirstPopulation(populationSize);
        final ExecutorService executorService = Executors.newFixedThreadPool(4);

        double error = Double.MAX_VALUE;
        int epoch = 0;
        while (contunueEvolution(maxEpoches, targetError, epoch, error)) {

            final CountDownLatch countDownLatch = new CountDownLatch(populationSize);
            for (NeuroNet net : population) {
                executorService.submit(() -> {
                    double sum = 0;
                    for (DataLine line : data) {
                        final List<Float> netResults = net.process(line.input);
                        for (int netRI = 0; netRI < netResults.size(); netRI++) {
                            final Float netR = netResults.get(netRI);
                            final Float dataR = line.output.get(netRI);
                            final double delta = netR - dataR;
                            sum += delta * delta;
                        }
                    }
                    final double nerror = Math.sqrt(sum);
                    net.setError(nerror);
                    countDownLatch.countDown();
                });
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {

            }

            population.sort(Comparator.comparingDouble(NeuroNet::getError));

            final NeuroNet winner1 = population.get(0);
            final NeuroNet winner2 = population.get(1);
            NeuroNet child1 = breed(winner1, winner2, MUTATION_CHANCE);
            NeuroNet child2 = breed(winner1, winner2, 0);
            population.remove(population.size() - 1);
            population.remove(population.size() - 1);

            //mutate all except winners to boost evolution
            for (int i = 2; i < populationSize - 2; i++) {
                NeuroNet neuroNet = population.get(i);
                //incest makes mutations
                final NeuroNet mutant = breed(neuroNet, neuroNet, 1);
                population.set(i, mutant);
            }

            population.add(child1);
            population.add(child2);


            error = winner1.getError();
            if (epoch % 100 == 0) System.out.println("Epoch [" + epoch + "] winner error: " + winner1.getError());
            epoch++;
        }

        return population.get(0);
    }

    private NeuroNet breed(NeuroNet parent1, NeuroNet parent2, float chance) {
        final NeuroNet child = new NeuroNet(layersConfig);
        final List<Float> parent1WeightGenes = parent1.getWeightGenes();
        final List<Float> parent2WeightGenes = parent2.getWeightGenes();
        final List<Float> childWeightGenes = new ArrayList<>(parent1WeightGenes.size());

        for (int i = 0; i < parent1WeightGenes.size(); i++) {
            float resGene = rnd.nextBoolean() ? parent1WeightGenes.get(i) : parent2WeightGenes.get(i);
            if (rnd.nextFloat() <= chance) {
                if (rnd.nextBoolean()) {
                    resGene = resGene / 2 * (rnd.nextBoolean() ? 1 : -1);
                } else {
                    if (rnd.nextFloat() < 0.3f) {
                        resGene = NeuroNet.randomWeight(rnd, Float.MAX_VALUE);
                    } else if (rnd.nextFloat() < 0.6f) {
                        resGene = NeuroNet.randomWeight(rnd);
                    } else {
                        resGene = rnd.nextInt();
                    }
                    //resGene = NeuroNet.randomWeight(rnd);
                }
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
