package org.fruttech.neurogene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        final Random random = new Random();
        List<DataLine> dataLines = getDataLines(random);

        final String net1 = "{\n" +
                "  \"layerConfig\": [\n" +
                "    3,\n" +
                "    5,\n" +
                "    1\n" +
                "  ],\n" +
                "  \"weights\": [\n" +
                "    0.6948342,\n" +
                "    0.13406599,\n" +
                "    -0.058102444,\n" +
                "    -0.3166605,\n" +
                "    0.13389918,\n" +
                "    0.19271499,\n" +
                "    0.99713475,\n" +
                "    0.12976697,\n" +
                "    0.68221635,\n" +
                "    0.93035424,\n" +
                "    0.44445583,\n" +
                "    0.13623436,\n" +
                "    -0.12307447,\n" +
                "    0.9531664,\n" +
                "    0.6492687,\n" +
                "    0.25635505,\n" +
                "    -0.38092393,\n" +
                "    0.99095744,\n" +
                "    0.8454099,\n" +
                "    0.4975139\n" +
                "  ],\n" +
                "  \"error\": 0.1477888430790542\n" +
                "}";
        final NeuroNet net = new NeuroNet(net1);
        final int lineIndex = 87;
        final List<Double> input = dataLines.get(lineIndex).input;
        final List<Double> output = dataLines.get(lineIndex).output;
        final List<Double> res1 = net.process(input);
        System.out.println("Result: " + res1 + " targetResult: " + output);

        System.out.println("Start evolution.");
        final Evolver evolver = new Evolver(Arrays.asList(3, 5, 2, 1));
        final NeuroNet neuroNet = evolver.evolveForData(dataLines, 100, null, 0.001);

        for (int i = 0; i < 10; i++) {
            final DataLine line = new DataLine();
            line.input = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                line.input.add(random.nextDouble());
            }
            line.output = new ArrayList<>();
            line.output.add(Math.sin(line.input.stream().collect(Collectors.summarizingDouble(v -> v)).getSum()));

            final List<Double> res = neuroNet.process(input);
            System.out.println("Result: " + res + " targetResult: " + output + " error:" + neuroNet.getError());

        }
    }

    private static List<DataLine> getDataLines(Random random) {
        List<DataLine> dataLines = new ArrayList<>(100);
        for (int i = 0; i < 1000; i++) {
            final DataLine line = new DataLine();
            line.input = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                line.input.add(random.nextDouble());
            }
            line.output = new ArrayList<>();
            line.output.add(Math.sin(line.input.stream().collect(Collectors.summarizingDouble(v -> v)).getSum()));

            dataLines.add(line);
        }
        return dataLines;
    }
}

