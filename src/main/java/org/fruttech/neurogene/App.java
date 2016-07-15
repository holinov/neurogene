package org.fruttech.neurogene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        List<DataLine> dataLines = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            final DataLine line = new DataLine();
            line.input = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                line.input.add(i * 1.0f + j);
            }
            line.output = new ArrayList<>();
            line.output.add(i * 1.0f + (float) line.input.stream().collect(Collectors.summarizingDouble(v -> v)).getSum());

            dataLines.add(line);
        }

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
        final List<Float> input = dataLines.get(lineIndex).input;
        final List<Float> output = dataLines.get(lineIndex).output;
        final List<Float> res1 = net.process(input);
        System.out.println("Result: " + res1 + " targetResult: " + output);

        System.out.println("Start evolution.");
        final Evolver evolver = new Evolver(Arrays.asList(3, 5, 1));
        final NeuroNet neuroNet = evolver.evolveForData(dataLines, 500, null, 0.001);
        final List<Float> res = neuroNet.process(input);
        System.out.println("Finished evolution.");
        System.out.println("Result: " + res + " error:" + neuroNet.getError() + " targetResult: " + output);

    }
}

