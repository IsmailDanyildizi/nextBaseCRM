package com.cybertek.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.cybertek.utilities.BrowserUtils;
import com.cybertek.utilities.ConfigurationReader;
import com.cybertek.utilities.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestBase {

    protected WebDriver driver;
    protected Actions actions;
    protected WebDriverWait wait;
    protected static ExtentReports report;
    protected static ExtentHtmlReporter htmlReporter;
    protected static ExtentTest extentLogger;
    String url;

    @BeforeTest
    public void setUpTest() {
        //initialize the class
        report = new ExtentReports();

        //create a report path
        String projectPath = System.getProperty("user.dir");
        String path = projectPath + "/test-output/report.html";

        //initialize the html reporter with the report path
        htmlReporter = new ExtentHtmlReporter(path);

        //attach the html report to report object
        report.attachReporter(htmlReporter);

        //title in the report
        htmlReporter.config().setReportName("NextBaseCRM Smoke Test");

        //set environment information
        report.setSystemInfo("Environment", ConfigurationReader.get("url"));
        report.setSystemInfo("Browser", ConfigurationReader.get("browser"));
        report.setSystemInfo("OS", System.getProperty("os.name"));
    }

    @BeforeMethod
    @Parameters("env")
    public void startMethod(@Optional String env) {

        if (env == null) {
            url = ConfigurationReader.get("url");
        } else {
            url = ConfigurationReader.get(env + "_url");
        }
        driver = Driver.get();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        actions = new Actions(driver);
        wait = new WebDriverWait(driver, 10);
        driver.get(url);
    }

    @AfterMethod
    public void closeMethod(ITestResult result) throws InterruptedException, IOException {

        //if test failed
        if (result.getStatus() == ITestResult.FAILURE) {
            //record the name of the failed test case
            extentLogger.fail(result.getName());

            //take the screenshot and return location of screenshot
            String screenshotPath = BrowserUtils.getScreenshot(result.getName());

            //add your screen shot to your report
            extentLogger.addScreenCaptureFromPath(screenshotPath);

            //capture the exception and put inside the report
            extentLogger.fail(result.getThrowable());
        }

        //close driver
        Thread.sleep(2000);
        Driver.closeDriver();

    }

    @AfterTest
    public void tearDownTest() {
        //this is when the report is actually created
        report.flush();
    }

    @DataProvider
    public Object[][] credentials() {
        String[][] data = {
                {ConfigurationReader.get("marketing_username"), ConfigurationReader.get("password")},
//                {ConfigurationReader.get("hr_username"), ConfigurationReader.get("password")},
//                {ConfigurationReader.get("helpdesk_username"), ConfigurationReader.get("password")}
        };

        return data;
    }


}
