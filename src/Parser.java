import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by filip on 2016-11-15.
 * Developed by Theo Walther & Filip Wennerdahl.
 * With troubleshooting assistance from Gabriel Banfalvi.
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

    /*
    block = ’{’ , stmts , ’}’ ;
     */
    class BlockNode implements INode {
        Lexeme ll = null;
        StatementsNode s = null;
        Lexeme lr = null;

        public BlockNode(Tokenizer t) throws ParserException, IOException, TokenizerException {
            if (t.current().token() != Token.LEFT_CURLY) {
                throw new ParserException("Invalid token!");
            }
            ll = t.current();
            t.moveNext();
            s = new StatementsNode(t);
            if (t.current().token() != Token.RIGHT_CURLY) {
                throw new ParserException("Invalid token!");
            }
            lr = t.current();

        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            if (s != null) {
                Map<String, Double> values = new HashMap<String, Double>();
                Object[] arguments = {values};
                s.evaluate(arguments);
                StringBuilder results = new StringBuilder();

                for(Map.Entry<String, Double> entry : values.entrySet()) {
                    results.append(entry.getKey() + " = " +  entry.getValue() + "\n");
                }
                return results.toString();
            }
            return "";
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("BlockNode" + "\n");
            tabs = +1;

            Tab.appendTabs(builder, tabs);
            builder.append(ll + "\n");
            Tab.appendTabs(builder, tabs);
            s.buildString(builder, tabs);
            Tab.appendTabs(builder, tabs);
            builder.append(lr + "\n");
        }
    }

    /*
    stmts = [ assign , stmts ] ;
     */
    class StatementsNode implements INode {
        AssignmentNode a = null;
        StatementsNode s = null;

        public StatementsNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
            if (t.current().token() == Token.IDENT) {
                a = new AssignmentNode(t);
                s = new StatementsNode(t);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {

            if (a != null) {
                a.evaluate(args);
                s.evaluate(args);
            }
            return args[0];
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("StatementsNode" + "\n");
            tabs += 1;

            if (a != null) {
                Tab.appendTabs(builder, tabs);
                a.buildString(builder, tabs);
            }
            if (s != null) {
                Tab.appendTabs(builder, tabs);
                s.buildString(builder, tabs);
            }
        }
    }

    /*
    assign = id , ’=’ , expr , ’;’ ;
     */
    class AssignmentNode implements INode {
        Lexeme lid = null;
        Lexeme lass = null;
        ExpressionNode e = null;
        Lexeme ls = null;

        public AssignmentNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
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
            e = new ExpressionNode(t);
            if (t.current().token() != Token.SEMICOLON) {
                throw new ParserException("Invalid token!");
            }
            ls = t.current();
            t.moveNext();
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            Map<String, Double> values = (Map<String, Double>) args[0];
            values.put(lid.value().toString(), (double) e.evaluate(args));
            return values;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("AssignmentNode" + "\n");
            tabs += 1;

            Tab.appendTabs(builder, tabs);
            builder.append(lid + "\n");
            Tab.appendTabs(builder, tabs);
            builder.append(lass + "\n");
            Tab.appendTabs(builder, tabs);
            e.buildString(builder, tabs);
            Tab.appendTabs(builder, tabs);
            builder.append(ls + "\n");
        }
    }

    /*
    expr = term , [ ( ’+’ | ’-’ ) , expr ] ;
     */
    class ExpressionNode implements INode {
        TermNode tn = null;
        Lexeme lop = null;
        ExpressionNode e = null;

        public ExpressionNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
            tn = new TermNode(t);
            if (t.current().token() == Token.ADD_OP || t.current().token() == Token.SUB_OP) {
                lop = t.current();
                t.moveNext();
                e = new ExpressionNode(t);
            }

        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            double td = (double) tn.evaluate(args);
            if (lop != null) {
                double ed = (double) e.evaluate(args);
                if (lop.token() == Token.ADD_OP) {
                    return (td + ed);
                } else {
                    return (td - ed);
                }
            } else {
                return (tn.evaluate(args));
            }
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("ExpressionNode" + "\n");
            tabs += 1;

            Tab.appendTabs(builder, tabs);
            tn.buildString(builder, tabs);
            if (lop != null) {
                Tab.appendTabs(builder, tabs);
                builder.append(lop + "\n");
                Tab.appendTabs(builder, tabs);
                e.buildString(builder, tabs);
            }
        }
    }

    /*
    term = factor , [ ( ’*’ | ’/’ ) , term ] ;
     */
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
            double fd = (double) f.evaluate(args);
            if (lop == null) {
                return fd;
            } else {
                double td = (double) tn.evaluate(args);
                if (lop.token() == Token.MULT_OP) {
                    return (fd * td);
                } else {
                    return (fd / td);
                }
            }
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("TermNode" + "\n");
            tabs += 1;

            Tab.appendTabs(builder, tabs);
            f.buildString(builder, tabs);
            if (lop != null) {
                Tab.appendTabs(builder, tabs);
                builder.append(lop + "\n");
                Tab.appendTabs(builder, tabs);
                tn.buildString(builder, tabs);
            }
        }
    }

    /*
    factor = int | id | ’(’ , expr , ’)’ ;
     */
    class FactorNode implements INode {
        Lexeme ll = null;
        ExpressionNode e = null;
        Lexeme lr = null;

        public FactorNode(Tokenizer t) throws IOException, TokenizerException, ParserException {
            if (t.current().token() != Token.INT_LIT && t.current().token() != Token.IDENT && t.current().token() != Token.LEFT_PAREN) {
                throw new ParserException("Invalid token!");
            } else if (t.current().token() == Token.LEFT_PAREN) {
                ll = t.current();
                t.moveNext();
                e = new ExpressionNode(t);
                lr = t.current();
                t.moveNext();
            } else {
                ll = t.current();
                t.moveNext();
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            if (e == null) {
                if (ll.token() != Token.IDENT && ll.token() != Token.LEFT_PAREN) {
                    double temp = Double.parseDouble((String) ll.value());
                    return temp;
                } else if (ll.token() == Token.IDENT) {
                    Map<String, Double> identifiers = (Map<String, Double>) args[0];
                    double val = identifiers.get(ll.value().toString());
                    return val;
                }
            }
            return e.evaluate(args);
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("FactorNode" + "\n");
            tabs += 1;

            Tab.appendTabs(builder, tabs);
            builder.append(ll + "\n");
            if (e != null) {
                Tab.appendTabs(builder, tabs);
                e.buildString(builder, tabs);
                Tab.appendTabs(builder, tabs);
                builder.append(lr + "\n");
            }
        }
    }

    static class Tab {
        static void appendTabs(StringBuilder builder, int tabs) {

            for (int i = 0; i < tabs; i++) {
                builder.append("\t");
            }

        }
    }

}
