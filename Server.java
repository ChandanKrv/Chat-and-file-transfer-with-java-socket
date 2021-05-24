import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by Chandan on May 18, 2021.
 */
public class Server extends JFrame {
    // Array list to hold information about the files received.
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    private static ServerSocket serverSocketFile, serverSocketChat;
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

    private static JFrame jFrame;
    private static JPanel jPanel;

    public static void main(String[] args) throws IOException {
        //final File[] fileToSend = new File[1];
        // Used to track the file (jpanel that has the file name in it on a label).
        int fileId = 0;
        System.out.println("This is server going to start server: ");
        new Server();
        fileDownloader();

/*
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
        });*/
        // Create a server socket that the server will be listening with.
        // ServerSocket serverSocket = new ServerSocket(1234);

        // This while loop will run forever so the server will never stop unless the application is closed.
        while (true) {

            try {
                // Wait for a client to connect and when they do create a socket to communicate with them.
                socketFile = serverSocketFile.accept();

                // Stream to receive data from the client through the socket.
                DataInputStream dataInputStream = new DataInputStream(socketFile.getInputStream());

                // Read the size of the file name so know when to stop reading.
                int fileNameLength = dataInputStream.readInt();
                // If the file exists
                if (fileNameLength > 0) {
                    // Byte array to hold name of file.
                    byte[] fileNameBytes = new byte[fileNameLength];
                    // Read from the input stream into the byte array.
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    // Create the file name from the byte array.
                    String fileName = new String(fileNameBytes);
                    // Read how much data to expect for the actual content of the file.
                    int fileContentLength = dataInputStream.readInt();
                    // If the file exists.
                    if (fileContentLength > 0) {
                        // Array to hold the file data.
                        byte[] fileContentBytes = new byte[fileContentLength];
                        // Read from the input stream into the fileContentBytes array.
                        dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                        // Panel to hold the picture and file name.
                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));
                        // Set the file name.
                        JLabel jlFileName = new JLabel(fileName);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            // Set the name to be the fileId so you can get the correct file from the panel.
                            jpFileRow.setName((String.valueOf(fileId)));
                            jpFileRow.addMouseListener(getMyMouseListener());
                            // Add everything.
                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            jFrame.validate();
                        } else {
                            // Set the name to be the fileId so you can get the correct file from the panel.
                            jpFileRow.setName((String.valueOf(fileId)));
                            // Add a mouse listener so when it is clicked the popup appears.
                            jpFileRow.addMouseListener(getMyMouseListener());
                            // Add the file name and pic type to the panel and then add panel to parent panel.
                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            // Perform a relayout.
                            jFrame.validate();
                        }

                        // Add the new file to the array list which holds all our data.
                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                        // Increment the fileId for the next file to be received.
                        fileId++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Constructor..
    public Server() {
        try {
            serverSocketChat = new ServerSocket(8888);
            serverSocketFile = new ServerSocket(8889);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting.... ");
            Socket socketText = serverSocketChat.accept();
            br = new BufferedReader(new InputStreamReader(socketText.getInputStream()));
            out = new PrintWriter(socketText.getOutputStream());

            createGUI("Server Messenger Tab", 400, 600, 170, 116);
            setIcon("back", "3.png", 40, 40, 5, 15, 30, 30, true);
            setIcon("profile", "1.png", 45, 45, 40, 1, 60, 60, true);
            setIcon("video", "video.png", 35, 35, 270, 20, 26, 25, true);
            setIcon("audio", "phone.png", 35, 35, 320, 20, 26, 25, true);
            setIcon("dots", "3icon.png", 13, 25, 370, 20, 13, 25, true);
            setLabel("Server (Admin)", true, 18, 110, 15, 200, 18);
            setLabel("Active Now", false, 14, 110, 35, 100, 20);

            handleEvents();


            startReading();


        } catch (Exception e) {
            e.printStackTrace();
        }


        setLayout(null);
        setSize(420, 670);
        setLocation(700, 80);
        setResizable(false);
        setVisible(true);


    }

    public static void fileDownloader() {
        // Main container, set the name.
        jFrame = new JFrame();
        jFrame.setTitle("File Downloader");
        // Set the size of the frame.
        jFrame.setSize(400, 400);
        // Give the frame a box layout that stacks its children on top of each other.
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        // When closing the frame also close the program.

        // Panel that will hold the title label and the other jpanels.
        jPanel = new JPanel();
        // Make the panel that contains everything to stack its child elements on top of eachother.
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        // Make it scrollable when the data gets in jpanel.
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        // Make it so there is always a vertical scrollbar.
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Title above panel.
        JLabel jlTitle = new JLabel("File Downloader");
        // Change the font of the title.
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        // Add a border around the title for spacing.
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        // Center the title horizontally in the middle of the frame.
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add everything to the main GUI.
        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        // Make the GUI show up.
        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame.setVisible(true);

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
                   // super.mouseClicked(e);
                    fileDownloader();
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

        // coding for component
  /*      heading =new JLabel();
        heading.setFont(font);
        heading.setIcon(new ImageIcon("logo.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setVerticalAlignment(SwingConstants.CENTER);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));*/


        // Setting Frame Layout


        this.setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    public void startReading() {
        // This thread will continuously read the client's data
        Runnable r1 = () -> {
            System.out.println("Reader Started... ");

            try {
                while (true) {
                    String msg = br.readLine();
                    /*
                     * if (msg.equals("exit")) { System.out.println("Client terminated the chat.");
                     * JOptionPane.showMessageDialog(this, "Client terminated the chat.");
                     * messageInput.setEnabled(false); socket.close(); break; }
                     */
                    messageArea.append("Client : " + msg + "\n");
                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
                }
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Connection Closed");
                System.out.println("Client terminated the chat.");
                JOptionPane.showMessageDialog(this, "Client terminated the chat.");
                messageInput.setEnabled(false);
            }
        };
        new Thread(r1).start();
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
            }
        }
    }

    /**
     * @param fileName
     * @return The extension type of the file.
     */
    public static String getFileExtension(String fileName) {
        // Get the file type by using the last occurence of . (for example aboutMe.txt returns txt).
        // Will have issues with files like myFile.tar.gz.
        int i = fileName.lastIndexOf('.');
        // If there is an extension.
        if (i > 0) {
            // Set the extension to the extension of the filename.
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }

    /**
     * When the jpanel is clicked a popup shows to say whether the user wants to download
     * the selected document.
     *
     * @return A mouselistener that is used by the jpanel.
     */
    public static MouseListener getMyMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Get the source of the click which is the JPanel.
                JPanel jPanel = (JPanel) e.getSource();
                // Get the ID of the file.
                int fileId = Integer.parseInt(jPanel.getName());
                // Loop through the file storage and see which file is the selected one.
                for (MyFile myFile : myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {

        // Frame to hold everything.
        JFrame jFrame = new JFrame("File Saver");
        // Set the size of the frame.
        jFrame.setSize(700, 700);

        // Panel to hold everything.
        JPanel jPanel = new JPanel();
        // Make the layout a box layout with child elements stacked on top of each other.
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        // Title above panel.
        JLabel jlTitle = new JLabel("File Preview");
        // Center the label title horizontally.
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Change the font family, size, and style.
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        // Add spacing on the top and bottom of the element.
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

        // Label to prompt the user if they are sure they want to download the file.
        JLabel jlPrompt = new JLabel("Are you sure you want to \n download " + fileName + "?");
        // Change the font style, size, and family of the label.
        jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        // Add spacing on the top and bottom of the label.
        jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
        // Center the label horizontally.
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create the yes for accepting the download.
        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 75));
        // Set the font for the button.
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));

