package be.ninedocteur.caesium.cli;

import dev.sim0n.caesium.Caesium;
import dev.sim0n.caesium.PreRuntime;
import dev.sim0n.caesium.exception.CaesiumException;
import dev.sim0n.caesium.manager.MutatorManager;
import dev.sim0n.caesium.mutator.impl.*;
import dev.sim0n.caesium.mutator.impl.crasher.BadAnnotationMutator;
import dev.sim0n.caesium.mutator.impl.crasher.ImageCrashMutator;
import dev.sim0n.caesium.util.Dictionary;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Command-line entry point for running Caesium obfuscation in CI/Gradle pipelines.
 * Supports enabling mutators, configuring dictionaries, and adding classpath libraries.
 * Credits : @9e-Docteur
 */
public class CaesiumCli {
    /**
     * Parses CLI arguments, configures mutators, and runs the obfuscation pipeline.
     * @param args CLI arguments
     */
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        Logger.info("Caesium CLI started.");
        OptionSpec<Void> help = parser
                .acceptsAll(Arrays.asList("h", "help"), "Show help")
                .forHelp();

        OptionSpec<File> input = parser
                .acceptsAll(Arrays.asList("i", "input"), "Input JAR path")
                .withRequiredArg()
                .required()
                .ofType(File.class);

        OptionSpec<File> output = parser
                .acceptsAll(Arrays.asList("o", "output"), "Output JAR path")
                .withRequiredArg()
                .ofType(File.class);

        OptionSpec<Void> overwrite = parser
                .accepts("overwrite", "Overwrite output JAR if it already exists");

        OptionSpec<String> dictionary = parser
                .accepts("dictionary", "Dictionary: abc, ABC, III, numbers, wack")
                .withRequiredArg()
                .ofType(String.class);

        OptionSpec<String> library = parser
                .acceptsAll(Arrays.asList("l", "library"), "Dependency JAR to add to classpath (repeatable)")
                .withRequiredArg()
                .ofType(String.class);

        OptionSpec<Void> stringMutation = parser.accepts("string", "Enable string mutation");
        OptionSpec<String> stringExclude = parser
                .accepts("string-exclude", "String literal to exclude from mutation (repeatable)")
                .withRequiredArg()
                .ofType(String.class);

        OptionSpec<Void> controlFlow = parser.accepts("control-flow", "Enable control flow mutation");
        OptionSpec<Void> numberMutation = parser.accepts("number", "Enable number mutation");
        OptionSpec<Void> polymorph = parser.accepts("polymorph", "Enable polymorph mutation");
        OptionSpec<Void> reference = parser.accepts("reference", "Enable reference mutation");
        OptionSpec<Void> classFolder = parser.accepts("class-folder", "Enable class folder mutation");
        OptionSpec<Void> trim = parser.accepts("trim", "Enable trim mutation");
        OptionSpec<Void> shuffle = parser.accepts("shuffle", "Enable member shuffle mutation");

        OptionSpec<Void> crasher = parser.accepts("crasher", "Enable crasher (bad annotation + image crash)");
        OptionSpec<Void> badAnnotation = parser.accepts("bad-annotation", "Enable bad annotation mutation");
        OptionSpec<Void> imageCrash = parser.accepts("image-crash", "Enable image crash mutation");

        OptionSpec<String> lineNumber = parser
                .accepts("line-number", "Line numbers: remove or scramble")
                .withRequiredArg()
                .ofType(String.class);

        OptionSpec<String> localVariables = parser
                .accepts("local-variables", "Local variables: remove or rename")
                .withRequiredArg()
                .ofType(String.class);

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (Exception e) {
            Logger.error("Failed to parse arguments: " + e.getMessage());
            printHelp(parser);
            System.exit(-1);
            return;
        }

        if (options.has(help)) {
            printHelp(parser);
            return;
        }

        File inputFile = input.value(options);
        if (!inputFile.exists()) {
            Logger.error("Input file not found: " + inputFile.getAbsolutePath());
            System.exit(-1);
            return;
        }

        File outputFile = options.has(output) ? output.value(options) : defaultOutputFile(inputFile);
        outputFile = ensureOutput(outputFile, options.has(overwrite));
        if (outputFile == null) {
            System.exit(-1);
            return;
        }

