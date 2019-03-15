package net.waret.demo.photoz.web.error;

@FunctionalInterface
public interface RunnableExc {

    void run() throws Exception;

    static void ignoringExc(RunnableExc r) {
        try {
            r.run();
        } catch (Exception ignored) {
        }
    }
}