        // No button for rejecting the download.
        JButton jbNo = new JButton("No");
        // Change the size of the button must be preferred because if not the layout will ignore it.
        jbNo.setPreferredSize(new Dimension(150, 75));
        // Set the font for the button.
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));

        // Label to hold the content of the file whether it be text of images.
        JLabel jlFileContent = new JLabel();
        // Align the label horizontally.
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel to hold the yes and no buttons and make the next to each other left and right.
        JPanel jpButtons = new JPanel();
        // Add spacing around the panel.
        jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
        // Add the yes and no buttons.
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

        // If the file is a text file then display the text.
        if (fileExtension.equalsIgnoreCase("txt")) {
            // Wrap it with <html> so that new lines are made.
            jlFileContent.setText("<html>" + new String(fileData) + "</html>");
            // If the file is not a text file then make it an image.
        } else {
            jlFileContent.setIcon(new ImageIcon(fileData));
        }

        // Yes so download file.
        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create the file with its name.
                File fileToDownload = new File(fileName);
                try {
                    // Create a stream to write data to the file.
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                    // Write the actual file data to the file.
                    fileOutputStream.write(fileData);
                    // Close the stream.
                    fileOutputStream.close();
                    // Get rid of the jFrame. after the user clicked yes.
                    jFrame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        // No so close window.
        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // User clicked no so don't download the file but close the jframe.
                jFrame.dispose();
            }
        });

        // Add everything to the panel before adding to the frame.
        jPanel.add(jlTitle);
        jPanel.add(jlPrompt);
        jPanel.add(jlFileContent);
        jPanel.add(jpButtons);

        // Add panel to the frame.
        jFrame.add(jPanel);

        // Return the jFrame so it can be passed the right data and then shown.
        return jFrame;

    }

}
