package se325.websocketchat.jackson.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Capable of converting between text streams ({@link Reader} and {@link Writer} instances) and POJOs, using Jackson.
 * <p>
 * Required for WebSockets - Jackson integration.
 *
 * @param <T> the type of object to convert.
 * @author https://dzone.com/articles/using-java-websockets-jsr-356
 */
public abstract class JSONCoder<T>
        implements Encoder.TextStream<T>, Decoder.TextStream<T> {


    private Class<T> _type;

    /**
     * One ObjectMapper per thread, as it's not threadsafe.
     */
    private final ThreadLocal<ObjectMapper> _mapper = ThreadLocal.withInitial(ObjectMapper::new);


    @Override
    @SuppressWarnings("unchecked")
    public void init(EndpointConfig endpointConfig) {

        ParameterizedType $thisClass = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type $T = $thisClass.getActualTypeArguments()[0];
        if ($T instanceof Class) {
            _type = (Class<T>) $T;
        } else if ($T instanceof ParameterizedType) {
            _type = (Class<T>) ((ParameterizedType) $T).getRawType();
        }
    }

    @Override
    public void encode(T object, Writer writer) throws IOException {
        _mapper.get().writeValue(writer, object);
    }

    @Override
    public T decode(Reader reader) throws IOException {
        return _mapper.get().readValue(reader, _type);
    }

    @Override
    public void destroy() {

    }

}