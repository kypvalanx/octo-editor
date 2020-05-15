import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PccFileTest {
    private static PccFile projectFile;
    private static File project;

    @BeforeAll
    public static void before(){
        String projectPath = "src/test/resources/data/pathfinder/paizo/player_companion/blood_of_the_moon/_blood_of_the_moon.pcc";
        project = new File(projectPath);

        System.out.println(project.getAbsolutePath());

        projectFile = new PccFile(project, PCCType.DEPENDENCY);
    }


    @Test
    public void getDependencies(){
        List<Dependency> dependencies = projectFile.getDependencies();

        assertThat(dependencies).hasSize(4);
        dependencies.forEach(d -> {
            assertThat(d.resolvedPCC().size()).isGreaterThan(0);
        });

    }

    @Test
    public void getLstFiles(){
        List<LstFile> lstFiles = projectFile.getLstFiles();

        assertThat(lstFiles).hasSize(9);
    }

@Test
    public void testSaveWithoutChange() throws IOException {
    long lastModifierd = project.lastModified();
    File saved = projectFile.saveToFile();

    assertThat(FileUtils.contentEquals(project, saved)).isTrue();
    assertThat(saved.lastModified()).isEqualTo(lastModifierd);
}

}
