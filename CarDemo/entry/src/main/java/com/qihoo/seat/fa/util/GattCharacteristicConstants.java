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

import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;

/**
 * GATT Characteristic Constants
 *
 * @since 2021-08-12
 */
public class GattCharacteristicConstants {
    /**
     * Characteristic property: Characteristic is broadcastable.
     */
    public static final int PROPERTY_BROADCAST = 0x01;

    /**
     * Characteristic property: Characteristic is readable.
     */
    public static final int PROPERTY_READ = 0x02;

    /**
     * Characteristic property: Characteristic can be written without response.
     */
    public static final int PROPERTY_WRITE_NO_RESPONSE = 0x04;

    /**
     * Characteristic property: Characteristic can be written.
     */
    public static final int PROPERTY_WRITE = 0x08;

    /**
     * Characteristic property: Characteristic supports notification
     */
    public static final int PROPERTY_NOTIFY = 0x10;

    /**
     * Characteristic property: Characteristic supports indication
     */
    public static final int PROPERTY_INDICATE = 0x20;

    /**
     * Characteristic property: Characteristic supports write with signature
     */
    public static final int PROPERTY_SIGNED_WRITE = 0x40;

    /**
     * Characteristic property: Characteristic has extended properties
     */
    public static final int PROPERTY_EXTENDED_PROPS = 0x80;

    /**
     * UIManager
     *
     * @since 2021-08-11
     */
    public static class UIManager {
        private static UIManager INSTANCE = null;
        private EventHandler mHander = null;

        private UIManager() {
        }

        /**
         * get obejct of UIManager
         *
         * @return UIManager
         */
        public static UIManager getInstance() {
            if (UIManager.INSTANCE == null) {
                UIManager.INSTANCE = new UIManager();
                UIManager.INSTANCE.init();
            }
            return UIManager.INSTANCE;
        }

        private void init() {
            EventRunner runner = EventRunner.getMainEventRunner();
            mHander = new EventHandler(runner) {
                @Override
                protected void processEvent(InnerEvent event) {
                    super.processEvent(event);
                    if (event == null) {
                        return;
                    }
                    if (event.object instanceof Runnable) {
                        Runnable runnable = (Runnable) event.object;
                        runnable.run();
                    }
                }
            };
        }

        /**
         * delay execution r
         *
         * @param runnable runnable对象
         * @param delayTime 延迟时间
         */
        public void postEventDelay(Runnable runnable, long delayTime) {
            int eventId1 = 0;
            long param = 0L;
            InnerEvent event1 = InnerEvent.get(eventId1, param, runnable);
            mHander.sendEvent(event1, delayTime);
        }
    }
}
