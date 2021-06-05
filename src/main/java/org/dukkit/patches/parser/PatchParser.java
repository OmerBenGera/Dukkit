package org.dukkit.patches.parser;

import org.dukkit.DukkitMain;
import org.dukkit.patches.Patch;
import org.dukkit.patches.exceptions.InvalidPatchException;
import org.dukkit.patches.exceptions.InvalidPatchSectionTypeException;
import org.dukkit.patches.exceptions.MissingPatchSectionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class PatchParser {

    private static final Pattern LINE_SECTION_SPLITTER_PATTERN = Pattern.compile(": ");
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile(System.lineSeparator());
    private static final Pattern LINE_SECTION_STRING_PATTERN = Pattern.compile("'(.*)'");
    private static final Pattern LIST_LINE_INDICATOR_PATTERN = Pattern.compile("-");

    private static final String SOURCE_SECTION_NAME = "Source";
    private static final String SOURCE_SECTION_START_INDICATOR = "";
    private static final String LIST_LINE_INDICATOR = "-";

    private static final String PATCH_TYPE_SECTION = "Patch";

    private final List<String> contents;

    public PatchParser(List<String> contents){
        this.contents = Collections.unmodifiableList(contents);
    }

    /**
     * Creates a new patch file from the contents of the parser.
     * @throws InvalidPatchException when there are issues with parsing the patch.
     */
    public Patch parse() throws InvalidPatchException{
        Map<String, Object> linesValues = parseLines(contents);
        Object patchTypeName = linesValues.get(PATCH_TYPE_SECTION);

        if(patchTypeName instanceof String) {
            try {
                PatchType patchType = PatchType.valueOf((String) patchTypeName);

                try{
                    return patchType.createPatch(linesValues);
                }catch (MissingPatchSectionException | InvalidPatchSectionTypeException ex){
                    throw new InvalidPatchException(ex.getMessage());
                }
            } catch (IllegalArgumentException ignored) { }
        }

        throw new InvalidPatchException("Invalid patch type '" + patchTypeName + "'.");
    }

    /**
     * Create a PatchParser object from contents of a file.
     * @param file The file to read contents from.
     * @return The new PatchParser for the file.
     * @throws IOException when cannot read the file.
     */
    public static PatchParser fromFile(File file) throws IOException {
        List<String> contents = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = reader.readLine()) != null){
                contents.add(NEW_LINE_PATTERN.matcher(line).replaceAll("").trim());
            }
        }

        return new PatchParser(contents);
    }

    private static Map<String, Object> parseLines(List<String> lines){
        Map<String, Object> linesValues = new HashMap<>();

        for(int i = 0; i < lines.size(); ++i){
            String currentLine = lines.get(i);

            // Source section starts after a new empty line until the end of the patch.
            if(currentLine.equals(SOURCE_SECTION_START_INDICATOR)){
                StringBuilder bodyValue = new StringBuilder();

                do{
                    ++i;
                    if(i < lines.size()) {
                        currentLine = lines.get(i);
                        if(!currentLine.equals("}")){
                            if(bodyValue.length() != 0){
                                bodyValue.append(System.lineSeparator());
                            }
                            bodyValue.append(currentLine);
                        }
                    }
                }while (i < lines.size());

                if(bodyValue.length() != 0){
                    linesValues.put(SOURCE_SECTION_NAME, bodyValue);
                }
            }

            else {
                String[] lineSections = LINE_SECTION_SPLITTER_PATTERN.split(currentLine);
                if(lineSections.length == 2){
                    String sectionName = lineSections[0].toLowerCase();

                    if(sectionName.contains(" ")){
                        DukkitMain.getLogger().warning(String.format("Cannot parse the line '%s' (#%d).",
                                currentLine, i));
                    }
                    else {
                        String sectionValue = lineSections[1];
                        if (LINE_SECTION_STRING_PATTERN.matcher(sectionValue).matches()) {
                            linesValues.put(sectionName, sectionValue);
                        } else {
                            try {
                                BigDecimal decimalValue = new BigDecimal(sectionValue);
                                linesValues.put(sectionName, decimalValue);
                            } catch (NumberFormatException ex) {
                                DukkitMain.getLogger().warning(String.format("Cannot parse the line '%s' (#%d).",
                                        currentLine, i));
                            }
                        }
                    }
                }
                else if(currentLine.endsWith(":")){
                    String listName = currentLine.substring(0, currentLine.length() - 1).toLowerCase();

                    if(listName.isEmpty()){
                        DukkitMain.getLogger().warning(String.format("Cannot parse the line '%s' (#%d).",
                                currentLine, i));
                    }
                    else {
                        List<String> listValue = new ArrayList<>();

                        do {
                            ++i;
                            if (i < lines.size()) {
                                currentLine = lines.get(i);
                                if(currentLine.startsWith(LIST_LINE_INDICATOR)){
                                    listValue.add(LIST_LINE_INDICATOR_PATTERN.matcher(currentLine)
                                            .replaceAll("").trim());
                                }
                            }
                        } while (i < lines.size() && currentLine.startsWith(LIST_LINE_INDICATOR));

                        if (!listValue.isEmpty()) {
                            linesValues.put(listName, listValue);
                        }
                    }

                }
                else{
                    DukkitMain.getLogger().warning(String.format("Cannot parse the line '%s' (#%d).", currentLine, i));
                }
            }
        }

        return linesValues;
    }

}
