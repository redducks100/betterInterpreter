package com.anotherInterpreter.barebones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.anotherInterpreter.barebones.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}
    private final List <Token> tokens;
    private int current = 0;

    Parser(List <Token> tokens) {
        this.tokens = tokens;
    }

    List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
        	statements.add(declaration());
        }
        return statements;
      }

    private Expression expression() {
        return assignment();
    }
    
    private Statement declaration() {
        try {
          if (match(VAR)) return varDeclaration();

          return statement();
        } catch (ParseError error) {
          synchronize();
          return null;
        }
    }
     
    private Statement statement() 
    {
    	//if (match(INCR)) return incrStatement();
    	//if (match(DECR)) return decrStatement();
    	if (match(FOR)) return forStatement();
    	if (match(IF)) return ifStatement();
    	if (match(WHILE)) return whileStatement();
        if (match(PRINT)) return printStatement();
        if (match(LEFT_BRACE)) return new Statement.Block(block());

        return expressionStatement();
    }
    
	private Statement forStatement() {
    	
        Statement initializer;
        if (match(SEMICOLON)) {
          initializer = null;
        } else if (match(VAR)) {
          initializer = varDeclaration();
        } else {
          initializer = expressionStatement();
        }
        
        Expression condition = null;
        if (!check(SEMICOLON)) {
          condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");
        
        Expression increment = null;
        if (!check(RIGHT_PAREN)) {
          increment = expression();
        }
        consume(THEN, "Expect 'then' after for clauses."); //replace this with then
        
        Statement body = statement();
        
        if (increment != null) {
            body = new Statement.Block(Arrays.asList(
                body,
                new Statement.Expression(increment)));
          }
        
        if (condition == null) condition = new Expression.Literal(true);
        body = new Statement.While(condition, body);
        
        if (initializer != null) {
            body = new Statement.Block(Arrays.asList(initializer, body));
         }

        return body;
      }
    
    private Statement ifStatement() {
        Expression condition = expression();
        consume(THEN, "Expect 'then' after if condition."); //USE ANOTHER VARIABLE LIKE THEN

        Statement thenBranch = statement();
        Statement elseBranch = null;
        if (match(ELSE)) {
          elseBranch = statement();
        }

        return new Statement.If(condition, thenBranch, elseBranch);
      }
    
    private Statement printStatement() {
        Expression value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Statement.Print(value);
    }
    
    private Statement varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expression initializer = null;
        if (match(EQUAL)) {
          initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.Var(name, initializer);
      }
    
    private Statement whileStatement() {
        Expression condition = expression();
        consume(THEN, "Expect 'then' after condition."); //REPLACE THIS WITH THEN
        Statement body = statement();

        return new Statement.While(condition, body);
      }
    
    private Statement expressionStatement() {
        Expression expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Statement.Expression(expr);
    }
    
    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
          statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
      }
  
    private Expression assignment() {
    	Expression expr = or();

        if (match(EQUAL)) {
          Token equals = previous();
          Expression value = assignment();

          if (expr instanceof Expression.Variable) {
            Token name = ((Expression.Variable)expr).name;
            return new Expression.Assign(name, value);
          }

          error(equals, "Invalid assignment target.");
        }

        return expr;
      }
    
    private Expression or() {
    	Expression expr = and();

        while (match(OR)) {
          Token operator = previous();
          Expression right = and();
          expr = new Expression.Logical(expr, operator, right);
        }

        return expr;
      }
    
    private Expression and() {
    	Expression expr = equality();

        while (match(AND)) {
          Token operator = previous();
          Expression right = equality();
          expr = new Expression.Logical(expr, operator, right);
        }

        return expr;
      }

    private Expression equality() {
        Expression expression = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private boolean match(TokenType...types) {
        for (TokenType type: types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peek().type == tokenType;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expression comparison() {
        Expression expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expression right = addition();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression addition() {
        Expression expr = multiplication();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expression right = multiplication();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression multiplication() {
        Expression expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expression right = unary();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return primary();
    }

    private Expression primary() {
        if (match(FALSE)) return new Expression.Literal(false);
        if (match(TRUE)) return new Expression.Literal(true);
        if (match(NIL)) return new Expression.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expression.Literal(previous().literal);
        }
        
        if (match(IDENTIFIER)) {
            return new Expression.Variable(previous());
          }
        
        if (match(LEFT_PAREN)) {
            Expression expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }
    private ParseError error(Token token, String message) {
        BareBones.error(token, message);
        return new ParseError();
    }
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                    return;
            }
            advance();
        }
    }
}