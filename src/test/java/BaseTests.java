import org.testng.annotations.AfterSuite;

import java.io.IOException;

public class BaseTests {
    @AfterSuite
    public void runCommand() throws IOException {
        Runtime.getRuntime().exec("mvn.cmd io.qameta.allure:allure-maven:serve");
    }
}
