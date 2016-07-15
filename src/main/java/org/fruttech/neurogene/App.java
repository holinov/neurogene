package org.fruttech.neurogene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            line.output.add(getResult1(line.input));

            dataLines.add(line);
        }

        System.out.println("Start evolution.");
        final long start = System.currentTimeMillis();
        final Evolver evolver = new Evolver(Arrays.asList(3, 5, 1));
        final NeuroNet neuroNet = evolver.evolveForData(dataLines, 100, null, 0.2);
        final long finish = System.currentTimeMillis();

        final List<Float> input = dataLines.get(3).input;
        final List<Float> res = neuroNet.process(input);

        System.out.println("Finished evolution. Took " + (finish - start) + " ms");
        System.out.println("Result: " + res + " error:" + neuroNet.getError() + " targetResult: " + dataLines.get(3).output);

    }


    private static float getResult1(List<Float> inputs) {
        return inputs.get(0) * inputs.get(1) / inputs.get(2);

    }

    private static float getResult(List<Float> inputs) {
        final double r = (inputs.get(0) + Math.sin(inputs.get(1))) * Math.log10(inputs.get(2));
        return (float) r;
    }
}

