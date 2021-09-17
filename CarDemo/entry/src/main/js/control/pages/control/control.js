import Log from '../../../share/common/util/log.js'
import app from '@system.app';
import configuration from '@system.configuration'
import bleOperator from '../../../share/common/ble/bleOperator.js'
import commonOperator from '../../../share/common/util/commonOperator.js'
import config from '../../../share/common/util/config.js'
import prompt from '@system.prompt';

export default {
    data: {
        title: "",
        number: 0,
        modeFlag: "half",
        isFullScreen: false,
        isShowSpeed: false,
        isShowLight: false,
        isShowSound: false,
        onOpenState: true,
        onSafeState: false,
        tip_hot: false,
        window_modal: 2,
        token_data: "",
        showSafeTip: "",
        songFlag: true,
        deviceInfo:
        {
            switchDesc: "已开启",
            already: false,
            batt_value: "",
            switchImg: "/common/img/base/switchoff2.png",
            switchOn: "/common/img/base/switch3.png"
        },
        features: [
            {
                name: "哄睡",
                value: "已启用",
                icon: "/common/img/base/sleep3.png",
                iconcolor: "/common/img/base/sleepcolor3.png",
                flag: false
            },
            {
                name: "风速",
                value: "2 档",
                icon: "/common/img/base/speed3.png",
                iconcolor: "/common/img/base/speedcolor3.png",
                flag: false
            },
            {
                name: "灯光",
                value: "2 档",
                icon: "/common/img/base/light3.png",
                iconcolor: "/common/img/base/lightcolor3.png",
                flag: false
            },
            {
                name: "音量",
                value: "2 档",
                icon: "/common/img/base/sound3.png",
                iconcolor: "/common/img/base/soundcolor3.png",
                flag: false
            }
        ],
        speedList: ['关闭', '自动', '1 档', '2 档', '3 档',],
        lightList: ['关闭', '1 档', '2 档', '3 档'],
        macAddr: undefined,
        productId: undefined,
        token: undefined
    },
    onInit() {
        Log.info("oninittttttttttt...............wwwwwwwwwwwwww")
        this.initListener()

        if (this.params == undefined) {
            Log.error("Params is undefined!");
        } else {
            let paramJson = JSON.parse(this.params);
            this.macAddr = paramJson.macAddr;
            this.productId = paramJson.productId;

            const MAC = this.macAddr.split(':').join('')
            Log.info(MAC)

            const CRC = this.getCRC(`0711${MAC}`);
            Log.info(CRC)

            setTimeout(() => {
                bleOperator.controlDevice(`AA0711${MAC}${CRC}55`)
            }, 200)
        }


    },
    initListener() {
        bleOperator.setDeviceInfoListener(this.deviceStateCallback)
    },
    exit() {
        Log.info("app terminate control")
        app.terminate();
    },
    // 滑动时触发
    swipe_move(msg) {

    },
    onShow() {
        Log.info("onshow...............wwwwwwwwwwwwww")
        const MAC = this.macAddr.split(':').join('')

        const CRC = this.getCRC(`0711${MAC}`);

        setTimeout(() => {
            bleOperator.controlDevice(`AA0711${MAC}${CRC}55`)
        }, 100)
    },

    image_click() {
        this.number++

        Log.info(this.number)
//        bleOperator.controlDevice(`AA071104C3E6CEFF371355`)
    },
    //    -----------------------------转CRC代码
    HexStringToByte(str) {
        var len = (str.length / 2);
        var byte_array = str.split('').map(function (x) {
            return parseInt('0x' + x)
        });
        var result = [];
        for (var i = 0; i < len; i++) {
            var pos = i * 2;
            result[i] = this.toByte(this.toXChart(byte_array[pos]) << 4 | this.toXChart(byte_array[pos + 1]));
        }
        return result;
    },
    toXChart(i) {
        return Number('0x' + '0123456789ABCDEF'.charAt(i));
    },
    ////8进制补位
    toByte(curr) {
        if ((curr & 0x80) !== 0) {
            curr = Math.abs(curr) - Math.pow(2, 8)
        } else {
            curr = curr
        }
        return curr;
    },
    calc_crc8(data) {
        if (data.length < 1) {
            return 0xff;
        }
        var thisbyte;
        var crc = 0xffff;
        var lastbit;

        for (var i = 0; i < data.length; i++) {
            if (data[i] < 0) {
                thisbyte = 0x100 + data[i];
            } else {
                thisbyte = data[i];
            }
            crc = crc ^ thisbyte;
            for (var shift = 1; shift <= 8; shift++) {
                lastbit = crc & 0x0001;
                crc = (crc >> 1) & 0x7fff;
                if (lastbit == 0x0001) {
                    crc = crc ^ 0xa001;
                }
            }
        }
        return (crc & 0xff);
    },
    ////十六进制%02
    to02xUC(num) {
        return ('0' + num.toString(16)).substr(-2, 2).toUpperCase()
    },
    getCRC(str) {
        var hexString = this.HexStringToByte(str);
        return this.to02xUC(this.calc_crc8(hexString));
    },
    //// 自动接收到后台发送过来的信息
    deviceStateCallback(callbackData) {
        Log.info("device info change = " + JSON.stringify(callbackData))
        let data = callbackData.data.deviceInfo.toUpperCase();

        if (!data) return;
        const cmd = data.substr(4, 2);

        if (cmd == 11) {
            this.token_data = data
            this.postDeviceInfo(data)
        }
        else if (cmd == 41) {
            // 设备状态 待机或正常
            this.onOpenState = Boolean(Number(data.substr(6, 2)));
            this.renderState()

            // 获取安全带的指令
            this.getSafetyBelt(data)

            // 风量档位
            var speed = Number(data.substr(8, 2)) || 0;
            this.features[1].value = this.speedList[speed]
            //  音量档位
            var volume = Number(data.substr(10, 2)) || 0;
            this.features[3].value = this.lightList[volume]
            //  灯光档位
            var light = Number(data.substr(12, 2)) || 0;
            this.features[2].value = this.lightList[light]
            // 哄睡状态
            var sleep = Number(data.substr(16, 2)) || 0;
            if (sleep) {
                this.features[0].value = '已启用'
                // 如果哄睡已启用 发送开启音乐指令
                this.startSong()
            } else {
                this.features[0].value = '关闭'
            }
        } else if (cmd == 52) {
            prompt.showToast({
                message: "进入待机状态"
            })
        } else if (cmd == 53) {
            prompt.showToast({
                message: "取消待机状态"
            })

            this.features[0].value = '关闭'
            this.songFlag = true

            setTimeout(() => {
                const crc = this.getCRC(`0563${this.token}`);
                // 取消待机后 发送获取安全带指令
                bleOperator.controlDevice(`AA0563${this.token}${crc}55`);
            }, 100)
        } else if (cmd == 47) {
            //            prompt.showToast({
            //                message: "切换灯光成功" + data
            //            })
            var light = Number(data.substr(6, 2)) || 0;

            switch (light) {
                case 0:
                    this.features[2].value = '关闭'
                    // 设置灯光 发送灯光为关闭
                    break;
                case 1:
                    this.features[2].value = '1 档'
                    break;
                case 2:
                    this.features[2].value = '2 档'
                    break;
                case 3:
                    this.features[2].value = '3 档'
                    break;
            }
        } else if (cmd == 43) {
            //            prompt.showToast({
            //                message: "切换风速成功" + data
            //            })
            var speed = Number(data.substr(6, 2)) || 0;

            switch (speed) {
                case 0:
                    this.features[1].value = '关闭'
                    break;
                case 1:
                    this.features[1].value = '自动'
                    break;
                case 2:
                    this.features[1].value = '1 档'
                    break;
                case 3:
                    this.features[1].value = '2 档'
                    break;
                case 4:
                    this.features[1].value = '3 档'
                    break;
            }
        } else if (cmd == 45) {
            var sound = Number(data.substr(6, 2)) || 0;
            switch (sound) {
                case 0:
                    this.features[3].value = '关闭'
                    break;
                case 1:
                    this.features[3].value = '1 档'
                    break;
                case 2:
                    this.features[3].value = '2 档'
                    break;
                case 3:
                    this.features[3].value = '3 档'
                    break;
            }
        } else if (cmd == "4A") {
            // 推送报警温度
            const value = parseInt(data.substr(6, 4), 16);
            const temperature = value < 32768 ? value : value - 65536;
            var hot_warning = (temperature / 100) > 35;

            if (hot_warning) {
                // 需要显示温度报警信息
                this.tip_hot = true
            } else if (!hot_warning) {
                this.tip_hot = false
            }
        } else if (cmd == 63) {
            // 座椅安全带状态
            // 判断是否为待机状态 如果是待机状态 return
            if (!this.onOpenState) {
                this.deviceInfo.already = false
                return
            }
            const safeState = Number(data.substr(6, 2))

           Log.info('安全带状态' + safeState)

            if (safeState == 0) {
                // 非扣上状态
                this.deviceInfo.already = true
                this.onSafeState = true
            } else if (safeState == 1) {
                // 扣上状态
                this.deviceInfo.already = true
                this.onSafeState = false
            }
        } else if (cmd == 48) {
//          var isSleep = Number(data.substr(6, 2))
//          Log.info('当前歌曲状态48为' + isSleep)
//
//          if (isSleep == 0) {
//              this.features[0].value = '关闭'
//          } else if (isSleep == 1) {
//              this.features[0].value = '已启用'
//
//              var sleepState = Number(data.substr(12, 2))
//
//              Log.info('当前播放状态为' + sleepState)
//              if (sleepState == 0) {
//                  Log.info('当前播放状态为 停止')
//
//              } else if (sleepState == 1) {
//                  Log.info('当前播放状态为 播放')
//
//                    //如果是开机状态 开始播放音乐
//                  if (this.onOpenState) {
//                      this.startSong()
//                  }
//
//              } else if (sleepState == 2) {
//                  Log.info('当前播放状态为 暂停')
//                  // 取消哄睡状态
//                  this.features[0].value = '关闭'
//              }
//          }
//          AA 05 48 00 00 01 00 E155
        } else if (cmd == 49) {
            // 哄睡
            Log.info('当前歌曲状态为' + data)
            var sleep = Number(data.substr(10, 2)) || 0;

            if (sleep == 0 || sleep == 2) {
                Log.info('当前歌曲状态为' + sleep)
                this.features[0].value = '关闭'
            } else if (sleep == 1) {
                this.features[0].value = '已启用'
            }
        }
    },
    // 开始播放歌曲
    startSong () {
        const crc = this.getCRC(`0849${this.token}000000`);
        bleOperator.controlDevice(`AA0849${this.token}000000${crc}55`);
    },
    // 发送获取设备当前信息
    postDeviceInfo(data) {
        this.token = data.substr(6, 8);
        const crc = this.getCRC(`0541${this.token}`);

        setTimeout(() => {
            bleOperator.controlDevice(`AA0541${this.token}${crc}55`);
        }, 100)
    },

    // 获取安全带是否绑定状态
    getSafetyBelt(data) {
        this.token = data.substr(6, 8);

        const crc = this.getCRC(`0563${this.token}`);

        setTimeout(() => {
            bleOperator.controlDevice(`AA0563${this.token}${crc}55`);
        }, 150)
    },

    // 渲染当前设备的状态
    renderState() {
        if (this.onOpenState) {
//            this.deviceInfo.already = true
            // 如果是打开状态
        } else {
            // 关闭状态
            this.features[0].value = "关闭"
            this.features[1].value = "关闭"
            this.features[2].value = "关闭"
            this.features[3].value = "关闭"
            this.deviceInfo.switchDesc = '已关闭'
            // 座椅安全带隐藏
            this.deviceInfo.already = false
        }
    },
    // 点击按钮时触发
    powerOffClick() {
        // 需要关闭
        this.onOpenState = false
        this.onSafeState = false
        this.deviceInfo.already = false

        this.features[0].value = "关闭"
        this.features[1].value = "关闭"
        this.features[2].value = "关闭"
        this.features[3].value = "关闭"

        this.deviceInfo.switchDesc = "已关闭"
        // 发送待机指令
        const crc = this.getCRC(`0552${this.token}`);
        bleOperator.controlDevice(`AA0552${this.token}${crc}55`);
    },
    powerOnClick() {
        // 需要打开
        this.onOpenState = true

        this.postDeviceInfo(this.token_data)

        this.deviceInfo.switchDesc = "已开启"
        // 发送开启指令
        const crc = this.getCRC(`0553${this.token}`);
        bleOperator.controlDevice(`AA0553${this.token}${crc}55`);

    },
    // 点击屏幕时触发
    container_click() {
        this.isShowLight = false
        this.isShowSound = false
        this.isShowSpeed = false
    },
    // 点击icon图片触发
    icon_click(idx, item) {
        //        先判断是否是待机状态
        if (!this.onOpenState) {
            return
        }

        switch (idx) {
            case 0:
                if (item.name == '哄睡') {
                    if (item.value == "关闭") {
                        // 发送启用音乐的指令
                        item.value = "已启用"

                        if (this.songFlag) {
                            // 第一次点击发送00
                            this.startSong()
                            this.songFlag = false
                        } else {
                            // 之后点击发送04
                            const crc = this.getCRC(`0849${this.token}040000`);
                            bleOperator.controlDevice(`AA0849${this.token}040000${crc}55`);
                        }
                    } else {
                        item.value = "关闭"
                        // 发送关闭音乐的指令
                        const crc = this.getCRC(`0849${this.token}040000`);
                        bleOperator.controlDevice(`AA0849${this.token}040000${crc}55`);
                    }
                }
                break;
            case 1:
                // this.$element("speedMenu").show({x:350, y: 436});
                // 原生弹出列表
                this.isShowLight = false
                this.isShowSound = false
                this.isShowSpeed = !this.isShowSpeed
                break;
            case 2:
                // this.$element("lightMenu").show({x:4, y: 511});
                this.isShowSound = false
                this.isShowSpeed = false
                this.isShowLight = !this.isShowLight
                break;
            case 3:
                this.isShowLight = false
                this.isShowSpeed = false
                // this.$element("soundMenu").show({x:350, y: 511});
                this.isShowSound = !this.isShowSound
                break;
        }
    },
    // 风速弹框选择
    onSpeedMenuSelected(n) {
        this.isShowSpeed = !this.isShowSpeed
        // 点击弹框时触发
        const val = this.to02xUC(n);
        const crc = this.getCRC(`0743${this.token}00${val}`);
        bleOperator.controlDevice(`AA0743${this.token}00${val}${crc}55`);
    },

    // 灯光弹框选择
    onLightMenuSelected(n) {
        this.isShowLight = !this.isShowLight
        // 点击弹框时触发
        const val = this.to02xUC(n);

        const crc = this.getCRC(`0747${this.token}00${val}`);
        bleOperator.controlDevice(`AA0747${this.token}00${val}${crc}55`);
    },
    // 音量选择弹框
    onSoundMenuSelected(n) {
        this.isShowSound = !this.isShowSound
        // 点击弹框时触发
        const val = this.to02xUC(n);
        const crc = this.getCRC(`0745${this.token}00${val}`);
        bleOperator.controlDevice(`AA0745${this.token}00${val}${crc}55`);
    },
    // 点击获取更多触发
    getMoreClick() {
        if (!this.isFullScreen) {
            this.isFullScreen = true;
            app.requestFullWindow();
        }
    },
    // 点击后退按钮触发
    backClicked() {
        bleOperator.closeBleConnection(this.macAddr)
         app.terminate();
    },
    controClick(id) {
        switch (id) {
            case 0:
                break;
            case 1:
                // this.$element("speedMenu").show({x:350, y: 436});
                // 原生弹出列表
                this.isShowLight = false
                this.isShowSound = false
                this.isShowSpeed = !this.isShowSpeed
                break;
            case 2:
                // this.$element("lightMenu").show({x:4, y: 511});
                this.isShowSound = false
                this.isShowSpeed = false
                this.isShowLight = !this.isShowLight
                break;
            case 3:
                this.isShowLight = false
                this.isShowSpeed = false
                // this.$element("soundMenu").show({x:350, y: 511});
                this.isShowSound = !this.isShowSound
                break;
        }
    }
}
