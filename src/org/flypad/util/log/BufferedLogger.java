/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package  org.flypad.util.log;
import java.util.Vector;

/**
 *
 * @author albus
 */
public class BufferedLogger extends AbstractLogger {
    private static final int DEFAULT_BUFFER_SIZE = 100;
    private final Vector messages;
    private final int size;

    private class Message {
        final boolean error;
        final String msg;

        Message(final String msg, final boolean error) {
            this.msg = msg;
            this.error = error;
        }
    }

    public BufferedLogger() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public BufferedLogger(final int bufferSize) {
        super();
        this.messages = new Vector(bufferSize);
        this.size = bufferSize;
    }

    public BufferedLogger(final Logger logger) {
        this(logger, DEFAULT_BUFFER_SIZE);
    }

    public BufferedLogger(final Logger logger, final int bufferSize) {
        super(logger);
        this.messages = new Vector(bufferSize);
        this.size = bufferSize;
    }

    protected final void logInternally(
            final String message, final boolean error) {
        protectFromOverflow();
        messages.addElement(new Message(message, error));
    }

    protected final void logInternally(Throwable t) {
        protectFromOverflow();
        messages.addElement(new Message(t.getClass() + ": " + t.getMessage(), true));
    }

    private void protectFromOverflow() {
        /*
         * Remove half the elements if there is no more space
         */
        if (size == messages.size()) {
            final int k = Math.min(size / 2, messages.size());

            for (int i = 0; i < k; i++) {
                messages.removeElementAt(0);
            }
        }
    }

    public final String[] getMessages(
            final boolean errorOnly, final boolean ascending) {

        Message m;
        final Vector found = new Vector(messages.size());

        final int end = messages.size() - 1;

        int i = (ascending ? 0 : end - 1);
        while (
                (ascending && i <= end) ||
                (!ascending && i >= 0)
                ) {

            m = (Message) messages.elementAt(i);
            if (!errorOnly || m.error) {
                found.addElement(m.msg);
            }

            if (ascending) {
                i++;
            } else {
                i--;
            }
        }

        String[] array = new String[found.size()];
        found.copyInto(array);
        return array;
    }

    public final boolean isEmpty() {
        return messages.isEmpty();
    }
}
