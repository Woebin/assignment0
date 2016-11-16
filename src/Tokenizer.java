import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by filip on 2016-11-15.
 */
public class Tokenizer implements ITokenizer {


    private static Map<Character, Token> symbols = null;

    private Scanner scanner = null;
    private Lexeme current = null;
    private Lexeme next = null;

    public Tokenizer() {
        symbols = new HashMap<Character, Token>();
        symbols.put(Scanner.EOF, Token.EOF);
    }

    public void open(String fileName) throws IOException, TokenizerException {
        scanner = new Scanner();
        scanner.open(fileName);
        scanner.moveNext();
        next = extractLexeme();
    }


    public Lexeme current() {
        return current;
    }


    public void moveNext() throws IOException, TokenizerException {
        if(scanner == null) {
            throw new IOException("No file open.");
        }
        current = next;
        if(next.token() != Token.EOF) {
            next = extractLexeme();
        }
    }

    private void consumeWhiteSpaces() throws IOException {
        while (Character.isWhitespace(scanner.current())) {
            scanner.moveNext();
        }
    }

    private Lexeme extractLexeme() throws IOException, TokenizerException {
        consumeWhiteSpaces();
        Character ch = scanner.current();

        if(ch == Scanner.EOF) {
            return new Lexeme(ch, Token.EOF);
        } else if(ch == '{') {
            return extractLeftCurly();
        } else if(ch == '}') {
            return extractRightCurly();
        } else if(Character.isLetter(ch)){
            return extractIdent();
        }else if(ch == '=') {
            return extractAssignOp();
        } else if(ch == ';') {
            return extractSemiColon();
        } else if(ch == '+') {
            return extractAddOp();
        } else if(ch == '-') {
            return extractSubOp();
        } else if(ch == '*') {
            return extractMultOp();
        } else if(ch == '/') {
            return extractDivOp();
        } else if(Character.isDigit(ch)) {
            return extractIntLit();
        } else if(ch == '(') {
            return extractLeftParen();
        } else if(ch == ')') {
            return extractRightParen();
        } else if(symbols.containsKey(ch)) {
            scanner.moveNext();
            return new Lexeme(ch, symbols.get(ch));
        } else {
            throw new TokenizerException("Unknown character: " + String.valueOf(ch));
        }
    }

    private Lexeme extractLeftCurly() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.LEFT_CURLY);
    }

    private Lexeme extractRightCurly() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.RIGHT_CURLY);
    }

    private Lexeme extractIdent() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.IDENT);
    }

    private Lexeme extractAssignOp() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.ASSIGN_OP);
    }

    private Lexeme extractSemiColon() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.SEMICOLON);
    }

    private Lexeme extractAddOp() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.ADD_OP);
    }

    private Lexeme extractSubOp() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.SUB_OP);
    }


    private Lexeme extractMultOp() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.MULT_OP);
    }


    private Lexeme extractDivOp() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.DIV_OP);
    }

    private Lexeme extractIntLit() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.INT_LIT);
    }

    private Lexeme extractLeftParen() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.LEFT_PAREN);
    }

    private Lexeme extractRightParen() throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(scanner.current());
        scanner.moveNext();
        return new Lexeme(strBuilder.toString(), Token.RIGHT_PAREN);
    }

    public void close() throws IOException {
        if(scanner != null) {
            scanner.close();
        }
    }


}
