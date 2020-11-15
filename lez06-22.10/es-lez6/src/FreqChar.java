import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FreqChar {
    public static void main(String[] args) throws IOException {

        FileInputStream fin;
        try {
            fin = new FileInputStream("testo.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File input non trovato");
            return;
        }
        System.out.println("File input trovato");

        FileChannel fc = fin.getChannel();
        ByteBuffer buf = ByteBuffer.allocate(256);

        int[] frequenze = new int[95]; //95 caratteri US-ASCII
        for (int i=0; i<95; ++i) {
            frequenze[i]=0;
        }

        while (fc.read(buf) != -1) {
            buf.flip();
            while (buf.hasRemaining()) {
                char c = (char) buf.get();
                int ascii = (int) c;
                if (97 <= ascii && ascii <= 122) ascii-=32;
                if (c == '\n') continue;
                ascii-=32;
                frequenze[ascii]++;
            }
            buf.clear();
        }

        fc.close();
        System.out.println("File input letto, frequenze dei caratteri contate");

        FileOutputStream fout;
        try {
            fout = new FileOutputStream("freqChar.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File output non trovato");
            return;
        }
        System.out.println("File output creato correttamente");

        FileChannel fcout = fout.getChannel();
        ByteBuffer buf1 = ByteBuffer.allocate(256);

        for (int i=0; i<95; ++i) {
            int freq=frequenze[i];
            if (i>=33 && i<=58) freq+=frequenze[i+32]; //Per non distinguere maiuscole e minuscole sommo le minuscole alle maiuscole
            if (i>=65 && i<=90) continue; //A questo punto non considero nel conto le minuscole (non le metto neanche nel file)
            String s = "#Frequenze \""+(char)(i+32)+"\" = "+freq+"\n";
            byte[] sByte = s.getBytes();
            buf1.put(sByte);
            buf1.flip();
            fcout.write(buf1);
            buf1.clear();
        }

        fcout.close();
        System.out.println("Scritto nel file output");

    }
}
