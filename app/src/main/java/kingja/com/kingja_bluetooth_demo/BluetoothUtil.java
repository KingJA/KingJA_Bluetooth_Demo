package kingja.com.kingja_bluetooth_demo;

/**
 * 项目名称：物联网城市防控(警用版)
 * 类描述：TODO
 * 创建人：KingJA
 * 创建时间：2016/4/2 14:21
 * 修改备注：
 */
public class BluetoothUtil {

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

    /**
     * 若都不为空，将arrSrc2添加到arrSrc1的末尾组合成新的byte数组
     *
     * @param arrSrc1
     * @param arrSrc2
     * @return
     */
    public static final byte[] ByteArrayCopy(byte[] arrSrc1, byte[] arrSrc2) {
        byte[] arrDes = null;
        if (arrSrc2 == null) {
            arrDes = arrSrc1;
            return arrDes;
        }

        if (arrSrc1 != null) {
            arrDes = new byte[arrSrc1.length + arrSrc2.length];
            System.arraycopy(arrSrc1, 0, arrDes, 0, arrSrc1.length);
            System.arraycopy(arrSrc2, 0, arrDes, arrSrc1.length, arrSrc2.length);
        } else {
            arrDes = new byte[arrSrc2.length];
            System.arraycopy(arrSrc2, 0, arrDes, 0, arrSrc2.length);
        }

        return arrDes;
    }

    /**
     * 判断两个byte数组是否相等
     *
     * @param myByte
     * @param otherByte
     * @return
     */
    public static final boolean checkByte(byte[] myByte, byte[] otherByte) {
        boolean b = false;
        int len = myByte.length;
        if (len == otherByte.length) {
            for (int i = 0; i < len; i++) {
                if (myByte[i] != otherByte[i]) {
                    return b;
                }
            }
            b = true;
        }
        return b;
    }
}
