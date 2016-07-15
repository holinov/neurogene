package org.fruttech.neurogene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class NeuroNet {

    private List<List<Neuron>> neurons;
    private List<Integer> layerConfig = new ArrayList<>();
    private int geneIdx = 0;
    private double error;

    public NeuroNet() {
        buildNet(Arrays.asList(3, 5, 1));
    }

    public NeuroNet(NetStorableData netStorableData) {
        setStorableData(netStorableData);
    }

    public NeuroNet(List<Integer> cfg) {
        buildNet(cfg);
    }

    public NeuroNet(String json) {
        final Gson gson = new GsonBuilder().create();
        setStorableData(gson.fromJson(json, NetStorableData.class));
    }

    private void buildNet(List<Integer> cfg) {
        final Random random = new Random();
        layerConfig = cfg;
        neurons = new ArrayList<>();
        for (int layerSize : cfg) {
            final ArrayList<Neuron> layer = new ArrayList<>();
            for (int i = 0; i < layerSize; i++) {
                layer.add(new Neuron());
            }
            neurons.add(layer);
        }

        for (int i = 1, neuronsSize = neurons.size(); i < neuronsSize; i++) {
            List<Neuron> prevLayer = neurons.get(i - 1);
            List<Neuron> layer = neurons.get(i);
            linkLayers(random, prevLayer, layer);
        }
    }

    private void linkLayers(Random random, List<Neuron> inputLayer, List<Neuron> outputLayer) {
        for (Neuron middleNeuron : outputLayer) {
            for (Neuron inputNeuron : inputLayer) {
                final NeuronLink link = new NeuronLink();
                link.setInput(inputNeuron);
                link.setWeight(random.nextFloat() - 0.5f);
                middleNeuron.getInputs().add(link);
            }
        }
    }

    public List<Float> process(List<Float> inputs) {
        resetVals();
        final List<Neuron> inputLayer = inputLayer();
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setVal(inputs.get(i));
        }

        for (int i = 1; i < neurons.size(); i++) {
            final List<Neuron> layer = this.neurons.get(i);
            layer.forEach(Neuron::countVal);
        }

        final List<Float> res = new ArrayList<>();
        outputLayer().forEach(n -> res.add(n.getVal()));
        return res;
    }

    public List<Float> getWeightGenes() {
        return neurons.stream().flatMap(Collection::stream)
                .flatMap(n -> n.getInputs().stream())
                .map(NeuronLink::getWeight)
                .collect(Collectors.toList());
    }

    public void setWeightGenes(List<Float> genes) {
        geneIdx = 0;
        neurons.forEach(layer -> layer.forEach(n -> n.getInputs().forEach(link -> {
            link.setWeight(genes.get(geneIdx));
            geneIdx++;
        })));
    }

    private List<Neuron> inputLayer() {return neurons.get(0);}

    private List<Neuron> outputLayer() {return neurons.get(neurons.size() - 1);}

    private void resetVals() {
        for (List<Neuron> layer : neurons) {
            for (Neuron neuron : layer) {
                neuron.setVal(0);
            }
        }
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public NetStorableData getStorableData() {
        return new NetStorableData(layerConfig, getWeightGenes(), error);
    }

    private void setStorableData(NetStorableData netStorableData) {
        buildNet(netStorableData.layerConfig);
        setWeightGenes(netStorableData.weights);
        setError(netStorableData.error);
    }

    public static class NetStorableData {
        private List<Integer> layerConfig;
        private List<Float> weights;
        private double error;

        public NetStorableData(List<Integer> layerConfig, List<Float> weights, double error) {
            this.layerConfig = layerConfig;
            this.weights = weights;
            this.error = error;
        }

        public NetStorableData() {
        }
    }
}
