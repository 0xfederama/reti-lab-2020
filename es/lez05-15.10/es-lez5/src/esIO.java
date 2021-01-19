import java.io.*;
import java.util.LinkedList;

public class esIO {
    public static void main(String[] args) throws IOException {
        File dir = new File("/home/federico/Desktop/Dropbox/coding/apps-settings");
        LinkedList<File> dirs = new LinkedList<>();
        dirs.add(dir);

        DataOutputStream outDir = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("directories.txt"))));
        DataOutputStream outFiles = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("files.txt"))));

        while (dirs.size()>0) {
            File[] files = dirs.remove(0).listFiles();
            for (File f : files) {
                String s = f.getName().toString()+"\n";
                if (f.isDirectory()) {
                    outDir.write(s.getBytes());
                    dirs.add(f);
                } else {
                    outFiles.write(s.getBytes());
                }
            }
        }

        outFiles.flush();
        outDir.flush();
        outDir.close();
        outFiles.close();

    }
}
