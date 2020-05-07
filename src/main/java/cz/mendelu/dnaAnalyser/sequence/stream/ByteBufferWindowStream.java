package cz.mendelu.dnaAnalyser.sequence.stream;

import cz.mendelu.dnaAnalyser.sequence.Sequence;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implementation of Window Spliterator using ByteBuffer directly allocated from disk file.
 */
public class ByteBufferWindowStream implements WindowStream {

    // Theoretically, these two attributes are not necessary to store.
    //private final int windowsSize;
    //private final long sequenceSize;
    //private final ByteBuffer buffer;
    private final FileChannel fileChannel;
    private Stream<Window> headOfStream;
    private int position = 0;
    private boolean processLeaveWindowStream = false;

    private ByteBufferWindowStream(FileChannel fileChannel, int windowsSize) throws IOException {
        this(fileChannel,
                fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size()),
                fileChannel.size(),
                windowsSize);
    }

    private ByteBufferWindowStream(ByteBuffer buffer, int windowsSize) {
        this(null,
                buffer.asReadOnlyBuffer(),
                buffer.limit(),
                windowsSize);
    }

    private ByteBufferWindowStream(FileChannel fileChannel, ByteBuffer buffer, long sequenceSize, int windowsSize) {
        this.fileChannel = fileChannel;
        //this.buffer = buffer;
        //this.sequenceSize = sequenceSize;
        //this.windowsSize = windowsSize;
        this.headOfStream = StreamSupport.stream(new SequenceSpliterator<Window>() {
            @Override
            public boolean tryAdvance(Consumer<? super Window> action) {
                if (position + windowsSize > sequenceSize)
                    return false;

                buffer.position(position);
                buffer.mark();
                buffer.limit(position + windowsSize);
                Window window = ByteBufferWindow.wrap(buffer);
                action.accept(window);
                position++;
                return true;
            }

            @Override
            public long estimateSize() {
                return sequenceSize;
            }
        }, false);
    }

    /**
     * Open WindowsStream over sequence file, witch have to be in internal format.
     * This method is mainly used in {@link ByteBufferWindowStreamService#open(Sequence, int)} as primary way how to work with sequence data.
     *
     * @param file        data file
     * @param windowsSize windows size
     * @return opened WindowStream.
     * @throws IOException         if file problem.
     * @throws NoSuchFileException id file not exists.
     */
    static ByteBufferWindowStream openFile(Path file, int windowsSize) throws IOException {
        FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ);
        return new ByteBufferWindowStream(fileChannel, windowsSize);
    }

    private void streamStateTest() {
        if (processLeaveWindowStream) {
            throw new WindowStreamException("Process leave Windows stream, you can not modify pipeline.");
        }
    }

    @Override
    public void forEach(Consumer<? super Window> action) {
        streamStateTest(); // This has to be call before each work with WindowsStream.
        headOfStream.forEach(action);
    }

    @Override
    public WindowStream filter(Predicate<? super Window> predicate) {
        streamStateTest(); // This has to be call before each work with WindowsStream.
        headOfStream = headOfStream.filter(predicate);
        return this;
    }

    @Override
    public <R> Stream<R> map(Function<? super Window, ? extends R> mapper) {
        streamStateTest(); // This has to be call before each work with WindowsStream.
        processLeaveWindowStream = true; // After call map, is not possible use this Class to work with stream.
        return headOfStream.map(mapper);
    }

    @Override
    public void close() throws Exception {
        if (fileChannel != null) {
            fileChannel.close();
        }
    }
}
