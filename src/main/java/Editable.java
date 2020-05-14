import javax.swing.JComponent;

public interface Editable {
    boolean isEditable();

    JComponent getEditor();

    void save();
}
