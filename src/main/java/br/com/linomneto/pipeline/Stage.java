package br.com.linomneto.pipeline;

import br.com.linomneto.plantuml.Debugger;
import com.google.gson.Gson;

public abstract class Stage<T> extends Flow<T> {

    public Stage(String label) {
        super(label);
    }

    @Override
    public void run(Debugger debugger, T token) {
        Object o = stageRun(token);
        if (o == null)
            debugger.action(this.label);
        else
            debugger.action(this.label, new Gson().toJson(o));
    }

    public abstract Object stageRun(T token);
}
