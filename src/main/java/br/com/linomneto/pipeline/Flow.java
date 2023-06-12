package br.com.linomneto.pipeline;

import br.com.linomneto.plantuml.Debugger;

public class Flow<T> {

    Flow<T>[] flows;
    protected String label;

    public Flow(String label) {
        this.label = label;
    }

    public Flow(String label, Flow<T> ... flows) {
        this.label = label;
        this.flows = flows;
    }

    public void run(Debugger debugger, T token) {
        debugger.action(this.label);
        for (Flow<T> f : this.flows)
            f.run(debugger, token);
    }
}
