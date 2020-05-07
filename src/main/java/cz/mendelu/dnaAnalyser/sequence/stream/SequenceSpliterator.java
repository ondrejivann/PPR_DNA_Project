package cz.mendelu.dnaAnalyser.sequence.stream;

import java.util.Spliterator;

interface SequenceSpliterator<T> extends Spliterator<T> {

    @Override
    default Spliterator<T> trySplit() {
        return null;
    }

    @Override
    default long getExactSizeIfKnown() {
        return estimateSize();
    }

    @Override
    default int characteristics() {
        return IMMUTABLE | ORDERED | NONNULL | SIZED;
    }

    @Override
    default boolean hasCharacteristics(int characteristics) {
        return false;
    }
}
