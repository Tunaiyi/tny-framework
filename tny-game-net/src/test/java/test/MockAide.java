package test;

import org.mockito.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public final class MockAide extends Mockito {

    public static final Logger LOGGER = LoggerFactory.getLogger(MockAide.class);

    private MockAide() {
    }

    public static <T extends M, M> ArgumentCaptor<T> captorAs(Class<M> argumentClass) {
        return as(ArgumentCaptor.forClass(argumentClass));
    }

    public static <T extends M, M> T mockAs(Class<M> mockClass) {
        return as(Mockito.mock(mockClass));
    }

}
