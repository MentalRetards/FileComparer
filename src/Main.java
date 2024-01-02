import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<File> files1 = new ArrayList<>();
    public static List<File> files2 = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        String dir1 = Util.getInput("Enter Dir 1");
        String dir2 = Util.getInput("Enter Dir 2");
        String out = Util.getInput("Enter output Dir");
        List<File> missingFiles = new ArrayList<>();
        File outt = new File(out);
        File output = FileHandler.createDir(outt, "output" + getNumber(outt));

        File directory1 = new File(dir1);
        if (directory1.isDirectory()) {
            files1 = getAllFiles(directory1);
        } else {
            for (String string : FileHandler.readFromFile(directory1)) {
                string.replaceAll("\n", "");
                files1.add(new File(string));
            }
        }

        File directory2 = new File(dir2);
        files2 = getAllFiles(directory2);

        File outputFiles1 = FileHandler.createFile(output.getAbsolutePath(), "files1Output.txt");
        File outputFiles2 = FileHandler.createFile(output.getAbsolutePath(), "files2Output.txt");
        File outputMissing = FileHandler.createFile(output.getAbsolutePath(), "filesMissing.txt");
        File dupesCopied = FileHandler.createFile(output.getAbsolutePath(), "dupesCopied.txt");
        File files1Dupes = FileHandler.createFile(output.getAbsolutePath(), "files1Dupes.txt");
        File files2Dupes = FileHandler.createFile(output.getAbsolutePath(), "files2Dupes.txt");
        FileHandler.overWriteToFileFile(outputFiles1, files1, true);
        FileHandler.overWriteToFileFile(outputFiles2, files2, true);
        for (File file2 : files2) {
            boolean found = false;
            for (File file1 : files1) {
                if (file1.getName().equals(file2.getName())) {
                    found = true;
                    break;
                }
            }
            if (found) continue;
            missingFiles.add(file2);
        }
        FileHandler.overWriteToFileFile(outputMissing, missingFiles, true);
        System.out.println("Found all files missing files. Copying them to output");
        File copyDir = FileHandler.createDir(output, "copy");
        for (File file : missingFiles) {
            copyFile(file, copyDir, directory2);
        }
        System.out.println("Copied all files over, checking for duplicates");
        List<String> dupes = new ArrayList<>();
        List<String> used = new ArrayList<>();
        for (File file : missingFiles) {
            for (File file1 : missingFiles) {
                if (file.getName().equals(file1.getName()) && !file.getAbsolutePath().equals(file1.getAbsolutePath())) {
                    if (used.contains(file1.getAbsolutePath()) && used.contains(file.getAbsolutePath())) continue;
                    used.add(file.getAbsolutePath());
                    used.add(file1.getAbsolutePath());
                    System.out.println("Found duplicate files in copied over files");
                    dupes.add("\n");
                    dupes.add(file.getAbsolutePath());
                    dupes.add(file1.getAbsolutePath());
                    dupes.add("\n");
                }
            }
        }
        FileHandler.overWriteToFile(dupesCopied, dupes);
        dupes.clear();
        used.clear();
        for (File file : files1) {
            for (File file1 : files1) {
                if (file.getName().equals(file1.getName()) && !file.getAbsolutePath().equals(file1.getAbsolutePath())) {
                    if (used.contains(file1.getAbsolutePath()) && used.contains(file.getAbsolutePath())) continue;
                    used.add(file.getAbsolutePath());
                    used.add(file1.getAbsolutePath());
                    System.out.println("Found duplicate files in files1");
                    dupes.add("\n");
                    dupes.add(file.getAbsolutePath());
                    dupes.add(file1.getAbsolutePath());
                    dupes.add("\n");
                }
            }
        }
        FileHandler.overWriteToFile(files1Dupes, dupes);
        dupes.clear();
        used.clear();
        for (File file : files2) {
            for (File file1 : files2) {
                if (file.getName().equals(file1.getName()) && !file.getAbsolutePath().equals(file1.getAbsolutePath())) {
                    if (used.contains(file1.getAbsolutePath()) && used.contains(file.getAbsolutePath())) continue;
                    used.add(file.getAbsolutePath());
                    used.add(file1.getAbsolutePath());
                    System.out.println("Found duplicate files in files2");
                    dupes.add("\n");
                    dupes.add(file.getAbsolutePath());
                    dupes.add(file1.getAbsolutePath());
                    dupes.add("\n");
                }
            }
        }
        FileHandler.overWriteToFile(files2Dupes, dupes);
        System.out.println("Finished checking for dupes");
    }
    public static void copyFile(File file, File out, File originalFileDirectory) throws IOException {
        File file1 = new File(file.getParent());
        StringBuilder path = new StringBuilder();
        while (!file1.getAbsolutePath().equals(originalFileDirectory.getAbsolutePath())) {
            path.insert(0, file1.getName() + "\\");
            file1 = new File(file1.getParent());
        }
        File directory = new File(out + "\\" + path);
        directory.mkdirs();




        Path copied = Paths.get(directory.getAbsolutePath() + "\\" + file.getName());
        Path originalPath = file.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        if (!copied.toFile().exists()) throw new IOException("COPIED FILE DOES NOT EXIST");
        try {
            if (!Files.readAllLines(originalPath).equals(Files.readAllLines(copied)))
                throw new IOException("COPIED FILE DOES NOT MATCH");
        } catch (MalformedInputException ignore) {

        }
    }
    public static int getNumber(File file) {
        int num = 1;
        List<Integer> nums = new ArrayList<>();
        for (File file1 : file.listFiles()) {
            if (!file1.isDirectory()) continue;
            nums.add(Integer.parseInt(file1.getName().replace("output", "")));
        }
        for (int i = 1;; i ++) {
            if (!nums.contains(i)) {
                num = i;
                break;
            }
        }
        return num;
    }
    public static List<File> getAllFiles(File file) {
        List<File> files = new ArrayList<>();
        List<File> dirsToCheck = new ArrayList<>();
        dirsToCheck.add(file);
        while (!dirsToCheck.isEmpty()) {
            File dir = dirsToCheck.get(0);
            if (dir.getName().equals("$exclude$443")) {
                dirsToCheck.remove(0);
                continue;
            }
            for (File file1 : dir.listFiles()) {
                if (file1.isDirectory()) dirsToCheck.add(file1);
                if (file1.isFile()) {
                    files.add(file1);
                }
            }
            dirsToCheck.remove(0);
        }
        return files;
    }
}