package org.jfuncmachine.examples.minilang;

import org.jfuncmachine.sexprlang.parser.SymbolMatcher;

import java.util.regex.Pattern;

public class MinilangSymbolMatcher implements SymbolMatcher {
    Pattern firstCharPattern;
    Pattern charPattern;

    public MinilangSymbolMatcher() {
        firstCharPattern = Pattern.compile("[-A-Za-z0-9.+=<>^&|!*/]");
        charPattern = Pattern.compile("[-A-Za-z0-9.+=<>^&|!*/]");
    }

    @Override
    public boolean isSymbolFirstChar(char ch) {
        return firstCharPattern.matcher(""+ch).matches();
    }

    @Override
    public boolean isSymbolChar(char ch) {
        return charPattern.matcher(""+ch).matches();
    }
}
