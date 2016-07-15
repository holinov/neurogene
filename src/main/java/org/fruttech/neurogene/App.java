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

        System.out.println("Start evolution.");
        final Evolver evolver = new Evolver(Arrays.asList(3, 5, 1));
        final NeuroNet neuroNet = evolver.evolveForData(dataLines, 500, null, 0.2);
        final List<Float> input = dataLines.get(3).input;
        final List<Float> res = neuroNet.process(input);
        System.out.println("Finished evolution.");
        System.out.println("Result: " + res + " error:" + neuroNet.getError() + " targetResult: " + dataLines.get(3).output);

    }
}

