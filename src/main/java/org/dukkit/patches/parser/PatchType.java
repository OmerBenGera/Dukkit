package org.dukkit.patches.parser;

import org.dukkit.patches.Patch;
import org.dukkit.patches.exceptions.InvalidPatchSectionTypeException;
import org.dukkit.patches.exceptions.MissingPatchSectionException;
import org.dukkit.patches.impl.NewFieldPatch;

import java.util.Collection;
import java.util.Map;

public enum PatchType {


    NEW_FIELD{

        private static final String SOURCE_SECTION = "Source";
        private static final String IMPORTS_SECTION = "Imports";
        private static final String GETTER_SECTION = "Getter";
        private static final String SETTER_SECTION = "Setter";

        @Override
        public Patch createPatch(Map<String, Object> linesValues)
                throws MissingPatchSectionException, InvalidPatchSectionTypeException {
            Object source = linesValues.get(SOURCE_SECTION);
            Object imports = linesValues.get(IMPORTS_SECTION);
            Object getter = linesValues.get(GETTER_SECTION);
            Object setter = linesValues.get(SETTER_SECTION);

            checkNotMissing(source, "Cannot find source for this patch.");
            checkType(source, String.class, "Cannot find source for this patch.");

            checkType(imports, Collection.class, "Cannot parse imports for this patch.");
            checkType(getter, String.class, "Cannot parse getter method for this patch.");
            checkType(setter, String.class, "Cannot parse setter method for this patch.");

            // noinspection unchecked
            return new NewFieldPatch((Collection<String>) imports, (String) source, (String) getter, (String) setter);
        }

    };

    public abstract Patch createPatch(Map<String, Object> linesValues)
            throws MissingPatchSectionException, InvalidPatchSectionTypeException;

    private static void checkType(Object obj, Class<?> type, String message) throws InvalidPatchSectionTypeException {
        if(obj != null && !obj.getClass().isAssignableFrom(type)){
            throw new InvalidPatchSectionTypeException(message);
        }
    }

    private static void checkNotMissing(Object obj, String message) throws MissingPatchSectionException {
        if(obj == null){
            throw new MissingPatchSectionException(message);
        }
    }

}
