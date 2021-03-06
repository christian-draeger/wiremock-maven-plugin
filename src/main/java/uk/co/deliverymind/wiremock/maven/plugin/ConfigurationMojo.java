package uk.co.deliverymind.wiremock.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

abstract public class ConfigurationMojo extends AbstractMojo {

    @Parameter(defaultValue = "${plugin}", required = true, readonly = true)
    private PluginDescriptor descriptor;

    @Parameter(property = "project.testClasspathElements", required = true, readonly = true)
    private List<String> classpathElements;

    /**
     * Set the root directory (<i>--root-dir</i>), under which <i>mappings</i> and <i>__files</i> reside.
     * This defaults to: <i>target/classes</i>
     */
    @Parameter(property = "dir", defaultValue = "target/classes")
    private File dir;

    /**
     * Set all other parameters in command-line format, e.g.:
     * <i>--port=8081 --verbose</i>
     * Do <b>NOT</b> specify <i>--root-dir</i> here.
     */
    @Parameter(property = "params")
    private String params;

    /**
     * Set to <i>true</i> if (and only if) you want to keep the plugin running indefinitely.
     */
    @Parameter(property = "keepRunning", defaultValue = "false")
    private String keepRunning;

    boolean shouldKeepRunning() {
        return keepRunning.equals("true");
    }

    String[] getAllParams() {
        return Parameters.getAllParams(dir.toString(), params);
    }

    PluginDescriptor getDescriptor() {
        return descriptor;
    }

    List<String> getClasspathElements() {
        return classpathElements;
    }
}
