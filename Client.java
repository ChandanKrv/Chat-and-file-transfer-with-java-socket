import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * Created by Chandan on May 24, 2021.
 */


public class Client extends JFrame {

    private static Socket socketFile, socketChat;


    //Global Variables
    private JPanel headerPanel;
    final String iconPath = "icons/";
    private JTextArea messageArea;
    private JTextField messageInput;
    private static JButton sendBtn;
    private static JLabel fileLabel;
    private static JButton chooseBtn;
    BufferedReader br;
    PrintWriter out;
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public static void main(String[] args) throws IOException {
        // Accessed from within inner class needs to be final or effectively final.
        final File[] fileToSend = new File[1];

        // Create a socket connection to connect with the server.
        System.out.println("This is client");
        new Client();

        chooseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser to open the dialog to choose a file.
                JFileChooser jFileChooser = new JFileChooser();
                // Set the title of the dialog.
                jFileChooser.setDialogTitle("Choose a file to send.");
                // Show the dialog and if a file is chosen from the file chooser execute the following statements.
                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    // Get the selected file.
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    // Change the text of the java swing label to have the file name.
                    fileLabel.setText(fileToSend[0].getName());
                }
            }
        });


        // Sends the file when the button is clicked.
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If a file has not yet been selected then display this message.
                if (fileToSend[0] == null) {
                    fileLabel.setText("NO File Selected");
                    fileLabel.setForeground(new Color(255, 0, 0));
                    // If a file has been selected then do the following.
                } else {
                    try {
                        // Create an input stream into the file you want to send.
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());

                        // Create an output stream to write to write to the server over the socket connection.
                        DataOutputStream dataOutputStream = new DataOutputStream(socketFile.getOutputStream());
                        // Get the name of the file you want to send and store it in filename.
                        String fileName = fileToSend[0].getName();
                        // Convert the name of the file into an array of bytes to be sent to the server.
                        byte[] fileNameBytes = fileName.getBytes();
                        // Create a byte array the size of the file so don't send too little or too much data to the server.
                        byte[] fileBytes = new byte[(int) fileToSend[0].length()];
                        // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
                        fileInputStream.read(fileBytes);
                        // Send the length of the name of the file so server knows when to stop reading.
                        dataOutputStream.writeInt(fileNameBytes.length);
                        // Send the file name.
                        dataOutputStream.write(fileNameBytes);
                        // Send the length of the byte array so the server knows when to stop reading.
                        dataOutputStream.writeInt(fileBytes.length);
                        // Send the actual file.
                        dataOutputStream.write(fileBytes);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }


    public Client() {


        try {
            socketChat = new Socket("localhost", 8888);
            socketFile = new Socket("localhost", 8889);
            System.out.println("Connection Success");
            br = new BufferedReader(new InputStreamReader(socketChat.getInputStream()));
            out = new PrintWriter(socketChat.getOutputStream());
            createGUI("Client Messenger Tab", 400, 600, 170, 116);
            setIcon("back", "3.png", 40, 40, 5, 15, 30, 30, true);
            setIcon("profile", "clientProfile.png", 45, 45, 40, 1, 60, 60, true);
            setIcon("video", "video.png", 35, 35, 270, 20, 26, 25, true);
            setIcon("audio", "phone.png", 35, 35, 320, 20, 26, 25, true);
            setIcon("dots", "3icon.png", 13, 25, 370, 20, 13, 25, true);
            setLabel("Client", true, 18, 110, 15, 200, 18);
            setLabel("Active Now", false, 14, 110, 35, 100, 20);
            handleEvents();
            startReading();
        } catch (Exception e) {
        }
        setLayout(null);
        setSize(420, 670);
        setLocation(250, 80);
        setResizable(false);
        setVisible(true);
    }

    private void setLabel(String labelText, Boolean styleBold, int labelSize, int lBoundX, int lBoundY, int lBoundWidth, int lBoundHeight) {
        JLabel l3 = new JLabel(labelText);
        if (styleBold) {
            l3.setFont(new Font("SAN_SERIF", Font.BOLD, labelSize));
        } else {
            l3.setFont(new Font("SAN_SERIF", Font.PLAIN, labelSize));
        }
        l3.setForeground(Color.WHITE);
        l3.setBounds(lBoundX, lBoundY, lBoundWidth, lBoundHeight);
        headerPanel.add(l3);

    }


    private void setIcon(String target, String iName, int iWidth, int iHeight, int iPosX, int iPosY, int iBoundWidth, int iBoundHeight, boolean onHeader) {
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource(iconPath + iName));
        Image i2 = i1.getImage().getScaledInstance(iWidth, iHeight, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l1 = new JLabel(i3);
        l1.setBounds(iPosX, iPosY, iBoundWidth, iBoundHeight);
        if (onHeader)
            headerPanel.add(l1);
        else
            add(l1);

        if (target.equals("back")) {
            l1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Alert", dialogButton);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            });
        }
        if (target.equals("dots")) {
            l1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                aboutSection();
                }
            });
        }
    }


    private void createGUI(String title, int width, int height, int posX, int posY) {
        // GUI codes
        this.setTitle(title);
        this.setSize(width, height); // width, height
        //   this.setLocationRelativeTo(null); // center
        this.setLocation(posX, posY);


        headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(new Color(24, 97, 89));
        headerPanel.setBounds(0, 0, 450, 60);
        add(headerPanel);


        messageArea = new JTextArea();
        messageArea.setBounds(5, 64, 397, 499);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("SEN_SERIF", Font.PLAIN, 18));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        //messageArea.setBackground(Color.BLACK);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        // this.add(messageArea, BorderLayout.NORTH);
        this.add(jScrollPane, BorderLayout.CENTER);
        add(messageArea);


        messageInput = new JTextField();
        messageInput.setBounds(6, 578, 240, 45);
        messageInput.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        //messageInput.setHorizontalAlignment(SwingConstants.CENTER); //Center Typing
        this.add(messageInput, BorderLayout.SOUTH);
        messageArea.setEditable(false);
        add(messageInput);

        chooseBtn = new JButton("Select");
        // chooseBtn.setBounds(260, 585, 70, 40);
        chooseBtn.setBounds(250, 590, 75, 30);
        chooseBtn.setForeground(new Color(3, 95, 84));
        chooseBtn.setFont(new Font("SAN_SERIF", Font.BOLD, 13));
        add(chooseBtn);


        sendBtn = new JButton("Send");
        sendBtn.setBounds(330, 590, 70, 30);
        sendBtn.setForeground(new Color(3, 95, 84));
        sendBtn.setFont(new Font("SAN_SERIF", Font.BOLD, 13));
        add(sendBtn);

        fileLabel = new JLabel("No file selected");
        fileLabel.setBounds(255, 560, 140, 30);
        fileLabel.setForeground(new Color(3, 95, 84));
        fileLabel.setFont(new Font("SAN_SERIF", Font.BOLD, 12));
        add(fileLabel);

        this.setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    private void aboutSection() {
        JOptionPane.showMessageDialog(Client.this, "This software is designed and developed by Chandan \n and group, under guidance of Ananya Banerjee ma'am\n\n" +
                "College: Narula Institute of Technology\n" +
                "Stream: Computer Science Engineering\n" +
                "Subject: Computer Network Lab\n" +
                "Language: Java (Swing, AWT)\n" +
                "Year/Sem: 3rd/6th\n" +
                "Date: 25/05/2021");
    }
    private void handleEvents() {
        messageInput.addKeyListener(new CustomKeyListener());
    }

    class CustomKeyListener implements KeyListener {
        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {

        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
                String contentToSend = messageInput.getText();
                messageArea.append("Me : " + contentToSend + "\n");
                out.println(contentToSend);
                out.flush();
                messageInput.setText(null);
                messageInput.requestFocus();
               // System.out.println(e.getKeyCode());
            }
        }
    }

    public void startReading() {
        // This thread will continuously read the client's data
        Runnable r1 = () -> {
            System.out.println("Reader Started... ");
            try {
                while (true) {
                    String msg = br.readLine();
                    messageArea.append("Server : " + msg + "\n");
                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
                }
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Connection Closed");
                System.out.println("Server terminated the chat.");
                JOptionPane.showMessageDialog(this, "Server terminated the chat.");
                chooseBtn.setEnabled(false);
                sendBtn.setEnabled(false);
                messageInput.setEnabled(false);
            }
        };
        new Thread(r1).start();
    }


}

