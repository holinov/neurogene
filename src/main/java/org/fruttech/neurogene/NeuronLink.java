package org.fruttech.neurogene;

public class NeuronLink {
    private Neuron input;
    private double weight;

    public Neuron getInput() {
        return input;
    }

    public void setInput(Neuron input) {
        this.input = input;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
