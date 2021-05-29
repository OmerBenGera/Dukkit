package org.dukkit.patches.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import org.dukkit.patches.Patch;

import java.util.Collection;
import java.util.Collections;

public final class NewFieldPatch implements Patch {

    private final Collection<String> imports;
    private final CtClass fieldType;
    private final String fieldName;
    private final int accessModifiers;
    private final String initValue;
    private final String getterMethodName;
    private final String setterMethodName;

    public NewFieldPatch(Collection<String> imports, CtClass fieldType, String fieldName, int accessModifiers,
                         String initValue, String getterMethodName, String setterMethodName){
        this.imports = Collections.unmodifiableCollection(imports);
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.accessModifiers = accessModifiers;
        this.initValue = initValue;
        this.getterMethodName = getterMethodName;
        this.setterMethodName = setterMethodName;
    }

    @Override
    public void applyPatch(CtClass ctClass) throws CannotCompileException {
        for(String packageName : this.imports){
            ctClass.getClassPool().importPackage(packageName);
        }

        CtField newField = new CtField(fieldType, fieldName, ctClass);
        newField.setModifiers(accessModifiers);

        ctClass.addField(newField, initValue);

        if(getterMethodName != null){
            ctClass.addMethod(CtNewMethod.getter(getterMethodName, newField));
        }

        if(setterMethodName != null){
            ctClass.addMethod(CtNewMethod.setter(setterMethodName, newField));
        }
    }

}
