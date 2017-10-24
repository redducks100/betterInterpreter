package com.anotherInterpreter.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {

    public static void main(String[] args) throws IOException {
        String outputDir = "E:\\Java Projects\\anotherInterpreter\\src\\com\\anotherInterpreter\\barebones\\";
        defineAst(outputDir, "Expression", Arrays.asList(
        	"Assign   : Token name, Expression value",
            "Binary   : Expression left, Token operator, Expression right",
            "Grouping : Expression expression",
            "Literal  : Object value",
            "Logical  : Expression left, Token operator, Expression right",
            "Unary    : Token operator, Expression right",
            "Variable : Token name"
        ));
        defineAst(outputDir, "Statement", Arrays.asList(
        		  "Block      : List<Statement> statements",
        		  "If         : com.anotherInterpreter.barebones.Expression condition, Statement thenBranch, Statement elseBranch",
        	      "Expression : com.anotherInterpreter.barebones.Expression expression",
        	      "Print      : com.anotherInterpreter.barebones.Expression expression",
        	      "Var        : Token name, com.anotherInterpreter.barebones.Expression initializer",
        	      "While      : com.anotherInterpreter.barebones.Expression condition, Statement body"
        	    ));
    }

    private static void defineAst(String outputDir, String baseName, List < String > types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.anotherInterpreter.barebones;");
        writer.println("");
        writer.println("import java.util.List;");
        writer.println("");
        writer.println("abstract class " + baseName + " {");
        defineVisitor(writer, baseName, types);
        for (String type: types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
        writer.println("");
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("");
        writer.println("  static class " + className + " extends " +
            baseName + " {");

        // Constructor.
        writer.println("    " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field: fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");

        writer.println();
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" +
            className + baseName + "(this);");
        writer.println("    }");

        // Fields.
        writer.println();
        for (String field: fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("  }");
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List < String > types) {
        writer.println("  interface Visitor<R> {");

        for (String type: types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" +
                typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("  }");
    }
}