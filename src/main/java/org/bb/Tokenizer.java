package org.bb;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    //generates tokens(identifier : a, number :2, print:print, equals:=, operator:+,operator:-,operator:*,operator:/)
    private final String code;
    private int lineNr = 0;

    public Tokenizer(String code){
        this.code =  code;
    }

    public List<Token> tokenize(){
        List<Token> tokens = new ArrayList<>();
        String[] lines = code.trim().split("\n");
        for(String line: lines){
            lineNr++;
            String[] parts = line.trim().split(" ");
            for (String part : parts) {
                if(part.equals("print")){
                    tokens.add(new Token("print",part));
                } else if (part.equals("=")) {
                    tokens.add(new Token("equals",part));
                } else if (part.equals("+")||part.equals("-")||part.equals("*")||part.equals("/")){
                    tokens.add(new Token("operator",part));
                } else if (part.matches("\\d+")){
                    tokens.add(new Token("number",part));
                } else if (part.matches("[a-zA-Z]")) {
                    tokens.add(new Token("identifier",part));
                } else{
                    raiseError("invalid token "+part);
                }
            }
            tokens.add(new Token("\n"));
        }
        return tokens;
    }

    private void raiseError(String message){
        throw new IllegalArgumentException(lineNr+" : "+message);
    }
}
