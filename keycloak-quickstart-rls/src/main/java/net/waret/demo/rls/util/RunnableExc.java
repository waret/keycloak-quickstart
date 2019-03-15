package net.waret.demo.rls.util;

@FunctionalInterface
public interface RunnableExc {

    void run() throws Exception;

    static void ignoringExc(RunnableExc r) {
        try { r.run(); } catch (Exception ignored) { }
    }
}
