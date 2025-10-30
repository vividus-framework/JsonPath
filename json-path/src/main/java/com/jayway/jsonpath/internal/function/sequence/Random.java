package com.jayway.jsonpath.internal.function.sequence;

import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Take a random item from collection of JSONArray
 */
public class Random implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if (ctx.configuration().jsonProvider().isArray(model)) {
            Iterable<?> objects = ctx.configuration().jsonProvider().toIterable(model);
            List<?> elements = StreamSupport.stream(objects.spliterator(), false)
                                            .collect(Collectors.toList());

            if (elements.isEmpty()) {
                throw new JsonPathException("Aggregation function attempted to get a random item from empty array");
            }

            SecureRandom rand = new SecureRandom();
            if (parameters == null) {
                return elements.get(rand.nextInt(elements.size()));
            } else {
                if (parameters.size() > 1) {
                    throw new JsonPathException("Random function accepts at most one parameter");
                }

                Object value = parameters.get(0).getValue();
                if (!(value instanceof Integer)) {
                    throw new JsonPathException("Random function parameter must be an integer");
                }

                int numberOfElement = (Integer) value;
                if (numberOfElement <= 0 || numberOfElement > elements.size()) {
                    throw new JsonPathException("Random function parameter must be greater than 0 and less than or equal to the size of the array");
                }

                List<Object> elementsToReturn = new ArrayList<>();
                while (numberOfElement > 0) {
                    int nextItem = rand.nextInt(elements.size());
                    elementsToReturn.add(elements.remove(nextItem));
                    numberOfElement--;
                }
                return elementsToReturn;
            }
        }
        throw new JsonPathException("Aggregation function attempted to get a random item from not array");
    }
}
