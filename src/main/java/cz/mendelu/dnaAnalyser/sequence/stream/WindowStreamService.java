package cz.mendelu.dnaAnalyser.sequence.stream;

import cz.mendelu.dnaAnalyser.sequence.Sequence;

import java.util.Optional;


/**
 * Window Stream Service. It Contains method for work with sequences data as Stream.
 */
public interface WindowStreamService {

    /**
     * Open new window stream for sequence.
     * <p>
     *     The sequence can have data "ready for use", if they are already imported, but when you import amount of sequences, it take same time.
     *     This is reason, why this object return {@link Optional} intend of {@link ByteBufferWindow}.
     * </p>
     * <p>
     *     The windows stream will progressively move through the sequence from one Nucleotide to the otherNucleotide.
     * <pre>
     * open(ACTGACTG, 4)
     *      |  |
     *      ACTG
     *       CTGA
     *        TGAC
     *         GACT
     *          ACTG
     * </pre>
     * </p>
     * @param sequence for the data to be opened.
     * @param windowsSize size of windows
     * @return {@link Optional} object of WindowStream.
     */
    // TODO Replace optional with Future???
    Optional<WindowStream> open(Sequence sequence, int windowsSize);
}
