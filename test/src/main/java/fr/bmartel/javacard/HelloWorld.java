package fr.bmartel.javacard;

import javacard.framework.*;

public class HelloWorld extends Applet {

    private final static byte[] hello = {0x48, 0x65, 0x6c, 0x6c, 0x6f};

    public static void install(byte[] buffer, short offset, byte length) {
        (new HelloWorld()).register();
    }

    public void process(APDU apdu) {
        byte[] buf = apdu.getBuffer();

        switch (buf[ISO7816.OFFSET_INS]) {
            case 0x40:
                Util.arrayCopy(hello, (byte) 0, buf, ISO7816.OFFSET_CDATA, (byte) 5);
                apdu.setOutgoingAndSend(
                        ISO7816.OFFSET_CDATA, (byte) 5);
                break;
            case ISO7816.INS_SELECT:
                apdu.setOutgoingAndSend(
                        ISO7816.OFFSET_CDATA, (byte) 5);
                break;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }
}
