package org.fruttech.neurogene;

public class NeuronLink {
    private Neuron input;
    private float weight;

    public Neuron getInput() {
        return input;
    }

    public void setInput(Neuron input) {
        this.input = input;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
