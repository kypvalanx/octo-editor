import javax.swing.JComponent;

public class PccLine implements Editable, SaveToLine{
    private final String line;

    public PccLine(String line) {
        this.line = line;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public JComponent getEditor() {
        return null;
    }

    @Override
    public void save() {

    }

    @Override
    public String saveToLine() {
        return line;
    }
}
