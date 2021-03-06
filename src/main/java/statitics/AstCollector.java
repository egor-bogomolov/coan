package statitics;

import static statitics.StatisticsHolder.AST;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.XmlPrinter;

public class AstCollector {

    /** Prints AST of a {@link CompilationUnit} in standard output. */
    public static void printAst(CompilationUnit cu) {
        String xmlString = new XmlPrinter(true).output(cu);
        System.out.println(addTabulationToXml(xmlString));
    }

    /** Adds ast of method in xml format to the {@link StatisticsHolder}. */
    public static void getAst(MethodDeclaration method, StatisticsHolder stats) {
//        String xmlString = new XmlPrinter(true).output(method);
//        stats.addToStringFeature(AST, method.getName() + ":\n" + addTabulationToXml(xmlString));
    }

    private static String addTabulationToXml(String xmlString) {
        int count = 0;
        StringBuilder resultingString = new StringBuilder();
        for (int i = 0; i < xmlString.length(); i++) {
            char now = xmlString.charAt(i);
            if (now == '<') {
                if (xmlString.charAt(i + 1) == '/') {
                    count--;
                    appendTabs(resultingString, count);
                } else {
                    appendTabs(resultingString, count);
                    count++;
                }
            }
            resultingString.append(now);
            if (now == '>') {
                resultingString.append('\n');
            }
        }
        return resultingString.toString();
    }

    private static void appendTabs(StringBuilder string, int n) {
        for (int i = 0; i < n; i++) {
            string.append('\t');
        }
    }
}
