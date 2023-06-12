package br.com.linomneto.pipeline;

import br.com.linomneto.plantuml.Debugger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class BroadcastFlow<T> extends Flow<T> {

    Flow<T>[] flows;
    Boolean ignoreError;

    public BroadcastFlow(String label, Boolean ignoreError, Flow<T> ... flows) {
        super(label);
        this.ignoreError = ignoreError;
        this.flows = flows;
    }

    @Override
    public void run(Debugger debugger, T token) {
        debugger.action("async: " + this.label);

        Map<FutureTask<Void>, Debugger> map = new HashMap<>();
        ExecutorService exe = Executors.newFixedThreadPool(this.flows.length);

        for (Flow<T> f : this.flows) {
            Debugger forkDebugger = debugger.newFork();
            FutureTask<Void> t = new FutureTask<Void>(() -> {
                f.run(forkDebugger, token);
                return null;
            });
            map.put(t, forkDebugger);
            exe.execute(t);
        }

        for (FutureTask<Void> t : map.keySet()) {
            try {
                t.get();
            }
            catch (ExecutionException | InterruptedException e) {
                if (!this.ignoreError)
                    break;
            }
            debugger.fork(map.get(t));
        }
        debugger.mergeForks();
    }

}
