package querqy.rewrite.contrib;

import querqy.CompoundCharSequence;
import querqy.model.*;
import querqy.rewrite.QueryRewriter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by muellenborn on 20.01.15.
 */
public class ShingleRewriter extends AbstractNodeVisitor<Node> implements QueryRewriter {

    Term previousTerm = null;
    List<BooleanQuery> bqToAdd = null;

    @Override
    public ExpandedQuery rewrite(ExpandedQuery query) {
        Query userQuery = query.getUserQuery();
        if (userQuery != null){
            previousTerm = null;
            bqToAdd = new LinkedList<BooleanQuery>();
            visit(userQuery);
            for (BooleanQuery bq : bqToAdd) {
                ((DisjunctionMaxQuery) bq.getParent()).addClause(bq);
            }
        }
        return query;
    }

    @Override
    public Node visit(DisjunctionMaxQuery dmq) {
        List<DisjunctionMaxClause> clauses = dmq.getClauses();
        if (clauses != null && !clauses.isEmpty()){
            if (clauses.size() > 1) {
                throw new IllegalArgumentException("cannot handle more then one DMQ clause");
            }
            super.visit(dmq);
        }
        return null;
    }

    @Override
    public Node visit(Term term) {
        if (previousTerm != null){
            //TODO check field name
            CharSequence seq = new CompoundCharSequence(null, previousTerm, term);

            BooleanQuery bqPrev = new BooleanQuery(previousTerm.getParent(), Clause.Occur.SHOULD, true);
            bqToAdd.add(bqPrev);

            DisjunctionMaxQuery dmqPrevShingle =  new DisjunctionMaxQuery(bqPrev, Clause.Occur.MUST, true);
            bqPrev.addClause(dmqPrevShingle);

            Term prevShingleTerm = new Term(dmqPrevShingle, previousTerm.getField(), seq, true);
            dmqPrevShingle.addClause(prevShingleTerm);

            DisjunctionMaxQuery dmqPrevNeg = new DisjunctionMaxQuery(bqPrev, Clause.Occur.MUST_NOT, true);
            bqPrev.addClause(dmqPrevNeg);

            Term prevNegTerm = new Term(dmqPrevNeg, previousTerm.getField(), previousTerm, true);
            dmqPrevNeg.addClause(prevNegTerm);

            BooleanQuery bq = new BooleanQuery(term.getParent(), Clause.Occur.SHOULD, true);
            bqToAdd.add(bq);

            DisjunctionMaxQuery dmqShingle = new DisjunctionMaxQuery(bq, Clause.Occur.MUST, true);
            bq.addClause(dmqShingle);

            Term shingleTerm = new Term(dmqShingle, term.getField(), seq, true);
            dmqShingle.addClause(shingleTerm);

            DisjunctionMaxQuery dmqNeg = new DisjunctionMaxQuery(bq, Clause.Occur.MUST_NOT, true);
            bq.addClause(dmqNeg);

            Term negTerm = new Term(dmqNeg, term.getField(), term, true);
            dmqNeg.addClause(negTerm);
        }
        previousTerm = term;
        return term;
    }

    @Override
    public Node visit(BooleanQuery bq) {
        previousTerm = null;
        return super.visit(bq);
    }
}
