import com.wellignton.cacheframework.Cache;
import com.wellignton.cacheframework.CacheBuilder;
import com.wellignton.cacheframework.Key;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CacheTest {
    @Test
    public void addKeyValuePairToCache_returnValue() {
        Cache localCache = CacheBuilder.newBuilder().build();
        Key number = new Key("Number");
        localCache.put(number, "One");
        assertThat("One", is(localCache.get(number)));
    }

    @Test
    public void addAnotherKeyValuePairToCache_returnValue() {
        Cache localCache = CacheBuilder.newBuilder().build();
        Key fruit = new Key("Fruit");
        localCache.put(fruit, "Orange");
        assertThat("Orange", is(localCache.get(fruit)));
    }

    @Test
    public void addAKeyValuePairToCache_sizeConstraint_returnValue() {
        Cache localCache = CacheBuilder.newBuilder().withMaxSize(1).build();
        Key number = new Key("Number");
        Key fruit = new Key("Fruit");
        localCache.put(number, "One");
        localCache.put(fruit, "Orange");

        assertThat("Orange", is(localCache.get(fruit)));
        assertThat(1, is(localCache.size()));
    }

    @Test
    public void expireKeyValueAfterWrite() throws InterruptedException {
        Cache localCache = CacheBuilder.newBuilder().expireAfterWriteSeconds(10).build();
        Key number = new Key("Number");
        localCache.put(number, "One");
        assertThat("One", is(localCache.get(number)));
        TimeUnit time = TimeUnit.SECONDS;
        time.sleep(7);
        assertThat("One", is(localCache.get(number)));
        time.sleep(4);
        assertThat(null, is(localCache.get(number)));
    }
}
