package cz.mendelu.dnaAnalyser.sequence.stream;

import cz.mendelu.dnaAnalyser.sequence.Sequence;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import static java.text.MessageFormat.format;

@Service("StreamService")
public class ByteBufferWindowStreamService implements WindowStreamService {

    private Path storageDir = Paths.get("./data");

    @Override
    public Optional<WindowStream> open(Sequence sequence, int windowsSize) {
        try {
            Path file = getPath(sequence.getBufferId());
            long fileSize = Files.size(file);
            assert fileSize < Integer.MAX_VALUE : "Datafile is to big.";
            //assert sequence.getLength() == fileSize : "Sequence size in database and size of file has to by same";

            if (fileSize <= windowsSize) {
                throw new IllegalArgumentException(format("windows size {} is too big, sequence length is only {}.", windowsSize, fileSize));
            }

            return Optional.of(ByteBufferWindowStream.openFile(file, windowsSize));
        } catch (IOException e) {
            throw new WindowStreamException(format("Sequence file id: {} can't be open", sequence.getBufferId()), e);
        }
    }

    private Path getPath(UUID bufferId) {
        return storageDir.resolve(bufferId.toString());
    }
}
