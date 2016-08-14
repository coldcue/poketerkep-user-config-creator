package com.botcrator;

import com.botcrator.exception.ConnectionResetException;
import com.botcrator.exception.TwitchBlockException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Main {
    public static File firefoxProfile = null;
    private static Logger logger = Logger.getGlobal();
    private static final String fileName = "users.xml";
    private static TorRunnerInstance[] torRunnerInstances;
    private static Map<TorRunnerInstance, WebRegisterInstance> webRegisterInstanceMap = new HashMap<>();

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static void main(String[] args) throws InterruptedException {

        int instances = 1;
        if (args.length > 0) {
            instances = Integer.parseInt(args[0]);
        }

        if (args.length > 1) {
            firefoxProfile = new File(args[1]);
        }

        torRunnerInstances = new TorRunnerInstance[instances];

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
                    entry.setValue(new WebRegisterInstance(torRunnerInstance.getProxyPort()));
                    entry.getValue().start();

                } else if (!registerInstance.isAlive() && registerInstance.isSuccess()) {
                    //If success
                    torRunnerInstance.incrementUseCount();
                    entry.setValue(null);

                } else if (!registerInstance.isAlive() &&
                        (registerInstance.getLastException().getClass() == TwitchBlockException.class
                                || registerInstance.getLastException().getClass() == ConnectionResetException.class)) {
                    //If twitch block
                    torRunnerInstance.newCicuit();
                    entry.setValue(null);
                } else if (!registerInstance.isAlive()) {
                    registerInstance.interrupt();
                    entry.setValue(new WebRegisterInstance(torRunnerInstance.getProxyPort()));
                    entry.getValue().start();
                }
            }

            System.gc();
            Thread.sleep(500);
        }
    }
}
