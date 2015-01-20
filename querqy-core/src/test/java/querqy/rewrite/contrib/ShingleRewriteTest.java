package querqy.rewrite.contrib;


import static org.hamcrest.MatcherAssert.assertThat;
import static querqy.QuerqyMatchers.bq;
import static querqy.QuerqyMatchers.dmq;
import static querqy.QuerqyMatchers.must;
import static querqy.QuerqyMatchers.mustNot;
import static querqy.QuerqyMatchers.term;

import org.junit.Test;

import querqy.model.ExpandedQuery;
import querqy.model.Query;
import querqy.parser.WhiteSpaceQuerqyParser;

/**
 * Created by muellenborn on 20.01.15.
 */
public class ShingleRewriteTest {

    @Test
    public void testShinglingForTwoTokens() {
        WhiteSpaceQuerqyParser parser = new WhiteSpaceQuerqyParser();
        Query query = parser.parse("cde ajk");
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("cde"),
                                bq(
                                        dmq(must(), term("cdeajk")),
                                        dmq(mustNot(), term("cde"))
                                )
                        ),
                        dmq(term("ajk"),
                                bq(
                                        dmq(must(), term("cdeajk")),
                                        dmq(mustNot(), term("ajk"))
                                )
                        )
                )
        );
    }

    @Test
    public void testShinglingForThreeTokens() {
        WhiteSpaceQuerqyParser parser = new WhiteSpaceQuerqyParser();
        Query query = parser.parse("cde ajk xyz");
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(term("cde"),
                            bq(
                                    dmq(must(), term("cdeajk")),
                                    dmq(mustNot(), term("cde"))
                            )
                        ),
                        dmq(term("ajk"),
                            bq(
                                    dmq(must(), term("cdeajk")),
                                    dmq(mustNot(), term("ajk"))
                            ),
                            bq(
                                    dmq(must(), term("ajkxyz")),
                                    dmq(mustNot(), term("ajk"))
                            )
                        ),
                        dmq(term("xyz"),
                            bq(
                                    dmq(must(), term("ajkxyz")),
                                    dmq(mustNot(), term("xyz"))
                            )
                        )
                )
        );
    }

}
