package hk.ust.cse.hunkim.questionroom;
import android.content.Context;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
public class AndroidJacocoTestRunner extends AndroidJUnitRunner {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    static {
        System.setProperty("jacoco-agent.destfile", "/data/data/"
                + BuildConfig.APPLICATION_ID + "/coverage.ec");
    }
    @Override
    public void finish(int resultCode, Bundle results) {
        try {
            Class rt = Class.forName("org.jacoco.agent.rt.RT");
            Method getAgent = rt.getMethod("getAgent");
            Method dump = getAgent.getReturnType().getMethod("dump", boolean.class);
            Object agent = getAgent.invoke(null);
            dump.invoke(agent, false);
        } catch (Throwable e) {
            final String trace = Log.getStackTraceString(e);
            try {
                System.out.write(trace.getBytes(UTF8));
            } catch (IOException ignored) {
            }
        }
        super.finish(resultCode, results);
    }
}