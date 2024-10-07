package cz.janklempar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class MainTest extends TestCase {

    private ObjectMapper mapper;

    @Before
    public void setUp() {

        this.mapper = new ObjectMapper();
    }

    @Test
    public void test8Battery() throws IOException {

        String[] args = {"test8_lowBattery.json", "test8_result_test.json"};
        Main.main(args);
        JsonNode expected = mapper.readTree(new File("test8_result.json"));
        JsonNode actual = mapper.readTree(new File(args[1]));
        assert(actual.equals(expected));

        new File(args[1]).delete();

    }

    @Test
    public void test7Minimap() throws IOException {

        String[] args = {"test7_1x1map.json", "test7_result_test.json"};
        Main.main(args);
        JsonNode expected = mapper.readTree(new File("test7_result.json"));
        JsonNode actual = mapper.readTree(new File(args[1]));
        assert(actual.equals(expected));

        new File(args[1]).delete();

    }

    @Test
    public void test6Prison() throws IOException {

        String[] args = {"test6_prison.json", "test6_result_test.json"};
        Main.main(args);
        JsonNode expected = mapper.readTree(new File("test6_result.json"));
        JsonNode actual = mapper.readTree(new File(args[1]));
        assert(actual.equals(expected));

        new File(args[1]).delete();

    }

    @Test
    public void test5Stuck() throws IOException {

        String[] args = {"test5_stuckB.json", "test5_result_test.json"};
        Main.main(args);
        JsonNode expected = mapper.readTree(new File("test5_result.json"));
        JsonNode actual = mapper.readTree(new File(args[1]));
        assert(actual.equals(expected));

        new File(args[1]).delete();

    }

    @Test
    public void test4Stuck() throws IOException {

        String[] args = {"test4_stuck.json", "test4_result_test.json"};
        Main.main(args);
        JsonNode expected = mapper.readTree(new File("test4_result.json"));
        JsonNode actual = mapper.readTree(new File(args[1]));
        assert(actual.equals(expected));

        new File(args[1]).delete();

    }

    @Test
    public void test3_2recovery() throws IOException {

        String[] args = {"test3_null.json", "test3_result_test.json"};

        Main.main(args);
        JsonNode expected = mapper.readTree(new File("test3_result.json"));
        JsonNode actual = mapper.readTree(new File(args[1]));
        assert(actual.equals(expected));

        new File(args[1]).delete();


    }

    // Given tests:
    @Test
    public void test1() throws IOException {

        final InputStream original = System.in;
        String[] args = {"test1.json", "test1_result_test.json"};

        Main.main(args);
        JsonNode expected = mapper.readTree(new File("test1_result.json"));
        JsonNode actual = mapper.readTree(new File(args[1]));
        assert(actual.equals(expected));

        System.setIn(original);
        new File(args[1]).delete();

    }

    @Test
    public void test2() throws IOException {
        final InputStream original = System.in;

        String[] args = {"test2.json", "test2_result_test.json"};

        Main.main(args);
        JsonNode expected = mapper.readTree(new File("test2_result.json"));
        JsonNode actual = mapper.readTree(new File(args[1]));
        assert(actual.equals(expected));

        System.setIn(original);
        new File(args[1]).delete();
    }

}