package org.fruttech.neurogene;

import java.util.ArrayList;
import java.util.List;

public class Neuron {
    private double val;
    private List<NeuronLink> inputs = new ArrayList<>();

    public List<NeuronLink> getInputs() {
        return inputs;
    }

    public void setInputs(List<NeuronLink> inputs) {
        this.inputs = inputs;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }

    public void countVal() {
        double res = 0;
        for (NeuronLink input : inputs) {
            res += input.getWeight() * input.getInput().getVal();
        }

        val = transferFunction(res);
    }

    protected double transferFunction(double val) {
        return val / Math.sqrt(1 + val * val);
    }
}

