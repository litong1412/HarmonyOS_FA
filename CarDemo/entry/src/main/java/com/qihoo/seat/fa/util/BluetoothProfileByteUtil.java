/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License,Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qihoo.seat.fa.util;

/**
 * BluetoothProfileByteUtil
 *
 * @since 2021-08-12
 */
public class BluetoothProfileByteUtil {
    /**
     * 16 radix
     */
    public static final int CONVERT_RADIX_16 = 16;

    /**
     * state connected
     */
    public static final int STATE_CONNECTED = 2;

    private static final String TAG = "BluetoothProfileByteUtil";
    private static final int STRING_BUFFER_DEFAULT_SIZE = 16;
    private static final int BINARY_COMPLEMENT = 0xff;
    private static final int DATA_BYTE_LENGTH = 2;

    private BluetoothProfileByteUtil() {
    }

    /**
     * bytes To Hex String
     *
     * @param src byte array
     * @return String
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder(STRING_BUFFER_DEFAULT_SIZE);
        if ((src == null) || (src.length <= 0)) {
            return "";
        }
        for (byte item : src) {
            int intValue = item & BINARY_COMPLEMENT;
            String stringValue = Integer.toHexString(intValue);

            if (stringValue.length() < DATA_BYTE_LENGTH) {
                stringBuilder.append(0);
            }
            stringBuilder.append(stringValue);
        }
        return stringBuilder.toString();
    }

    /**
     * Hex string to byte array
     *
     * @param string The hexadecimal string to be converted
     * @return byte[] The byte array corresponding to the hexadecimal string
     */
    public static byte[] hexToBytes(String string) {
        LogUtils.i("string = " + string);
        if (string == null) {
            return new byte[0];
        }
        String hexString = string.replace(" ", "");
        int cycleLength = hexString.length() / DATA_BYTE_LENGTH;
        byte[] resultByte = new byte[cycleLength];
        int beginIndex;
        int endIndex;
        for (int index = 0; index < cycleLength; index++) {
            beginIndex = index * DATA_BYTE_LENGTH;
            endIndex = index * DATA_BYTE_LENGTH + DATA_BYTE_LENGTH;
            try {
                resultByte[index] =
                        (byte) Integer.parseInt(hexString.substring(beginIndex, endIndex), CONVERT_RADIX_16);
            } catch (NumberFormatException e) {
                LogUtils.e("hexToBytes NumberFormatException");
            }
        }
        return resultByte;
    }
}
