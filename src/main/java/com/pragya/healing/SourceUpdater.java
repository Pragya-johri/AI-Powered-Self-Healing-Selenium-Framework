package com.pragya.healing;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import java.nio.file.*;
import java.util.*;

/** Restricts edits to the unique matching By.xpath declaration in the calling page class. */
public final class SourceUpdater {
    private SourceUpdater() {}

    public static String update(String oldXpath, String newXpath, StackTraceElement[] trace) throws Exception {
        Path root = Paths.get("").toAbsolutePath().normalize();
        for (StackTraceElement frame : trace) {
            String className = frame.getClassName();
            if (!className.startsWith("com.pragya.pages.")) continue;
            String topLevel = className.split("\\$")[0];
            String relative = topLevel.replace('.', '/') + ".java";
            Path file = root.resolve("src/test/java").resolve(relative).normalize();
            if (!file.startsWith(root) || !Files.isRegularFile(file)) continue;
            CompilationUnit cu = StaticJavaParser.parse(file);
            List<StringLiteralExpr> matches = new ArrayList<>();
            for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
                if (!call.getNameAsString().equals("xpath") || call.getArguments().size() != 1) continue;
                if (call.getScope().isEmpty() || !call.getScope().get().toString().equals("By")) continue;
                if (!(call.getArgument(0) instanceof StringLiteralExpr literal)) continue;
                if (literal.getValue().equals(oldXpath)) matches.add(literal);
            }
            if (matches.size() != 1) {
                throw new IllegalStateException("Expected exactly one By.xpath declaration in " + file + ", found " + matches.size() + "; refusing source update");
            }
            LexicalPreservingPrinter.setup(cu);
            matches.get(0).setString(newXpath);
            // Review mode is the default. Retry uses the healed locator in memory.
            // AUTO mode must be explicitly opted into for this synthetic demo.
           String mode = System.getenv().getOrDefault("HEALING_MODE", "REVIEW");

           // String mode = "AUTO";
            System.out.println("[HEALING MODE] " + mode);
            if (!"AUTO".equalsIgnoreCase(mode)) {
                Path proposals = root.resolve("reports");
                Files.createDirectories(proposals);
                Path report = proposals.resolve("proposed-patches.txt");
                String entry = "File: " + file + System.lineSeparator()
                    + "Old XPath: " + oldXpath + System.lineSeparator()
                    + "New XPath: " + newXpath + System.lineSeparator()
                    + "Mode: REVIEW (source unchanged)" + System.lineSeparator() + System.lineSeparator();
                Files.writeString(report, entry, java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.APPEND);
                System.out.println("[PATCH PROPOSED] " + file + " (review reports/proposed-patches.txt)");
                return file.toString();
            }
            Path backup = file.resolveSibling(file.getFileName() + ".bak");
            Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
           // Files.writeString(file, LexicalPreservingPrinter.print(cu));
            Files.writeString(file, cu.toString());
            System.out.println("[FILE CONTENT VERIFIED] "
                    + Files.readString(file).contains(newXpath));
            System.out.println("[SOURCE UPDATED] " + file + " (backup: " + backup.getFileName() + ")");
            return file.toString();
        }
        throw new IllegalStateException("No supported calling Page Object in stack trace; source left unchanged");
    }
}
