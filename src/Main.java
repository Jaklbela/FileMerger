import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static Map<String, ArrayList<String>> tie_files = new HashMap<>();
    static List<String> all_files = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        File root = findRoot();
        readFilesInDirectory(root);
        String[] sorted_files = makeFileList();
        if (filesCorrect(sorted_files)) {
            printFiles(sorted_files);
        }
    }

    private static void printFiles (String[] sorted_files) throws IOException {
        for (String el : sorted_files) {
            System.out.println(el);
        }
        System.out.println("\nLet's see the whole text!\n");
        for (String el : sorted_files) {
            List<String> lines = Files.readAllLines(Paths.get(el), StandardCharsets.ISO_8859_1);
            String text = String.join(System.lineSeparator(), lines);
            System.out.println(text);
        }
    }

    private static boolean filesCorrect(String[] sorted_files) {
        Set<String> keys = tie_files.keySet();
        for (String key : keys) {
            boolean flag = false;
            for (String el : sorted_files) {
                if (el.contains(key)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                System.out.println("Impossible to make a list. There is no file: " + key);
                return false;
            }
        }
        return true;
    }

    private static String[] makeFileList() {
        List<String> files = List.copyOf(all_files);
        String[] sorted_files = new String[files.size()];
        files.toArray(sorted_files);
        String[] keys = new String[tie_files.size()];
        tie_files.keySet().toArray(keys);
        for (int i = 0; i < keys.length; ++i) {
            ArrayList<String> list = tie_files.get(keys[i]);
            if (list != null) {
                for (String file : list) {
                    int ind = all_files.indexOf(file);
                    if (ind < i) {
                        sorted_files[i] = all_files.get(ind);
                        sorted_files[ind] = all_files.get(i);
                    }
                }
            }
        }
        return sorted_files;
    }

    private static void readFilesInDirectory(File folder) throws IOException {
        String[] paths = folder.list();
        if (paths != null && paths.length != 0) {
            for (String el : paths) {
                String this_file = folder + "\\" + el;
                File path = new File(this_file);
                if (path.isFile()) {
                    all_files.add(this_file);
                    List<String> lines = Files.readAllLines(Paths.get(this_file), StandardCharsets.ISO_8859_1);
                    for (String line : lines) {
                        if (line.contains("require")) {
                            String another_file = line.substring(9, line.length() - 1) + ".txt";
                            Set<String> keys = tie_files.keySet();
                            boolean flag = false;
                            if (keys.size() != 0) {
                                for (String key : keys) {
                                    if (key.contains(another_file)) {
                                        tie_files.get(key).add(this_file);
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            if (!flag) {
                                ArrayList<String> files = new ArrayList<>();
                                files.add(this_file);
                                tie_files.put(another_file, files);
                            }
                        }
                    }
                } else if (path.isDirectory()){
                    readFilesInDirectory(path);
                }
            }
        }
    }

    private static File findRoot() {
        Scanner scanner = new Scanner(System.in);
        File root;
        while (true) {
            System.out.println("Please input the name of root folder and make sure that it is in a program directory.");
            String root_name = scanner.nextLine();
            root = new File(root_name);
            if (root.exists())
                return root;
            System.out.println("No such directory. Please try again.");
        }
    }
}