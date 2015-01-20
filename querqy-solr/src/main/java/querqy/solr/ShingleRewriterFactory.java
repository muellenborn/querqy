package querqy.solr;

import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.solr.common.util.NamedList;

import querqy.rewrite.RewriterFactory;

import java.io.IOException;

/**
 * Created by muellenborn on 20.01.15.
 */
public class ShingleRewriterFactory implements RewriterFactoryAdapter {

    @Override
    public RewriterFactory createRewriterFactory(NamedList<?> args, ResourceLoader resourceLoader) throws IOException {
        return new querqy.rewrite.contrib.ShingleRewriterFactory();
    }
}
