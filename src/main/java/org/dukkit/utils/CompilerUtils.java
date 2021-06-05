package org.dukkit.utils;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

import java.util.Collection;

public final class CompilerUtils {

    private CompilerUtils(){}

    public static CtMethod makeMethod(String src, CtClass ctClass) throws CannotCompileException {
        return makeMember(src, ctClass, CtMethod.class);
    }

    public static CtField makeField(String src, CtClass ctClass) throws CannotCompileException {
        return makeMember(src, ctClass, CtField.class);
    }

    public static void importPackages(CtClass ctClass, Collection<String> imports){
        imports.forEach(ctClass.getClassPool()::importPackage);
    }

    private static <T extends CtMember> T makeMember(String src, CtClass ctClass, Class<T> memberType)
            throws CannotCompileException{
        Javac compiler = new Javac(ctClass);
        try {
            CtMember member = compiler.compile(src);
            if(member.getClass().isAssignableFrom(memberType))
                return memberType.cast(member);
        }
        catch (CompileError e) {
            throw new CannotCompileException(e);
        }

        throw new CannotCompileException("not a method");
    }

}
