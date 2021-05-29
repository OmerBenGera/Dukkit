package org.dukkit.patches.parser;

import org.dukkit.patches.Patch;
import org.dukkit.patches.exceptions.InvalidPatchSectionTypeException;
import org.dukkit.patches.exceptions.MissingPatchSectionException;
import org.dukkit.patches.impl.NewFieldPatch;

import java.util.Map;

public enum PatchType {

    NEW_FIELD{

        private static final String TYPE_SECTION = "Type";
        private static final String NAME_SECTION = "Name";
        private static final String MODIFIERS_SECTION = "Modifiers";
        private static final String INIT_VALUE_SECTION = "InitValue";
        private static final String GETTER_SECTION = "Getter";
        private static final String SETTER_SECTION = "Setter";

        @Override
        public Patch createPatch(Map<String, Object> linesValues)
                throws MissingPatchSectionException, InvalidPatchSectionTypeException {
            Object type = linesValues.get(TYPE_SECTION);
            Object name = linesValues.get(NAME_SECTION);
            Object modifiers = linesValues.get(MODIFIERS_SECTION);
            Object initValue = linesValues.get(INIT_VALUE_SECTION);
            Object getter = linesValues.get(GETTER_SECTION);
            Object setter = linesValues.get(SETTER_SECTION);

            return null;
        }

    };

    public abstract Patch createPatch(Map<String, Object> linesValues)
            throws MissingPatchSectionException, InvalidPatchSectionTypeException;

}
