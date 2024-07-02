package org.bb;

public class Token { //follows SRP
    final String type;
    final String value;

    public Token(String type, Object value){
        this.type = type;
        if(value instanceof Integer){
            this.value = String.valueOf(value);
        }else {
            this.value = (String)value;
        }
    }

    public Token(String value){
        this.type = value;
        this.value = value;
    }
}
