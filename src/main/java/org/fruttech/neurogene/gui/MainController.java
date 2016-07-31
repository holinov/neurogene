package org.fruttech.neurogene.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MainController {
    @FXML
    Canvas canvas;

    @FXML
    protected void initialize() {
        drawShapes(canvas.getGraphicsContext2D());
    }

    private void drawShapes(GraphicsContext gc) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
        gc.strokeLine(40, 10, 10, 40);
    }
}

