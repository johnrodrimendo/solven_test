package com.affirm.client.model.wordpress;

import java.util.List;

/**
 * Created by john on 21/06/17.
 */
public interface WordPressTransformer<P extends WordPressPost> {
    List<P> transform(String var1) throws Exception;
}
