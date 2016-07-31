package org.fruttech.neurogene;

import java.util.List;

public interface EvolverHandler {
    void epoch(int epoch, NeuroNet winner1, List<NeuroNet> population, List<DataLine> data);
}
