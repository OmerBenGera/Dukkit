package org.dukkit;

import org.dukkit.patches.Patch;
import org.dukkit.patches.exceptions.InvalidPatchException;
import org.dukkit.patches.parser.PatchParser;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public final class DukkitMain {

    private static final Logger logger = Logger.getLogger("Dukkit");
    private static final String PATCHES_PATH = "patches";

    public static void premain(String agentArgs, Instrumentation instrumentation) {
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
                long startTime = System.nanoTime();
                transformer.addPatch(PatchParser.fromFile(patchFile).parse());
                logger.info(String.format("Loading patch %s is done (Took %dms)!",
                        patchFileName, System.nanoTime() - startTime));
            } catch (IOException ex) {
                logger.warning(String.format("Cannot read the patch '%s'.", patchFileName));
            } catch (InvalidPatchException ex) {
                logger.warning(String.format("Cannot parse the patch '%s': '%s'.", patchFileName, ex.getMessage()));
            }
        }

        instrumentation.addTransformer(transformer);
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

}
