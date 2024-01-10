import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

// access = (MatrixMultiplicationInterface) Naming.lookup("rmi://" + "length-scuba.gl.at.ply.gg" + ":" + 23042 + "/MatrixMultiplication");
enum Algorithm {
    NONE,
    SEQUENTIAL,
    FORKJOIN,
    EXECUTE,
}

public class View extends JFrame {
    private final JTextPane unsortedTextPane;
    private final JTextPane sortedTextPane;
    private final JTextField arraySizeTextField;
    private final JLabel selectedAlgorithmLabel;
    private final JLabel elapsedTimeSequential;
    private final JLabel elapsedTimeForkJoin;
    private final JLabel elapsedTimeExecute;
    private final JLabel messageLabel;
    private Algorithm algorithm = Algorithm.NONE;
    private String host;
    private int port;

    private final MatrixMultiplicationInterface access;

    {
        try {
            access = (MatrixMultiplicationInterface) Naming.lookup("rmi://" + "length-scuba.gl.at.ply.gg" + ":" + 23042 + "/MatrixMultiplication");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    final DecimalFormat formatter = new DecimalFormat("#,###");

    public View(String host, int port){
        this.host = host;
        this.port = port;
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException |
                InstantiationException |
                IllegalAccessException |
                UnsupportedLookAndFeelException e) {

            System.out.println("Look and feel not set");
        }
        Panel panel = new Panel();
        panel.setBackground(Color.DARK_GRAY);
        this.setContentPane(panel);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setTitle("Algoritmos de ordenamiento");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 480);

        JButton sequentialButton = new JButton("Secuencial");
        sequentialButton.setBounds(25, 60, 100, 25);

        JButton forkJoinButton = new JButton("Execute");
        forkJoinButton.setBounds(25, 95, 100, 25);

        JButton executeButton = new JButton("ForkJoin");
        executeButton.setBounds(25, 130, 100, 25);

        arraySizeTextField = new JTextField();
        arraySizeTextField.setBounds(25, 25, 100, 25);
        arraySizeTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                arraySizeTextField.setEditable(e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '\u0008');
            }
        });

        unsortedTextPane = new JTextPane();
        unsortedTextPane.setEditable(false);

        sortedTextPane = new JTextPane();
        sortedTextPane.setEditable(false);

        JButton startButton = new JButton("Comenzar");
        startButton.setBounds(25, 200, 100, 25);

        JButton clearButton = new JButton("Borrar");
        clearButton.setBounds(25, 235, 100, 25);

        selectedAlgorithmLabel = new JLabel("<html>Algoritmo<br>selecciónado:<br>Ninguno</html>");
        selectedAlgorithmLabel.setBounds(25, 270, 100, 50);
        selectedAlgorithmLabel.setForeground(Color.WHITE);

        JScrollPane scrollPane1 = new JScrollPane(unsortedTextPane);
        scrollPane1.setBounds(150, 25, 400, 150);

        JScrollPane scrollPane2 = new JScrollPane(sortedTextPane);
        scrollPane2.setBounds(150, 200, 400, 150);

        elapsedTimeSequential = new JLabel("");
        elapsedTimeSequential.setBounds(25, 390, 400, 25);
        elapsedTimeSequential.setForeground(Color.WHITE);

        elapsedTimeForkJoin = new JLabel("");
        elapsedTimeForkJoin.setBounds(225, 390, 400, 25);
        elapsedTimeForkJoin.setForeground(Color.WHITE);

        elapsedTimeExecute = new JLabel("");
        elapsedTimeExecute.setBounds(425, 390, 400, 25);
        elapsedTimeExecute.setForeground(Color.WHITE);

        messageLabel = new JLabel("");
        messageLabel.setBounds(150, 175, 400, 25);
        messageLabel.setForeground(Color.RED);


        panel.add(sequentialButton);
        panel.add(arraySizeTextField);
        panel.add(forkJoinButton);
        panel.add(executeButton);
        panel.add(scrollPane1);
        panel.add(scrollPane2);
        panel.add(startButton);
        panel.add(clearButton);
        panel.add(selectedAlgorithmLabel);
        panel.add(elapsedTimeSequential);
        panel.add(elapsedTimeForkJoin);
        panel.add(elapsedTimeExecute);
        panel.add(messageLabel);

        sequentialButton.addActionListener(e -> selectSortingAlgorithm(Algorithm.SEQUENTIAL));

        forkJoinButton.addActionListener(e -> selectSortingAlgorithm(Algorithm.FORKJOIN));

        executeButton.addActionListener(e -> selectSortingAlgorithm(Algorithm.EXECUTE));

        clearButton.addActionListener(e -> {
            selectSortingAlgorithm(Algorithm.NONE);
            arraySizeTextField.setText("");
            sortedTextPane.setText("");
            unsortedTextPane.setText("");
            elapsedTimeSequential.setText("");
            elapsedTimeForkJoin.setText("");
            elapsedTimeExecute.setText("");
            messageLabel.setText("");
        });

        startButton.addActionListener(e -> {
            int rows = Integer.parseInt(arraySizeTextField.getText());
            int[][] matrix = MatrixUtilities.createRandomMatrix(10, rows * 12);

            unsortedTextPane.setText(MatrixUtilities.getMatrixString(matrix));

            switch (algorithm) {
                case NONE: {
                    messageLabel.setText("Seleccione un algoritmo!");
                    break;
                }
                case SEQUENTIAL: {
                    try {
                        int[][] resultMatrix = access.sequentialMultiplication(matrix, 2);
                        long duration = access.getDuration();
                        elapsedTimeSequential.setText("Secuencial: " + formatter.format(duration / 1000) + " Microsegundos");
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
                case EXECUTE:  {
                    try {
                        int[][] resultMatrix = access.executorServiceMultiplication(matrix, 2);
                        long duration = access.getDuration();
                        elapsedTimeForkJoin.setText("Fork: " + formatter.format(duration / 1000) + " Microsegundos");
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
                case FORKJOIN: {
                    try {
                        int[][] resultMatrix  = access.forkJoinMultiplication(matrix, 2, 0, matrix.length);
                        long duration = access.getDuration();
                        elapsedTimeExecute.setText("Execute: " + formatter.format(duration / 1000) + " Microsegundos");
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
        });
    }

    private void selectSortingAlgorithm(Algorithm selected) {
        this.algorithm = selected;
        switch (algorithm) {
            case NONE: selectedAlgorithmLabel.setText("<html>Algoritmo<br>selecciónado:<br>Ninguno</html>");
            case SEQUENTIAL: selectedAlgorithmLabel.setText("<html>Algoritmo<br>selecciónado:<br>Secuencial</html>");
            case FORKJOIN: selectedAlgorithmLabel.setText("<html>Algoritmo<br>selecciónado:<br>Execute</html>");
            case EXECUTE: selectedAlgorithmLabel.setText("<html>Algoritmo<br>selecciónado:<br>Forkjoin</html>");
        }
    }
}
