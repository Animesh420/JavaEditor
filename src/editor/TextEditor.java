package editor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {
    private String currentSearchText = "";
    private List<List<String>> allwordList;
    private int maxLen = 0;
    private int sindex = 0;
    private String currentFilePath = "";
    private JCheckBox checkBox;

    public TextEditor() {
        String myPath = System.getProperty("user.dir");
        FlowLayout experimentLayout = new FlowLayout();
        int width = 25;
        int length = 25;

        // adding all image icons.
        ImageIcon saveIcon = resizeIcon("saveButton.png", width, length);
        ImageIcon loadIcon = resizeIcon("loadButton.png", width, length);
        ImageIcon searchIcon = resizeIcon("searchButton.png", width, length);
        ImageIcon leftArrow = resizeIcon("leftArrow.png", width, length);
        ImageIcon rightArrow = resizeIcon("rightArrow.png", width, length);
        ImageIcon closeIcon = resizeIcon("closeButton.png", width, length);

        // text area creation
        JTextArea TextArea = new JTextArea();
        TextArea.setEnabled(true);
        TextArea.setEditable(true);
        TextArea.setVisible(true);

        // load button, save button and close button
        JButton SaveButton = new JButton(saveIcon);
        JButton LoadButton = new JButton(loadIcon);
        JButton CloseButton = new JButton(closeIcon);

        // adding text area to scroll pane
        JScrollPane ScrollPane = new JScrollPane(TextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JTextField FilenameField = new JTextField("", 25);
        JMenuBar menuBar = new JMenuBar();
        ScrollPane.setVisible(true);

        // File menu details
        JMenu file = new JMenu("File");
        JMenuItem load = new JMenuItem("MenuLoad");
        JMenuItem save = new JMenuItem("MenuSave");
        JMenuItem exit = new JMenuItem("MenuExit");

        // Search menu details
        JMenu searchMenu = new JMenu("Search");
        JMenuItem startSearchLabel = new JMenuItem("Start search");
        JMenuItem prevSearchLabel = new JMenuItem("Previous search");
        JMenuItem nextSearchLabel = new JMenuItem("Next match");
        JMenuItem useRegularExpression = new JMenuItem("Use regular expressions");

        // search field and buttons to navigate search results.
        JTextField searchField = new JTextField("", 12);
        JButton startSearch = new JButton(searchIcon);
        JButton prevMatch = new JButton(leftArrow);
        JButton nextMatch = new JButton(rightArrow);
        JCheckBox checkBox = new JCheckBox();
        this.checkBox = checkBox;
        JFileChooser fc = new JFileChooser();
        fc.setVisible(false);


        // name everything
        TextArea.setName("TextArea");
        searchField.setName("SearchField");
        SaveButton.setName("SaveButton");
        LoadButton.setName("OpenButton");
        startSearch.setName("StartSearchButton");
        prevMatch.setName("PreviousMatchButton");
        nextMatch.setName("NextMatchButton");
        checkBox.setName("UseRegExCheckbox");
        fc.setName("FileChooser");

        ScrollPane.setName("ScrollPane");
        file.setName("MenuFile");
        searchMenu.setName("MenuSearch");
        startSearchLabel.setName("MenuStartSearch");
        prevSearchLabel.setName("MenuPreviousMatch");
        nextSearchLabel.setName("MenuNextMatch");
        useRegularExpression.setName("MenuUseRegExp");
        load.setName("MenuOpen");
        save.setName("MenuSave");
        exit.setName("MenuExit");

        // adding all components to the panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(experimentLayout);
        northPanel.add(SaveButton);
        northPanel.add(LoadButton);
        northPanel.add(CloseButton);
        northPanel.add(searchField);
        northPanel.add(startSearch);
        northPanel.add(prevMatch);
        northPanel.add(nextMatch);
        northPanel.add(checkBox);
        northPanel.add(new JLabel("Use regex"));
        northPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        // adding panel to the main frame
        add(northPanel, BorderLayout.NORTH);
        add(ScrollPane, BorderLayout.CENTER);

        // adding file menu to the frame
        file.add(load);
        file.add(save);
        file.addSeparator();
        file.add(exit);
        menuBar.add(file);

        // adding search menu to the frame
        searchMenu.add(startSearchLabel);
        searchMenu.add(prevSearchLabel);
        searchMenu.add(nextSearchLabel);
        searchMenu.add(useRegularExpression);

        menuBar.add(searchMenu);

        getContentPane().add(fc, BorderLayout.CENTER);
        getContentPane().add(ScrollPane, BorderLayout.CENTER);

        // adding action listeners to all buttons
        SaveButton.addActionListener((actionEvent -> {
            handleSaving(this.currentFilePath, TextArea);
        }));

        LoadButton.addActionListener(actionEvent -> {

            String filePath = invokeFileChooser(fc, this);
            handleLoading(filePath, TextArea);
        });

        CloseButton.addActionListener(actionEvent -> {
            dispose();
        });

        save.addActionListener(actionEvent -> {
            handleSaving(this.currentFilePath, TextArea);
        });

        load.addActionListener(actionEvent -> {
            String filePath = invokeFileChooser(fc, this);
            handleLoading(filePath, TextArea);
        });

        exit.addActionListener(actionEvent -> {
            dispose();
        });

        startSearch.addActionListener(actionEvent -> {
            doSearch(searchField, TextArea, this);
        });

        nextMatch.addActionListener(actionEvent -> {
            doNextSearch(this, TextArea);
        });

        prevMatch.addActionListener(actionEvent -> {
            doPrevSearch(this, TextArea);
        });

        startSearchLabel.addActionListener(actionEvent -> {
            doSearch(searchField, TextArea, this);
        });

        prevSearchLabel.addActionListener(actionEvent -> {
            doPrevSearch(this, TextArea);
        });

        nextSearchLabel.addActionListener(actionEvent -> {
            doNextSearch(this, TextArea);
        });

        useRegularExpression.addActionListener(actionEvent -> {
            checkBox.setSelected(true);
            doSearch(searchField, TextArea, this);
        });

        setJMenuBar(menuBar);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setVisible(true);
        setTitle("EditorSwing");
    }

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void writeToFile(String data, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter writer = new FileWriter(file, false);
        writer.write(data);
        writer.close();
    }

    public static void handleSaving(String basePath, JTextArea TextArea) {

        String data = "";
        try {
            data = TextArea.getText();
            writeToFile(data, basePath);
        } catch (IOException e) {
            data = "Sorry error saving the file ";
        }
    }

    public static void handleLoading(String filePath, JTextArea TextArea) {
        String data = "";
        try {
            data = readFileAsString(filePath);
            System.out.println("File loaded successfully to be inserted in " + TextArea.getName());
        } catch (IOException e) {
            System.out.println("Exception occurred while loading file " + Arrays.toString(e.getStackTrace()));
            data = "";
        }

        TextArea.setText("");
        TextArea.setText(data);
        TextArea.setVisible(true);
    }

    private static ImageIcon resizeIcon(String fileName, int resizedWidth, int resizedHeight) {
        String imagePath = System.getProperty("user.dir") + "/src/images/";
        String filePath = imagePath + fileName;
        ImageIcon icon = new ImageIcon(filePath);
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private static String invokeFileChooser(JFileChooser fc, TextEditor obj) {
        // function to choose files from the current file system
        fc.setVisible(true);
        int i = fc.showOpenDialog(obj);
        String filePath = "";
        if (i == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            filePath = f.getPath();
        }
        fc.setVisible(false);
        obj.currentFilePath = filePath;
        return filePath;

    }

    private static void highlight(JTextArea textArea, List<String> row) {
        // highlight the word and the caret in the text area
        String indexStr = row.get(1);
        String foundText = row.get(0);
        int index = Integer.parseInt(indexStr);
        textArea.setCaretPosition(index + foundText.length());
        textArea.select(index, index + foundText.length());
        textArea.grabFocus();
    }

    private static List<List<String>> searchAll(JTextArea textArea, String searchText, boolean isRegex) {
        // string search in text area and saving it locally to a list.
        String regex = "\\b[\\S]*" + searchText + "[\\S]*\\b";
        Pattern pattern;
        if (isRegex) {
            pattern = Pattern.compile(regex);
        } else {
            pattern = Pattern.compile(Pattern.quote(searchText));
        }

        String text = textArea.getText();
        Matcher matches = pattern.matcher(text);
        List<List<String>> mainList = new ArrayList<>();

        while (matches.find()) {
            int start = matches.start();
            String found = matches.group();
            List<String> small = new ArrayList<>();
            small.add(found);
            small.add(String.valueOf(start));
            mainList.add(small);
        }

        return mainList;
    }

    private static void doSearch(JTextField searchField, JTextArea textArea, TextEditor obj) {
        // search first occurrence
        String toSearch = searchField.getText();
        boolean isRegex = obj.checkBox.isSelected();
        List<List<String>> allList = searchAll(textArea, toSearch, isRegex);
        if (allList.size() > 0) {

            obj.currentSearchText = toSearch;
            obj.allwordList = allList;
            highlight(textArea, allList.get(0));

        }
        obj.sindex = 0;
        obj.maxLen = allList.size();
    }

    private static void doNextSearch(TextEditor obj, JTextArea textArea) {
        // go to next search result
        obj.sindex = (obj.sindex + 1) % obj.maxLen;
        List<String> row = obj.allwordList.get(obj.sindex);
        highlight(textArea, row);
    }

    private static void doPrevSearch(TextEditor obj, JTextArea textArea) {
        // go to previous search result
        obj.sindex = (obj.sindex - 1 + obj.maxLen) % obj.maxLen;
        List<String> row = obj.allwordList.get(obj.sindex);
        highlight(textArea, row);
    }
}
