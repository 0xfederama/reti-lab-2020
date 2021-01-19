import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CongressoClient {
    public static void main(String[] args) {

        //Controllo degli argomenti
        if (args.length != 1) {
            System.out.println("Usage: java CongressoClient <port>");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("ERR -port "+args[0]+": not a number");
            System.out.println("Usage: java CongressoClient <port>");
            return;
        }

        //Apro lo scanner da linea di comando
        Scanner scanner = new Scanner(System.in);
        System.out.println("Comandi:\n" +
                "  speaker - registra uno speaker\n" +
                "  programma - leggi il programma dei giorni\n" +
                "  help - stampa i comandi\n" +
                "  quit - chiudi\n");

        //Mi collego al server ed eseguo i comandi dati dal terminale
        CongressoService serverObject;
        Remote remoteObject;
        try {

            //Prendo il registry
            Registry r = LocateRegistry.getRegistry(port);
            remoteObject = r.lookup("CONGRESSO-SERVER");
            serverObject = (CongressoService) remoteObject;

            //While di lettura dei comandi da terminale
            while (true) {

                //Inserire il comando
                System.out.print("$> ");
                String cmd = scanner.next();

                if (cmd.equals("speaker")) {

                    //Leggo e aggiungo lo speaker
                    String nome;
                    int giorno;
                    int sessione;
                    int intervento;
                    System.out.print("Nome dello speaker: ");
                    nome = scanner.next();
                    try {
                        System.out.print("Giorno (in [1,3]): ");
                        giorno = scanner.nextInt();
                        System.out.print("Sessione (in [1,12]): ");
                        sessione = scanner.nextInt();
                        System.out.print("Intervento (in [1,5]): ");
                        intervento = scanner.nextInt();
                        if (!serverObject.addSpeaker(giorno, sessione, intervento, nome)) {
                            System.out.println("Posto occupato o non esistente, riprovare\n");
                        } else {
                            System.out.println("Speaker registrato correttamente\n");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Non sono stati inseriti dei numeri, riprovare\n");
                    }

                } else if (cmd.equals("programma")) {

                    //Stampo l'intero programma del congresso
                    Giorno[] giorni = serverObject.getProgCompleto();
                    for (int i = 0; i < 3; ++i) {
                        System.out.println("Giorno #" + (i + 1));
                        String[][] progGiorno = giorni[i].getProgramma();
                        for (int j = 0; j < 12; j++) {
                            System.out.printf("Sessione %2d: ", j + 1);
                            for (int k = 0; k < 5; ++k) {
                                System.out.print(progGiorno[j][k] + " ");
                            }
                            System.out.println();
                        }
                        System.out.println();
                    }

                } else if (cmd.equals("quit")) {
                    System.out.println("Chiudo il client...");
                    break;
                } else if (cmd.equals("help")) {
                    System.out.println("Comandi:\n  speaker - registra uno speaker\n  programma - leggi il programma dei giorni\n  help - stampa i comandi\n  quit - chiudi\n");
                } else {
                    System.out.println("Comando non supportato, riprovare\n");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
