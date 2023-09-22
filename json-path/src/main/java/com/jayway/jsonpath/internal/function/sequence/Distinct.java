package com.jayway.jsonpath.internal.function.sequence;

import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Take the unique items from collection of JSONArray
 *
 * Created by PavelSakharchuk on 21/09/23
 */
public class Distinct implements PathFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if(ctx.configuration().jsonProvider().isArray(model)){
            Iterable<?> objects = ctx.configuration().jsonProvider().toIterable(model);
            Set<Object> objectSet = new HashSet<>();
            objects.forEach(objectSet::add);

            return  new ArrayList<>(objectSet);
        }
        throw new JsonPathException("Aggregation function attempted to calculate value using empty array");
    }
}
