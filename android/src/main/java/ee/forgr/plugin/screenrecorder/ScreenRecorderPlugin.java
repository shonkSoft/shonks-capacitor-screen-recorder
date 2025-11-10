package ee.forgr.plugin.screenrecorder;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import dev.bmcreations.scrcast.ScrCast;
import dev.bmcreations.scrcast.config.Options;

import java.io.File;

@CapacitorPlugin(name = "ScreenRecorder")
public class ScreenRecorderPlugin extends Plugin {

    private final String pluginVersion = "7.3.5";
    private ScrCast recorder;

    @Override
    public void load() {
        recorder = ScrCast.use(this.bridge.getActivity());
        Options options = new Options();
        recorder.updateOptions(options);
    }

    @PluginMethod
    public void start(PluginCall call) {
        recorder.record();
        call.resolve();
    }

    @PluginMethod
    public void stop(PluginCall call) {
        try {
            // Create recordings folder in private app storage
            File recordingsDir = new File(getContext().getFilesDir(), "recordings");
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs();
            }

            // Define the output file path inside the app folder
            File outputFile = new File(recordingsDir, "stream_" + System.currentTimeMillis() + ".mp4");

            // Stop recording and save directly to the private folder
            // This requires your forked ScrCast to support stopRecording(File outputFile)
            recorder.stopRecording(outputFile);

            // Return the file path to JS
            JSObject ret = new JSObject();
            ret.put("video", outputFile.getAbsolutePath());
            call.resolve(ret);

        } catch (Exception e) {
            call.reject("Failed to stop recording", e);
        }
    }

    @PluginMethod
    public void getPluginVersion(final PluginCall call) {
        try {
            JSObject ret = new JSObject();
            ret.put("version", this.pluginVersion);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Could not get plugin version", e);
        }
    }
}