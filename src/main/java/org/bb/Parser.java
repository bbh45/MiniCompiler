package org.bb;

import java.util.*;

public class Parser {

    //parses - assignment(a=10), print(print a), assignment+expression(a=b+c)
    private int lineNo;
    private Token returnedToken;
    private Iterator<Token> tokenFeed;
    private Stack<Token> stack;
    private Map<String,Integer> vars;
    private Map<String,Integer> precedence;

    public Parser(List<Token> tokens){
        this.lineNo = 1;
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
        if(!token.value.equals("\n")){ //checking for end of statement
            raiseError("Expected: end of line");
        }
        lineNo++;
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
        int value = stackCollapse(); //running stackCollapse() on the parsed expression to evaluate the final result of expression
        System.out.println(value);
        return true;
    }

    private boolean parseAssignment(){  //a = b + c or a = b + 4 or a = 4
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
        if(!parseExpression()){      //checks and parses if there is an expression or number after the  "="
            raiseError("Expected expression");
        }
        //running stackCollapse() on the parsed expression to evaluate the final result of expression
        //storing the left identifier value and expression result into th vars Map eg:(a,4)
        vars.put(identifier, stackCollapse());
        return true;
    }

    //To parse the expression, all the identifiers present in the expression must have already been parsed and
    //their value should be present in the vars map
    private boolean parseExpression(){ //eg a+b
        if(!parseValue()){        //checks if the first value is already parsed(a) => present in vars map and push into stack to be used by stackCollapse()
            return false;
        }
        if(parseOperator()){      //checks if there is an operator, if yes, push it into stack to be used by stackCollapse() and recursively call parseExpression(b)
            parseExpression();
        }
        return true;
    }

    //visit each identifier/number and push its value into stack to be used by stackCollapse()
    private boolean parseValue(){
        Token token = nextToken();
        //if it is not a number or identifier, return token and return false
        if(!token.type.equals("number") && !token.type.equals("identifier")){
            returnToken(token);
            return false;
        }
        //if it is an identifier, which is already parsed, it's value will be in the vars map
        //get the value and push it into stack
        if(token.type.equals("identifier")){
            if(!vars.containsKey(token.value)){
                raiseError("Syntax Error: Unknown variable "+token.value);
            }else {
                stack.push(new Token("digit",String.valueOf(vars.get(token.value))));
            }
        }//if it is number, push its value into stack
        else {
            stack.push(new Token("digit",String.valueOf(token.value)));
        }
        return true;
    }

    //each time an operator is encountered, call stackCollapse() to evaluate if previous operator had more precedence
    //eg: if stack has - 4,*[2],5, now + is encountered, calling stackCollapse(1) will evaluate 4*5 and returns 20
    //20 is pushed into stack
    //then +[1] is added to stack. Now stack will have - 20,+[1]
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

    //To finally evaluate the whole expression, when all the operators in stack has same precedence - 20, +[1], 4, -[1], 5 (evaluates from right to left)
    private int stackCollapse(){
        return stackCollapse(null);
    }


    //to evaluate expressions based on precedence
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
        throw new IllegalArgumentException("at line"+lineNo + " - " + message);
    }
}
