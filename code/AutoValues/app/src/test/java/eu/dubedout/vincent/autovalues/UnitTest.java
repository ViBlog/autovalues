package eu.dubedout.vincent.autovalues;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UnitTest {



    @Test
    public void testStringIdentity() {
        String object1 = new String("banana");
        String object2 = new String("banana");
        assertThat(object1 == object2).isFalse();
        assertThat(object1.equals(object2)).isTrue();
    }
}