package com.anotherInterpreter.barebones;

import java.util.List;

abstract class Statement {
  interface Visitor<R> {
    R visitBlockStatement(Block statement);
    R visitIfStatement(If statement);
    R visitExpressionStatement(Expression statement);
    R visitPrintStatement(Print statement);
    R visitVarStatement(Var statement);
    R visitWhileStatement(While statement);
  }

  static class Block extends Statement {
    Block(List<Statement> statements) {
      this.statements = statements;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStatement(this);
    }

    final List<Statement> statements;
  }

  static class If extends Statement {
    If(com.anotherInterpreter.barebones.Expression condition, Statement thenBranch, Statement elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStatement(this);
    }

    final com.anotherInterpreter.barebones.Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;
  }

  static class Expression extends Statement {
    Expression(com.anotherInterpreter.barebones.Expression expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStatement(this);
    }

    final com.anotherInterpreter.barebones.Expression expression;
  }

  static class Print extends Statement {
    Print(com.anotherInterpreter.barebones.Expression expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStatement(this);
    }

    final com.anotherInterpreter.barebones.Expression expression;
  }

  static class Var extends Statement {
    Var(Token name, com.anotherInterpreter.barebones.Expression initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStatement(this);
    }

    final Token name;
    final com.anotherInterpreter.barebones.Expression initializer;
  }

  static class While extends Statement {
    While(com.anotherInterpreter.barebones.Expression condition, Statement body) {
      this.condition = condition;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStatement(this);
    }

    final com.anotherInterpreter.barebones.Expression condition;
    final Statement body;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
