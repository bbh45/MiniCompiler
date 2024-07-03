package org.bb;

import java.util.List;

public class MiniCompiler {

    /*
    Language Grammar:
        - Statements should with new line
        - All identifiers, operators, numbers and keywords should be seperated by a single space
        - operations supported - +,-,*,/
        - Operator precedence -
                -> *,/ have same precedence
                -> +,- have same precedence
                -> *,/ have more precedence than +,-
        - Assignments(a = 4, b = a, c = a + b), Expressions(d = a + b - c) and Print statements(print a, print a + b) are supported
     */

    private final String code;
    public MiniCompiler(String code){
        this.code = code;

    }

    public static void main(String[] args) {
        String code = "a = 10\n" +
                "b = 20\n" +
                "c = 2\n" +
                "d = a + b * c\n" +
                "print d + 5\n";
        MiniCompiler program = new MiniCompiler(code);
        program.run(code);
    }

    public void run(String code){
        Tokenizer t = new Tokenizer(code);
        List<Token> tokens = t.tokenize();
        for(Token token: tokens){
            System.out.println(token.type+" : "+ token.value);
        }
        System.out.print("Code output: ");
        Parser parser = new Parser(tokens);
        parser.parseProgram();
    }
}