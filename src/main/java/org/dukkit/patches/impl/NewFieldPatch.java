package org.dukkit.patches.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import org.dukkit.patches.Patch;
import org.dukkit.utils.CompilerUtils;

import java.util.Collection;
import java.util.Collections;

public final class NewFieldPatch implements Patch {

    private final Collection<String> imports;
    private final String fieldSignature;
    private final String getterMethodName;
    private final String setterMethodName;

    public NewFieldPatch(Collection<String> imports, String fieldSignature, String getterMethodName,
                         String setterMethodName){
        this.imports = Collections.unmodifiableCollection(imports);
        this.fieldSignature = fieldSignature;
        this.getterMethodName = getterMethodName;
        this.setterMethodName = setterMethodName;
    }

    @Override
    public void applyPatch(CtClass ctClass) throws CannotCompileException {
        CompilerUtils.importPackages(ctClass, this.imports);

        CtField ctField = CompilerUtils.makeField(fieldSignature, ctClass);
        ctClass.addField(ctField);

        if(getterMethodName != null){
            ctClass.addMethod(CtNewMethod.getter(getterMethodName, ctField));
        }

        if(setterMethodName != null){
            ctClass.addMethod(CtNewMethod.setter(setterMethodName, ctField));
        }
    }

}
