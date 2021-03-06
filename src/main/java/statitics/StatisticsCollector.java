package statitics;

import static statitics.StatisticsHolder.PATH;
import static statitics.StatisticsHolder.PROJECT;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class StatisticsCollector {

    public static List<StatisticsHolder> collectFromProject(Path path) throws IOException {
        return collectFromProject(path, m -> true);
    }

    /**
     * Recursively collects information about classes stored in .java files in directory and it's subdirectories.
     * @param path path to the root of project.
     * @param filter returns true if you want to take the method into account.
     * @return cumulative statistics about classes stored in the project.
     */
    public static List<StatisticsHolder> collectFromProject(Path path, Predicate<MethodDeclaration> filter)
            throws IOException {
        String projectName = path.getFileName().toString();
        List<StatisticsHolder> stats = new ArrayList<>();
        Files.walk(path).forEach(p -> {
            if (p.getFileName().toString().endsWith(".java")) {
                StatisticsHolder stat = new StatisticsHolder();
                try {
                    collectFromFile(p, stat, filter);
                    stat.addToStringFeature(PATH, p.toString());
                    stat.addToStringFeature(PROJECT, projectName);
                    stats.add(stat);
                } catch(Exception e) {
                    System.out.println("Unable to read file " + path.toString());
                } catch(Error e) {
                    System.out.println("JavaParser failed to parse something at " + path.toString() + " :(");
                }
            }
        });
        return stats;
    }

    public static Set<String> collectTypesFromProject(Path path) throws IOException {
        Set<String> types = new HashSet<>();
        Files.walk(path).forEach(p -> {
            if (p.getFileName().toString().endsWith(".java")) {
                try {
                    types.addAll(collectTypesFromFile(p));
                } catch(Exception e) {
                    System.out.println("Unable to read file " + p.toString());
                }
            }
        });
        return types;
    }

    public static StatisticsHolder collectFromFile(Path path) throws Exception {
        return collectFromFile(path, m -> true);
    }

    /**
     * Collects information about the class stored in .java file.
     * @param path path to the .java file.
     * @param filter returns true if you want to take the method into account.
     * @return statistics about the class that is stored in that file.
     */
    public static StatisticsHolder collectFromFile(Path path, Predicate<MethodDeclaration> filter) throws Exception {
        StatisticsHolder stats = new StatisticsHolder();
        collectFromFile(path, stats, filter);
        return stats;
    }

    public static void collectFromFile(Path path, StatisticsHolder stats, Predicate<MethodDeclaration> filter)
            throws Exception {
        String code = getCodeFromFile(path);
        CompilationUnit cu = JavaParser.parse(code);
        CompilationUnitCollector.getStatistics(cu, stats, filter);
        LayoutCollector.getLayoutFeatures(code, stats);
        AstFeaturesCollector.getStatistics(cu, stats);
    }

    public static Set<String> collectTypesFromFile(Path path) throws Exception {
        CompilationUnit cu = JavaParser.parse(getCodeFromFile(path));
        return AstFeaturesCollector.getNodeTypes(cu);
    }

    private static String getCodeFromFile(Path path) throws Exception {
        FileInputStream in = new FileInputStream(path.toString());
        int content;
        StringBuilder codeBuilder = new StringBuilder();
        while ((content = in.read()) != -1) {
            // convert to char and display it
            codeBuilder.append((char) content);
        }
        return codeBuilder.toString();
    }
}