        Logger.info("Input: " + inputFile.getAbsolutePath());
        Logger.info("Output: " + outputFile.getAbsolutePath());

        addBootClassPath();
        for (String lib : options.valuesOf(library)) {
            File libFile = new File(lib);
            if (!libFile.exists()) {
                Logger.error("Library not found: " + libFile.getAbsolutePath());
                System.exit(-1);
                return;
            }
            PreRuntime.libraries.addElement(libFile.getAbsolutePath());
        }

        try {
            PreRuntime.loadInput(inputFile.getAbsolutePath());
        } catch (CaesiumException e) {
            Logger.error("Failed to load input: " + e.getMessage());
            System.exit(-1);
            return;
        }

        PreRuntime.loadClassPath();
        PreRuntime.buildInheritance();

        Caesium caesium = new Caesium();

        if (options.has(dictionary)) {
            try {
                caesium.setDictionary(parseDictionary(dictionary.value(options)));
            } catch (IllegalArgumentException e) {
                Logger.error(e.getMessage());
                System.exit(-1);
                return;
            }
        }

        MutatorManager mutatorManager = caesium.getMutatorManager();

        boolean enableString = options.has(stringMutation) || !options.valuesOf(stringExclude).isEmpty();
        if (enableString) {
            StringMutator stringMutator = mutatorManager.getMutator(StringMutator.class);
            stringMutator.setEnabled(true);
            for (String exclusion : options.valuesOf(stringExclude)) {
                stringMutator.getExclusions().add(exclusion);
            }
        }

        if (options.has(controlFlow)) {
            mutatorManager.getMutator(ControlFlowMutator.class).setEnabled(true);
        }

        if (options.has(numberMutation)) {
            mutatorManager.getMutator(NumberMutator.class).setEnabled(true);
        }

        if (options.has(polymorph)) {
            mutatorManager.getMutator(PolymorphMutator.class).setEnabled(true);
        }

        if (options.has(reference)) {
            mutatorManager.getMutator(ReferenceMutator.class).setEnabled(true);
        }

        if (options.has(classFolder)) {
            mutatorManager.getMutator(ClassFolderMutator.class).setEnabled(true);
        }

        if (options.has(trim)) {
            mutatorManager.getMutator(TrimMutator.class).setEnabled(true);
        }

        if (options.has(shuffle)) {
            mutatorManager.getMutator(ShuffleMutator.class).setEnabled(true);
        }

        boolean enableBadAnnotation = options.has(crasher) || options.has(badAnnotation);
        boolean enableImageCrash = options.has(crasher) || options.has(imageCrash);

        if (enableBadAnnotation) {
            mutatorManager.getMutator(BadAnnotationMutator.class).setEnabled(true);
        }

        if (enableImageCrash) {
            mutatorManager.getMutator(ImageCrashMutator.class).setEnabled(true);
        }

        if (options.has(lineNumber)) {
            LineNumberMutator lineNumberMutator = mutatorManager.getMutator(LineNumberMutator.class);
            try {
                lineNumberMutator.setType(parseLineNumberType(lineNumber.value(options)));
            } catch (IllegalArgumentException e) {
                Logger.error(e.getMessage());
                System.exit(-1);
                return;
            }
            lineNumberMutator.setEnabled(true);
        }

        if (options.has(localVariables)) {
            LocalVariableMutator localVariableMutator = mutatorManager.getMutator(LocalVariableMutator.class);
            try {
                localVariableMutator.setType(parseLocalVariableType(localVariables.value(options)));
            } catch (IllegalArgumentException e) {
                Logger.error(e.getMessage());
                System.exit(-1);
                return;
            }
            localVariableMutator.setEnabled(true);
        }

