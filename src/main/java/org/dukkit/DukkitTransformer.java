package org.dukkit;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.dukkit.patches.Patch;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class DukkitTransformer implements ClassFileTransformer {

    private final Map<String, Collection<Patch>> patches = new HashMap<>();

    public void addPatch(Patch patch) {
        for (String path : patch.getTargetClasses()) {
            patches.computeIfAbsent(path.toLowerCase(), classPath -> new ArrayList<>()).add(patch);
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String fixedClassName = className.replace("/", ".");

        Collection<Patch> patchesToApply = patches.get(fixedClassName);

        if (patchesToApply != null) {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass;

            try {
                ctClass = classPool.get(fixedClassName);
            } catch (NotFoundException ex) {
                System.err.println("Failed to transform class " + fixedClassName + ":");
                ex.printStackTrace();
                return null;
            }

            for (Patch patch : patchesToApply) {
                try {
                    patch.applyPatch(ctClass);
                } catch (CannotCompileException ex) {
                    System.err.println("Failed to apply patch " + patch + ":");
                    ex.printStackTrace();
                }
            }

            try {
                return ctClass.toBytecode();
            } catch (IOException | CannotCompileException ex) {
                System.err.println("Failed to build class " + fixedClassName + ":");
                ex.printStackTrace();
            }
        }

        return null;
    }

}
