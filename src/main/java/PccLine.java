import javax.swing.JComponent;

public class PccLine implements Editable, SaveToLine{
    private final String line;
    private final HasDirty parent;

    public PccLine(String line, HasDirty parent) {
        this.line = line;
        this.parent = parent;
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
        parent.isDirty();
    }

    @Override
    public String saveToLine() {
        return line;
    }
}
