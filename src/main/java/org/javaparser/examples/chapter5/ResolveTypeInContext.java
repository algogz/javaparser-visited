package org.javaparser.examples.chapter5;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;

public class ResolveTypeInContext {

    private static final String FILE_PATH = "/Users/algo/dev/java/javaparser-maven-sample/src/main/java/algo/test/impl/Dog.java";
    private static final String SRC_PATH = "/Users/algo/dev/java/javaparser-maven-sample/src/main/java";
//    private static final String FILE_PATH = "src/main/java/org/javaparser/examples/chapter5/Foo.java";
//    private static final String SRC_PATH = "src/main/java";

    public static void main(String[] args) throws Exception {
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(SRC_PATH));
        reflectionTypeSolver.setParent(reflectionTypeSolver);

        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        combinedSolver.add(javaParserTypeSolver);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        StaticJavaParser
                .getConfiguration()
                .setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

        System.out.println("\n==== NameExpr");
        cu.findAll(NameExpr.class).forEach(c -> {
            System.out.println(c.calculateResolvedType().asReferenceType().getQualifiedName());
        });

        System.out.println("\n==== ClassOrInterfaceType");
        cu.findAll(ClassOrInterfaceType.class).forEach(c -> {
            System.out.println(c.resolve().asReferenceType().getQualifiedName());
        });

        System.out.println("\n==== FieldDeclaration");
        FieldDeclaration fieldDeclaration = Navigator.findNodeOfGivenClass(cu, FieldDeclaration.class);

        System.out.println("Field type: " + fieldDeclaration.getVariables().get(0).getType()
                .resolve().asReferenceType().getQualifiedName());
    }
}
