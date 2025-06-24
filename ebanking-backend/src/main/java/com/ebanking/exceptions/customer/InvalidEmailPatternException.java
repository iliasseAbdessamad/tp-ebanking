package com.ebanking.exceptions.customer;

public class InvalidEmailPatternException extends RuntimeException {

    private String invalidEmailPattern;

    public InvalidEmailPatternException(String invalidEmailPattern) {
      super();
      this.invalidEmailPattern = invalidEmailPattern;
    }
}
