package org.dukkit.patches.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import org.dukkit.patches.Patch;
import org.dukkit.utils.CompilerUtils;

import java.util.Collection;
import java.util.Collections;

public final class NewMethodPatch implements Patch {

    private final Collection<String> imports;
    private final String methodSignature;

    public NewMethodPatch(Collection<String> imports, String methodSignature){
        this.imports = Collections.unmodifiableCollection(imports);
        this.methodSignature = methodSignature;
    }

    @Override
    public void applyPatch(CtClass ctClass) throws CannotCompileException {
        CompilerUtils.importPackages(ctClass, this.imports);
        ctClass.addMethod( CompilerUtils.makeMethod(methodSignature, ctClass));
    }

}
