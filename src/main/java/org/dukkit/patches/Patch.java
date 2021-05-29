package org.dukkit.patches;

import javassist.CannotCompileException;
import javassist.CtClass;

public interface Patch {

    /**
     * Apply a patch on a class.
     * @param ctClass The class to apply the patch on.
     */
    void applyPatch(CtClass ctClass) throws CannotCompileException;

}
