package propofol.tilservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "file")
@ConstructorBinding
public class FileProperties {
    private final String profileDir;
    private final String boardDir;

    public FileProperties(String profileDir, String boardDir) {
        this.profileDir = profileDir;
        this.boardDir = boardDir;
    }
}
