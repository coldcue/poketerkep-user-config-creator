package com.botcreator;

import com.botcreator.exception.ConnectionResetException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Main {
    public static String workerName;
    private static Logger logger = Logger.getGlobal();
    private static Map<TorRunnerInstance, WebRegisterInstance> webRegisterInstanceMap = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        int instances = 1;

        if (args.length > 0) {
            workerName = args[0];
        }

        if (workerName == null) {
            workerName = "default";
        }

        TorRunnerInstance[] torRunnerInstances = new TorRunnerInstance[instances];

        //noinspection InfiniteLoopStatement
        while (true) {

            //Check tor instances
            for (int i = 0; i < instances; i++) {
                TorRunnerInstance runnerInstance = torRunnerInstances[i];
                if (runnerInstance == null || !runnerInstance.isAlive()) {
                    TorRunnerInstance torRunnerInstance = new TorRunnerInstance(i);
                    torRunnerInstances[i] = torRunnerInstance;
                    torRunnerInstance.start();
                }

                if (!webRegisterInstanceMap.containsKey(runnerInstance)) {
                    webRegisterInstanceMap.put(torRunnerInstances[i], null);
                }
            }

            //Check web instances
            for (Map.Entry<TorRunnerInstance, WebRegisterInstance> entry : webRegisterInstanceMap.entrySet()) {

                WebRegisterInstance registerInstance = entry.getValue();
                TorRunnerInstance torRunnerInstance = entry.getKey();

                //If tor is rate limited, continue
                if (torRunnerInstance.isRateLimited() || !torRunnerInstance.isStarted()) {
                    continue;
                }

                if (registerInstance == null) {
                    //If not started, add a new instance
                    entry.setValue(new WebRegisterInstance(torRunnerInstance));
                    entry.getValue().start();

                } else if (!registerInstance.isAlive() && registerInstance.isSuccess()) {
                    //If success
                    torRunnerInstance.incrementUseCount();
                    entry.setValue(null);

                } else if (!registerInstance.isAlive() &&
                        (registerInstance.getLastException().getClass() == ConnectionResetException.class)) {
                    //If twitch block
                    torRunnerInstance.newCircuit();
                    entry.setValue(null);
                } else if (!registerInstance.isAlive()) {
                    registerInstance.interrupt();
                    entry.setValue(new WebRegisterInstance(torRunnerInstance));
                    entry.getValue().start();
                }
            }

            System.gc();
            Thread.sleep(500);
        }
    }
}
