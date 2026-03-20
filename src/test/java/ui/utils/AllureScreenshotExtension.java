package ui.utils;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class AllureScreenshotExtension implements AfterTestExecutionCallback {

    private WebDriver driver;

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        boolean testFailed = context.getExecutionException().isPresent();

        if (testFailed && driver instanceof TakesScreenshot) {
            byte[] screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);

            Allure.addAttachment(
                    "Screenshot on failure",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
        }
    }
}
