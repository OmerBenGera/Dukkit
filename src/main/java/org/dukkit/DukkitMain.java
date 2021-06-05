package org.dukkit;

import org.dukkit.patches.exceptions.InvalidPatchException;
import org.dukkit.patches.parser.PatchParser;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class DukkitMain {

    private static final Logger logger = initLogger();
    private static final String PATCHES_PATH = "patches";

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("Starting Dukkit...");

            DukkitTransformer transformer = new DukkitTransformer();

            File patchesFolder = createPatchesFolder();

            if (patchesFolder == null || !patchesFolder.isDirectory()) {
                logger.warning("Cannot create patches folder, disabling Dukkit...");
                return;
            }

            File[] patchesFiles = patchesFolder.listFiles();

            if (patchesFiles == null) {
                logger.warning("Cannot read the patches folder, disabling Dukkit...");
                return;
            }

            for (File patchFile : patchesFiles) {
                String patchFileName = patchFile.getName();
                try {
                    logger.info(String.format("Loading patch %s...", patchFileName));
                    long patchStartTime = System.currentTimeMillis();
                    transformer.addPatch(PatchParser.fromFile(patchFile).parse());
                    long patchEndTime = System.currentTimeMillis();
                    logger.info(String.format("Loading patch %s is done (Took %dms)!",
                            patchFileName, patchEndTime - patchStartTime));
                } catch (IOException ex) {
                    logger.warning(String.format("Cannot read the patch '%s'.", patchFileName));
                } catch (InvalidPatchException ex) {
                    logger.warning(String.format("Cannot parse the patch '%s': '%s'.", patchFileName, ex.getMessage()));
                }
            }

            instrumentation.addTransformer(transformer);

            long endTime = System.currentTimeMillis();

            logger.info(String.format("Dukkit was successfully done (Took %dms)!", endTime - startTime));
        }catch (Exception ex){
            logger.warning("An unexpected error occurred while running Dukkit:");
            ex.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    private static File createPatchesFolder() {
        File patchesFolder = new File(PATCHES_PATH);

        if (!patchesFolder.exists()) {
            if (!patchesFolder.mkdir())
                patchesFolder = null;
        }

        return patchesFolder;
    }

    private static Logger initLogger(){
        if(DukkitMain.logger != null){
            return DukkitMain.logger;
        }

        Logger logger = Logger.getLogger("Dukkit");
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String LOG_FORMAT = "[%1$tT %2$s]: %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(LOG_FORMAT,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        });
        logger.addHandler(handler);

        return logger;
    }

}
