package cz.mendelu.dnaAnalyser.sequence.stream;

import cz.mendelu.dnaAnalyser.sequence.Nucleotide;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;


public class ByteBufferWindow implements Window {

    public static Window wrap(String plainData) {
        return wrap((ByteBuffer) ByteBuffer.wrap(plainData.getBytes()).mark());
    }

    static Window wrap(ByteBuffer buffer) {
        return new ByteBufferWindow(buffer);
    }

    private ByteBufferWindow(ByteBuffer buffer) {
        try {
            buffer.reset();
        } catch (java.nio.InvalidMarkException e) {
            throw new WindowStreamException("Buffer is not set correctly, missing start mark.", e);
        }
        this.buffer = buffer;
    }

    private final ByteBuffer buffer;

    @Override
    public int getSize() {
        return buffer.reset().remaining();
    }

    @Override
    public int getPosition() {
        return buffer.reset().position();
    }

    @Override
    public Nucleotide get(int index) {
        if (0 > index || index >= getSize()) {
            throw new IndexOutOfBoundsException("Index is out of windows size");
        }
        int markPosition = buffer.reset().position();
        return Nucleotide.get(buffer.get(markPosition + index));
    }

    @Override
    public String toPlain() {
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    @Override
    public void forEach(Consumer<Nucleotide> consumer) {
        this.forEach((i, n) -> consumer.accept(n));
    }

    @Override
    public void forEach(BiConsumer<Integer, Nucleotide> biConsumer) {
        ByteBuffer duplicate = (ByteBuffer) buffer.duplicate().reset();
        int index = 0;
        while (duplicate.position() < duplicate.limit()) {
            biConsumer.accept(index++, Nucleotide.get(duplicate.get()));
        }
    }

    @Override
    public String toString() {
        return String.format("Window{pos=%d size=%d $=%s}", getPosition(), getSize(), toPlain());
    }

    @Override
    public DoubleStream doubleStream(ToDoubleBiFunction<Integer, Nucleotide> function) {
        // TODO refaktoring
        ByteBuffer duplicate = (ByteBuffer) buffer.duplicate().reset();
        Spliterator.OfDouble spliterator = new Spliterator.OfDouble() {
            int index = 0;

            @Override
            public OfDouble trySplit() {
                return null;
            }

            @Override
            public boolean tryAdvance(DoubleConsumer action) {
                if (duplicate.position() >= duplicate.limit())
                    return false;
                Nucleotide nucleotide = Nucleotide.get(duplicate.get());
                double value = function.applyAsDouble(index++, nucleotide);
                action.accept(value);
                return true;
            }

            @Override
            public long estimateSize() {
                return getSize();
            }

            @Override
            public int characteristics() {
                return IMMUTABLE | ORDERED | NONNULL | SIZED;
            }
        };

        return StreamSupport.doubleStream(spliterator, false);


    }
}
