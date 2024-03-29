package propofol.tilservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "file")
@ConstructorBinding
public class FileProperties {
    private final String boardDir;
    private final String codeDir;

    public FileProperties(String boardDir, String codeDir) {
        this.boardDir = boardDir;
        this.codeDir = codeDir;
    }
}
