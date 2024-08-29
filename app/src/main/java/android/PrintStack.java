package android;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * @author JCat
 * &#064;qq  3147359496
 */
public class PrintStack {

    private final Throwable exception;

    public PrintStack(Throwable e){
        exception = e;
    }

    @NonNull
    public String toString(){
        if(exception == null){
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        printStackTrace(exception,sb);
        return sb.toString();
    }

    public static void println(Throwable e){
        if(e==null){
            return;
        }
        StringBuilder sb = new StringBuilder();
        printStackTrace(e,sb);
        System.out.println("报错堆栈信息：\n"+sb);
    }

    public void println(){
        println(exception);
    }

    public static void printStackTrace(Throwable e, StringBuilder s) {
        Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        dejaVu.add(e);
        s.append(e).append("\n");
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement traceElement : trace) {
            s.append("\tat ").append(traceElement);
        }
        for (Throwable se : e.getSuppressed()) {
            printEnclosedStackTrace(se,s, trace, "Suppressed: ", "\t", dejaVu);
        }

        Throwable ourCause = e.getCause();
        if (ourCause != null) {
            printEnclosedStackTrace(ourCause,s, trace, "Suppressed: ", "", dejaVu);
        }
    }

    public static void printEnclosedStackTrace(Throwable thr,StringBuilder s, StackTraceElement[] enclosingTrace, String caption, String prefix, Set<Throwable> dejaVu) {
        if (dejaVu.contains(thr)) {
            s.append(prefix).append(caption).append("[CIRCULAR REFERENCE: ").append(thr).append("]");
        } else {
            dejaVu.add(thr);
            StackTraceElement[] trace = thr.getStackTrace();
            int m = trace.length - 1;
            for(int n = enclosingTrace.length - 1; m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n]); --n) {
                --m;
            }
            int framesInCommon = trace.length - 1 - m;
            s.append(prefix).append(caption).append(thr);
            for(int i = 0; i <= m; ++i) {
                s.append(prefix).append("\tat ").append(trace[i]);
            }
            if (framesInCommon != 0) {
                s.append(prefix).append("\t... ").append(framesInCommon).append(" more");
            }
            Throwable[] var14 = thr.getSuppressed();
            for (Throwable se : var14) {
                printEnclosedStackTrace(se, s, trace, "Suppressed: ", prefix + "\t", dejaVu);
            }
            Throwable ourCause = thr.getCause();
            if (ourCause != null) {
                printEnclosedStackTrace(ourCause,s, trace, "Caused by: ", prefix, dejaVu);
            }
        }

    }
}
