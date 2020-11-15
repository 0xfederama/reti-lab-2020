import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.*;

public class producerConti extends Thread {

    private final FileInputStream fin;
    private final Conti conti;

    public producerConti(Conti conti, String nomeFile) throws FileNotFoundException {
        fin = new FileInputStream(nomeFile);
        this.conti=conti;
    }

    @Override
    public void run() {

        //Creo thread pool dei thread consumer, che analizzano le occorrenze delle causali di un conto
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //Leggo il file con NIO e lo inserisco nella stinga
            FileChannel fc = fin.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            String s = "";
            while (fc.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    s+=(char)buffer.get();
                }
                buffer.clear();
            }
            //Deserializzo la stringa
            Conti contiJson = objectMapper.readValue(s, Conti.class);
            ArrayList<ContoCorrente> contiCorr = contiJson.getConti();
            for (ContoCorrente conto : contiCorr) {
                //System.out.println("PRODUCER: trovato il conto #"+conto.getId()+" di "+conto.getNome());
                pool.execute(new consumerConti(conto, conti));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        pool.shutdown();

        try {
            pool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread producer finito");
    }

}