        try {
            int exit = caesium.run(inputFile, outputFile);
            if (exit != 0) {
                Caesium.getLogger().warn("Exited with non default exit code.");
            } else {
                Logger.success("Obfuscation completed successfully.");
            }
        } catch (Exception e) {
            Logger.error("Obfuscation failed.", e);
            System.exit(-1);
        }
    }

    private static void printHelp(OptionParser parser) {
        try {
            parser.printHelpOn(System.out);
        } catch (IOException e) {
            Logger.error("Unable to print help: " + e.getMessage());
        }
    }

    /**
     * Loads boot classpath JARs into the pre-runtime library list.
     */
    private static void addBootClassPath() {
        String path = System.getProperty("sun.boot.class.path");
        if (path == null || path.trim().isEmpty()) {
            return;
        }

        String[] pathFiles = path.split(File.pathSeparator);
        for (String lib : pathFiles) {
            if (lib.endsWith(".jar")) {
                PreRuntime.libraries.addElement(lib);
            }
        }
    }

    /**
     * Builds the default output file name based on the input JAR.
     * @param inputFile Input JAR file
     * @return Suggested output file path
     */
    private static File defaultOutputFile(File inputFile) {
        String name = inputFile.getName();
        String outputName;
        if (name.endsWith(".jar")) {
            outputName = name.substring(0, name.length() - 4) + "-mutated.jar";
        } else {
            outputName = name + "-mutated.jar";
        }

        File parent = inputFile.getParentFile();
        return parent == null ? new File(outputName) : new File(parent, outputName);
    }

    /**
     * Ensures the output file can be written, optionally backing up an existing file.
     * @param outputFile Output file path
     * @param overwrite Whether to overwrite instead of backing up
     * @return The output file to use, or null when it cannot be created
     */
    private static File ensureOutput(File outputFile, boolean overwrite) {
        if (!outputFile.exists() || overwrite) {
            return outputFile;
        }

        File parent = outputFile.getParentFile();
        File baseDir = parent == null ? new File(".") : parent;
        File target = outputFile;

        File[] siblings = baseDir.listFiles();
        int max = siblings == null ? 0 : siblings.length;
        for (int i = 0; i < max; i++) {
            String filePath = String.format("%s.BACKUP-%d", outputFile.getAbsolutePath(), i);
            File backup = new File(filePath);

            if (!backup.exists() && outputFile.renameTo(backup)) {
                target = new File(outputFile.getAbsolutePath());
                break;
            }
        }

        if (target.exists()) {
            Logger.error("Output file already exists and could not be renamed: " + outputFile.getAbsolutePath());
            return null;
        }

        return target;
    }

    /**
     * Parses a dictionary option into a {@link Dictionary} value.
     * @param value Dictionary string
     * @return Parsed dictionary enum
     */
    private static Dictionary parseDictionary(String value) {
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Dictionary value cannot be empty.");
        }

        if (normalized.equals("ABC")) {
            return Dictionary.ABC;
        }
        if (normalized.equals("abc")) {
            return Dictionary.ABC_LOWERCASE;
        }

        String lower = normalized.toLowerCase();
        switch (lower) {
            case "abc":
            case "abc_lowercase":
            case "lower":
            case "lowercase":
                return Dictionary.ABC_LOWERCASE;
            case "upper":
            case "uppercase":
            case "abc_uppercase":
            case "abc_upper":
            case "abc_uppercase_only":
            case "abcupper":
            case "abc-upper":
            case "abc-upper-case":
                return Dictionary.ABC;
            case "iii":
                return Dictionary.III;
            case "numbers":
            case "number":
            case "123":
            case "num":
                return Dictionary.NUMBERS;
            case "wack":
                return Dictionary.WACK;
            default:
                throw new IllegalArgumentException("Unknown dictionary: " + value);
        }
    }

    /**
     * Parses the line-number option into an internal type id.
     * @param value Line number option string
     * @return 0 for remove, 1 for scramble
     */
    private static int parseLineNumberType(String value) {
        String normalized = value.trim().toLowerCase();
        switch (normalized) {
            case "remove":
                return 0;
            case "scramble":
                return 1;
            default:
                throw new IllegalArgumentException("Unknown line-number option: " + value);
        }
    }

    /**
     * Parses the local-variables option into an internal type id.
     * @param value Local variable option string
     * @return 0 for remove, 1 for rename
     */
    private static int parseLocalVariableType(String value) {
        String normalized = value.trim().toLowerCase();
        switch (normalized) {
            case "remove":
                return 0;
            case "rename":
                return 1;
            default:
                throw new IllegalArgumentException("Unknown local-variables option: " + value);
        }
    }
}
