package org.bb;

public class Operator { //follows SRP
    final String operator;
    final int precedence;


    public Operator(String operator, int precedence) {
        this.operator = operator;
        this.precedence = precedence;
    }
}
