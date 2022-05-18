package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.common.exception.CompileException;

import java.io.*;

@Service
@RequiredArgsConstructor
public class CodeService {

    private final ImageService imageService;

    public String compileCode(String code, String type, String boardId, String codeDir) throws IOException {
        StringBuilder result = new StringBuilder();
        try{
            String path = createDirAndGetPath(boardId, codeDir);

            File fileC = createFile(type, boardId, path);
            createNewFileAndWrite(code, fileC);

            Process process = null;
            process = Runtime.getRuntime()
                    .exec(getAbPath() +
                            "code.sh " + boardId + " " + type + " " + getCodeFileName(type, boardId));

            writeResult(result, process);

        }catch (Exception e){
            throw e;
        }


        return result.toString();
    }

    private void writeResult(StringBuilder result, Process process) throws IOException {
        if(process != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line + "\n");
            }

            BufferedReader errorBufferedReader =
                    new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine = null;
            StringBuilder errorResult = new StringBuilder();
            while ((errorLine = errorBufferedReader.readLine()) != null) {
                errorResult.append(errorLine + "\n");
            }

            if(errorResult.length() != 0){
                throw new CompileException(errorResult.toString());
            }
        }
    }

    private void createNewFileAndWrite(String code, File fileC) throws IOException {
        fileC.createNewFile();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileC));
        writer.write(code);
        writer.flush();
        writer.close();
    }

    private String createDirAndGetPath(String boardId, String codeDir) {
        String path = getAbPath() + "/" + codeDir;
        File file = new File(path);

        if(!file.exists()) file.mkdir();

        path = path + "/" + boardId;
        file = new File(path);

        if(!file.exists()) file.mkdir();

        return path;
    }

    private String getAbPath() {
        String abPath = imageService.findBoardPath("");
        return abPath;
    }

    private String getCodeFileName(String type, String boardId) {
        String codeFileName = null;
        if(type.equals("java")) codeFileName = "Main" + "." + type;
        else codeFileName = boardId + "." + type;

        return codeFileName;
    }

    private File createFile(String type, String boardId, String path) {
        File fileC = new File(path + "/" + getCodeFileName(type, boardId));

        if(fileC.exists()) fileC.delete();

        if(type.equals("c")) {
            new File(path + "/" + "a.out").delete();
        }else if(type.equals("java")){
            new File(path + "/" + "Main.class").delete();
        }
        return fileC;
    }
}
