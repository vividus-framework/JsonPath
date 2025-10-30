package com.jayway.jsonpath.internal.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jayway.jsonpath.JsonPathException;
import org.junit.Test;

/**
 * Test cases for functions
 * 
 * -first
 * -last
 * -index(X)
 * -distinct
 * -random
 *
 * Created by git9527 on 6/11/22.
 */
public class SequentialPathFunctionTest extends BaseFunctionTest {

    private Configuration conf = Configurations.JACKSON_CONFIGURATION;

    @Test
    public void testFirstOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.first()", NUMBER_SERIES, 1);
    }
    
    @Test
    public void testLastOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.last()", NUMBER_SERIES, 10);
    }
    
    @Test
    public void testIndexOfNumbers() throws Exception {
        verifyFunction(conf, "$.numbers.index(0)", NUMBER_SERIES, 1);
        verifyFunction(conf, "$.numbers.index(-1)", NUMBER_SERIES, 10);
        verifyFunction(conf, "$.numbers.index(1)", NUMBER_SERIES, 2);
    }

    @Test
    public void testFirstOfText() throws Exception {
        verifyFunction(conf, "$.text.first()", TEXT_SERIES, "a");
    }

    @Test
    public void testLastOfText() throws Exception {
        verifyFunction(conf, "$.text.last()", TEXT_SERIES, "f");
    }

    @Test
    public void testIndexOfText() throws Exception {
        verifyFunction(conf, "$.text.index(0)", TEXT_SERIES, "a");
        verifyFunction(conf, "$.text.index(-1)", TEXT_SERIES, "f");
        verifyFunction(conf, "$.text.index(1)", TEXT_SERIES, "b");
    }

    @Test
    public void testDistinctOfText() {
        verifyFunction(conf, "$.text_with_duplicates.distinct()", TEXT_SERIES, Arrays.asList("a", "b"));
    }

    @Test
    public void testDistinctOfObjects() {
        final Object expectedValue = Configuration.defaultConfiguration().jsonProvider()
                .parse("[{\"a\":\"a-val\"}, {\"b\":\"b-val\"}]");
        verifyFunction(conf, "$.objects.distinct()", OBJECT_SERIES, expectedValue);
    }

    @Test
    public void testDistinctArrayOfObjects() {
        final Object expectedValue = Configuration.defaultConfiguration().jsonProvider()
                .parse("[{\"a\":[{\"a\":\"a-val\"}, {\"b\":\"b-val\"}]}, {\"b\":[{\"b\":\"b-val\"}]}]");
        verifyFunction(conf, "$.array_of_objects.distinct()", OBJECT_SERIES, expectedValue);
    }

    @Test
    public void testDistinctArrayOfArrays() {
        final Object expectedValue = Configuration.defaultConfiguration().jsonProvider()
                .parse("[[{\"a\":\"a-val\"}, {\"b\":\"b-val\"}], [{\"b\":\"b-val\"}]]");
        verifyFunction(conf, "$.array_of_arrays.distinct()", OBJECT_SERIES, expectedValue);
    }

    @Test
    public void testDistinctOfEmptyObjects() throws Exception {
        final Object expectedValue = Configuration.defaultConfiguration().jsonProvider().parse("[]");
        verifyFunction(conf, "$.empty.distinct()", OBJECT_SERIES, expectedValue);
    }

    @Test
    public void testRandomForStrings() throws Exception {
        List<Object> elements = (List<Object>) evaluate(conf, "$.text", TEXT_AND_NUMBER_SERIES);
        Object randomElement = evaluate(conf, "$.text.random()", TEXT_AND_NUMBER_SERIES);
        assertThat(randomElement).isInstanceOf(String.class).isIn(elements);
    }

    @Test
    public void testRandomForNumbers() throws Exception {
        Object randomElements = evaluate(conf, "$.numbers.random(1)", TEXT_AND_NUMBER_SERIES);
        assertThat(randomElements).isInstanceOf(List.class);
        List<?> randomList = (List<?>) randomElements;
        assertThat(randomList).hasSize(1);
    }

    @Test
    public void testRandomForMoreNumbers() {
        List<Object> elements = (List<Object>) evaluate(conf, "$.numbers", TEXT_AND_NUMBER_SERIES);
        Object randomElements = evaluate(conf, "$.numbers.random(5)", TEXT_AND_NUMBER_SERIES);
        assertThat(randomElements).isInstanceOf(List.class);
        List<?> randomList = (List<?>) randomElements;
        assertThat(randomList).hasSize(5);
        Set<Object> randomSet = new HashSet<>(randomList);
        assertThat(randomList.size()).isEqualTo(randomSet.size());
        assertThat(elements).containsAll(randomList);
    }

    @Test
    public void testRandomOnNonArrayShouldFail() {
        String json = "{\"value\":\"not-an-array\"}";
        assertThatThrownBy(() -> evaluate(conf, "$.value.random()", json))
                .isInstanceOf(JsonPathException.class)
                .hasMessage("Aggregation function attempted to get a random item from not array");
    }

    @Test
    public void testMoreThanOneParameterShouldFail() {
        assertThatThrownBy(() -> evaluate(conf, "$.numbers.random(1, 2)", TEXT_AND_NUMBER_SERIES))
                .isInstanceOf(JsonPathException.class)
                .hasMessage("Random function accepts at most one parameter");
    }

    @Test
    public void testNegativeParameterShouldFail() {
        assertThatThrownBy(() -> evaluate(conf, "$.numbers.random(-1)", TEXT_AND_NUMBER_SERIES))
                .isInstanceOf(JsonPathException.class)
                .hasMessage("Random function parameter must be greater than 0 and less than or equal to the size of the array");
    }

    @Test
    public void testZeroValueParameterShouldFail() {
        assertThatThrownBy(() -> evaluate(conf, "$.numbers.random(0)", TEXT_AND_NUMBER_SERIES))
                .isInstanceOf(JsonPathException.class)
                .hasMessage("Random function parameter must be greater than 0 and less than or equal to the size of the array");
    }

    @Test
    public void testBiggerThanSizeShouldFail() {
        assertThatThrownBy(() -> evaluate(conf, "$.numbers.random(100)", TEXT_AND_NUMBER_SERIES))
                .isInstanceOf(JsonPathException.class)
                .hasMessage("Random function parameter must be greater than 0 and less than or equal to the size of the array");
    }


}
