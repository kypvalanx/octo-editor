import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

public class LstFile implements Editable, SaveToFile{
    private final String type;
    private final File file;
    private final PCCType dependency;


    private List<Listener> listeners;


    private List<LstLine> values = new ArrayList<>();

    public LstFile(File file, String type, PCCType dependency) {
        this.file = file;
        this.type = type;
        this.dependency = dependency;

        listeners = getListeners(type);

        try (InputStream inputStream = new FileInputStream(file)) {
            try (BufferedReader br
                         = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String finalLine = line;
                    listeners.stream().filter(l -> l.listen(finalLine)).findFirst();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Listener> getListeners(String type) {
            return List.of(line -> {
                        values.add(new LstLine(line, type, dependency));
                        return true;
                    }
            );
    }

    @Override
    public String toString() {
        return type.toLowerCase() + " : " + file.getName();
    }

    public List<LstLine> getLines() {
        return values;
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
    public File saveToFile() {
        try {
        PrintWriter writer = new PrintWriter(file);
        for(SaveToLine line : values){
            writer.println(line.saveToLine());
        }
        writer.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }

        return file;
    }
}
