import java.io.File;

public class Main {
    public static void main(String[] args) {
        String projectPath = "C:/Users/Thor_2/git/pcgen/data/pathfinder/paizo/player_companion/blood_of_the_moon/_blood_of_the_moon.pcc";
        File project = new File(projectPath);
//
//        PccFile projectFile = new PccFile(project, PCCType.ACTIVE);
//
//        List<LstFile> lstFiles = projectFile.getLstFiles();
//        List<Dependency> dependencies = projectFile.getDependencies();

        new MainFrame(project);
    }

}
