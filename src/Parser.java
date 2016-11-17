import java.io.IOException;

/**
 * Created by filip on 2016-11-15.
 * Developed by Filip Wennerdahl & Theo Walther.
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
        StmtsNode s = null;

        public BlockNode(Tokenizer t) {
            if (t.current().token() != Token.LEFT_CURLY){

            }
            s = new StmtsNode(t);
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    class StmtsNode implements INode {
        AssignNode a = null;
        StmtsNode s = null;

        public StmtsNode(Tokenizer t) {
            a = new AssignNode(t);
            s = new StmtsNode(t);
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    class AssignNode implements INode {
        ExprNode e = null;

        public AssignNode(Tokenizer t) {
            e = new ExprNode(t);
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    class ExprNode implements INode {
        TermNode tn = null;
        ExprNode e = null;

        public ExprNode(Tokenizer t) {
            tn = new TermNode(t);
            e = new ExprNode(t);
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
        TermNode tn = null;

        public TermNode(Tokenizer t) {
            f = new FactorNode(t);
            tn = new TermNode(t);
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
        ExprNode e = null;
        Lexeme l1 = null;
        Lexeme l2 = null;

        public FactorNode(Tokenizer t) {
            l1 = t.current();
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null; // Temporary return just to alleviate errors.
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

}
