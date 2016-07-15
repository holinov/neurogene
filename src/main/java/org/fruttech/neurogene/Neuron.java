package org.fruttech.neurogene;

import java.util.ArrayList;
import java.util.List;

public class Neuron {
    private float val;
    private List<NeuronLink> inputs = new ArrayList<>();

    public List<NeuronLink> getInputs() {
        return inputs;
    }

    public void setInputs(List<NeuronLink> inputs) {
        this.inputs = inputs;
    }

    public float getVal() {
        return val;
    }

    public void setVal(float val) {
        this.val = val;
    }

    public void countVal() {
        float res = 0;
        for (NeuronLink input : inputs) {
            res += input.getWeight() * input.getInput().getVal();
        }
        val = res;
    }
}

