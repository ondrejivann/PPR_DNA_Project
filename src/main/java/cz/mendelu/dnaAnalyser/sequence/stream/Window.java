package cz.mendelu.dnaAnalyser.sequence.stream;

import cz.mendelu.dnaAnalyser.sequence.Nucleotide;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.DoubleStream;

/**
 * Window represents part of DNA/RNA sequence, witch are currently analysed.
 * <p>
 *
 * <pre>
 *     Data: ACTGTGGTGTTTTGGTGTGTGGCCCGCAAATTTVVTGGG
 *                   |     |
 *     Window:       GTTTTGG
 *                   |>   <| size = 7
 *                   |
 *                   postition = 8
 * </pre>
 * @author Ing. Jan Kolomazník, Ph.D.
 */
public interface Window {

    /**
     * Return size of windows. as number of nucleotide which contains.
     * @return Size of window
     */
    int getSize();

    /**
     * Get Absolute position in sequence.
     * @return  position first nucleotide in sequence, start from <code>0</code> to <code>(length - 1)</code>.
     */
    int getPosition();

    /**
     * Get nucleotide from specifik potion inside window.
     * This method reset inner buffer and calculate nucleotide position for each call.
     * If you need go throw all window, nucletide by nucletode, id match use {@link #forEach(Consumer)} or {@link #forEach(BiConsumer)}
     * @param index inside window, <code>0 <= index < size</code>
     * @return Nucleotide at the index.
     * @throws IndexOutOfBoundsException if index out of window size.
     */
    Nucleotide get(int index);

    /**
     * Transform window to simple text result.
     * This method is design for development and debugging mostly.
     * @return Window as ACTG String
     */
    String toPlain();

    /**
     * This method iterate over whole window.
     * @param consumer which process data using only nucleotide.
     */
    void forEach(Consumer<Nucleotide> consumer);

    /**
     * This method iterate over whole window with index.
     * @param biConsumer which process data using index and nucleotide.
     */
    void forEach(BiConsumer<Integer, Nucleotide> biConsumer);

    /**
     * Transform {@link Window} to stream of doubles.
     * @param function
     * @return
     */
    DoubleStream doubleStream(ToDoubleBiFunction<Integer, Nucleotide> function);
}
