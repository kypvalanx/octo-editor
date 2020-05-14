import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JComponent;

public class PccFile implements Editable{
    private static Pattern precampaignPattern = Pattern.compile(",([\\w]*=[\\w\\s']*)");
    private PCCType dependency;
    private File parentFolder = null;

    private List<Dependency> dependencies = new ArrayList<>();
    private String bookType;
    private String campaign;
    private List<LstFile> lstFiles = new ArrayList<>();


    List<Listener> listeners = List.of(
            line -> {
                if (!line.startsWith("PRECAMPAIGN")) {
                    return false;
                }

                precampaignPattern.matcher(line).results().forEach(d -> {
                    dependencies.add(new Dependency(d.group(1)));
                });
                return true;
            },
            line -> {
                    if (!line.startsWith("CAMPAIGN")) {
                        return false;
                    }
                    campaign = line.split(":")[1];
                return true;
            },
            line -> {
                    if (!line.startsWith("BOOKTYPE")) {
                        return false;
                    }
                    bookType = line.split(":")[1];
                return true;
            },
            line -> {
                if(!startsWithAny(line, "ABILITYCATEGORY:", "ABILITY:", "CLASS:", "DEITY:", "DOMAIN:", "EQUIPMENT:",
                        "EQUIPMOD:", "ABILITY:", "KIT:","LANGUAGE:", "RACE:", "SKILL:", "SPELL:", "TEMPLATE:",
                        "ARMORPROF:", "SHIELDPROF:", "WEAPONPROF:", "ALIGNMENT:", "DATACONTROL:", "DATATABLE:", "DYNAMIC:", "GLOBALMODIFIER:", "SAVE:", "STAT:", "VARIABLE:", "BIOSET:")){
                    return false;
                }
                String[] tokens = line.split(":");
                for(File file:parentFolder.listFiles()){
                    if(file.getName().equals(tokens[1])){
                        lstFiles.add(new LstFile(file, tokens[0], dependency));
                    }
                }
                return true;
            }
    );

    private boolean startsWithAny(String line, String ... prefixes) {
        for(String prefix : prefixes){
            if (line.startsWith(prefix)){
                return true;
            }
        }
        return false;
    }


    public PccFile(File project) {
        this(project, PCCType.ACTIVE);
    }

    public PccFile(File project, PCCType dependency) {
        Context.initiateContext(project);
        this.dependency = dependency;
        parentFolder = project.getParentFile();
        try (InputStream inputStream = new FileInputStream(project)) {
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

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public List<LstFile> getLstFiles() {
        return lstFiles;
    }

    public String getBookType() {
        return bookType;
    }

    public String getCampaign() {
        return campaign;
    }

    @Override
    public String toString() {
        return dependency + " : " + campaign;
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
}
