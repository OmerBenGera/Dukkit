package org.dukkit.patches.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import org.dukkit.patches.Patch;

import java.util.Collection;
import java.util.Collections;

public final class NewMethodPatch implements Patch {

    private final Collection<String> imports;
    private final int accessModifiers;
    private final CtClass returnType;
    private final String methodName;
    private final CtClass[] parameters;
    private final CtClass[] exceptions;
    private final String body;

    public NewMethodPatch(Collection<String> imports, int accessModifiers, CtClass returnType, String methodName,
                          CtClass[] parameters, CtClass[] exceptions, String body){
        this.imports = Collections.unmodifiableCollection(imports);
        this.accessModifiers = accessModifiers;
        this.returnType = returnType;
        this.methodName = methodName;
        this.parameters = parameters;
        this.exceptions = exceptions;
        this.body = body;
    }

    @Override
    public void applyPatch(CtClass ctClass) throws CannotCompileException {
        for(String packageName : this.imports){
            ctClass.getClassPool().importPackage(packageName);
        }

        CtMethod newMethod = CtNewMethod.make(accessModifiers, returnType, methodName, parameters, exceptions, body, ctClass);

        ctClass.addMethod(newMethod);
    }

}
