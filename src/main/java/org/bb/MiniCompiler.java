package org.bb;

import java.util.List;

public class MiniCompiler {

    private final String code;
    public MiniCompiler(String code){
        this.code = code;

    }

    public static void main(String[] args) {
        String code = "a = 10\n" +
                "b = 20\n" +
                "c = 2\n" +
                "d = a + b * c\n" +
                "print d\n";
        MiniCompiler program = new MiniCompiler(code);
        program.run(code);
    }

    public void run(String code){
        Tokenizer t = new Tokenizer(code);
        List<Token> tokens = t.tokenize();
        for(Token token: tokens){
            System.out.println(token.type+" : "+ token.value);
        }
        Parser parser = new Parser(tokens);
        parser.parseProgram();
    }
}