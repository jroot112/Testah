package org.testah.driver.web.browser;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testah.TS;
import org.testah.framework.cli.Params;

public class PhantomJsBrowser extends AbstractBrowser {

	private PhantomJSDriverService service = null;

	public WebDriver getWebDriver(final DesiredCapabilities capabilities) {
		return new PhantomJSDriver(service, capabilities);
	}

	public AbstractBrowser getDriverBinay() {
		return this;
	}

	public AbstractBrowser startService() throws IOException {
		service = new PhantomJSDriverService.Builder().usingPhantomJSExecutable(new File(getPhantomJsBinPath()))
				.usingAnyFreePort().usingCommandLineArguments(new String[] { "--ignore-ssl-errors=true" }).build();
		service.start();
		return this;
	}

	public DesiredCapabilities createCapabilities() {
		final DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();

		if (null != getUserAgentValue()) {
			capabilities.setCapability("phantomjs.page.settings.userAgent", getUserAgentValue());
		}
		capabilities.setCapability("takesScreenshot", true);
		capabilities.setJavascriptEnabled(true);
		capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, getPhantomJsBinPath());

		return capabilities;
	}

	private String getPhantomJsBinPath() {

		String binPath = TS.params().getPhantomJsDriverBinary();
		try {
			if (null == binPath || binPath.length() == 0 || !(new File(binPath)).exists()) {
				String urlSource = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-linux-x86_64.tar.bz2";
				if (Params.isWIndows()) {
					urlSource = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-windows.zip";
				} else if (Params.isMac()) {
					urlSource = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-macosx.zip";
				}
				final File zip = TS.util().downloadFile(urlSource, "drivers");
				final File dest = TS.util().unZip(zip, new File(zip.getParentFile(), "phantomjs"));
				binPath = dest.getAbsolutePath();
			}
		} catch (final Exception e) {
			TS.log().warn(e);
		}
		return binPath;

	}

	public AbstractBrowser stopService() throws IOException {
		if (null != service) {
			service.stop();
		}
		return null;
	}

}
