package cz.mendelu.dnaAnalyser.sequence.stream;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface WindowStream extends AutoCloseable  {

    /**
     * Performs an action for each element of this stream.
     *
     * <p>The behavior of this operation is explicitly deterministic.
     * This operation guarantee to respect the encounter position of window in sequence.
     *
     * @param action a <a href="package-summary.html#NonInterference">
     *               non-interfering</a> action to perform on the elements
     */
    void forEach(Consumer<? super Window> action);


    /**
     * Returns a stream consisting of the sequence windows of this stream that match
     * the given predicate.
     *
     * @param predicate predicate to apply to each window to determine if it
     *                  should be included
     * @return the new stream
     */
    WindowStream filter(Predicate<? super Window> predicate);

    /**
     * Returns a stream consisting of the results of applying the given
     * function to the elements of this stream.
     * <p>
     * This method finish work with our Windows stream and continue with a regular Java {@link Stream}
     *
     * @param <R>    The element type of the new stream
     * @param mapper a function to apply to each element
     * @return the new stream
     */
    <R> Stream<R> map(Function<? super Window, ? extends R> mapper);
}
