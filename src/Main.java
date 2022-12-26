import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static Scanner IN = new Scanner(System.in);
    static File ROOT;
    static Map<String, ArrayList<String>> TIE_FILES = new HashMap<>();
    static List<String> FILES = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        findRoot();
        readFilesInDirectory(ROOT);
        Object[] sorted_files = makeFileList();
        for (Object el : sorted_files) {
            System.out.println(el);
        }
    }

    private static Object[] makeFileList() {
        List<String> files = List.copyOf(FILES);
        Object[] sorted_files = files.toArray();
        for (int i = 0; i < FILES.size(); ++i) {
            ArrayList<String> list = TIE_FILES.get(FILES.get(i));
            if (list != null) {
                for (String file : list) {
                    int ind = FILES.indexOf(file);
                    if (ind < i) {
                        sorted_files[i] = FILES.get(ind);
                        sorted_files[ind] = FILES.get(i);
                    }
                }
            }
        }
        return sorted_files;
    }

    private static void readFilesInDirectory(File folder) throws IOException {
        String[] paths = folder.list();
        if (paths.length != 0) {
            for (String el : paths) {
                String this_file = folder + "/" + el;
                File path = new File(this_file);
                if (path.isFile()) {
                    FILES.add(this_file);
                    List<String> lines = Files.readAllLines(Paths.get(this_file), StandardCharsets.ISO_8859_1);
                    for (String line : lines) {
                        if (line.contains("require")) {
                            String another_file = line.substring(9, line.length() - 1) + ".txt";
                            if (TIE_FILES.containsKey(another_file)) {
                                TIE_FILES.get(another_file).add(this_file);
                            } else {
                                ArrayList<String> files = new ArrayList<>();
                                files.add(this_file);
                                TIE_FILES.put(another_file, files);
                            }
                        }
                    }
                } else if (path.isDirectory()){
                    readFilesInDirectory(path);
                }
            }
        }
    }

    private static void findRoot() {
        while (true) {
            System.out.println("Please input the name of root folder and make sure that it is in a program directory.");
            String root_name = IN.nextLine();
            ROOT = new File(root_name);
            if (ROOT.exists())
                break;
            else
                System.out.println("No such directory. Please try again.");
        }
    }
}