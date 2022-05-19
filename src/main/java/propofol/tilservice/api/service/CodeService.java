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
        String path = createDirAndGetPath(boardId, codeDir);

        try{
            File fileC = createOriginFile(type, boardId, path);
            createNewFileAndWrite(code, fileC);

            Process process = null;
            process = Runtime.getRuntime()
                    .exec(getAbPath() +
                            "code.sh " + boardId + " " + type + " " + getCodeFileName(type, boardId));

            writeResult(result, process, boardId);

        }catch (Exception e){
            throw e;
        }finally {
            deleteFile(type, boardId, path);
        }


        return result.toString();
    }

    private String getAbPath() {
        String abPath = imageService.findBoardPath("");
        return abPath;
    }

    private void createNewFileAndWrite(String code, File fileC) throws IOException {
        fileC.createNewFile();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileC));
        writer.write(code);
        writer.flush();
        writer.close();
    }

    private String createDirAndGetPath(String boardId, String codeDir) {
        String path = getAbPath() + codeDir;
        File file = createFile(path);

        if(!file.exists()) file.mkdir();

        path = path + "/" + boardId;
        file = createFile(path);

        if(!file.exists()) file.mkdir();

        return path;
    }

    private File createOriginFile(String type, String boardId, String path) {
        File fileC = createFile(path + "/" + getCodeFileName(type, boardId));

        return fileC;
    }

    private File createFile(String path) {
        return new File(path);
    }

    private String getCodeFileName(String type, String boardId) {
        String codeFileName = null;
        if(type.equals("java")) codeFileName = "Main" + "." + type;
        else codeFileName = boardId + "." + type;

        return codeFileName;
    }

    private void writeResult(StringBuilder result, Process process, String boardId) throws IOException {
        if(process != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line + "\n");
            }

            BufferedReader errorBufferedReader =
                    new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine = null;
            String errorResult = null;
            while ((errorLine = errorBufferedReader.readLine()) != null) {
                errorResult += errorLine;
            }

            if(errorResult != null){

                errorResult = checkAndDelete(boardId, errorResult);

                close(bufferedReader, errorBufferedReader);

                throw new CompileException(errorResult);
            }

            close(bufferedReader, errorBufferedReader);
        }
    }

    private void close(BufferedReader bufferedReader, BufferedReader errorBufferedReader) throws IOException {
        bufferedReader.close();
        errorBufferedReader.close();
    }

    private String checkAndDelete(String boardId, String errorResult) {
        if(errorResult.contains("File \"/Users/yc/study/back/code/propofol" +
                "/propofol-til-service/save-board-code/"+ boardId + "/" + boardId +".python\",")){
            errorResult = errorResult.replace("File \"/Users/yc/study/back/code/propofol" +
                    "/propofol-til-service/save-board-code/"+ boardId + "/" + boardId +".python\",", "");
        }

        if(errorResult.startsWith("null"))
            errorResult = errorResult.substring(5);

        return errorResult;
    }

    private void deleteFile(String type, String boardId, String path) {
        File fileC = createFile(path + "/" + getCodeFileName(type, boardId));

        if(fileC.exists()) fileC.delete();

        if(type.equals("c")){
            File file = createFile(path + "/a.out");
            if(file.exists()) file.delete();
        }else if(type.equals("java")){
            File file = createFile(path + "/Main.class");
            if(file.exists()) file.delete();
            file = createFile(path + "/Main.java");
            if(file.exists()) file.delete();
        }

        File file = createFile(path);
        if(file.exists()) file.delete();
    }
}
