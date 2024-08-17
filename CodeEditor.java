import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class CodeEditor extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JEditorPane webPane;
    private File currentFile;

    public CodeEditor() {
        // Set up the main window
        setTitle("GoGame Creator Studio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create the text area for editing code
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane textScrollPane = new JScrollPane(textArea);

        // Create the editor pane for displaying WebGL content
        webPane = new JEditorPane();
        webPane.setEditable(false);
        webPane.setContentType("text/html"); // Set content type to HTML
        JScrollPane webScrollPane = new JScrollPane(webPane);
        webScrollPane.setPreferredSize(new Dimension(800, 300));

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu scriptMenu = new JMenu("Scripts");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        JMenuItem runItem = new JMenuItem("Run");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem scriptsItem = new JMenuItem("main.js");

        // Add action listeners
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        saveAsItem.addActionListener(this);
        runItem.addActionListener(this);
        exitItem.addActionListener(this);
        scriptsItem.addActionListener(this);

        // Add menu items to the File menu
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(runItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Add the menu items to the Scripts menu
        scriptMenu.add(scriptsItem);

        // Add the File menu to the menu bar
        menuBar.add(fileMenu);

        // Add the Scripts menu to the menu bar
        menuBar.add(scriptMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Layout setup
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textScrollPane, webScrollPane);
        splitPane.setResizeWeight(0.5);
        getContentPane().add(splitPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Open":
                openFile();
                break;
            case "Save":
                saveFile(false);
                break;
            case "Save As...":
                saveFile(true);
                break;
            case "Run":
                runWebGLCode();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JavaScript Files", "js"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                textArea.read(reader, null);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile(boolean saveAs) {
        if (saveAs || currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("JavaScript Files", "js"));
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
            } else {
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
            textArea.write(writer);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runWebGLCode() {
        String code = textArea.getText();
        String htmlContent = "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>WebGL Preview</title>\n" +
                "<script>" + code + "</script>\n</head>\n<body></body>\n</html>";

        try {
            // Create a temporary HTML file
            File tempFile = File.createTempFile("webgl_preview", ".html");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(htmlContent);
            }

            // Open the temporary file in the default web browser
            Desktop.getDesktop().browse(tempFile.toURI());

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error running WebGL code: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CodeEditor editor = new CodeEditor();
            editor.setVisible(true);
        });
    }
}
