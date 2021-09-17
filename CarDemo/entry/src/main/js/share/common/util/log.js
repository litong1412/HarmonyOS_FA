const logSetting = {
    LOG_DEBUG: true,
    LOG_TAG: "net_work"
}
class Log {
    info(data) {
        if (logSetting.LOG_DEBUG) {
            console.info(logSetting.LOG_TAG + ":" + data)
        }
    }

    error(data) {
        if (logSetting.LOG_DEBUG) {
            console.error(logSetting.LOG_TAG + ":" + data)
        }
    }
}

var log = new Log()

export default log