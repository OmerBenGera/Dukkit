package org.dukkit;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public final class DukkitMain {

    private static final Logger logger = Logger.getLogger("Dukkit");

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        DukkitTransformer transformer = new DukkitTransformer();
        instrumentation.addTransformer(transformer);
    }

    public static Logger getLogger() {
        return logger;
    }

}
