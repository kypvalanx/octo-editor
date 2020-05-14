import java.io.File;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PccFileTest {
    private static PccFile projectFile;

    @BeforeAll
    public static void before(){
        String projectPath = "C:/Users/Thor_2/git/pcgen/data/pathfinder/paizo/player_companion/blood_of_the_moon/_blood_of_the_moon.pcc";
        File project = new File(projectPath);

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

}
