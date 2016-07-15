package org.fruttech.neurogene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class Evolver {

    private static final double MUTATION_CHANCE = 0.1;
    private final List<Integer> layersConfig;
    private Random rnd = new Random();

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

        //for (int i = 0; i < maxEpoches; i++) {
        double error = Double.MAX_VALUE;
        int epoch = 0;
        while (contunueEvolution(maxEpoches, targetError, epoch, error)) {
            for (NeuroNet net : population) {
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
            }

            population.sort(Comparator.comparingDouble(NeuroNet::getError));

            final NeuroNet winner1 = population.get(0);
            final NeuroNet winner2 = population.get(1);
            NeuroNet child = breed(winner1, winner2);
            population.remove(population.size() - 1);

            //mutate all except winners to boost evolution
            for (int i = 2, populationSize1 = population.size(); i < populationSize1; i++) {
                NeuroNet neuroNet = population.get(i);
                //incest makes mutations
                final NeuroNet mutant = breed(neuroNet, neuroNet);
                population.set(i, mutant);
            }

            population.add(child);


            error = winner1.getError();
            if (epoch % 100 == 0) System.out.println("Epoch [" + epoch + "] winner error: " + winner1.getError());
            epoch++;
        }

        return population.get(0);
    }

    private NeuroNet breed(NeuroNet parent1, NeuroNet parent2) {
        final NeuroNet child = new NeuroNet();
        final List<Float> parent1WeightGenes = parent1.getWeightGenes();
        final List<Float> parent2WeightGenes = parent2.getWeightGenes();
        final List<Float> childWeightGenes = new ArrayList<>(parent1WeightGenes.size());

        for (int i = 0; i < parent1WeightGenes.size(); i++) {
            float resGene = rnd.nextBoolean() ? parent1WeightGenes.get(i) : parent2WeightGenes.get(i);
            if (rnd.nextFloat() < MUTATION_CHANCE) {
                if (rnd.nextBoolean()) {
                    resGene = resGene / 2 * (rnd.nextBoolean() ? 1 : -1);
                } else {
                    resGene = rnd.nextFloat();
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
            final NeuroNet neuroNet = new NeuroNet();
            neuroNet.buildDefaultNet();
            population.add(neuroNet);
        }
        return population;
    }
}
