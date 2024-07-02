package org.bb;

import java.util.*;

public class Parser {

    //parses - assignment(a=10), print(print a), assignment+expression(a=b+c)

    private Token returnedToken;
    private Iterator<Token> tokenFeed;
    private Stack<Token> stack;
    private Map<String,Integer> vars;
    private Map<String,Integer> precedence;

    public Parser(List<Token> tokens){
        this.returnedToken = null;
        this.tokenFeed = tokens.iterator();
        this.stack = new Stack<>();
        this.vars = new HashMap<>();
        this.precedence = new HashMap<>();
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);
    }

    //parse entire program statement by statement
    public boolean parseProgram(){
        if(!parseStatement()){
            raiseError("Expected: statement");
        }
        Token token = nextToken();
        while (token != null) {
            returnToken(token);
            if (!parseStatement()) {
                raiseError("Expected: statement");
            }
            token = nextToken();
        }
        return true;
    }

    private boolean parseStatement(){
        if(!parsePrintStatement() && !parseAssignment()){
            raiseError("Expected: print statement or assignment");
        }
        Token token = nextToken();
        if(!token.value.equals("\n")){
            raiseError("Expected: end of line");
        }
        return true;
    }

    private boolean parsePrintStatement(){ //eg: print a+b
        Token token = nextToken();
        if(!token.type.equals("print")){
            returnToken(token);
            return false;
        }
        if(!parseExpression()){
            raiseError("Expected: expression");
        }
        int value = stackCollapse();
        System.out.println(value);
        return true;
    }

    private boolean parseAssignment(){
        Token token = nextToken();
        if(!token.type.equals("identifier")){
            returnToken(token);
            return false;
        }
        String identifier = token.value;
        token = nextToken();
        if(!token.value.equals("=")){
            raiseError("Expected =");
        }
        if(!parseExpression()){
            raiseError("Expected expression");
        }
        vars.put(identifier, stackCollapse());
        return true;
    }

    private boolean parseExpression(){ //checks expression part of print statement : a+b
        if(!parseValue()){             //parses first value
            return false;
        }
        if(parseOperator()){          //checks if there is a operator
            parseExpression();
        }
        return true;
    }

    private boolean parseValue(){
        Token token = nextToken();
        if(!token.type.equals("number") && !token.type.equals("identifier")){
            returnToken(token);
            return false;
        }
        if(token.type.equals("identifier")){
            if(!vars.containsKey(token.value)){
                raiseError("Syntax Error: Unknown variable "+token.value);
            }else {
                stack.push(new Token("digit",String.valueOf(vars.get(token.value))));
            }
        }else {
            stack.push(new Token("digit",String.valueOf(token.value)));
        }
        return true;
    }

    private boolean parseOperator(){
        Token token = nextToken();
        if(!token.type.equals("operator")){
            returnToken(token);
            return false;
        }
        //Before pushing operator into stack, collapsing stack.
        //Since stack has only one element and there is nothing to do
        stack.push(new Token("digit",stackCollapse(token.value)));

        //pushing operator, precedence as token eg: (+,1)
        stack.push(new Token(token.value, precedence.get(token.value)));
        return true;
    }

    private int stackCollapse(){
        return stackCollapse(null);
    }

    private int stackCollapse(String nextOperator){
        int opPrecedence = nextOperator == null ? 0 : precedence.get(nextOperator);
        while (stack.size() > 1 && (Integer.parseInt(stack.get(stack.size() - 2).value)) > opPrecedence){
            int value2 = Integer.parseInt(stack.pop().value);
            String prevOp = stack.pop().type;
            int value1 = Integer.parseInt(stack.pop().value);
            int result;
            if(prevOp.equals("+"))
                result = value1+value2;
            else if(prevOp.equals("-"))
                result = value1-value2;
            else if(prevOp.equals("*"))
                result = value1*value2;
            else if(prevOp.equals("/"))
                result = value1/value2;
            else
                throw new IllegalArgumentException("Invalid operator: " + prevOp);
            stack.push(new Token("digit",String.valueOf(result)));
        }
        return Integer.parseInt(stack.pop().value);
    }

    private Token nextToken(){
        if(returnedToken != null){
            Token token = returnedToken;
            returnedToken = null;
            return token;
        }else{
            if(tokenFeed.hasNext()){
                return tokenFeed.next();
            }else {
                return null;
            }
        }
    }

    private void returnToken(Token token){
        if(returnedToken != null){
            throw new RuntimeException("Cannot return more than one token at a time");
        }
        returnedToken = token;
    }

    private void raiseError(String message) {
        throw new IllegalArgumentException(message);
    }
}
