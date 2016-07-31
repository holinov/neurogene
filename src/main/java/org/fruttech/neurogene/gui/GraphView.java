package org.fruttech.neurogene.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.DoubleStream;

public class GraphView {
    private static final int DEFAULT_MAX_SIZE = 100;
    private final GraphicsContext cg;
    private LinkedList<Double> datum = new LinkedList<>();
    private int maxSize = DEFAULT_MAX_SIZE;
    private double width;
    private double height;

    public GraphView(GraphicsContext cg, double width, double height) {
        this.cg = cg;
        this.width = width;
        this.height = height;
    }

    public void add(double data) {
        if (datum.size() >= maxSize)
            datum.remove(0);

        datum.add(data);

        redraw();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void redraw() {
        cg.setFill(Color.WHITE);
        cg.clearRect(0, 0, width, height);
        cg.setStroke(Color.BLACK);

        if (datum.size() > 1) {

            for (int i = 1, datumSize = datum.size(); i < datumSize; i++) {
                double value = datum.get(i);

            }
        }
    }

    private List<Double> normalize(List<Double> arr) {
        final DoubleStream arrStream = arr.stream().mapToDouble(d -> d);
        final double min = arrStream.min().getAsDouble();
        final double max = arrStream.max().getAsDouble();
        final double maxMinimized = max - min;
        final DoubleStream minimized = arrStream.map(v -> v - min);

        final List<Double> result = new ArrayList<>(arr.size());
        /*
        max/x =
         */
        minimized.map(x -> max / x)
    }
}
