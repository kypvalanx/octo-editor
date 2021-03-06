import java.awt.BorderLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class MainFrame extends JFrame implements ActionListener, TreeSelectionListener {
    private final JTree tree;
    private final JPanel editorPanel;
    private Editable current;

    public MainFrame(File projectPath) {
        setTitle("PcGen Data Editor");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel);

        tree = generateTree();


        createMenuBar();


        editorPanel = new JPanel();
        editorPanel.setLayout(new BorderLayout());


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tree), editorPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(600);
        //splitPane.setLayout(new BorderLayout());


        mainPanel.add(splitPane);

        if (projectPath != null) {
            loadFile(projectPath);
        }


        setSize(1200, 600);
        setVisible(true);
    }

    private void createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem menuOpen = new MenuItem("Open");
        menuOpen.addActionListener(this);
        MenuItem menuSave = new MenuItem("Save");
        menuSave.addActionListener(this);
        menu.add(menuOpen);
        menu.add(menuSave);
        menuBar.add(menu);
        setMenuBar(menuBar);
    }

    private JFileChooser getjFileChooser() {
        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".pcc");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        return fc;
    }

    private JTree generateTree() {
        JTree tree = new JTree();
        tree.addTreeSelectionListener(this);
        tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
        return tree;
    }

    private void loadFile(File selectedFile) {
        PccFile pccFile = new PccFile(selectedFile, PCCType.ACTIVE);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        addPCC(pccFile, root);
        addPccDependencies(root, pccFile.getDependencies());

        tree.setModel(new DefaultTreeModel(root));
    }

    private void addPccDependencies(DefaultMutableTreeNode root, List<Dependency> dependencies) {
        dependencies.stream().map(Dependency::resolvedPCC).forEach(resolved -> addPCC(selectAvailable(resolved), root));
    }

    private PccFile selectAvailable(List<PccFile> resolved) {
        if (resolved.size() == 1) {
            return resolved.get(0);
        }

        Object[] options = resolved.stream().map(PccFile::getCampaign).toArray();

        String s = (String) JOptionPane.showInputDialog(
                this,
                "There are multiple PCC files that match type: \"" + resolved.get(0).getBookType() + "\" please select the one you want to reference.",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if(s == null){
            return null; //?
        }
        return resolved.get(Arrays.asList(options).indexOf(s));
    }

    private void addPCC(PccFile pccFile, DefaultMutableTreeNode root) {
        DefaultMutableTreeNode pccNode = new DefaultMutableTreeNode(pccFile);
        for (LstFile lst : pccFile.getLstFiles()) {
            addLst(pccNode, lst);
        }
        root.add(pccNode);
    }

    private void addLst(DefaultMutableTreeNode pccNode, LstFile lst) {
        DefaultMutableTreeNode lstNode = new DefaultMutableTreeNode(lst);
        for (Object value : lst.getLines()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(value);
            lstNode.add(node);
        }
        pccNode.add(lstNode);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "Open":

                final JFileChooser fc = getjFileChooser();

                if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    loadFile(fc.getSelectedFile());
                }
                break;
            case "Save":
                DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
                Enumeration<TreeNode> children = root.children();
                while(children.hasMoreElements()){
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();

                    PccFile user = (PccFile)child.getUserObject();

                    if(user.isActiveFile()){
                        user.saveToFile();
                    }
                }
                break;
            default:
                throw new IllegalStateException(e.getActionCommand());

        }

    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node == null) return;

        if (current != null) {
            current.save();
            editorPanel.removeAll();
        }

        current = (Editable) node.getUserObject();
        if (current.isEditable()) {
            JComponent editor = current.getEditor();
            if (editor instanceof JTextComponent) {
                ((JTextComponent) editor).setCaretPosition(0);
            }
            editorPanel.add(new JScrollPane(editor,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
            editorPanel.updateUI();
            ((JSplitPane) editorPanel.getParent()).updateUI();
        }
    }
}
