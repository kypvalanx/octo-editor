import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JTextArea;

public class LstLine implements Editable, SaveToLine {

    private final PCCType dependency;
    private final String type;
    private final HasDirty parent;
    private String line;
    private static JTextArea textArea = new JTextArea();

    public LstLine(String line, String type, PCCType dependency, HasDirty parent) {
        this.line = line;
        this.type = type;
        this.dependency = dependency;
        this.parent = parent;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public JComponent getEditor() {
        textArea.setText(line.replace("\t", "\n"));
        textArea.setBackground(Color.white);
        textArea.setEditable(dependency.equals(PCCType.ACTIVE));
        return textArea;
    }

    @Override
    public void save() {
        String temp = line;
        line = textArea.getText().replace("\n", "\t");

        if(!temp.equals(line)){
            parent.isDirty();
        }
    }

    @Override
    public String toString() {
        return line;
    }

    @Override
    public String saveToLine() {
        return line;
    }
}
