package br.com.linomneto.pipeline;

import br.com.linomneto.plantuml.Debugger;

public class Pipeline<T> {

    String title;
    Flow[] flows;

    public Pipeline(String title, Flow<T>... flows) {
        this.title = title;
        this.flows = flows;
    }

    public String run(T token) {
        Debugger debugger = new Debugger(this.title);

        for (Flow flow : this.flows)
            flow.run(debugger, token);

        return debugger.end(this.title);
    }

}
