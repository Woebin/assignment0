import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by filip on 2016-11-15.
 * Developed by Theo Walther & Filip Wennerdahl.
 */
public class Parser implements IParser {

    Tokenizer t = null;

    public void open(String fileName) throws IOException, TokenizerException {
        t = new Tokenizer();
        t.open(fileName);
        t.moveNext();
    }

    public INode parse() throws IOException, TokenizerException, ParserException {
        if (t == null) {
            throw new IOException("No file open.");
        }

        return new BlockNode(t);
    }

    public void close() throws IOException {
        if (t != null) {
            t.close();
        }
    }

    class BlockNode implements INode {
        Lexeme ll = null;
        StmtsNode s = null;
        Lexeme lr = null;

        public BlockNode(Tokenizer t) throws ParserException, IOException, TokenizerException {
            if (t.current().token() != Token.LEFT_CURLY) {
                throw new ParserException("Invalid token!");
            }
            ll = t.current();
            t.moveNext();
            s = new StmtsNode(t);
            if (t.current().token() != Token.RIGHT_CURLY) {
                throw new ParserException("Invalid token!");
            }
            lr = t.current();
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            if (s != null) {
                Map<String, Integer> values = new HashMap<String, Integer>();
                Object[] blargs = {values};
                return s.evaluate(blargs);
            }
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("BlockNode");
            tabs = +1;

            Tab.addTabs(builder, tabs);
            builder.append(ll + "\n");
            Tab.addTabs(builder, tabs);
            s.buildString(builder, tabs);
            Tab.addTabs(builder, tabs);
            builder.append(lr + "\n");
        }
    }

    class StmtsNode implements INode {
        AssignNode a = null;
        StmtsNode s = null;

        public StmtsNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
            if (t.current().token() == Token.IDENT) {
                a = new AssignNode(t);
                s = new StmtsNode(t);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {

            if (s != null) {
                s.evaluate(null); // To be changed.
            }
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    class AssignNode implements INode {
        Lexeme lid = null;
        Lexeme lass = null;
        ExprNode e = null;
        Lexeme ls = null;

        public AssignNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
            if (t.current().token() != Token.IDENT) {
                throw new ParserException("Invalid token!");
            }
            lid = t.current();
            t.moveNext();
            if (t.current().token() != Token.ASSIGN_OP) {
                throw new ParserException("Invalid token!");
            }
            lass = t.current();
            t.moveNext();
            e = new ExprNode(t);
            if (t.current().token() != Token.SEMICOLON) {
                throw new ParserException("Invalid token!");
            }
            ls = t.current();
            t.moveNext();
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            Map<String, Integer> values = (Map<String, Integer>)args[0];
            values.put(lid.value().toString(), (Integer)e.evaluate(args));
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    class ExprNode implements INode {
        TermNode tn = null;
        Lexeme lop = null;
        ExprNode e = null;

        public ExprNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
            tn = new TermNode(t);
            if (t.current().token() == Token.ADD_OP || t.current().token() == Token.SUB_OP) {
                lop = t.current();
                t.moveNext();
                e = new ExprNode(t);
            }

        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    class TermNode implements INode {
        FactorNode f = null;
        Lexeme lop = null;
        TermNode tn = null;

        public TermNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
            f = new FactorNode(t);
            if (t.current().token() == Token.MULT_OP || t.current().token() == Token.DIV_OP) {
                lop = t.current();
                t.moveNext();
                tn = new TermNode(t);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    class FactorNode implements INode {
        Lexeme ll = null;
        ExprNode e = null;
        Lexeme lr = null;

        public FactorNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
            if (t.current().token() != Token.INT_LIT && t.current().token() != Token.IDENT && t.current().token() != Token.LEFT_PAREN) {
                throw new ParserException("Invalid token!");
            } else if (t.current().token() == Token.LEFT_PAREN) {
                ll = t.current();
                t.moveNext();
                e = new ExprNode(t);
                lr = t.current();
                t.moveNext();
            } else {
                ll = t.current();
                t.moveNext();
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            if (e == null){
                if (ll.token() != Token.IDENT){
                    return new Integer.parseInt(ll.value());
                } else {

                    Map<String, Integer> values = (Map<String, Integer>)args[0];
                    Integer val = values.get(ll.value().toString());
                    if (val == null) {
                        return 0;
                    } else {
                        return val;
                    }

                }
            } else {
                return e.evaluate();
            }
            return null; // Temporary return just to alleviate errors.
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    static class Tab {
        static void addTabs(StringBuilder builder, int tabs) {

            for (int i = 0; i < tabs; i++) {
                builder.append("\t");
            }

        }
    }

}
