package br.com.linomneto.pipeline;

import br.com.linomneto.plantuml.Debugger;

public abstract class IfFlow<T> extends Flow<T> {

    Flow<T> trueFlow;
    Flow<T> falseFlow;

    public IfFlow(String label, Flow<T> trueFlow, Flow<T> falseFlow) {
        super(label);
        this.trueFlow = trueFlow;
        this.falseFlow = falseFlow;
    }

    @Override
    public void run(Debugger debugger, T token) {
        Boolean conditionResult = this.condition(token);
        debugger.condition(this.label, conditionResult);

        if (conditionResult)
            this.trueFlow.run(debugger, token);
        else
            this.falseFlow.run(debugger, token);

        debugger.endCondition(conditionResult);
    }

    public abstract boolean condition(T token);
}
