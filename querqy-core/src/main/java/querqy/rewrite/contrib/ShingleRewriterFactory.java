package querqy.rewrite.contrib;

import querqy.model.ExpandedQuery;
import querqy.rewrite.QueryRewriter;
import querqy.rewrite.RewriterFactory;

import java.util.Map;

/**
 * Created by muellenborn on 20.01.15.
 */
public class ShingleRewriterFactory implements RewriterFactory {

    @Override
    public QueryRewriter createRewriter(ExpandedQuery input, Map<String, ?> context) {
        return new ShingleRewriter();
    }

}
